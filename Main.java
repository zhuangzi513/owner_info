public class Main {
    private static String URL_PRE = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/stockid/";
    private static String URL_POS = "/displaytype/30.phtml";

    private static void deleteTable(String databaseName, String tableName) {
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        dbHelper.deleteTable();
    }

    private static void deleteDatabase(String databaseName) {
        SharesInfoDBHelper dbHelper = new SharesInfoDBHelper(databaseName);
        dbHelper.deleteDatabase();
    }

    public static void main(String[] args) {
        String finalUrl = null;
        String stockId = null;
        ReadHtml tmpHtmlReader = null;
        

        for ( int i = 5; i < 10; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             System.out.println(">> >> >> >>:" + finalUrl + "\n");
             //deleteDatabase("Y" + stockId);
             tmpHtmlReader = new ReadHtml(finalUrl, ReadHtml.TYPE_WEB);
             tmpHtmlReader.parseHtml();
             tmpHtmlReader.insertIntoTable("Y" + stockId);
        }


/*
        for ( int i = 0 ; i < 1000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             System.out.println(">> >> >> >>:" + finalUrl + "\n");
             deleteDatabase("R" + stockId);
        }

        for ( int i = 2000 ; i < 3000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             System.out.println(">> >> >> >>:" + finalUrl + "\n");
             deleteDatabase("R" + stockId);
        }
*/


/*
        for ( int i = 300000 ; i < 301000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             System.out.println(">> >> >> >>:" + finalUrl + "\n");
             deleteTable("R" + stockId);
        }

        for ( int i = 600000 ; i < 602000; ++i) {
             stockId = String.format("%06d", i);
             finalUrl = URL_PRE + stockId + URL_POS;
             System.out.println(">> >> >> >>:" + finalUrl + "\n");
             deleteTable("R" + stockId);
        }
*/
    }
}
