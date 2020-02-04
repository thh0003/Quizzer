package quizzer;

/**
 * <p>Title: QuestionIndex</p>
 * <p>Description: A class that acts as an index of the offsets in the quiz file
 * for the question, answer choices, and correct answer. The first index in the vector of
answers
 * is the offset for the correct answer.</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.util.Vector;

public class QuestionIndex
{
    private long    qStart;
    private long    qEnd;

    private Vector<Long>  answers;

    /**
     * Constructor
     * @param qStart the beginning offset of the quiz question
     * @param qEnd the ending offset of the quiz question
     */
    public QuestionIndex( long qStart, long qEnd )
    {
        this.qStart = qStart;
        this.qEnd   = qEnd;
    }

    /**
     * @return the beginning offset of the quiz question
     */
    public long getQuestionStart()
    {
        return qStart;
    }

    /**
     * @return the ending offset of the quiz question
     */
    public long getQuestionEnd()
    {
        return qEnd;
    }

    /**
     * @return A Vector containing the offsets for the answer choices to the quiz question.
The first
     * element is the correct answer.
     */
    public Vector<Long> getAnswerVector()
    {
        return this.answers;
    }

    /**
     * Adds the offset to an answer choice to the quiz question.
     * @param answerOffset an offset to an answer choice to the quiz question
     */
    public void addAnswerOffset( long answerOffset )
    {
        if( this.answers == null )
        {
            this.answers = new Vector<Long>();
        }

        this.answers.addElement( Long.valueOf( answerOffset ) );
    }
}

