package quizzer;

/**
 * <p>Title: QuestionFileIndexer</p>
 * <p>Description: Class to index a quiz file. Creates a vector in
QuestionIndex containing
 * the offsets to where in the quiz file questions and answer choices are
located.</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.io.File;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
import java.io.RandomAccessFile;
//import java.util.StringTokenizer;
import java.util.Vector;

public class QuestionFileIndexer
{
    private final File qFile;
    private Vector<QuestionIndex> qFileIndex;

    /**
     * Constructor
     * 
     * @param qFilename The name of the quiz file containing quiz questions and
     *                  answers
     */
    public QuestionFileIndexer(final String qFilename) {
        this(new File(qFilename));
    }

    /**
     * Constructor
     * 
     * @param qFile A File object containing quiz questions and answers
     */
    public QuestionFileIndexer(final File qFile) {
        this.qFile = qFile;
    }

    /**
     * Indexes the quiz file. Places the offsets of questions and answer choices
     * into QuestionIndex objects
     * 
     * @return whether or not the quiz file was successfully indexed.
     */
    public boolean indexFile() {
        try {
            final RandomAccessFile raf = new RandomAccessFile(qFile, "r");

            long fp = 0;
            String line = null;

            long qStart = 0;
            long qEnd = 0;
            QuestionIndex qIndex = null;

            int status = 0; // 0 = default, 1 = question, 2 = answer

            while ((line = raf.readLine()) != null) {

                if (!(line.startsWith(QuizzerProperties.COMMENT)) && !(line.trim().length() == 0)) {
                    if (status == 0) {
                        // expecting a question

                        if (line.startsWith(QuizzerProperties.Q_START)) {
                            // question start
                            status = 1;
                            int lineLen = line.replaceAll("\\W", "").length();
                            if (lineLen > 75) {
	                            System.out.println("Question: "+ line.replaceAll("\\W", "") +" Line Length: "+ Integer.toString(lineLen));
                            }
                            qStart = raf.getFilePointer();
                        } else {
                            System.err.println("QuestionFileReader: unexpected input: " + line);
                        }
                    } else if (status == 1) {

                        // reading question, checking for answer start
                        int lineLen = line.replaceAll("\\W", "").length();
                        if (lineLen > 75) {
                            System.out.println("Question: "+ line.replaceAll("\\W", "") +" Line Length: "+ Integer.toString(lineLen));
                        }
                    	
                        if (line.startsWith(QuizzerProperties.A_START)) {
                            // question end, answer start
                            status = 2;

                            qIndex = new QuestionIndex(qStart, qEnd);
                        } else {
                            // keep track of end of last part of question
                            qEnd = raf.getFilePointer();
                        }
                    } else {
                        // reading answer, checking for answer end
                        if (line.startsWith(QuizzerProperties.A_END)) {
                            // answer end
                            status = 0;
                            
                            if (qFileIndex == null) {
                                qFileIndex = new Vector<QuestionIndex>();
                            }

                            qFileIndex.addElement(qIndex);
                        } else {
                            // add answer offset
                            qIndex.addAnswerOffset(fp);
                        }
                    }
                }

                fp = raf.getFilePointer();
            }

            raf.close();
            if (qFileIndex == null || qFileIndex.size() < 1) {
                return false;
            }

            return true;
        } catch (final Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @return a vector of QuestionIndex.
     */
    public Vector<QuestionIndex> getQuestionFileIndex()
    {
        return qFileIndex;
    }

 }

