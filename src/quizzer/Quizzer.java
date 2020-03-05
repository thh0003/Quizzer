package quizzer;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.NumberFormat;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Text;

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
    
    public Display quizDisplay;
    
    public Shell quizShell;
    
    private QuizController qc = null;
    
    private Boolean quizStart=false;
	
    private CliParser cliargs; //command line argument parser class

	private boolean qGUI;
	
//	private String helpMsg = "";
    public final String helpMsg =
            "Quizzer [-h] [-n count] [-a show_answers] [-q quiz_file] [-g gui]"
            + QuizzerProperties.EOL
            + "-h : This help message"
            + QuizzerProperties.EOL
            + "-n : Integer - The number of questions in the quiz"
            + QuizzerProperties.EOL
            + "-a : Yes|No|Y|N - Option to show the correct answer to incorrect questions"
            + QuizzerProperties.EOL
            + "-q : String - Specify a quiz file with questions"
            + QuizzerProperties.EOL
            + "-g : Yes|No|Y|N - Enable a graphic user interface.  Default is No. And is disabled when a desktop environment is unavailable";


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

			//Check the possible command line arguments
			if (this.cliargs.switchPresent("-h")) {
				exit (helpMsg);
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
        float percentage = (float) correct / (float) asked;
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
        output( "You correctly answered " + correct + " of " + asked + " questions." );
        output( "Percentage: " + formattedPercentage + "%" );
        output( "Elapsed Time: " + formattedElapsedTime + " (" + elapsedTime +" milliseconds)" );

        output( QuizzerProperties.EOL );

        exit( "Quiz Complete." );
    }
    
    /*
     * Run a text version of the quiz
     * */
    public void runTxtQuiz() {
    	try {
    		if (qc == null) {
				qc = new QuizController(qCount, qFilename, showAnswers);
				quizStart = qc.initialize();
			}

    		int asked = qc.getAsked();
    		int correct = qc.getCorrect();
    		
	    	QuestionRecord qRecord = null;
	    	
	    	this.intro();
	    	qc.setStartTime(System.currentTimeMillis());
	    	
	    	while (true) {
	
			    			
				if( asked >= qCount )
	            {
	                this.quizTextResults();
	            }
				
				try
	            {
	                qRecord = qc.getqFileReader().getQuestionRecord();
	            }
	            catch( Exception e )
	            {
	                this.quizTextResults();
	            }
				
				if( qRecord == null )
	            {
	                //no more questions, show results
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
		
		
		Group quizzerActions = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
		data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 3;
		data.verticalSpan = 1;
		quizzerActions.setLayoutData (data);
		quizzerActions.setLayout(new GridLayout(3,false));
		
		Button quizzerStart = new Button (quizzerActions, SWT.PUSH);
		quizzerStart.setText ("Start");
		quizzerStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Dispose of Necessary Display Items
				quizShell.moveBelow(quizzerOptions);
				quizzerOptions.dispose();
				quizzerActions.dispose();
				quizShell.requestLayout();
				quizScreen();
				
			}
		});
		
		Button quizzerExit = new Button (quizzerActions, SWT.PUSH);
		quizzerExit.setText ("Exit");
		quizzerExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Exit Quiz
				quizDisplay.close();
				
			}
		});

		
		quizShell.requestLayout();;
    }

    public void quizresults() {

		Group quizResults = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
		quizResults.setText ("Quiz Results");
		GridData data = new GridData (SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 3;
		data.verticalSpan = 10;
		quizResults.setLayoutData (data);
		quizResults.setLayout(new GridLayout(3,false));
		Double correctPer = (qc.getAsked()>0)?(((double) qc.getCorrect())/qc.getAsked())*100:0.00;
		String resultOut = "Total Questions: "+Integer.toString(qCount)
						  +" # Correct: "+Integer.toString(qc.getCorrect())
						  +" Quiz %: "+Double.toString(correctPer)+"%";
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
				
			}
		});
		
		quizShell.requestLayout();
		
		
    }
    
    public void quizScreen() {
    	
    	try {
    		if (qc == null) {
    			qc = new QuizController(qCount, qFilename, showAnswers);
    			quizStart = qc.initialize();
    		}

			Label prevQuestionResult=null;
	    	
	    	QuestionRecord qRecord = null;
	    	
	    	if (quizStart) {
				
				qc.setStartTime(System.currentTimeMillis());
				if( qc.getAsked() >= qCount )
	            {
					quizStart=false;
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
	                e.printStackTrace();
	                quizresults();
	                throw (new Error("Error reading quiz file. Printing results and exiting"));
	                
	            }
				
				if( qRecord == null )
	            {
	                //no more questions, show results
					quizStart=false;
					quizresults();
					return;
	            }

				//Questions Asked
				Group quizProgress = new Group (quizShell, SWT.SHADOW_ETCHED_IN | SWT.WRAP);
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
    		error.printStackTrace();
    	}
    	
    }
    
}

