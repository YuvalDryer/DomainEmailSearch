# DomainEmailSearch

## Setup:

#### 1. Place all files and libraries in the same folder (3 java files and 1 Jar file)
Files included: </br>
DMS_Main.java </br>
DMS_Parser.java </br>
DMS_JSoupParser.java

Additional libraries included: </br>
jsoup-1.10.1.jar

#### 2. Make sure the Path Environment System Variables points to the bin folder of the installed JDK.
#### 3. Compile command
For Windows: </br>
javac -classpath .;jsoup-1.10.1.jar *.java </br>

For Linux/Mac: </br>
javac -classpath .:./jsoup-1.10.1.jar *.java

#### 4. Execution command
For Windows: </br>
java -classpath .;jsoup-1.10.1.jar DMS_Main \<domain name> \<optional> </br>

For Linux/Mac: </br>
java -classpath .:./jsoup-1.10.1.jar DMS_Main \<domain name> \<optional> </br>

##### optional: (Space separated)
-cont use this to print the email addresses as they are discovered instead of at the end. </br>
-all  use this to option to view a list of all successful and failed url read attempts </br>

## Output:
\<Result Header> </br>
\<Emails>

e.g:

Found these email addresses: </br>
press@domain.com </br>
sales@domain.com </br>


