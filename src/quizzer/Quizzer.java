package quizzer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

/**
 * <p>Title: Quizzer</p>
 * <p>Description: Starting class for Quizzer. Parses command line arguments and starts QuizController to begin quiz.</p>
 * @author Corey Wineman; adapted by Jim Mooney; and by Trevor Holmes
 * @version 1.1
 */


public class Quizzer
{
    public boolean showAnswers;
    
    private int     qCount;

    public String  qFilename;
    
    public String qLogFile;
    
    public Display quizDisplay;
    
    public Shell quizShell;
    
    public long timelimit;
    
    public long stopTime;
    
    private QuizController qc = null;
    
    private Boolean quizStart=false;
    public Boolean quizStopped=false;
	
    private CliParser cliargs; //command line argument parser class
	private QuizUser quizUser;
	private QuizUser[] qUsers;
	private QuizResult[] qResults;
	private QuizResult[] allQResults;
	private long quizReportHandle = -1;
	private long quizProgressHandle = -1;
	private long quizCurrentQuestionHandle = -1;
    
	private boolean qGUI;
	
	Timer quizTimer = new Timer();
	TimerTask qtTask = new TimerTask() {
		
		public void run() {
			if (!qGUI) {
				if (!quizStopped) {
					output("Time is UP! put down your pencils!");
					quizTextResults();
				} else {
					 System.exit( 0 );
				}
			} else {
				quizStart=false;
                quizresults();
			}
		}
	};
	
	Runnable timer = new Runnable() {
	      public void run() {
	    	quizStart=false;
    		if (quizProgressHandle != -1) {
        		Group tempquizProgress = (Group) quizShell.getDisplay().findWidget(quizProgressHandle);
        		tempquizProgress.dispose();
        		quizProgressHandle = -1;
        	}
    		if (quizCurrentQuestionHandle != -1) {
    			Group tempquizCurrentQuestion = (Group) quizShell.getDisplay().findWidget(quizCurrentQuestionHandle);
    			tempquizCurrentQuestion.dispose();
    			quizCurrentQuestionHandle = -1;
        	}
	    	quizresults();
	      }
	    };
	
//	Please note -A and -L help messages are not displayed for security purposes;
    public final String helpMsg =
            "Quizzer [-h] [-l] [-n count] [-a show_answers] [-q quiz_file] [-g gui] [-t time_limit]"
            + QuizzerProperties.EOL
            + "-h : This help message"
		    + QuizzerProperties.EOL
		    + "-l : Display the user's log report"
            + QuizzerProperties.EOL
            + "-n : Integer - The number of questions in the quiz"
            + QuizzerProperties.EOL
            + "-a : Yes|No|Y|N - Option to show the correct answer to incorrect questions"
            + QuizzerProperties.EOL
            + "-q : String - Specify a quiz file with questions"
            + QuizzerProperties.EOL
            + "-g : Yes|No|Y|N - Enable a graphic user interface.  Default is No. And is disabled when a desktop environment is unavailable"
		    + QuizzerProperties.EOL
		    + "-t : Integer - The time limit for the quiz in seconds.  The default is unlimited";



    /**
     * Constructor, main class for Quizzer. Parses command line options
     * and  starts a QuizController
     * or exits if the command line arguments were invalid.
     * Parsing will fail if the quiz filename is not the last argument.
     * @param args Command line arguments
     */
    public Quizzer( String [] args )
    {
    	this.qGUI = QuizzerProperties.GUI;
    	this.qCount = QuizzerProperties.DEFAULT_Q_COUNT;
    	this.showAnswers = QuizzerProperties.SHOW_ANSWERS;
    	this.qFilename = QuizzerProperties.DEFAULT_Q_FILE;
    	this.qLogFile = QuizzerProperties.DEFAULT_L_FILE;
    	this.timelimit = QuizzerProperties.TIME_LIMIT*1000;

    	this.quizUser = Unirest.get(QuizzerProperties.API_URL+"qq/userLookup/"+QuizzerProperties.userName).asObject(QuizUser.class).getBody();
    	this.qResults = Unirest.get(QuizzerProperties.API_URL+"qq/QHlookup/"+QuizzerProperties.userName).asObject(new GenericType<QuizResult[]>() {}).getBody();

    	if (this.quizUser.getQU_ROLE().equals("ADMIN")) {
	    	this.allQResults = Unirest.get(QuizzerProperties.API_URL+"qq/QHlookupAll/"+QuizzerProperties.userName).asObject(new GenericType<QuizResult[]>() {}).getBody();
	    	this.qUsers = Unirest.get(QuizzerProperties.API_URL+"qq/usersLookup/"+QuizzerProperties.userName).asObject(new GenericType<QuizUser[]>() {}).getBody();
    	} else {
    		this.allQResults= null;
    		this.qUsers= null;
    	}

    	if (this.qGUI) {
	    	this.quizDisplay = new Display ();
	    	this.quizShell = new Shell(quizDisplay);
    	}

    	if (!this.parseArguments(args)) {
    		exit( "Invalid Command Line Arguments" );
    	}
    	
    }
    
    /**
     * Displays and information message to standard output and exits the program
     * This could also be where any cleanup would occur. Currently no cleanup is needed.
     * @param exitMessage Information message displayed before exit.
     */
    private void exit( String exitMessage )
    {
        System.out.println( exitMessage + QuizzerProperties.EOL );
        System.out.flush();

        //cleanup??

        System.exit( 0 );
    }
    
    /**
     * Parses the command line arguments. Any unexpected or invalid
     * argument will cause the parsing to fail.
     * @param args Command line arguments
     * @return whether or not the command line arguments were valid
     * and successfully parsed.
     * 
     * show_answers = (y|n|yes|no) show answers to incorrect choices
     * gui = (y|n|yes|no) show answers to incorrect choices
     */
	private boolean parseArguments( String [] args )
    {
		try {
			boolean g2g = true;
			this.cliargs = new CliParser(args);
			
			//Make the user an Admin
			if (this.cliargs.switchPresent("-A")) {
				String argPassword = this.cliargs.switchValue("-A");
				if (argPassword == null || argPassword.length()==0 || !argPassword.equals("AlanTuringCrackedEnigma")) {
					g2g=false;
					output("Invalid Password, You're No Alan Turing");
				} else {
					quizUser=Unirest.get(QuizzerProperties.API_URL+"qq/makeAdmin/"+QuizzerProperties.userName).asObject(QuizUser.class).getBody();
					exit ("User: "+ QuizzerProperties.userName +", Is now an Administrator. Goodbye!");
				}
			}

			//Check the possible command line arguments
			if (this.cliargs.switchPresent("-h")) {
				exit (helpMsg);
			}

			//Check the possible command line arguments
			if (this.cliargs.switchPresent("-l")) {
				logReport("USER");
				exit ("Goodbye");
			}

			
			if (this.cliargs.switchPresent("-L")) {
				String argLogFilename = this.cliargs.switchValue("-L");
				if (argLogFilename == null || argLogFilename.length()==0 ) {
					g2g=false;
					output("Invalid Question File");
				} else {
					this.qLogFile =argLogFilename;
					this.logReport(quizUser.getQU_ROLE());
					this.logExport(quizUser.getQU_ROLE());
					exit ("Goodbye");
				}
			}
			
			//Check the possible command line arguments
			if (this.cliargs.switchPresent("-q")) {
				String argFilename = this.cliargs.switchValue("-q");
				if (argFilename == null || argFilename.length()==0 ) {
					g2g=false;
					output("Invalid Question File");
				} else {
					this.qFilename =argFilename;
				}
			}
			
			if (this.cliargs.switchPresent("-n")) {
				int argCount = Integer.parseInt(this.cliargs.switchValue("-n"));
				
				//check the count is within limits
				if (argCount > 0 && argCount <= QuizzerProperties.MAX_QUESTIONS) {
					this.qCount = argCount;
				} else{
					output("Question count must be between 1 and "+ QuizzerProperties.MAX_QUESTIONS);
					g2g = false;	
				}	
			}
			
			if (this.cliargs.switchPresent("-t")) {
				int argLimit = Integer.parseInt(this.cliargs.switchValue("-t"));
				
				//check the count is within limits
				if (argLimit > 0 ) {
					this.timelimit = argLimit*1000;
				} else{
					output("The time limit must be greater than 0 ");
					g2g = false;	
				}	
			}

			
			if (this.cliargs.switchPresent("-a")) {
				String argShow = this.cliargs.switchValue("-a").toLowerCase();
				switch (argShow) {
					case "yes": this.showAnswers = true;
								break;
					case "y": this.showAnswers = true;
							  break;
					case "no": this.showAnswers = false;
							   break;
					case "n": this.showAnswers = false;
							  break;
					default: g2g = false;
							 output("ShowAnswer option can be yes|no|y|n");
							 break;
				}
			}
			
			if (this.cliargs.switchPresent("-g")) {
				String argGUI = this.cliargs.switchValue("-g").toLowerCase();
				if (QuizzerProperties.osName.startsWith("Linux")) {
					this.qGUI = false;
				} else {
					switch (argGUI) {
						case "yes": this.qGUI = true;
									break;
						case "y": this.qGUI = true;
								  break;
						case "no": this.qGUI = false;
								   break;
						case "n": this.qGUI = false;
								  break;
						default: g2g = false;
								 output("GUI option can be yes|no|y|n");
								 break;
					}
				}
				
			}
	        return g2g;
		}catch( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public void startQuiz() {
    	if (this.qGUI) {
	    	GridLayout gridLayout = new GridLayout (3, false);
			quizShell.setLayout (gridLayout);
			quizShell.setSize(QuizzerProperties.INITIAL_WIDTH, QuizzerProperties.INITIAL_HEIGHT);
			
			GridData data = new GridData (SWT.NONE, SWT.TOP, true, false, 3, 3);
			quizShell.setText(QuizzerProperties.TITLE);
			quizShell.setLayoutData(data);
			Image wvuImage = new Image(quizDisplay,"media" + File.separator + QuizzerProperties.BACKGROUND_IMAGE_FILE);
			Image wvuLogo = new Image(quizDisplay,"media" + File.separator + QuizzerProperties.LOGO_FILE);
			Image halfWVULogo = new Image(quizDisplay, wvuLogo.getImageData().scaledTo(40,40));
			quizShell.setBackgroundImage(wvuImage);
			Label quizzerLogo = new Label (quizShell, SWT.SHADOW_NONE | SWT.WRAP);
			quizzerLogo.setImage(halfWVULogo);
			data = new GridData (SWT.BEGINNING, SWT.TOP, true, false, 1, 1);
			quizzerLogo.setLayoutData (data);
			
			Label quizzerWelcom = new Label (quizShell, SWT.SHADOW_NONE | SWT.WRAP);
			quizzerWelcom.setText (QuizzerProperties.WELCOME);
			data = new GridData (SWT.BEGINNING, SWT.TOP, true, false, 2, 1);
			quizzerWelcom.setLayoutData (data);
			quizShell.open();
	      	startScreen(quizDisplay);
	        
	        while (!quizShell.isDisposed()) {
	            if (!quizDisplay.readAndDispatch()) {
	            	quizDisplay.sleep();
	            }
	        }
	        
	        quizDisplay.dispose();
    	} else {
    		this.runTxtQuiz();
    	}
    
    }
    
    /**
     * Calculates and displays the results of the quiz.
     */
    private void quizTextResults()
    {
        output( QuizzerProperties.EOL );
        int correct = qc.getCorrect();
        int asked = qc.getAsked();
//        QuizResult[] quizResults = qc.getQuizResults();
        QuizResult curQuiz = new QuizResult();
        
        float percentage = (float) correct / (float) qCount;
        percentage = percentage * 100;

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits( 1 );
        String formattedPercentage = numberFormat.format( percentage );

        String formattedElapsedTime = null;
        long elapsedTime = System.currentTimeMillis() - qc.getStartTime();
        if( (elapsedTime / 1000) > 60 )
        {
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            formattedElapsedTime = new String( minutes + " minutes " + seconds + " seconds " );
        }
        else
        {
            formattedElapsedTime = new String( (elapsedTime / 1000) + "seconds" );
        }

        output( "Results:" );
        output( "Your quiz contained " + qCount + " questions "+ QuizzerProperties.EOL );
        output( "You were asked  " + asked + " questions "+ QuizzerProperties.EOL );
        output( "You correctly answered " + correct +" questions." );
        output( "Percentage: " + formattedPercentage + "%" );
        output( "Elapsed Time: " + formattedElapsedTime + " (" + elapsedTime +" milliseconds)" );

        output( QuizzerProperties.EOL );
        curQuiz.setQRdata((qGUI)?1:0, QuizzerProperties.osName, asked, correct, elapsedTime, qc.getStartTime(), this.qFilename, quizUser.getQU_LOGIN());
        curQuiz.saveQR();
//        logReport();
        exit( "Quiz Complete." );
    }
    
    /*
     * Run a text version of the quiz
     * */
    public void runTxtQuiz() {
    	try {
    		if (qc == null) {
				qc = new QuizController(qCount, qFilename, showAnswers, timelimit);
				quizStart = qc.initialize();
			}

    		int asked = qc.getAsked();
    		int correct = qc.getCorrect();
    		QuestionRecord qRecord = null;
	    	
	    	this.intro();
	    	
	    	qc.setStartTime(System.currentTimeMillis());
	    	if (qc.getTimeLimit()>0) {
	    		quizTimer.schedule(qtTask, timelimit);
	    	}

	    	while ( true ) {
			    			
				if( asked >= qCount )
	            {
			        quizStopped=true;
	                this.quizTextResults();
	            }
				
				try
	            {
	                qRecord = qc.getqFileReader().getQuestionRecord();
	            }
	            catch( Exception e )
	            {
	                quizStopped=true;
	                this.quizTextResults();
	            }
				
				if( qRecord == null )
	            {
	                //no more questions, show results
			        quizStopped=true;
					this.quizTextResults();
	            }
	        	
				showQuestion( qRecord );
				asked++;
	            qc.setAsked(asked);
	            
	            int answer = getAnswer(qRecord.getChoices().length);
	            if (answer == qRecord.getAnswerNumber() ) {
	            	correct++;
	            	qc.setCorrect(correct);
	            	output( QuizzerProperties.EOL + "CORRECT!" );
	            } else {
	            	if( showAnswers )
	                {
	                    output( QuizzerProperties.EOL + "Incorrect, the correct	answer was " + qRecord.getAnswerNumber() + "." );
	                }
	                else
	                {
	                    output( QuizzerProperties.EOL + "Incorrect." );
	                }	
	            }
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		this.quizTextResults();
    	}
    }

    /**
     * Reads the user's answer choice from the standard input. Displays error messages for invalid
     * choices and allows user to exit program.
     * @param the index of the highest answer.
     * @return The user's answer choice
     */
    private int getAnswer( int highest )
    {
        int answer = -1;

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader(System.in ) );
            String line = null;
            while( true )
            {
                output( "Select your answer (Type 'Q' to exit): " );

                line = reader.readLine();
                line = line.trim().toLowerCase();

                if( line.equals( "q" ) || line.equals( "quit" ) )
                {
                	this.quizTextResults();
                }

                try
                {
                    answer = Integer.parseInt( line );
                    if( answer > 0 && answer <= highest )
                    {
                        break;
                    }
                }
                catch( NumberFormatException nfe )
                {
                }

                output( "You must enter a number between 1-" + highest + "." );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return answer;
    }    
    
    /**
     * Displays a questions to standard output.
     * @param qRecord An object containing the current question, choices and correct answer
     */
    private void showQuestion( QuestionRecord qRecord )
    {
    	int asked = qc.getAsked();
        output( QuizzerProperties.EOL );

        output( "Question " + (asked+1) );
        output( qRecord.getQuestion() );

        String [] choices = qRecord.getChoices();
        for( int i=0; i<choices.length; i++ )
        {
//            output( (i+1) + ": " + choices[i] );
            output( choices[i] );
        }

        output( QuizzerProperties.EOL );
    }    
    
    /**
     * Display an introductory message to standard output.
     */
    private void intro()
    {
        this.output( QuizzerProperties.EOL + this.qc.INTRO_MESSAGE );
        this.output( QuizzerProperties.EOL + "Hello "+ quizUser.getQU_LOGIN() + " You have taken: "+ qResults.length +" Quizes" );
    }
    
    /**
     * Displays a message to the standard output. Uses println, so each message ends with a NEWLINE
     * @param message The message to be displayed
     */
    private void logReport( String type )
    {
    	try {
    		QuizResult[] reportQResults = (type.equals("ADMIN"))?allQResults:qResults;
	    	//QuizResult[] quizResults = qc.getQuizResults();
	        this.output( "LogReport for User: "+ quizUser.getQU_LOGIN());
	        this.output( quizUser.getQU_LOGIN()+" has taken a total of "+ reportQResults.length +" Quizes");
	        //Generate History Report
	        String reportHeader = "";
	        String reportBody = "";
	        int[] columnWidth = new int[10];
	        String[] columnHeaders = {
	        		" USER ", " TEST ID ", " ASKED ", " CORRECT ", " SCORE ", " DURATION ", "QUIZ DATE", " QUIZ FILE "," OPERATING SYSTEM ", " GUI "
	        };
	
	        for (int y=0;y<columnHeaders.length;y++) {
	        	columnWidth[y] = columnHeaders[y].length();
	        }
	        
	        int totalWidth = 0;
	        for (int x=0;x<reportQResults.length;x++) {
	//        	output (String.valueOf(columnHeaders[0].length()));
	//        	output (reportQResults[x].getUser());
	        	
	        	columnWidth[0] = (columnWidth[0]>=(reportQResults[x].getUser().length()+2))?columnWidth[0]:(reportQResults[x].getUser().length()+2);
	        	columnWidth[1] = (columnWidth[1]>=(String.valueOf(reportQResults[x].getQQH_ID()).length()+2))?columnWidth[1]:(String.valueOf(reportQResults[x].getQQH_ID()).length()+2);
	        	columnWidth[2] = (columnWidth[2]>=(String.valueOf(reportQResults[x].getQQH_ASKED()).length()+2))?columnWidth[2]:(String.valueOf(reportQResults[x].getQQH_ASKED()).length()+2);
	        	columnWidth[3] = (columnWidth[3]>=(String.valueOf(reportQResults[x].getQQH_CORRECT()).length()+2))?columnWidth[3]:(String.valueOf(reportQResults[x].getQQH_CORRECT()).length()+2);
	        	columnWidth[6] = 15;
	        	columnWidth[7] = (columnWidth[7]>=(reportQResults[x].getQQH_QUIZ_FILE().length()+2))?columnWidth[7]:(reportQResults[x].getQQH_QUIZ_FILE().length()+2);
	        }
	
	        for (int y=0;y<columnWidth.length;y++) {
	        	totalWidth = totalWidth + columnWidth[y] + 2;
	        }
	        
	        reportHeader = 	reportHeader + "-" + String.format("%"+String.valueOf(totalWidth)+"d", 0).replace(" ", "-").replace("0", "")+QuizzerProperties.EOL;
	//        				+"|"+String.format("%"+String.valueOf(totalWidth)+"d", 0).replace("0", "")+"|"+QuizzerProperties.EOL;
	
	        for (int x=0;x<columnHeaders.length;x++) {
	        	reportHeader = reportHeader+ String.format("|%"+String.valueOf(columnWidth[x])+"s|", columnHeaders[x]);
	        }
	        
	        reportHeader = reportHeader +QuizzerProperties.EOL+ "-" + String.format("%"+String.valueOf(totalWidth)+"d", 0).replace(" ", "-").replace("0", "")+QuizzerProperties.EOL;
	        
	        for (int x=0;x<reportQResults.length;x++) {
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[0])+"s|", reportQResults[x].getUser());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[1])+"d|", reportQResults[x].getQQH_ID());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[2])+"d|", reportQResults[x].getQQH_ASKED());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[3])+"d|", reportQResults[x].getQQH_CORRECT());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[4]-1)+".2f%%|", ((float)reportQResults[x].getQQH_CORRECT()/(float)this.qCount)*100);
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[5])+".2f|", ((float)reportQResults[x].getQQH_DURATION()/1000));
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[6])+"TF|", (new Date(reportQResults[x].getQQH_START_TS())));
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[7])+"s|", reportQResults[x].getQQH_QUIZ_FILE());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[8])+"s|", reportQResults[x].getQQH_OS());
	        	reportBody = reportBody+ String.format("|%"+String.valueOf(columnWidth[9])+"b|", ((reportQResults[x].getQQH_GUI()==0)?false:true));
	        	
	        	reportBody = reportBody+QuizzerProperties.EOL;
	        }
	        output(reportHeader);
	        output(reportBody);
    	} catch (Exception error) {

    		error.printStackTrace();
    	}
        
    }

    /**
     * Displays a message to the standard output. Uses println, so each message ends with a NEWLINE
     * @param message The message to be displayed
     */
    private void logExport( String type )
    {
    	try {
    		QuizResult[] reportQResults = (type.equals("ADMIN"))?allQResults:qResults;
	    	FileWriter csvWriter = new FileWriter(this.qLogFile);
	        String[] columnHeaders = {
	        		" USER ", " TEST ID ", " ASKED ", " CORRECT ", " SCORE ", " DURATION ", "QUIZ DATE", " QUIZ FILE "," OPERATING SYSTEM ", " GUI "
	        };
	
	        for (int x=0;x<columnHeaders.length;x++) {
	        	csvWriter.append(String.format("%s,", columnHeaders[x]));
	        }
	        	csvWriter.append(QuizzerProperties.EOL);
	        
	        for (int x=0;x<reportQResults.length;x++) {
	        	csvWriter.append(String.format("%s,", reportQResults[x].getUser()));
	        	csvWriter.append(String.format("%d,", reportQResults[x].getQQH_ID()));
	        	csvWriter.append(String.format("%d,", reportQResults[x].getQQH_ASKED()));
	        	csvWriter.append(String.format("%d,", reportQResults[x].getQQH_CORRECT()));
	        	csvWriter.append(String.format("%.2f%%,", ((float)reportQResults[x].getQQH_CORRECT()/(float)this.qCount)*100));
	        	csvWriter.append(String.format("%.2f,", ((float)reportQResults[x].getQQH_DURATION()/1000)));
	        	csvWriter.append(String.format("%TF,", (new Date(reportQResults[x].getQQH_START_TS()))));
	        	csvWriter.append(String.format("%s,", reportQResults[x].getQQH_QUIZ_FILE()));
	        	csvWriter.append(String.format("%s,", reportQResults[x].getQQH_OS()));
	        	csvWriter.append(String.format("%b,", ((reportQResults[x].getQQH_GUI()==0)?false:true)));
	        	csvWriter.append(QuizzerProperties.EOL);

	        }
	        csvWriter.flush();
	        csvWriter.close();
    	} catch (Exception error) {

    		error.printStackTrace();
    	}
        
    }

    
    /**
     * Displays a message to the standard output. Uses println, so each message ends with a NEWLINE
     * @param message The message to be displayed
     */
    private void output( String message )
    {
        System.out.println( message );
    }
    
    private void quizRestart() {
    	
    	qc = null;
    	timelimit = QuizzerProperties.TIME_LIMIT*1000;
    	
    	
    	startScreen(quizDisplay);
    }

    private void startScreen( Display display) {


		//options section
		Group quizzerOptions = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
		quizzerOptions.setText ("Quiz Options");
		GridData data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 3;
		data.verticalSpan = 2;
		quizzerOptions.setLayoutData (data);
		quizzerOptions.setLayout(new GridLayout(3,false));
		
		Label quizzerQNumberLabel = new Label (quizzerOptions, SWT.SHADOW_NONE | SWT.WRAP);
		quizzerQNumberLabel.setText ("# of questions (Default: 10 questions, Max: 1000 questions)");
		data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 2, 1);
		quizzerQNumberLabel.setLayoutData (data);
		
		Combo quizzerQNumber = new Combo (quizzerOptions, SWT.NONE);
		quizzerQNumber.setItems (new String [] {"5", "10", "25", "50", "75", "100"});
		quizzerQNumber.setText ("# of Questions");
		data = new GridData (SWT.BEGINNING, SWT.FILL, false, false, 1, 1);
		quizzerQNumber.setLayoutData (data);
		quizzerQNumber.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int questionCount = Integer.parseInt(quizzerQNumber.getText());
				
				if (questionCount > 0) {
						qCount=questionCount;
				}
			}
		});
		
		Label quizzerTLimitLabel = new Label (quizzerOptions, SWT.SHADOW_NONE | SWT.WRAP);
		quizzerTLimitLabel.setText ("Quiz Time Limit (Default: no limit, Max: 3600 seconds)");
		data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 2, 1);
		quizzerTLimitLabel.setLayoutData (data);
		
		Combo quizzerTLimit = new Combo (quizzerOptions, SWT.NONE);
		quizzerTLimit.setItems (new String [] {"5", "10", "30", "60", "300", "600", "900", "1200", "1800", "3600"});
		quizzerTLimit.setText ("Time Limit");
		data = new GridData (SWT.BEGINNING, SWT.FILL, false, false, 1, 1);
		quizzerTLimit.setLayoutData (data);
		quizzerTLimit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int tLimit = Integer.parseInt(quizzerTLimit.getText());
				
				if (tLimit > 0) {
						timelimit=tLimit*1000;
				}
			}
		});
		
		
		Label quizzerABoolLabel = new Label (quizzerOptions, SWT.SHADOW_NONE | SWT.WRAP);
		quizzerABoolLabel.setText ("Display Correct Answers:");
		data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 2, 1);
		quizzerABoolLabel.setLayoutData (data);
		
		Combo quizzerABool = new Combo (quizzerOptions, SWT.NONE);
		quizzerABool.setItems (new String [] {"YES", "NO"});
		quizzerABool.setText ("Show Correct Answers");
		data = new GridData (SWT.BEGINNING, SWT.FILL, false, false, 1, 1);
		quizzerABool.setLayoutData (data);

		quizzerABool.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int idx = quizzerABool.getSelectionIndex();
				String answerShow = quizzerABool.getItem(idx);
				if (answerShow.contentEquals("NO")) {
					showAnswers=false;
				} else {
					showAnswers=true;
				}
			}
		});

		
		
		Label quizzerQFileLabel = new Label (quizzerOptions, SWT.SHADOW_NONE);
		quizzerQFileLabel.setText ("Quiz Questions File (sample questions are used by default):");
		data = new GridData (SWT.BEGINNING, SWT.FILL, false, false, 3, 1);
		quizzerQFileLabel.setLayoutData (data);
		
		Text quizzerQFilePath = new Text (quizzerOptions, SWT.BORDER);
		quizzerQFilePath.setText ("Question File: ");
		data = new GridData (SWT.FILL, SWT.FILL, true, false, 2, 1);
		quizzerQFilePath.setLayoutData (data);
		quizzerQFilePath.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				qFilename = quizzerQFilePath.getText();
				System.out.println("qfileName: "+ qFilename);
			}
			
		});
		
		
		Button quizzerQFileBrowse = new Button (quizzerOptions, SWT.PUSH);
		quizzerQFileBrowse.setText("Browse...");
		data = new GridData (SWT.BEGINNING, SWT.FILL, false, false, 1, 1);
		quizzerQFileBrowse.setLayoutData (data);
		quizzerQFileBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
					String fileName = new FileDialog(quizShell).open();
					System.out.println("fileName: "+ fileName);
					if (fileName == null) {
						fileName = QuizzerProperties.DEFAULT_Q_FILE;
					}
					quizzerQFilePath.setText(fileName);
					
			}
		});
		
		//Admin section
		
		Group quizzerActions = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
		data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 6;
		data.verticalSpan = 1;
		quizzerActions.setLayoutData (data);
		quizzerActions.setLayout(new GridLayout(6,false));
		
		Button quizzerStart = new Button (quizzerActions, SWT.PUSH);
		quizzerStart.setText ("Start Quiz");
		quizzerStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				quizShell.setSize(QuizzerProperties.INITIAL_WIDTH, QuizzerProperties.INITIAL_HEIGHT);
				//Dispose of Necessary Display Items
				quizShell.moveBelow(quizzerOptions);
				if (quizReportHandle != -1) {
		    		Group quizResultHistory = (Group) quizShell.getDisplay().findWidget(quizReportHandle);
		    		quizShell.moveBelow(quizResultHistory);
		    		quizResultHistory.dispose();
		    		quizReportHandle = -1;
		    	}
				quizzerOptions.dispose();
				quizzerActions.dispose();
				quizShell.requestLayout();
				quizScreen();
				
			}
		});
		
		Button quizzerReport = new Button (quizzerActions, SWT.PUSH);
		quizzerReport.setText ("Quiz History Report");
		quizzerReport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Dispose of Necessary Display Items
				quizReport(quizUser.getQU_ROLE());
				
			}
		});
		
		if (quizUser.getQU_ROLE().equals("ADMIN")) {
			Button quizzerExportReport = new Button (quizzerActions, SWT.PUSH);
			quizzerExportReport.setText ("Export Quiz Log");
			quizzerExportReport.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//Dispose of Necessary Display Items
					logExport(quizUser.getQU_ROLE());
					quizReport(quizUser.getQU_ROLE());
					
				}
			});
			
			Text quizzerLogPath = new Text (quizzerActions, SWT.BORDER);
			quizzerLogPath.setText ("Log File: ");
			quizzerLogPath.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					// TODO Auto-generated method stub
					qLogFile = quizzerLogPath.getText();
					System.out.println("qfileName: "+ qFilename);
				}
				
			});
			
			
			Button quizzerLogBrowse = new Button (quizzerActions, SWT.PUSH);
			quizzerLogBrowse.setText("Browse...");
			quizzerLogBrowse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
						String fileName = new FileDialog(quizShell).open();
						System.out.println("fileName: "+ fileName);
						if (fileName == null) {
							fileName = QuizzerProperties.DEFAULT_L_FILE;
						}
						quizzerLogPath.setText(fileName);
						
				}
			});

		}
		
		Button quizzerExit = new Button (quizzerActions, SWT.PUSH);
		quizzerExit.setText ("Exit");
		quizzerExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Exit Quiz
				quizDisplay.close();
				exit ("Goodbye");
				
			}
		});


		quizShell.setSize(QuizzerProperties.INITIAL_WIDTH, QuizzerProperties.INITIAL_HEIGHT);
		quizShell.requestLayout();;
    }

    protected void quizReport(String type) {

    	Group quizResultHistory;
    	QuizResult[] reportQResults = (type.equals("ADMIN"))?allQResults:qResults;
    	
    	if (quizReportHandle != -1) {
    		quizResultHistory = (Group) quizShell.getDisplay().findWidget(quizReportHandle);
    		quizResultHistory.dispose();
    		quizReportHandle = -1;
    	}
    	
    	String[] columnHeaders = {
        		" USER ", " TEST ID ", " ASKED ", " CORRECT ", " SCORE ", " DURATION ", "QUIZ DATE", " QUIZ FILE "," OPERATING SYSTEM ", " GUI "
        };
    	
    	quizResultHistory = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
    	quizReportHandle = quizResultHistory.handle;
    	quizResultHistory.setText ("User Quiz Result History Report");
		GridData data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 3;
		data.verticalSpan = 1;
		quizResultHistory.setLayoutData (data);
		quizResultHistory.setLayout(new GridLayout(3,false));
		ScrolledComposite sc = new ScrolledComposite(quizResultHistory, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.RESIZE);
    	Table table = new Table(sc, SWT.NONE);
    	table.setHeaderVisible(true);
    	for (int x = 0; x < columnHeaders.length; x++) {
    		TableColumn column = new TableColumn(table, SWT.NONE);
    		column.setText(columnHeaders[x]);
    	}

    	for (int x = 0; x < reportQResults.length; x++) {
    	      TableItem item = new TableItem(table, SWT.NONE);
    	      item.setText(String.format("Quiz %d", reportQResults[x].getQQH_ID()));
    	      item.setText(0, String.format("%s", reportQResults[x].getUser()));
    	      item.setText(1, String.format("Quiz %d", reportQResults[x].getQQH_ID()));
    	      item.setText(2, String.format("%d", reportQResults[x].getQQH_ASKED()));
    	      item.setText(3, String.format("%d", reportQResults[x].getQQH_CORRECT()));
    	      item.setText(4, String.format("%.2f%%", ((float)reportQResults[x].getQQH_CORRECT()/(float)this.qCount)*100));
    	      item.setText(5, String.format("%.2f", ((float)reportQResults[x].getQQH_DURATION()/1000)));
    	      item.setText(6, String.format("%TF", (new Date(reportQResults[x].getQQH_START_TS()))));
    	      item.setText(7, String.format("%s", reportQResults[x].getQQH_QUIZ_FILE()));
    	      item.setText(8, String.format("%s", reportQResults[x].getQQH_OS()));
    	      item.setText(9, String.format("%b", ((reportQResults[x].getQQH_GUI()==0)?false:true))); 
    	    }

//    	table.setTopIndex(0);
    	sc.setLayoutData(new GridData(GridData.FILL_BOTH));
    	sc.setContent(table);
    	for (int x = 0; x < columnHeaders.length; x++) {
    	      table.getColumn(x).pack();
   	    }
    	
    	table.setBounds(0, 0, QuizzerProperties.INITIAL_WIDTH-75, QuizzerProperties.INITIAL_HEIGHT-175);
    	quizShell.setSize(QuizzerProperties.INITIAL_WIDTH, QuizzerProperties.INITIAL_HEIGHT+200);
    	quizShell.requestLayout();
	}

	public void quizresults() {

        int correct = qc.getCorrect();
        int asked = qc.getAsked();
        QuizResult curQuiz = new QuizResult();
        float percentage = (float) correct / (float) qCount;
        percentage = percentage * 100;

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits( 1 );
        String formattedPercentage = numberFormat.format( percentage );

        String formattedElapsedTime = null;
        long elapsedTime = System.currentTimeMillis() - qc.getStartTime();
        if( (elapsedTime / 1000) > 60 )
        {
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            formattedElapsedTime = new String( minutes + " minutes " + seconds + " seconds " );
        }
        else
        {
            formattedElapsedTime = new String( (elapsedTime / 1000) + "seconds" );
        }

        /*
         * 
         * output( "Results:" );
        output( "Your quiz contained " + qCount + " questions "+ QuizzerProperties.EOL );
        output( "You were asked  " + asked + " questions "+ QuizzerProperties.EOL );
        output( "You correctly answered " + correct +" questions." );
        output( "Percentage: " + formattedPercentage + "%" );
        output( "Elapsed Time: " + formattedElapsedTime + " (" + elapsedTime +" milliseconds)" );
         * */
		
		Group quizResults = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
		quizResults.setText ("Quiz Results");
		GridData data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 3;
		data.verticalSpan = 10;
		quizResults.setLayoutData (data);
		quizResults.setLayout(new GridLayout(3,false));
//		Double correctPer = (qc.getAsked()>0)?(((double) qc.getCorrect())/qc.getAsked())*100:0.00;
		String resultOut = "Total Questions: "+Integer.toString(qCount)
						  +" # Asked: "+Integer.toString(asked)
						  +" # Correct: "+Integer.toString(correct)
						  +" Quiz %: "+formattedPercentage+"%"
						  +" Elapsed Time: "+formattedElapsedTime+" ( "+ elapsedTime +" milliseconds)";
		Label quizzerResultsDetail = new Label (quizResults, SWT.SHADOW_NONE | SWT.WRAP);
		quizzerResultsDetail.setText (resultOut);
		data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 3, 1);
		quizzerResultsDetail.setLayoutData (data);

		Button quizzerStart = new Button (quizResults, SWT.PUSH);
		quizzerStart.setText ("Take Another Quiz");
		quizzerStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Dispose of Necessary Display Items
				quizShell.moveAbove(quizResults);
				quizResults.dispose();
				quizShell.requestLayout();
				quizRestart();
				
			}
		});
		
		Button quizzerExit = new Button (quizResults, SWT.PUSH);
		quizzerExit.setText ("Exit");
		quizzerExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Exit Quiz
				quizDisplay.close();
				exit ("Goodbye");
				
			}
		});

		//save the quiz result data
        curQuiz.setQRdata((qGUI)?1:0, QuizzerProperties.osName, asked, correct, elapsedTime, qc.getStartTime(), this.qFilename, quizUser.getQU_LOGIN());
        curQuiz.saveQR();

		quizShell.requestLayout();
		
		
    }
    
    public void quizScreen() {
    	
    	try {
    		
  		
    		if (qc == null) {
    			qc = new QuizController(qCount, qFilename, showAnswers,timelimit);
    			quizStart = qc.initialize();
    			qc.setStartTime(System.currentTimeMillis());
				if (qc.getTimeLimit()>0) {
					quizDisplay.timerExec((int)timelimit, timer);
		    	}
    		}

			Label prevQuestionResult=null;
	    	
	    	QuestionRecord qRecord = null;
	    	
	    	if (quizStart) {
	    		
				if( qc.getAsked() >= qCount )
	            {
					quizStart=false;
					quizStopped=true;
	                quizresults();
	                return;
	            }
				
				try
	            {
	                qRecord = qc.getqFileReader().getQuestionRecord();
	            }
	            catch( Exception e )
	            {
	            	quizStart=false;
	            	quizStopped=true;
	                e.printStackTrace();
	                quizresults();
	                throw (new Error("Error reading quiz file. Printing results and exiting"));
	                
	            }
				
				if( qRecord == null )
	            {
	                //no more questions, show results
					quizStart=false;
					quizStopped=true;
					quizresults();
					return;
	            }

				//Questions Asked
				Group quizProgress = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
				quizProgressHandle = quizProgress.handle;
				quizProgress.setText ("Quiz Progress");
				GridData data = new GridData (SWT.FILL, SWT.TOP, true, false);
				data.horizontalSpan = 3;
				data.verticalSpan = 2;
				quizProgress.setLayoutData (data);
				quizProgress.setLayout(new GridLayout(3,false));
				
				Label quizzerQNumberLabel = new Label (quizProgress, SWT.SHADOW_NONE | SWT.WRAP);
				quizzerQNumberLabel.setText ("Question #: "+Integer.toString(qc.getAsked()+1));
				data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 1, 1);
				quizzerQNumberLabel.setLayoutData (data);
				
				Label quizzerCorrectLabel = new Label (quizProgress, SWT.SHADOW_NONE | SWT.WRAP);
				quizzerCorrectLabel.setText ("# Correct: "+Integer.toString(qc.getCorrect()));
				data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 1, 1);
				quizzerCorrectLabel.setLayoutData (data);
		
				Double correctPer = (qc.getAsked()>0)?(((double) qc.getCorrect())/qc.getAsked())*100:0.00;
				Label quizzerPerLabel = new Label (quizProgress, SWT.SHADOW_NONE | SWT.WRAP);
				quizzerPerLabel.setText ("Quiz %: "+Double.toString(correctPer)+"%");
				data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 1, 1);
				quizzerPerLabel.setLayoutData (data);
				
				if (showAnswers) {
					if (qc.getAsked() > 0) {
						prevQuestionResult = new Label (quizProgress, SWT.SHADOW_NONE | SWT.WRAP);
						data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 3, 1);
						Color red = new Color (quizDisplay, 225, 0, 0);
						prevQuestionResult.setBackground(red);
						prevQuestionResult.setLayoutData (data);
					}
				}
				
				Group quizCurrentQuestion = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
				quizCurrentQuestionHandle = quizCurrentQuestion.handle;
				quizCurrentQuestion.setText ("Question");
				data = new GridData (SWT.FILL, SWT.TOP, true, false);
				data.horizontalSpan = 3;
				data.verticalSpan = 12;
				quizCurrentQuestion.setLayoutData (data);
				quizCurrentQuestion.setLayout(new GridLayout(3,false));
				
				Label quizzerQuestionLabel = new Label (quizCurrentQuestion, SWT.SHADOW_NONE | SWT.WRAP);
				quizzerQuestionLabel.setText ("Question: "+qRecord.getQuestion());
				data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 3, 1);
				quizzerQuestionLabel.setLayoutData (data);

				//get Answer Choices
		        String [] choices = qRecord.getChoices();
		        Button [] choiceButtons = new Button[choices.length];
		        qc.setCurAnswer(qRecord.getAnswerNumber());
		        
		        
		        for (int x=0; x<choices.length;x++) {
		        	
		        	choiceButtons[x] = new Button(quizCurrentQuestion, SWT.RADIO);
		        	choiceButtons[x].setText(choices[x]);
					data = new GridData (SWT.BEGINNING, SWT.FILL, true, false, 3, 1);
					choiceButtons[x].setLayoutData (data);
					final int xCopy = x;
					choiceButtons[x].addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							qc.setSelectedAnswer(xCopy);
						}
					});
		        }

		        
		        Button quizzerSubmitButton = new Button (quizCurrentQuestion, SWT.PUSH);
				quizzerSubmitButton.setText ("Submit Answer");
				quizzerSubmitButton.addSelectionListener(new SelectionAdapter() {
					
					public void widgetSelected(SelectionEvent event) {
						
						//Check Answer First
						if ((qc.getSelectedAnswer()+1) == qc.getCurAnswer()) {
							qc.incAsked();
							qc.incCorrect();
							qc.setquestionResult("Your answer: "+ Integer.toString(qc.getSelectedAnswer()+1) +" was CORRECT!");
						} else {
							qc.incAsked();
							qc.setquestionResult("Your answer: "+ Integer.toString(qc.getSelectedAnswer()+1) +" was incorrect.  The correct answer is: "+ Integer.toString(qc.getCurAnswer()));
						}
						quizShell.moveAbove(quizCurrentQuestion);
						quizProgress.dispose();
						quizCurrentQuestion.dispose();
						quizScreen();
					}
				});
				
				Button quizzerExit = new Button (quizCurrentQuestion, SWT.PUSH);
				quizzerExit.setText ("Exit");
				quizzerExit.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						//Exit Quiz
						quizDisplay.close();
						exit ("Goodbye");
						
					}
				});
				
				if (showAnswers) {
					if (qc.getAsked() > 0) {
						prevQuestionResult.setText(qc.getquestionResult());
					}
				}
				
				quizShell.requestLayout();

	    	}
    	} catch (Exception error) {
    		MessageBox alert = new MessageBox(quizShell, SWT.OK);
    		alert.setText("Error: "+ error.getMessage());
    		//startQuiz();
    		//error.printStackTrace();
    	}
    	
    }

	/**
	 * @return the qUsers
	 */
	public QuizUser[] getqUsers() {
		return qUsers;
	}
    
}

