#!/bin/bash
HTML_PARSER_PATH="./htmlparser/lib"
#DEP_JARS=${HTML_PARSER_PATH}/org.htmlparser.jar \
#${HTML_PARSER_PATH}/filterbuilder.jar \
#${HTML_PARSER_PATH}/htmllexer.jar \
#${HTML_PARSER_PATH}/junit.jar \
#${HTML_PARSER_PATH}/sax2.jar \
#${HTML_PARSER_PATH}/thumbelina.jar

javac -cp ".:${HTML_PARSER_PATH}/org.htmlparser.jar:${HTML_PARSER_PATH}/filterbuilder.jar:${HTML_PARSER_PATH}/htmllexer.jar:${HTML_PARSER_PATH}/junit.jar:${HTML_PARSER_PATH}/sax2.jar:${HTML_PARSER_PATH}/thumbelina.jar" OwnerShareBuilder.java SharesInfoDBHelper.java ReadHtml.java  Main.java
