cp */*.class .
SET HTML_PARSER_PATH=.\htmlparser\lib
SET MYSQL_PATH=.\libmysql
SET JAR_SEPERATOR=;

SET DEP_JARS=%HTML_PARSER_PATH%\org.htmlparser.jar ^
   %HTML_PARSER_PATH%\filterbuilder.jar  ^
   %HTML_PARSER_PATH%\htmllexer.jar      ^
   %HTML_PARSER_PATH%\junit.jar          ^
   %HTML_PARSER_PATH%\sax2.jar           ^
   %HTML_PARSER_PATH%\thumbelina.jar     ^
   %MYSQL_PATH%\mysql-connector-java-5.1.39-bin.jar

@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
for %%i in (%DEP_JARS%) do (
    SET FINAL_DEP_JARS=%%i%JAR_SEPERATOR%!FINAL_DEP_JARS!
    echo %FINAL_DEP_JARS%
)

java -cp %FINAL_DEP_JARS% Main
