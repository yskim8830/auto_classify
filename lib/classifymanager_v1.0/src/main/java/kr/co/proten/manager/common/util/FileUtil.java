package kr.co.proten.manager.common.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 


public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
	public static String fileseperator = System.getProperty("file.separator");

    public static void moveDirectory(String fromDir, String toDir) throws IOException {
        moveDirectory(new File(fromDir), new File(toDir));
    }
    /**
     * 파일 이름 변경 메소드
     *
     * @param fromDir
     * @param toDir
     * @throws IOException
     */
    public static void moveDirectory(File fromDir, File toDir) throws IOException {

        if ( !toDir.exists() ) {
            toDir.mkdirs();
        }

        if ( fromDir.isDirectory() ) {
            File [] listFiles = fromDir.listFiles();
            if(listFiles != null) {
            	for ( int idx=0 ; idx < listFiles.length; idx ++ ) {
                    if ( listFiles[idx].isDirectory() ) {
                        moveDirectory(listFiles[idx],toDir);
                    } else {
                        File to = new File(toDir.getPath() + File.separator + listFiles[idx].getName());
                        if (!listFiles[idx].renameTo(to)) {
                            copyFile(listFiles[idx], to);
                            if (!listFiles[idx].delete()) {
                                throw new IOException("Failed to delete " + fromDir.getPath() + " while trying to rename it.");
                            }
                        }

                    }
                }
            }
        }
    }


    public static void copyFile(File source, File target) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            if ( !new File(target.getParent()).exists()    ) {
                new File(target.getParent()).mkdirs();
            }
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(target));
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
            out.flush();	// just in case
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                	log.error("IOException " + e.getMessage(), e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                	log.error("IOException " + e.getMessage(), e);
                }
        }
    }
	 /**
     * @param dir  directory
     * @return String
     */
    public static String lastSeparator(String dir) {
        if (dir == null || dir.equals("")) return "";
        if (dir.lastIndexOf(fileseperator) != dir.length() - 1) {
            dir += fileseperator;

        }
        return dir;
    }
 //파일을 존재여부를 확인하는 메소드
	public static Boolean fileIsLive(String isLivefile) {
	
		if(null == isLivefile) {
			  return false;
		}
		
		File f1 = new File(isLivefile); 
		if(f1.exists()){
		    return true;
		}else{
			return false;
		}
	 }
	
	public static void makeDir(String path){
		  
		File dir = new File(path); // ex) D:\temp\
		 
		if(!dir.exists()){
			//System.out.println("################# make디렉토리");
			dir.mkdirs();
		}
		 
	 }


	//파일을 생성하는 메소드
	public static String getPath4Unix(File file){
    	String path  = file.getPath();
    	path = StringUtil.replace(path,"\\","/");
  		return path ;
	}
	
 	//파일을 생성하는 메소드
	public static boolean fileMake(String path, String fileName){
	  
		File dir = new File(path); // ex) D:\temp\
		File fullFileName = new File(path+fileName);
		if(!dir.exists()){
			//System.out.println("################# make디렉토리");
			dir.mkdirs();
		}
		try {
			fullFileName.createNewFile();
		} catch (IOException e) {
			log.error("IOException " + e.getMessage(), e);
		}
		return true;
	 }
	/**
	*
	* @param directory directory to clean.
	* @throws IOException in case cleaning is unsuccessful
	*/
	public static void cleanDirectoryOnExit(File directory) {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			return;
			//throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			return;
			//throw new IllegalArgumentException(message);
		} 
		File[] files = directory.listFiles();
		if(files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					forceDeleteOnExit(file);
				} catch (IOException e) {
					log.error("IOException " + e.getMessage(), e);
				}
			}			
		}
	}
	/**
	 * force delete file or directory
	 *
	 * @param file file or directory to delete.
	 * @throws IOException in case deletion is unsuccessful
	 */
	public static void forceDeleteOnExit(File file) throws IOException {
	    if (file.isDirectory()) {
	        deleteDirectoryOnExit(file);
	    } else {
	        file.deleteOnExit();
	    }
	}

	/**
	 *
	 * @param directory directory to delete.
	 * @throws IOException in case deletion is unsuccessful
	 */
	private static void deleteDirectoryOnExit(File directory)
	        throws IOException {
	    if (!directory.exists()) {
	        return;
	    }
	
	    try {
			cleanDirectoryOnExit(directory);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		}
	    directory.deleteOnExit();
	}


	//파일을 삭제하는 메소드
	public static boolean fileDelete(String deleteFileName) {
		File I = new File(deleteFileName);
	
		return I.delete();
	}
	
	//파일을 복사하는 메소드
	public static boolean fileCopy(String inFileName, String outFileName) {
		FileInputStream fis = null;
		FileOutputStream fos = null;	
		boolean result = false;
		try {
			fis = new FileInputStream(inFileName);
			fos = new FileOutputStream(outFileName);
	
			int data = 0;
			while((data=fis.read())!=-1) {
				fos.write(data);
			}
			result = true;
			
		} catch (IOException e) {
			log.error("IOException " + e.getMessage(), e);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					log.error("IOException " + e.getMessage(), e);
				}
			}
			
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("IOException " + e.getMessage(), e);
				}
			}
		}
		return result;
	}
	
	//파일을 이동하는 메소드
	public static void fileMove(String inFileName, String outFileName) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(inFileName);
			fos = new FileOutputStream(outFileName);
	
			int data = 0;
			while((data=fis.read())!=-1) {
				fos.write(data);
			}
			
			fos.close();
		} catch (IOException e) {
			log.error("IOException " + e.getMessage(), e);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					log.error("IOException " + e.getMessage(), e);
				}
			}
			
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("IOException " + e.getMessage(), e);
				}
			}
			
			//복사한뒤 원본파일을 삭제함
			fileDelete(inFileName);
		}
	}
 
 
    //디렉토리의 파일 리스트를 읽는 메소드
	public static List<File> getDirFileList(String dirPath){
		// 디렉토리 파일 리스트
		List<File> dirFileList = null;
	
		// 파일 목록을 요청한 디렉토리를 가지고 파일 객체를 생성함
		File dir = new File(dirPath);
	
		// 디렉토리가 존재한다면
		if (dir.exists())
		{
		// 파일 목록을 구함
			File[] files = dir.listFiles(); 
			// 파일 배열을 파일 리스트로 변화함 
			dirFileList = Arrays.asList(files);
		}
	
		return dirFileList;
	}

    public static void writeString(String path, String data) throws IOException {
        File file = new File(path);
        writeString(file,data);
    }



   public static void writeString(File dir, String name, String data) throws IOException {
       File sf = new File(dir, name);
       writeString(sf,data);
   
   }
   
   public static void writeString(File sf, String data) {
	   BufferedWriter writer = null;
	   try {
		   writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sf), "UTF-8"));
		   writer.write(data);		   
	   } catch(Exception e) {
		   log.error("Exception " + e.getMessage(), e);
	   } finally {
		   if(writer != null) {
			   try {				   
				   writer.close();
			   } catch (IOException e) {
				   log.error("Exception " + e.getMessage(), e);
			   }			   
		   }
	   }
   }
   

   public static String readFile(File file) {
	   
       StringBuffer buf = new StringBuffer();
       BufferedReader in = null;
       String result = "";
       
       try {
           in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
           String line="";
           while ((line = in.readLine()) != null) {
        	   buf.append(line);
               buf.append("\n");
               
           }
       } catch (Exception fnfe) {
    	   if (in != null) {
    		   try {
    			   in.close();
    		   } catch (IOException e) {
    			   log.error("IOException " + e.getMessage(), e);
    		   }
    	   }
       }
       
       result = buf.toString();
       buf.setLength(0);
       return result;
   }
   
   public static List<String> readFileList(File file) {
       BufferedReader in = null;
       List<String> rList = new ArrayList<String>();
       try {
           in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
           String line;
           while ((line = in.readLine()) != null) {
        	   rList.add(line); 
           }
       } catch (Exception fnfe) {
    	   log.error("IOException " + fnfe.getMessage(), fnfe);
       } finally {
    	   if (in != null) {
    		   try {
    			   in.close();
    		   } catch (IOException e) {
    			   log.error("IOException " + e.getMessage(), e);
    		   }
    	   }
       }
       return rList;
   }
   
   /**
    * 
    *
    * @param file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static String getText(File file) throws IOException {
   	 
        
       BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
       StringBuilder sbText = new StringBuilder();
       String line = "";
       try {
           while ((line = reader.readLine()) != null) {
           	sbText.append(line);
           	sbText.append(StringUtil.newLine);
           }
           
       } catch (Exception e) {
    	   log.error("Exception " + e.getMessage(), e);
       } finally {
       	try {
       		reader.close();
       	}catch(Exception e) {
       		log.error("Exception " + e.getMessage(), e);
       	};
       }
       return sbText.toString();
   }
   
   
   /**
    * 
    *
    * @param file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static String getText(File file,int rowCount) throws IOException {
   	 
        
       BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
       StringBuilder sbText = new StringBuilder();
       String line = "";
       int rCnt = 0;
       try {
           while ((line = reader.readLine()) != null) {
           	rCnt++;
           	if(rowCount!=0 && rowCount<rCnt) {
           		break;
           	}
           	sbText.append(line);
           	sbText.append(StringUtil.newLine);
           }
           
       } catch (Exception e) {
    	   log.error("IOException " + e.getMessage(), e);
       } finally {
       	try {
       		reader.close();
       	}catch(Exception e) {
       		log.error("Exception " + e.getMessage(), e);
       	};
       }
       return sbText.toString();
   }



	/**
	 * String sort method.
	 * @param source
	 *            the source array
	 * @return the sorted array (never null)
	 */
	public static File [] sortStringFiles(File [] source) {
		if (source == null) {
			return null;
		}
		Arrays.sort(source);
		return source;
	}


 
}






