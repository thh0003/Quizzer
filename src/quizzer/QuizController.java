package quizzer;

/**
 * <p>Title: QuizController</p>
 * <p>Description: Program to control the interactive quiz for CS533 assignment
1</p>
 * @author Corey Wineman, mods by Jim Mooney
 * @version 1.0
 */

import java.io.File;
import java.util.Vector;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

public class QuizController
{
    public final String INTRO_MESSAGE =
            "CS 533 Assignment 1 - Quiz Program"
            + QuizzerProperties.EOL
            + QuizzerProperties.AUTHOR
            + QuizzerProperties.EOL
            + QuizzerProperties.VERSION
            + QuizzerProperties.EOL
            + QuizzerProperties.DATE;

    private boolean showAnswers;
    private int     qCount;  
    private File    qFile;

    private int     asked       = 0;
    private int     correct     = 0;
    private long    startTime   = 0;
    private long	timeLimit 	= 0;
    private String questionResult="";
	private int curAnswer=0;
	private int selectedAnswer=0;
	private int qState=0;
	private QuizUser quizUser;
	private QuizResult[] quizResults;
	

    private QuestionFileReader  qFileReader;

    /**
     * Constructor
     * @param qCount The number of questions to be asked in the quiz
     * @param qFile A File object containing quiz questions and answers
     * @param showAnswers Toggle to determine if answers are shown
     * for incorrect selections
     */
    public QuizController( int qCount, String qFile, boolean showAnswers, long timeLimit  )
    {
    	this(qCount,new File(qFile),showAnswers,timeLimit);
    }

    
    /**
     * Constructor
     * @param qCount The number of questions to be asked in the quiz
     * @param qFile A File object containing quiz questions and answers
     * @param showAnswers Toggle to determine if answers are shown
     * for incorrect selections
     */
    public QuizController( int qCount, File qFile, boolean showAnswers, long timeLimit  )
    {
        this.qCount         = qCount;
        this.qFile          = qFile;
        this.showAnswers    = showAnswers;
        this.timeLimit		= timeLimit*1000;
        this.quizUser = Unirest.get(QuizzerProperties.API_URL+"qq/userLookup/"+QuizzerProperties.userName).asObject(QuizUser.class).getBody();
    	this.quizResults = Unirest.get(QuizzerProperties.API_URL+"qq/QHlookup/"+QuizzerProperties.userName).asObject(new GenericType<QuizResult[]>() {}).getBody();
    }

    
	public int getqState() {
		return qState;
	}

	public void setqState(int qState) {
		this.qState = qState;
	}

	public int getSelectedAnswer() {
		return selectedAnswer;
	}

	public void setSelectedAnswer(int selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}

	public int getCurAnswer() {
		return curAnswer;
	}

	public void setCurAnswer(int curAnswer) {
		this.curAnswer = curAnswer;
	}

	public boolean isShowAnswers() {
		return showAnswers;
	}

	public void setShowAnswers(boolean showAnswers) {
		this.showAnswers = showAnswers;
	}
	

	public String getquestionResult() {
		return this.questionResult;
	}

	public void setquestionResult(String questionResult) {
		this.questionResult = questionResult;
	}
	
	public int getqCount() {
		return qCount;
	}

	public void setqCount(int qCount) {
		this.qCount = qCount;
	}

	public File getqFile() {
		return qFile;
	}

	public void setqFile(File qFile) {
		this.qFile = qFile;
	}

	public int getAsked() {
		return asked;
	}

	public void setAsked(int asked) {
		this.asked = asked;
	}
	
	public void incAsked() {
		this.asked++;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}
	
	public void incCorrect() {
		this.correct++;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public QuestionFileReader getqFileReader() {
		return qFileReader;
	}

	public void setqFileReader(QuestionFileReader qFileReader) {
		this.qFileReader = qFileReader;
	}


    /**
     * Initialized the QuizController by indexing the quiz file and creating a
QuizFileReader.
     *  Exits the program if there is a problem reading or indexing the file.
This normally means that
     *  the quiz file is an invalid format.
     * @throws Exception 
     */
    public Boolean initialize() throws Exception
    {
    	try {
	        if( qCount < 1 )
	        {
	            throw new Exception( "Invalid question count: " + qCount + ". You must ask at least one question" );
	        }
	
	        if( qFile == null )
	        {
	        	throw new Exception( "Invalid quiz file" );
	        }
	
	        if( !qFile.exists()
	         || !qFile.isFile() )
	        {
	        	throw new Exception( "Invalid quiz file: " + qFile );
	        }
	
	        QuestionFileIndexer qFileIndexer = new QuestionFileIndexer( qFile );
	
	        boolean fileIndexed = qFileIndexer.indexFile();
	        if( !fileIndexed )
	        {
	        	throw new Exception( "Unable to read quiz file: " + qFile +". It may be in an invalid format." );
	        }
	
	        Vector<QuestionIndex> qFileIndex = qFileIndexer.getQuestionFileIndex();
	
	        if( qCount > qFileIndex.size() )
	        {
	        	throw new Exception( "The quiz file contained only " + qFileIndex.size() + "questions." + QuizzerProperties.EOL
	                  + "Use a larger quiz file of you want to ask " + qCount + "questions." + QuizzerProperties.EOL );
	        }
	
	        qFileReader = new QuestionFileReader( qFile, qFileIndex );
	        return true;
    	} catch(Exception Error) {
    		throw Error;
    	}
    }

	/**
	 * @return the quizUser
	 */
	public QuizUser getQuizUser() {
		return quizUser;
	}

	/**
	 * @return the quizResults
	 */
	public QuizResult[] getQuizResults() {
		return quizResults;
	}


	/**
	 * @return the timeLimit
	 */
	public long getTimeLimit() {
		return timeLimit;
	}


	/**
	 * @param timeLimit the timeLimit to set
	 */
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

}

   
