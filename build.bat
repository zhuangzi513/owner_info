SET HTML_PARSER_PATH=htmlparser\lib
SET JAR_SEPERATOR=;

SET DEP_JARS=%HTML_PARSER_PATH%\org.htmlparser.jar ^
   %HTML_PARSER_PATH%\filterbuilder.jar  ^
   %HTML_PARSER_PATH%\htmllexer.jar      ^
   %HTML_PARSER_PATH%\junit.jar          ^
   %HTML_PARSER_PATH%\sax2.jar           ^
   %HTML_PARSER_PATH%\thumbelina.jar

@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
for %%i in (%DEP_JARS%) do (
    SET FINAL_DEP_JARS=%%i%JAR_SEPERATOR%!FINAL_DEP_JARS!
    echo %FINAL_DEP_JARS%
)

SET OWNER_SHARE_SRC= ^
ownershare\OwnerShareBuilder.java ^
ownershare\OwnerSharesHelper.java 


SET SHARES_INFO_SRC= ^
sharesinfo\SharesInfoDBHelper.java ^
sharesinfo\ReadHtml.java

SET ROOT_SRC= Main.java

SET ALL_SRC=%OWNER_SHARE_SRC%%SHARES_INFO_SRC%%ROOT_SRC%

@echo on

echo "build these SRC:"
echo "%OWNER_SHARE_SRC%"
echo "%SHARES_INFO_SRC%"
echo "%ROOT_SRC%"

::for %%SRC in %ALL_SRC%
::do
::   echo %%SRC
::done

echo ""
echo "using these jar:"
::for JAR in %DEP_JARS%
::do
::    echo "%%JAR"
::done

javac -encoding UTF-8 -cp %FINAL_DEP_JARS% %ALL_SRC%
