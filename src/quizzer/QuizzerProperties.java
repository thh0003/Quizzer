package quizzer;

/**
 * <p>Title: QuizzerProperties</p>
 * <p>Description: Class containing constants and convenience variables for
Quizzer.</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.Properties;

public class QuizzerProperties
{
    public static final String AUTHOR               = "Trevor Holmes";
    public static final String VERSION              = "Version 1.0";
    public static final String DATE                 = "February, 1, 2020";

    public static final String COMMENT              = "*";

    public static final String Q_START              = "@Q";
    public static final String A_START              = "@A";
    public static final String A_END                = "@E";

    public static final String MAC_EOL              = "\r";
    public static final String UNIX_EOL             = "\n";
    public static final String WINDOWS_EOL          = "\r\n";

    public static final int MAX_QUESTIONS           = 1000;
    public static final int MAX_QUESTION_LINES      = 10;
    public static final int MAX_CHARS_PER_LINE      = 75;

    public static final int DEFAULT_Q_COUNT       = 10;

    public static final boolean SHOW_ANSWERS        = true;

    public static String EOL                        = WINDOWS_EOL;

    public static String QUIZZER_DIR                   = "P:\\GIT-HUB\\CS533\\quizzer\\Quizzer";

    public static String DEBUG_FILE                 = QUIZZER_DIR + File.separator + "debug.txt";

    public static String DEFAULT_Q_FILE             = QUIZZER_DIR + File.separator + "QFILES" + File.separator + "test10.q.txt";
}
