package com.web.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 解析HTML
 * @author chenxiaobing
 *
 */
public class ReadHtml {
    public static void main(String[] args) throws IOException {
        //1.网页HTML
        //String strUrl="http://www.usd-cny.com/";
        //URL url=new URL(strUrl);
        //InputStreamReader isr=new InputStreamReader(url.openStream());
        //BufferedReader br=new BufferedReader(isr);

        //2.本地HTML
        File f=new File("fortest.htm");
        //输入流
        // InputStreamReader isr1=new InputStreamReader(new FileInputStream(f));
        // BufferedReader br=new BufferedReader(isr1);

        //获取html转换成String
        String s;
        String AllContent="";
        while((s=br.readLine())!=null) {
            AllContent=AllContent+s;
        }

        //使用后HTML Parser 控件
        Parser myParser;
        NodeList nodeList = null;
        myParser =Parser.createParser(AllContent, "utf-8");
        NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { tableFilter });
        try {
           //获取标签为table的节点列表
           nodeList =myParser.parse(lastFilter);

           //循环读取每个table
           for (int i = 0; i <=nodeList.size(); i++) {
                if (nodeList.elementAt(i)instanceof TableTag) {
                   TableTag tag = (TableTag)nodeList.elementAt(i);
                    System.out.println(tag.getChildrenHTML());
                    System.out.println("-----------------------------------------------------");
                    /* TableRow[] rows =tag.getRows();
                   System.out.println("----------------------table "+i+"--------------------------------");
                   //循环读取每一行
                    for (int j = 0; j <rows.length; j++) {
                        TableRow tr =(TableRow) rows[j];
                        TableColumn[] td =tr.getColumns();
                        //读取每行的单元格内容
                        for (int k = 0; k< td.length; k++) {
                            System.out.println(td[k].getStringText());//（按照自己需要的格式输出）
                        }
                    }*/

                }

            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
