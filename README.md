# Quizzer
# Java Quiz Application

### West Virginia Univeristy - CS - Software Portablility - Programming Assignment #3

##### Quizzer - Description

Quizzer is a Java application which will take a file with questions and run a quiz based on those questions.
To run a quiz:
1) Type: java -jar /home/tholmes/asg3/Quizzer.jar (If command line arguments are not set, the default quiz will run)
2) Command Line Options:
java -jar Quizzer.jar -h
Quizzer [-h] [-l] [-n count] [-a show_answers] [-q quiz_file] [-g gui] [-t time_limit] [-A password] [-L logfile]
-h : This help message
-l : Display the user's log report
-n : Integer - The number of questions in the quiz
-a : Yes|No|Y|N - Option to show the correct answer to incorrect questions
-q : String - Specify a quiz file with questions
-g : Yes|No|Y|N - Enable a graphic user interface.  Default is No. And is disabled when a desktop environment is unavailable
-t : Integer - The time limit for the quiz in seconds.  The default is unlimited
-A : String - If the user enters the correct password they will be come an adminstrator
-L : String - Logfile location and name

Examples:

1) Run a quiz with 5 questions, showing the answers, and a time limit of 60 seconds
java -jar /home/tholmes/asg3/Quizzer.jar -n 5 -a yes -t 60

2) Show the help screen
java -jar /home/tholmes/asg3/Quizzer.jar -h 

3) Show the User's Quiz Result History
java -jar /home/tholmes/asg3/Quizzer.jar -l

4) Run a quiz with your the defaults but with your quiz file (/home/tholmes/as3/QFILES/test100.q.txt)
java -jar /home/tholmes/asg3/Quizzer.jar -q /home/tholmes/asg3/QFILES/test100.q.txt

5) Make the current user an Admin.  Which allows the user to make and see the Quiz Log File.
java -jar /home/tholmes/asg3/Quizzer.jar -A AlanTuringCrackedEnigma

6) Make and show the Log file.
java -jar /home/tholmes/asg3/Quizzer.jar -L logfile.txt

NOTE 2: Sample question files are located in the QFILES subfolder.

Below is the format of a Question File.

#### Sample Quiz File Format
* SAMPLE QUIZ QUESTIONS
* Jim Mooney
* CS 533
* Fall 2011

* This is a question file for the quiz program.
* Any line that begins with an asterisk should be ignored.
* Totally blank lines should also be ignored.

* The file consists of a sequence of questions.
* Each question has the following form:
*
*	1. A line beginning with "@Q".
*	2. Up to ten lines giving the text of the question.
*	3. A line beginning with "@A"
*	4. A line consisting of the integer value for the correct answer
*	5. Up to ten lines giving answer choices, one line each.
*	6. A line beginning with "@E".
*
* The question file has the form of a sequence of variable-length
* text lines, Each containing 75 characters or fewer.  The character
* code and the detailed form of the file (line terminators, etc.) are
* those conventional for the system on which it is installed.
*
* The following example file contains two questions.  The first has
* four possible answers; the correct answer is the second.  The second
* has six possible answers; the correct answer is the fourth.

@QUESTIONS
How many class days are there in this entire term?
@ANSWERS
2
forty-one
twenty-nine
seems like hundreds
who's counting?
@END

@QUESTION

This is a long question which rambles on with no apparent end in sight.
It has two purposes.  The first is to catch those of you who have not
provided enough buffer space for a very long question.  The second is to
ask you to figure out how many letters there are (not counting spaces or
punctuation marks) in this entire paragraph.

@ANSWERS
4
509
266
1066
263
None of the above
All of the above
@END
* Remember: blank lines and comments are ignored throughout!

* END OF FILE






