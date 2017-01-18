#!/bin/bash
cp */*.class .
java -cp .:./htmlparser/lib/filterbuilder.jar:./htmlparser/lib/htmllexer.jar:./htmlparser/lib/junit.jar:./htmlparser/lib/org.htmlparser.jar:./htmlparser/lib/sax2.jar:./htmlparser/lib/thumbelina.jar:./libmysql/mysql-connector-java-5.1.39-bin.jar Main
