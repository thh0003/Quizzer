package quizzer;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * <p>Title: QuizzerProperties</p>
 * <p>Description: Class containing constants and convenience variables for
Quizzer.</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.io.File;
import java.util.Map;


public class QuizzerProperties
{
    
    //WINDOWS ENVIRONMENT VARIABLES
    public static String ALLUSERSPROFILE            = System.getenv("ALLUSERSPROFILE").replace("\\", "\\\\");
    public static String SystemDrive             	= System.getenv("SystemDrive").replace("\\", "\\\\");
    public static String JRE_HOME             		= System.getenv("JRE_HOME").replace("\\", "\\\\");
//    public static String USERNAME             		= System.getenv("USERNAME"); //use java version
    public static String ProgramFilesPath      		= System.getenv("ProgramFiles(x86)").replace("\\", "\\\\");
    public static String ProgramDataPath      		= System.getenv("ProgramData").replace("\\", "\\\\");
//    public static String HOMEPATH		      		= System.getenv("HOMEPATH"); //user the java user home path
    public static String windir			      		= System.getenv("windir").replace("\\", "\\\\");
//    public static String JAVA_HOME		      		= System.getenv("JAVA_HOME"); //use the java home
    public static String SystemRootPath	      		= System.getenv("SystemRoot").replace("\\", "\\\\");
    public static String USERPROFILE	      		= System.getenv("USERPROFILE").replace("\\", "\\\\");
    
    //JAVA VARIABLES
    public static String desktopOS		      		= System.getProperty("sun.desktop");
    public static String javaSpecVersion		   	= System.getProperty("java.specification.version");
    public static String cpuArch			      	= System.getProperty("sun.cpu.isalist");
    public static String osArch				      	= System.getProperty("sun.arch.data.model");
    public static String javaVMSpecVersion		   	= System.getProperty("java.vm.specification.version");
    public static String osName				      	= System.getProperty("os.name");
    public static String jdkBinPath			      	= System.getProperty("sun.boot.library.path").replace("\\", "\\\\");
    public static String appStartCmd		      	= System.getProperty("sun.java.command");
    public static String cpuEndian			      	= System.getProperty("sun.cpu.endian");
    public static String userHome			      	= System.getProperty("user.home").replace("\\", "\\\\");
    public static String javaHome			      	= System.getProperty("java.home").replace("\\", "\\\\"); 
    public static String javaRuntimeVer		      	= System.getProperty("java.runtime.version");
    public static String userName			      	= System.getProperty("user.name"); 
    public static String osVersion			      	= System.getProperty("os.version");
    public static String javaVersion			   	= System.getProperty("java.version");
    public static String javaLibPath			   	= System.getProperty("java.library.path");
    public static String javaVMVer				   	= System.getProperty("java.vm.version");
    public static String javaClassVer			   	= System.getProperty("java.class.version");
    public static String userAppDir				   	= System.getProperty("user.dir").replace("\\", "\\\\");
    

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static double width = screenSize.getWidth();
	static double height = screenSize.getHeight();
	
    public static final String AUTHOR               = "Trevor Holmes";
    public static final String VERSION              = "Version 1.0";
    public static final String DATE                 = "February, 1, 2020";

    public static final String COMMENT              = "*";
    public static final String TITLE                = "QUIZZER - CS533";
    public static final String Q_START              = "@Q";
    public static final String A_START              = "@A";
    public static final String A_END                = "@E";

    public static final String MAC_EOL              = "\r";
    public static final String UNIX_EOL             = "\n";
    public static final String WINDOWS_EOL          = "\r\n";
    public static final Integer INITIAL_WIDTH       = (width > 700)?700:(int) width;
    public static final Integer INITIAL_HEIGHT      = (height > 500)?500:(int) height;

    public static final int MAX_QUESTIONS           = 1000;
    public static final int MAX_QUESTION_LINES      = 10;
    public static final int MAX_CHARS_PER_LINE      = 75;

    public static final int DEFAULT_Q_COUNT       	= 10;
    public static final String LOGO_FILE       		= "wv-logo.png";
    public static final String BACKGROUND_IMAGE_FILE       = "wvu-campus.jpg";
    public static final boolean SHOW_ANSWERS        = true;
    public static String EOL                        = WINDOWS_EOL;
    
    public static final String WELCOME              = "Welcome to CS533 Quizzer Application " + EOL;
    public static String QUIZZER_DIR                = userAppDir;
    public static String DEBUG_FILE                 = QUIZZER_DIR + File.separator + "debug.txt";
    public static String DEFAULT_Q_FILE             = QUIZZER_DIR + File.separator + "QFILES" + File.separator + "test10.q.txt";
    
    
    
    public QuizzerProperties()
    {
    	
    	
    	
    }
    
    public static void envQuiz() {
    	
    	Map<String, String> sysEnv = System.getenv();
    	sysEnv.entrySet().forEach(System.out::println);
    	System.out.println("Properties: ");
    	System.getProperties().list(System.out);
    	
    	System.out.println("App Path: "+ QUIZZER_DIR);
    }
}
