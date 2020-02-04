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

public class QuizzerProperties
{
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

    public static final int DEFAULT_Q_COUNT       = 10;
    public static final String LOGO_FILE       = "wv-logo.png";
    public static final String BACKGROUND_IMAGE_FILE       = "wvu-campus.jpg";

    public static final boolean SHOW_ANSWERS        = true;

    public static String EOL                        = WINDOWS_EOL;
    
    public static final String WELCOME              = "Welcome to CS533 Quizzer Application " + EOL;


    public static String QUIZZER_DIR                   = "P:\\GIT-HUB\\CS533\\quizzer\\Quizzer";

    public static String DEBUG_FILE                 = QUIZZER_DIR + File.separator + "debug.txt";

    public static String DEFAULT_Q_FILE             = QUIZZER_DIR + File.separator + "QFILES" + File.separator + "test10.q.txt";
}
