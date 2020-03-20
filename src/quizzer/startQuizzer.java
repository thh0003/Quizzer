package quizzer;

public class startQuizzer{
	
/**
     * The main method for invoking Quizzer.
     * @param args Command line arguments
     * Usage: cs533.assignment1.Assignment1 [-h] [-n count] [-a show_answers] [-q quiz_file] [-g gui]
     */
    public static void main( String[] args )
    {
        try
        {
        	Quizzer quiz = new Quizzer(args);
        	quiz.startQuiz();
        	
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}