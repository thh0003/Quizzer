package quizzer;


import java.io.File;
import java.util.Map;

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
 * <p>Description: Starting class for Quizzer. Parses command line
arguments and starts QuizController to begin quiz.</p>
 * @author Corey Wineman; adapted by Jim Mooney; and by Trevor Holmes
 * @version 1.0
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

    /**
     * Constructor, main class for Quizzer. Parses command line options
     * and  starts a QuizController
     * or exits if the command line arguments were invalid.
     * Parsing will fail if the quiz filename is not the last argument.
     * @param args Command line arguments
     */
    public Quizzer()
    {
    	qCount = QuizzerProperties.DEFAULT_Q_COUNT;
    	showAnswers = QuizzerProperties.SHOW_ANSWERS;
    	qFilename = QuizzerProperties.DEFAULT_Q_FILE;
    	quizDisplay = new Display ();
    	quizShell = new Shell(quizDisplay);

    }
    
    public void startQuiz() {
		GridLayout gridLayout = new GridLayout (3, false);
		quizShell.setLayout (gridLayout);
		quizShell.setSize(QuizzerProperties.INITIAL_WIDTH, QuizzerProperties.INITIAL_HEIGHT);
		
		GridData data = new GridData (SWT.NONE, SWT.TOP, true, false, 3, 3);
		quizShell.setText(QuizzerProperties.TITLE);
		quizShell.setLayoutData(data);
		Image wvuImage = new Image(quizDisplay,QuizzerProperties.QUIZZER_DIR + File.separator + "media" + File.separator + QuizzerProperties.BACKGROUND_IMAGE_FILE);
		Image wvuLogo = new Image(quizDisplay,QuizzerProperties.QUIZZER_DIR + File.separator + "media" + File.separator + QuizzerProperties.LOGO_FILE);
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

