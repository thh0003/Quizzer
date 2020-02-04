package quizzer;


/**
 * <p>Title: Quizzer</p>
 * <p>Description: Starting class for Quizzer. Parses command line
arguments and starts QuizController to begin quiz.</p>
 * @author Corey Wineman; adapted by Jim Mooney; and by Trevor Holmes
 * @version 1.0
 */

import java.io.File;

public class Quizzer
{
    public static final String USAGE = QuizzerProperties.EOL
            + "Usage: cs533.assignment1.Quizzer [-n count]"
            + " [-a show_answers] quiz_file"
            + QuizzerProperties.EOL
            + QuizzerProperties.EOL + "\t" 
            + "count = number of questions to be asked"
            + QuizzerProperties.EOL + "\t"
            + "show_answers = (y|n|yes|no) show answers to incorrect choices"
            + QuizzerProperties.EOL;

    private boolean showAnswers;
    
    private int     qCount;

    private String  qFilename;

    /**
     * Constructor, main class for Quizzer. Parses command line options
     * and  starts a QuizController
     * or exits if the command line arguments were invalid.
     * Parsing will fail if the quiz filename is not the last argument.
     * @param args Command line arguments
     */
    public Quizzer( String [] args )
    {
    	if( !parseArguments( args ) )
        {
            System.out.println( USAGE );
            return;
        } else {
            QuizController quizController = new QuizController( this.qCount, this.qFilename, this.showAnswers );
            quizController.quiz();
        }
    }

    /**
     * Parses the command line arguments. Any unexpected or invalid
     * argument will cause the parsing to fail.
     * @param args Command line arguments
     * @return whether or not the command line arguments were valid
     * and successfully parsed.
     */
     
    private boolean parseArguments( String [] args )
    {
        Boolean retVal = true;
        this.showAnswers = QuizzerProperties.SHOW_ANSWERS;
        this.qCount = QuizzerProperties.DEFAULT_Q_COUNT;
        this.qFilename = QuizzerProperties.DEFAULT_Q_FILE;

        if (args.length == 0){
            retVal =  false;
        } else{
            for (var x=0;x<args.length;x++){
                switch (args[x].charAt(0)) {
                    case '-':
                        if (args[x].charAt(1) == 'n'){
                            if (args[x+1].length() > 0 && Integer.parseInt(args[x+1])<=QuizzerProperties.MAX_QUESTIONS && Integer.parseInt(args[x+1])>0){
                                this.qCount = Integer.parseInt(args[x+1]);
                                x = x + 1;
                            } else {
                                retVal =  false;
                            }
                        } else if (args[x].charAt(1) == 'a'){
                            if (args[x+1].length() > 0){
                                if (Character.toLowerCase(args[x+1].charAt(0))=='y'){
                                    this.showAnswers = true;
                                    x = x + 1;
                                } else if (Character.toLowerCase(args[x+1].charAt(0))=='n') {
                                    this.showAnswers = false;
                                    x = x + 1;
                                } else {
                                    retVal = false;
                                }
                            } else {
                                retVal =  false;
                            }
                        } else{
                            retVal = false;

                        }
                        break;
                    default:
                        this.qFilename = QuizzerProperties.QUIZZER_DIR + File.separator + "QFILES" + File.separator + args[x];
                        x = x + 1;
                        break;

                }

            }
        }
        return retVal;
    }

    /**
     * The main method for invoking Quizzer.
     * @param args Command line arguments
     */
    public static void main( String [] args )
    {
        try
        {
            new Quizzer(args);
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}

