#!/bin/bash
HTML_PARSER_PATH="./htmlparser/lib"
JAR_SEPERATOR=":"

DEP_JARS="
   ${HTML_PARSER_PATH}/org.htmlparser.jar
   ${HTML_PARSER_PATH}/filterbuilder.jar
   ${HTML_PARSER_PATH}/htmllexer.jar
   ${HTML_PARSER_PATH}/junit.jar
   ${HTML_PARSER_PATH}/sax2.jar
   ${HTML_PARSER_PATH}/thumbelina.jar
"

for JAR in $DEP_JARS
do
    FINAL_DEP_JARS="${FINAL_DEP_JARS}${JAR_SEPERATOR}${JAR}"
done

OWNER_SHARE_SRC="
./ownershare/OwnerShareBuilder.java
./ownershare/OwnerSharesHelper.java
"

SHARES_INFO_SRC="
./sharesinfo/SharesInfoDBHelper.java
./sharesinfo/ReadHtml.java
"
ROOT_SRC="
Main.java
"

ALL_SRC="
$OWNER_SHARE_SRC
$SHARES_INFO_SRC
$ROOT_SRC
"

echo "build these SRC:"
echo ""
for SRC in $ALL_SRC
do
   echo $SRC
done
echo ""
echo "using these jar:"
for JAR in $DEP_JARS
do
    echo "$JAR"
done

#javac -cp ".:${HTML_PARSER_PATH}/org.htmlparser.jar:${HTML_PARSER_PATH}/filterbuilder.jar:${HTML_PARSER_PATH}/htmllexer.jar:${HTML_PARSER_PATH}/junit.jar:${HTML_PARSER_PATH}/sax2.jar:${HTML_PARSER_PATH}/thumbelina.jar" ownershare/OwnerShareBuilder.java ownershare/SharesInfoDBHelper.java ReadHtml.java  OwnerSharesHelper.java Main.java
javac -cp $FINAL_DEP_JARS $ALL_SRC
