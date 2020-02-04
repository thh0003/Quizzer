package quizzer;

/**
 * <p>Title: QuestionFileReader</p>
 * <p>Description: Class to read random questions and answer choices from the quiz file
 * Uses a vector of QustionIndex objects to determine where in the quiz file to read.</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Vector;

public class QuestionFileReader
{
    private File qFile;
    private Vector<QuestionIndex>  qFileIndex;

    private Random  random;

    /**
     * Constructor
     * @param qFilename The name of the quiz file containing quiz questions and answers
     * @param qFileIndex A vector of QuestionIndex, containing the offsets to quiz
questions and answer choices.
     */
    public QuestionFileReader( String qFilename, Vector<QuestionIndex> qFileIndex )
    {
        this( new File( qFilename ), qFileIndex );
    }

    /**
     * Constructor
     * @param qFile A File object containing quiz questions and answers
     * @param qFileIndex A vector of QuestionIndex, containing the offsets to quiz
questions and answer choices.
     */
    public QuestionFileReader( File qFile, Vector<QuestionIndex> qFileIndex )
    {
        this.qFile      = qFile;
        this.qFileIndex = qFileIndex;

        this.random = new Random();
    }

    /**
     * Randomly selects a QuestionIndex from the vector of indexes and reads from the
     * question and answer choices from the quiz file using a RandomAccessFile.
     * @return a random QuestionRecord object or null if there are no more questions
     */
    public QuestionRecord getQuestionRecord() throws Exception
    {
        int questionID = this.random.nextInt(this.qFileIndex.size());
        QuestionIndex questionIndex = this.qFileIndex.get(questionID);

        RandomAccessFile raf = new RandomAccessFile( this.qFile, "r" );
        Long qStart              = questionIndex.getQuestionStart();
        //int intStart            = (int)questionIndex.getQuestionStart();
        Long qEnd                = questionIndex.getQuestionEnd();

        int byteLength = (int)(qEnd - qStart);
        byte[] Questionbytes = new byte[byteLength];
        Vector<Long> ansVector = questionIndex.getAnswerVector();
        int bytesRead = 0;
        String question;
        String choices[] = null;
        int answerNumber = 0;


        // Find the start of the question in the file
        raf.seek(qStart);
        bytesRead = raf.read(Questionbytes);
        question = new String(Questionbytes).trim();

        //Find the Answer
        raf.seek(ansVector.get(0));
        answerNumber = Integer.parseInt(raf.readLine().trim());

        //Get Choices
        choices = new String[ansVector.size()-1];
        for (int x=1; x<ansVector.size()-1; x++){
            raf.seek(ansVector.get(x));
            choices[x] = raf.readLine().trim();
        }

        raf.close();
        return new QuestionRecord( question, choices, answerNumber );
    }
}

