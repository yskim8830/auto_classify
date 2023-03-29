package kr.co.proten.manager.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PidFileUtil {

	private static final Logger log = LoggerFactory.getLogger(PidFileUtil.class);
	
	private String m_proId;
    private String m_pid_path;
    private File pidFile;
    public PidFileUtil(String proId, String homeRoot) {
        m_proId = proId;
        homeRoot = homeRoot.trim();
        String baseDir = FileUtil.lastSeparator(homeRoot);

        m_pid_path = baseDir + "pid" + FileUtil.fileseperator;
        FileUtil.makeDir(m_pid_path);
        pidFile = new File(m_pid_path,m_proId+".pid");
    }
    
   /* public static Integer getWinPID(Process proc) {
        if (proc.getClass().getName().equals("java.lang.Win32Process")
                || proc.getClass().getName().equals("java.lang.ProcessImpl")) {

            try {
                Field f = proc.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(proc);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE handle = new WinNT.HANDLE();
                handle.setPointer(Pointer.createConstant(handl));
                return kernel.GetProcessId(handle);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                LOG.error("Cannot get Windows pid: " + e.getMessage());
            }
        }

        return null;
    }*/
    
    
	
	public int getProcessPid() {
		try {
			return getProcessPidImpl1();
		}
		catch (Exception e) {
			log.error("Retrieving Java Process PID failed with first method, trying second ...");

			try {
				return getProcessPidImpl2();
			}
			catch (Exception e1) {
				return -1;
			} 
		}
	}

	/**
	 * Determines the process identifier (PID) for the currently active Java process and writes this
	 * PID to the given file.
	 * 
	 * @see #getProcessPid()
	 */
	public void createPidFile() throws IOException {
		pidFile.delete();
		
		try (FileWriter pidFileWriter = new FileWriter(pidFile)) {
			String pidStr = "" + getProcessPid();
			log.debug("Writing PID file (for PID " + pidStr + ") to " + pidFile + " ...");
			pidFileWriter.write(pidStr);
			pidFileWriter.close();
		}
		pidFile.deleteOnExit();		
	}
	
	public void deletePidFile()  {
		if(pidFile!=null)
			pidFile.deleteOnExit();		
	}
	
	/**
	 * Determines whether a process is running, based on the given PID file. The method
	 * reads the PID file and then calls {@link #isProcessRunning(int)}. If the PID file
	 * does not exist, it returns <tt>false</tt>. 
	 */
	public boolean isProcessRunning() {
		log.info("is pid file exists : " + pidFile.exists() + " ::: " +  pidFile.getPath() );
		if (pidFile.exists()) {
			try (BufferedReader pidFileReader = new BufferedReader(new FileReader(pidFile))) {
				int pid = Integer.parseInt(pidFileReader.readLine());
				log.info("Get process id : " + pid );
				return isProcessRunning(pid);
			}
			catch (Exception e) {
				log.error("Cannot read pidfile from " + pidFile + ". Assuming process not running.");
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determines whether a process with the given PID is running. Depending on the
	 * underlying OS, this method either calls {@link #isProcessRunningUnixLike(int)} 
	 * or {@link #isProcessRunningWindows(int)}. 
	 */
	private boolean isProcessRunning(int pid) {
		if (EnvironmentUtil.isUnixLikeOperatingSystem()) {
			return isProcessRunningUnixLike(pid);
		}
		else if (EnvironmentUtil.isWindows()) {
			return isProcessRunningWindows(pid);
		}
		return false;
	}	
	
	/**
	 * Determines whether a process is running, based on the given PID file. The method
	 * reads the PID file and then calls {@link #isProcessRunning(int)}. If the PID file
	 * does not exist, it returns <tt>false</tt>. 
	 */
	public boolean isKill() {
		if (pidFile.exists()) {
			try (BufferedReader pidFileReader = new BufferedReader(new FileReader(pidFile))) {
				int pid = Integer.parseInt(pidFileReader.readLine());
				return isKilling(pid);
			}
			catch (Exception e) {
				log.error("Cannot read pidfile from " + pidFile + ". Assuming process not running.");
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Determines whether a process with the given PID is running. Depending on the
	 * underlying OS, this method either calls {@link #isProcessRunningUnixLike(int)} 
	 * or {@link #isProcessRunningWindows(int)}. 
	 */
	private boolean isKilling(int pid) {
		if (EnvironmentUtil.isUnixLikeOperatingSystem()) {
			return isKillUnixLike(pid);
		}
		else if (EnvironmentUtil.isWindows()) {
			return isKillWindows(pid);
		}
		return false;
	}	
	
	
	
	/**
	 * Uses the {@link RuntimeMXBean}'s name to determine the PID. On Linux, this name 
	 * typically has a value like <tt>12345@localhost</tt> where 12345 is the PID.
	 * However, this is not guaranteed for every VM, so this is only one of two implementations.
	 *
	 */
	private  int getProcessPidImpl1() throws Exception {
		String pidStr = ManagementFactory.getRuntimeMXBean().getName();

		if (pidStr.contains("@")) {
			int processPid = Integer.parseInt(pidStr.split("@")[0]);
			
			log.debug("Java Process PID is " + processPid);
			return processPid;
		}
		else {
			throw new Exception("Cannot find pid from string: " + pidStr);
		}
	}

	/**
	 * Uses the private method <tt>VMManagement.getProcessId()</tt> of Sun's <tt>sun.management.VMManagement</tt>
	 * class to determine the PID (using reflection to make the relevant fields visible).
	 *
	 */
	private  int getProcessPidImpl2() throws Exception {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		Field jvmField = runtimeMXBean.getClass().getDeclaredField("jvm");
		jvmField.setAccessible(true);
		
		// The returned object is of the type 'sun.management.VMManagement', but since we
		// don't need the exact type here and we don't want to reference it in the
		// imports, we'll just hope it's there.
		
		Object vmManagement = jvmField.get(runtimeMXBean); 
		Method getProcessIdMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
		
		getProcessIdMethod.setAccessible(true);
		int processPid = (Integer) getProcessIdMethod.invoke(vmManagement);
		
		log.debug( "Java Process PID is " + processPid);
		return processPid;
	}
	
	/**
	 * Determines whether a process with the given PID is running using the POSIX 
	 * <tt>kill -0</tt> command.
	 */
	private  boolean isProcessRunningUnixLike(int pid) {
		try {
			/*	Runtime runtime = Runtime.getRuntime();
				Process killProcess = runtime.exec(new String[] { "kill", "-0", ""+pid });
				
				int killProcessExitCode = killProcess.waitFor();
				boolean processRunning = killProcessExitCode == 0;

				log.debug("isProcessRunningUnixLike(" + pid + ") returned " + killProcessExitCode + ", process running = " + processRunning);
				return processRunning;
				*/
				
				//log.info("isProcessRunningUnixLike : ps -ef | grep " + pid + " | grep java");
				Process tasklistProcess = Runtime.getRuntime().exec(new String[] { "/bin/sh","-c","ps -ef | grep " + pid + " | grep java" });
				BufferedReader tasklistOutputReader = new BufferedReader(new InputStreamReader(tasklistProcess.getInputStream()));
				
				String line = null;
				boolean processRunning = false;
				while ((line = tasklistOutputReader.readLine()) != null) {
					//Crawler에서는 java.exe를 확인 
					//log.info("isProcessRunningUnixLike : ps -ef | grep " + pid + " | grep java");
					if (line.contains(" " + pid + " ") && ( line.contains("java") || line.contains("javaw") ) && !line.contains("ps -ef") ) {
						processRunning = true;
						log.debug( "Processor Info ( "+line+")");
						break;
					}
				}

				log.info( "isProcessRunningUnix (" + pid + ") returned " + line + ", process running = " + processRunning);
				return processRunning;
			}
			catch (Exception e) {
				log.error( "Cannot retrieve status of PID " + pid + "; assuming process not running.");
				return false;
			}
	}

	 
	private boolean isProcessRunningWindows(int pid) {
		try {
			Process tasklistProcess = Runtime.getRuntime().exec(new String[] { "cmd", "/c", "tasklist /FI \"PID eq " + pid + "\"" });
			BufferedReader tasklistOutputReader = new BufferedReader(new InputStreamReader(tasklistProcess.getInputStream()));
			
			String line = null;
			boolean processRunning = false;
			
			while ((line = tasklistOutputReader.readLine()) != null) {
				if (line.contains(" " + pid + " ")) {
					processRunning = true;
					break;
				}
			}

			log.debug( "isProcessRunningWindows(" + pid + ") returned " + line + ", process running = " + processRunning);
			return processRunning;
		}
		catch (Exception ex) {
			log.error( "Cannot retrieve status of PID " + pid + "; assuming process not running.");
			return false;
		}
	}
	
	private boolean isKillUnixLike(int pid) {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process killProcess = runtime.exec(new String[] { "kill", "-9", ""+pid });
			
			int killProcessExitCode = killProcess.waitFor();
			boolean processRunning = killProcessExitCode == 0;

			log.debug("isKillUnixLike(" + pid + ") returned " + killProcessExitCode + ", process running = " + processRunning);
			return processRunning;
		}
		catch (Exception e) {
			log.error( "Cannot retrieve status of PID " + pid + "; assuming process not running.");
			return false;
		}
	}
	private boolean isKillWindows(int pid) {
		try {
			Process killProcess = Runtime.getRuntime().exec(new String[] { "cmd", "/c", "taskkill /F /T /PID " + pid + "" });
			int killProcessExitCode = killProcess.waitFor();
			boolean processRunning = killProcessExitCode == 0;

			log.debug("isKillWindows(" + pid + ") returned " + killProcessExitCode + ", process running = " + processRunning);
			return processRunning;
		}
		catch (Exception ex) {
			log.error( "Cannot retrieve status of PID " + pid + "; assuming process not running.");
			return false;
		}
	}



}
