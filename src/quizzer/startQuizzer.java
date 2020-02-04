package quizzer;

public class startQuizzer{
/**
     * The main method for invoking Quizzer.
     * @param args Command line arguments
     */
    public static void main( String[] args )
    {
        try
        {
        	Quizzer quiz = new Quizzer();
            quiz.startQuiz();

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}