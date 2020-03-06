package quizzer;

import java.util.concurrent.CompletableFuture;

import kong.unirest.Empty;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class QuizResult {
	private int QQH_ID;
	private int QQH_GUI;
	private String QQH_OS;
	private int QQH_ASKED;
	private int QQH_CORRECT;
	private long QQH_DURATION;
	private long QQH_START_TS;
	private String QQH_QUIZ_FILE;
	private String user;
	public QuizResult() {}
	
	public int getQQH_ID() {
		return QQH_ID;
	}
	
	public void setQRdata (
			int QQH_GUI,
			String QQH_OS,
			int QQH_ASKED,
			int QQH_CORRECT,
			long QQH_DURATION,
			long QQH_START_TS,
			String QQH_QUIZ_FILE,
			String user			
			) {
		this.QQH_GUI=QQH_GUI;
		this.QQH_OS=QQH_OS;
		this.QQH_ASKED=QQH_ASKED;
		this.QQH_CORRECT = QQH_CORRECT;
		this.QQH_DURATION =QQH_DURATION; 
		this.QQH_START_TS = QQH_START_TS;
		this.QQH_QUIZ_FILE = QQH_QUIZ_FILE;
		this.user = user;
	}

	public void saveQR() {

		Unirest.post(QuizzerProperties.API_URL+"qq/addQuizHistory")
		  .header("Content-Type", "application/x-www-form-urlencoded")
		  .field("QQH_GUI", this.QQH_GUI)
		  .field("QQH_OS", this.QQH_OS)
		  .field("QQH_ASKED", Integer.toString(this.QQH_ASKED))
		  .field("QQH_CORRECT", Integer.toString(this.QQH_CORRECT))
		  .field("QQH_DURATION", Long.toString(this.QQH_DURATION))
		  .field("QQH_START_TS", Long.toString(this.QQH_START_TS))
		  .field("QQH_QUIZ_FILE", this.QQH_QUIZ_FILE)
		  .field("user", this.user)
		  .asEmpty();
	}

	/**
	 * @return the qQH_GUI
	 */
	public int getQQH_GUI() {
		return QQH_GUI;
	}

	/**
	 * @return the qQH_OS
	 */
	public String getQQH_OS() {
		return QQH_OS;
	}

	/**
	 * @return the qQH_ASKED
	 */
	public int getQQH_ASKED() {
		return QQH_ASKED;
	}

	/**
	 * @return the qQH_CORRECT
	 */
	public int getQQH_CORRECT() {
		return QQH_CORRECT;
	}


	/**
	 * @return the qQH_DURATION
	 */
	public long getQQH_DURATION() {
		return QQH_DURATION;
	}

	/**
	 * @return the qQH_START_TS
	 */
	public long getQQH_START_TS() {
		return QQH_START_TS;
	}

	/**
	 * @return the qQH_QUIZ_FILE
	 */
	public String getQQH_QUIZ_FILE() {
		return QQH_QUIZ_FILE;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
}
