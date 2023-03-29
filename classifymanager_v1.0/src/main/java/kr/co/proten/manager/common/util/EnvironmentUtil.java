package kr.co.proten.manager.common.util;

import java.io.File;

public class EnvironmentUtil {
	public enum OperatingSystem { WINDOWS, UNIX_LIKE };

	private static OperatingSystem operatingSystem;
	
	static {
		operatingSystem = (File.separatorChar == '\\') ? OperatingSystem.WINDOWS : OperatingSystem.UNIX_LIKE;
	}		

	public static void setOperatingSystem(OperatingSystem aOperatingSystem) {
		operatingSystem = aOperatingSystem;
	}
	
	public static OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}
	
	public static boolean isUnixLikeOperatingSystem() {
		return operatingSystem == OperatingSystem.UNIX_LIKE;
	}

	public static boolean isWindows() {
		return operatingSystem == OperatingSystem.WINDOWS;
	}	

	public static boolean symlinksSupported() {
		return isUnixLikeOperatingSystem();
	}
}