import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
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
    private int mState = 0;
    final private static int START_NEW_RECORD = 0;
    final private static int PREPARE_COUNTING = 1;;
    final private static int BEGIN_COUNTING = 2;;
    final private static int BUSY_COUNTING = 3;;

    private int mType = 0;
    final private static int TYPE_LOCAL = 1;
    final private static int TYPE_WEB   = 2;

    final String mStringUrl;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferReader;
    private Vector<String> mSharesInfos;

    private static String URL_PRE = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/stockid/";
    private static String URL_POS = "/displaytype/30.phtml";

    public static void main(String[] args)  {
        //for (int i = 0; i < 2000; ++i) {
             ReadHtml testReadHtml = new ReadHtml("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/stockid/000581/displaytype/30.phtml", TYPE_WEB);
             testReadHtml.parseHtml();
        //}
    }

    private void init() {
        try {
            if (mType == TYPE_WEB) {
                URL tmpURLInstance = new URL(mStringUrl);
                mInputStreamReader = new InputStreamReader(tmpURLInstance.openStream(), "gb2312");
            } else if (mType == TYPE_LOCAL) {
                File tmpFile = new File(mStringUrl);
                mInputStreamReader = new InputStreamReader(new FileInputStream(tmpFile), "gb2312");
            }

            mBufferReader = new BufferedReader(mInputStreamReader); 
        } catch(IOException e) {
            e.printStackTrace();
        }

        mSharesInfos = new Vector<String>();
    }

    public ReadHtml(String url, int type) {
        mStringUrl = url;
        mType = type;
        init();
    }

    private int appedToShareInfos(String date, TableColumn[] td) {
        mSharesInfos.add(date);
        for (int k = 0; k< td.length; k++) {
            mSharesInfos.add(td[k].childAt(0).getChildren().elementAt(0).getText());
        }

        String tmpShareInfo[] = new String[6];
        for (int k = mSharesInfos.size() - 1; k >= mSharesInfos.size() -6 ; --k) {
             tmpShareInfo[k%6] = mSharesInfos.get(k);
             System.out.println(">> K: "  + k + " " + tmpShareInfo[k%6]);
        }

        OwnerShareBuilder.buildOwnerShareItem(tmpShareInfo);

        return mSharesInfos.size();
    }

    public Vector getSharesInfos() {
        return mSharesInfos;
    }

    public void parseHtml() {
        //获取html转换成String
        String htmlString;
        String htmlContent = "";
        try {
            while((htmlString = mBufferReader.readLine()) != null) {
                htmlContent = htmlContent + htmlString;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        //使用后HTML Parser 控件
        Parser tmpParser;
        NodeList nodeList = null;
        tmpParser = Parser.createParser(htmlContent, "unicode");
        //TODO: Maybe need to modify to fit the html
        NodeFilter tableFilter = new HasAttributeFilter("id", "CirculateShareholderTable");
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { tableFilter });
        try {
           nodeList = tmpParser.parse(lastFilter);
           //循环读取每个table
           for (int i = 0; i <=nodeList.size(); i++) {
                if (nodeList.elementAt(i)instanceof TableTag) {
                    TableTag tag = (TableTag)nodeList.elementAt(i);
                    //System.out.println(tag.getChildrenHTML());
                    TableRow[] rows =tag.getRows();
                    //循环读取每一行
                    String date = "";
                    for (int j = 0; j <rows.length; j++) {
                         TableRow tr =(TableRow) rows[j];
                         TableColumn[] td =tr.getColumns();
                         //读取每行的单元格内容
                         if (td.length == 5 && mState == BUSY_COUNTING) {
                             appedToShareInfos(date, td);
                             //for (int k = 0; k< td.length; k++) {
                             //    System.out.println(td[k].getStringText());//（按照自己需要的格式输出）
                             //}
                         } else if (mState == BEGIN_COUNTING) {
                             mState = BUSY_COUNTING;
                         }

                         if (td.length == 2) {
                             ++mState;
                             if (mState == PREPARE_COUNTING) {
                                 date = td[1].getStringText();
                                 System.out.println("mState: " + mState + date);//（按照自己需要的格式输出）
                             }
                             continue;
                         } else if (td.length == 1) {
                             mState = START_NEW_RECORD;
                             continue;
                         }
                    }

                }

            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
