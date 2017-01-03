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
javac -classpath .;jsoup-1.10.1.jar *.java

#### 4. Run command
java -classpath .;jsoup-1.10.1.jar DMS_Main \<domain name>

## Output:
\<Result Header> </br>
\<Emails>

e.g:

Found these email addresses: </br>
press@domain.com </br>
sales@domain.com </br>
info@domain.com </br>

