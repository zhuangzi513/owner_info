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
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 解析HTML
 *
 */
public class ReadHtml {
    private static String DEFAULT_VALUE_FOR_ALL_TYPES = "-1";
    public static String THE_EXISTING_NEWEST_DATE = "2015-03-31";
    private int mState = 0;
    final private static int START_NEW_RECORD = 0;
    final private static int PREPARE_COUNTING = 1;;
    final private static int BEGIN_COUNTING = 2;;
    final private static int START_COUNTING = 3;;
    final private static int BUSY_COUNTING = 6;;

    private int mType = 0;
    final public static int TYPE_LOCAL = 1;
    final public static int TYPE_WEB   = 2;

    final String mStringUrl;
    final String mStockID;
    private String mTipSavedDate;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferReader;
    private Vector<String> mSharesInfos;
    private Vector<OwnerShareBuilder.OwnerShareItem> mOwnerShareItems;
    private SharesInfoDBHelper mSharesInfoDBHelper;

    private static String URL_PRE = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_StockHolder/stockid/";
    private static String URL_POS = ".phtml";

    private void init() throws IOException {
        if (mType == TYPE_WEB) {
            URL tmpURLInstance = new URL(mStringUrl);
            mInputStreamReader = new InputStreamReader(tmpURLInstance.openStream(), "gb2312");
        } else if (mType == TYPE_LOCAL) {
            File tmpFile = new File(mStringUrl);
            mInputStreamReader = new InputStreamReader(new FileInputStream(tmpFile), "gb2312");
        }

        mBufferReader = new BufferedReader(mInputStreamReader); 
        mSharesInfos = new Vector<String>();
        mOwnerShareItems = new Vector<OwnerShareBuilder.OwnerShareItem>();
        mSharesInfoDBHelper = new SharesInfoDBHelper("Y" + mStockID, "root", "123456");
    }

    public ReadHtml(String stockId, String url, int type) throws IOException {
        mStringUrl = url;
        mStockID = stockId;
        mType = type;
        init();
    }

    public int insertIntoTable() {
        //deleteRecords();
        if (mOwnerShareItems.size() > 0) {
            OwnerShareBuilder.OwnerSharesRecord sharesRecord = OwnerShareBuilder.buildOwnerSharesRecord(mOwnerShareItems);
            mSharesInfoDBHelper.connectDB();
            mSharesInfoDBHelper.executeInsert(sharesRecord);
            mSharesInfoDBHelper.dispose();
        }

        return 1;
    }

    private int appedToShareInfos(String date, TableColumn[] td) {
        mSharesInfos.add(date);
        //try {
            for (int k = 0; k < td.length; k++) {
                String origin = null;
                if (td[k].childAt(0).getChildren().elementAt(0) instanceof LinkTag) {
                    origin = ((LinkTag)td[k].childAt(0).getChildren().elementAt(0)).getLinkText();//（按照自己需要的格式输出）
                } else {
                    origin = td[k].childAt(0).getChildren().elementAt(0).getText();
                }
                //String utf8 = new String(origin.getBytes("utf-8"),"utf-8");
                if (origin.indexOf("&nbsp") >= 0) {
                    origin = origin.substring(0, origin.indexOf("&nbsp"));
                    if (origin.isEmpty()) {
                        origin = DEFAULT_VALUE_FOR_ALL_TYPES;
                    }
                }

                mSharesInfos.add(origin);
            }
        //} catch (UnsupportedEncodingException e) {
        //    e.printStackTrace();
        //    return -1;
        //}

        String tmpShareInfo[] = new String[6];
        for (int k = mSharesInfos.size() - 1; k >= mSharesInfos.size() -6 ; --k) {
             tmpShareInfo[k%6] = mSharesInfos.get(k);
             //System.out.println(">> K: "  + k + " " + tmpShareInfo[k%6]);
        }

        //System.out.println(">> >> >> >>\n");

        mOwnerShareItems.add(OwnerShareBuilder.buildOwnerShareItem(tmpShareInfo));

        return mSharesInfos.size();
    }

    public Vector getSharesInfos() {
        return mSharesInfos;
    }

    public boolean validateInfo(TableColumn[] td) {
        String firstItem = td[0].childAt(0).getChildren().elementAt(0).getText();
        if (firstItem.length() <= 2
            && Integer.parseInt(firstItem) <= 10) {
            return true;
        }

        return false;
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

        mSharesInfoDBHelper.connectDB();
        mSharesInfoDBHelper.deleteRecords(THE_EXISTING_NEWEST_DATE);
        OwnerShareBuilder.OwnerSharesRecord tipSharesRecord = mSharesInfoDBHelper.getTipOwnerSharesRecord();
        if (tipSharesRecord != null && tipSharesRecord.size() > 0) {
            mTipSavedDate = tipSharesRecord.get(0).mDate;
        }
        System.out.println("mTipSavedDate: " + mTipSavedDate);//（按照自己需要的格式输出）

        //使用后HTML Parser 控件
        Parser tmpParser;
        NodeList nodeList = null;
        tmpParser = Parser.createParser(htmlContent, "unicode");
        //TODO: Maybe need to modify to fit the html
        NodeFilter tableFilter = new HasAttributeFilter("id", "Table1");
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { tableFilter });
        try {
           nodeList = tmpParser.parse(lastFilter);
           //循环读取每个table
           for (int i = 0; i <=nodeList.size(); i++) {
                if (nodeList.elementAt(i)instanceof TableTag) {
                    TableTag tag = (TableTag)nodeList.elementAt(i);
                    TableRow[] rows =tag.getRows();
                    //循环读取每一行
                    String date = "";
                    for (int j = 0; j <rows.length; j++) {
                         TableRow tr =(TableRow) rows[j];
                         TableColumn[] td =tr.getColumns();
                         //读取每行的单元格内容
                         if (td.length == 5 && mState == BUSY_COUNTING) {
                             //System.out.println("mState: " + mState);//（按照自己需要的格式输出）
                             if (!validateInfo(td)) {
                                 continue;
                             }
                             //for (int k = 0; k < td.length; k++) {
                             //    if (td[k].childAt(0) != null
                             //        && td[k].childAt(0).getChildren() != null
                             //        && td[k].childAt(0).getChildren().elementAt(0) != null) {
                             //        if (td[k].childAt(0).getChildren().elementAt(0) instanceof LinkTag) {
                             //            System.out.println("k: " + k + " " + ((LinkTag)td[k].childAt(0).getChildren().elementAt(0)).getLinkText());//（按照自己需要的格式输出）
                             //        } else {
                             //            System.out.println("k: " + k + " " + td[k].childAt(0).getChildren().elementAt(0).getText());
                             //        }
                             //    }
                             //}
                             if (mTipSavedDate == null || date.compareTo(mTipSavedDate) > 0) {
                                 //System.out.println("date: " + date);
                                 //System.out.println("mTipSavedDate: " + mTipSavedDate);
                                 appedToShareInfos(date, td);
                             } else {
                                 break;
                             }
                         } else if (mState == BEGIN_COUNTING) {
                             //System.out.println("mState: set BUSY_COUNTING, " + mState + date);//（按照自己需要的格式输出）
                             mState = START_COUNTING;
                         }

                         if (td.length == 2) {
                             ++mState;
                             //System.out.println("mState: " + mState + date);//（按照自己需要的格式输出）
                             if (mState == PREPARE_COUNTING) {
                                 date = td[1].getStringText();
                                 //System.out.println("mState: " + mState + date);//（按照自己需要的格式输出）
                             }
                             continue;
                         } else if (td.length == 1) {
                             mState = START_NEW_RECORD;
                             //System.out.println("mState: " + mState);//（按照自己需要的格式输出）
                             continue;
                         }
                    }

                } else {
                    //System.out.println("nodeList.elementAt("+ i +"): not table");
                }

            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

    }

/*

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
                                 //System.out.println("mState: " + mState + date);//（按照自己需要的格式输出）
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
*/
}
