package quizzer;

/**
 * <p>Title: QuestionRecord</p>
 * <p>Description: A class to encapsulate a quiz question, choices and the correct
answer</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

public class QuestionRecord
{
    private String      question;
    private String []   choices;
    private int         answerNumber;

    /**
     * Constructor
     * @param question String contain the question
     * @param choices String array contain the answer choices
     * @param answerNumber The number of the correct answer
     */
    public QuestionRecord( String question, String [] choices, int answerNumber )
    {
        this.question       = question;
        this.choices        = choices;
        this.answerNumber   = answerNumber;
    }

    /**
     * @return the question
     */
    public String getQuestion()
    {
        return question;
    }

    /**
     * @return an array of answer choices
     */
    public String [] getChoices()
    {
        return choices;
    }

    /**
     * @return the number of the correct answer
     */
    public int getAnswerNumber()
    {
        return answerNumber;
    }

    /**
     * Displays the question, choices and correct answer
     * @return the question record in readable form
     */
    public String toString()
    {
        StringBuffer sBuffer = new StringBuffer();
        String CRLF = "\r\n";

        sBuffer.append( "Question: " + question + CRLF  + CRLF );

        sBuffer.append( "Choices: " + CRLF );
        for( int i=0; i<choices.length; i++ )
        {
            sBuffer.append( (i+1) + ": " + choices[i] + CRLF );
        }

        sBuffer.append( CRLF + "Answer: " + choices[ answerNumber-1 ] + CRLF );

        return sBuffer.toString();
    }
}

