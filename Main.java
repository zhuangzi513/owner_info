import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Main {
    //private static String URL_PRE = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/stockid/";
    //private static String URL_POS = ".phtml";
    private static String URL_PRE = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_StockHolder/stockid/";
    private static String URL_POS = ".phtml";
    private static Map<String, Integer> S_TARGET_RESULTS_ORIGIN = new LinkedHashMap<String, Integer>();
    private static Map<String, Integer> S_TARGET_RESULTS_SORTED = new LinkedHashMap<String, Integer>();
    private static Map<String, String> S_TARGET_RESULTS_DATES = new LinkedHashMap<String, String>();

    public static class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {  
        public int compare(Entry<String, Integer> me1, Entry<String, Integer> me2) {  
            return me2.getValue().compareTo(me1.getValue());  
        }  
    }  

    private static void sortByValue() {
        if (S_TARGET_RESULTS_ORIGIN == null || S_TARGET_RESULTS_ORIGIN.isEmpty()) {  
            return ;  
        }  
        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(S_TARGET_RESULTS_ORIGIN.entrySet());  
        Collections.sort(entryList, new MapValueComparator());  
        Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();  
        Map.Entry<String, Integer> tmpEntry = null;  
        while (iter.hasNext()) {  
            tmpEntry = iter.next();  
            S_TARGET_RESULTS_SORTED.put(tmpEntry.getKey(), tmpEntry.getValue());  
        }  
    }

    private static void dumpResults() {
         Iterator<Map.Entry<String, Integer>> entries = S_TARGET_RESULTS_SORTED.entrySet().iterator();  
         while (entries.hasNext()) {  
             Map.Entry<String, Integer> entry = entries.next();  
             String newestDate = S_TARGET_RESULTS_DATES.get(entry.getKey());
             if (newestDate.compareTo("2016-09-30") > 0) {
                 System.out.println("股票ＩＤ = " + entry.getKey() + " 新进股东持仓= " + entry.getValue() / 100.0f + "%" + " 最新统计日期:" + newestDate);  
             }
         }  
    }

    private static void addToResults(String id, int value) {
        S_TARGET_RESULTS_ORIGIN.put(id, value);
    }

    private static void deleteTable(String databaseName, String tableName) {
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        dbHelper.deleteTable();
    }

    private static void deleteDatabase(String databaseName) {
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        dbHelper.deleteDatabase();
    }

    private static void getSumOfFlesh(String databaseName) {
        //System.out.println("beginning id: " + databaseName);
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        OwnerShareBuilder.OwnerSharesRecord newRecord = new OwnerShareBuilder.OwnerSharesRecord(10);
        OwnerShareBuilder.OwnerSharesRecord oldRecord = new OwnerShareBuilder.OwnerSharesRecord(10);
        dbHelper.getTopTwoOwnerSharesRecord(newRecord, oldRecord);
        int flesh = OwnerSharesHelper.sumOfFlesh(newRecord, oldRecord);
        dbHelper.dispose();

        if (flesh > 400) {
            System.out.println("TARGET: id: " + databaseName + " flesh: " + flesh + " newestDate:" + newRecord.get(0).mDate);
            S_TARGET_RESULTS_DATES.put(databaseName, newRecord.get(0).mDate);
            addToResults(databaseName, flesh);
        }
    }

    private static void getSumOfDecrease(String databaseName) {
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        OwnerShareBuilder.OwnerSharesRecord newRecord = new OwnerShareBuilder.OwnerSharesRecord(10);
        OwnerShareBuilder.OwnerSharesRecord oldRecord = new OwnerShareBuilder.OwnerSharesRecord(10);
        dbHelper.getTopTwoOwnerSharesRecord(newRecord, oldRecord);
        int decreased = OwnerSharesHelper.sumOfDecreased(newRecord, oldRecord);
        dbHelper.dispose();

        //if (decreased > 0 && decreased < 500) {
        //    //System.out.println("TARGET: id: " + databaseName + " flesh: " + flesh);
        //    addToResults(databaseName, decreased);
        //}

        System.out.println("========" + decreased + "============");
        newRecord.dump();
        oldRecord.dump();
    }

    private static void getDecreased() {
        String stockId = null;
             getSumOfDecrease("R000581");
/*
        for ( int i = 2000 ; i < 3000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfDecrease("Y" + stockId);
        }
  
        for ( int i = 0 ; i < 1000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfDecrease("Y" + stockId);
        }    
 
        for ( int i = 300000 ; i < 301000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfDecrease("Y" + stockId);
        }
 
        for ( int i = 600000 ; i < 602000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfDecrease("Y" + stockId);
        }
 */
        sortByValue();
        dumpResults();
    }

    private static void getFlesh() {
        String stockId = null;
        for ( int i = 2000 ; i < 3000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfFlesh("Y" + stockId);
        }
 
        for ( int i = 0 ; i < 1000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfFlesh("Y" + stockId);
        }
 
        for ( int i = 600000; i < 602000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfFlesh("Y" + stockId);
        }

        for ( int i = 603000; i < 604000; ++i) {
             stockId = String.format("%06d", i);
             getSumOfFlesh("Y" + stockId);
        }

        sortByValue();
        dumpResults();
    }

    private static boolean doParseAndInsert(String stockId, int type) {
        try {
            System.out.println(stockId);
            String finalUrl = URL_PRE + stockId + URL_POS;
            ReadHtml tmpHtmlReader = new ReadHtml(stockId, finalUrl, ReadHtml.TYPE_WEB);
            if (tmpHtmlReader != null) {
                tmpHtmlReader.parseHtml();
                tmpHtmlReader.insertIntoTable();
                //tmpHtmlReader.insertIntoTable("R" + stockId);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void parseHtml() {
        String finalUrl = null;
        String stockId = null;
        ReadHtml tmpHtmlReader = null;
/*
        for ( int i = 1; i < 22; ++i) {
             stockId = String.format("%06d", i);
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }
*/


        for ( int i = 1; i < 1000; ++i) {
             stockId = String.format("%06d", i);
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }

        for ( int i = 2000 ; i < 3000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }

        for ( int i = 300000; i < 301000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }

        for ( int i = 600000; i < 602000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }

        for ( int i = 603000; i < 604000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             if (!doParseAndInsert(stockId, ReadHtml.TYPE_WEB)) {
                 System.out.println("skip : " + stockId);
                 continue;
             }
        }
    }

    public static void main(String[] args) {
        //parseHtml();
        getFlesh();
        //getDecreased();
    }
}
