import java.sql.*;
//import OwnerShareBuilder;
//import OwnerShareBuilder.OwnerSharesRecord;
//import OwnerShareBuilder.OwnerShareItem;

public class SharesInfoDBHelper {
    private static final String INSERT_SQL = "INSERT INTO shares_info (Date, Ranking, Shares, Ratio, OwnerName) VALUES(?, ?, ?, ?, ?)";
    private static final String CHECK_TABLE_SQL_PRE = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '";
    private static final String CHECK_TABLE_SQL_FORMAT = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '%s' AND table_name = '%s'"; 
    private static final String CREATE_TABLE_SQL_FORMAT = "CREATE TABLE IF NOT EXISTS shares_info (Date varchar(10),  Ranking int, Shares bigint, Ratio float, OwnerName varchar(100)) DEFAULT CHARSET=utf8";
    private static final String DELETE_TABLE_SQL_FORMAT = "DROP TABLE IF EXISTS shares_info";
    private static final String CREATE_DATABASE_SQL_FORMAT = "CREATE DATABASE %s CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'";
    private static final String DELETE_DATABASE_SQL_FORMAT = "DROP DATABASE IF EXISTS %s";
    private static final String SELECT_OWNER_SHARES_ITEMS = "SELECT * FROM shares_info LIMIT %d,%d";

    private static final int MAX_CONNECT_TRIES = 5;
    private static final String DB_DIR = "jdbc:mysql://127.0.0.1:3306/";
    private static final String DB_URL_POST = "?useUnicode=true&characterEncoding=utf-8";
    private static final String TABLE_NAME = "shares_info";
    private static final String USER_NAME = "root";
    private static final String PASS_WD = "123456";

    private static Connection mServerConnection;

    public SharesInfoDBHelper(String dir, String dbName, String userName, String passwd) {
        mDBDir = dir;
        mDBName = dbName;
        mDBUrl = mDBDir + mDBName + DB_URL_POST;
        mUserName = userName;
        mPasswd = passwd;
        mTryConnectTimes = 0;
    }

    public SharesInfoDBHelper(String dbName, String userName, String passwd) {
        this(DB_DIR, dbName, userName, passwd);
    }

    public SharesInfoDBHelper(String dbName) {
        this(DB_DIR, dbName, USER_NAME, PASS_WD);
    }

    public boolean deleteDatabase() {
        connectServer();
        if (mServerConnection != null) {
            try {
                Statement deleteDBStmt = mServerConnection.createStatement();
                String deleteDBSql = String.format(DELETE_DATABASE_SQL_FORMAT, mDBName);
                deleteDBStmt.executeUpdate(deleteDBSql);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }

    public boolean deleteTable() {
        connectDB();
        if (mDBConnection != null) {
            try {
                Statement deleteTableStmt = mDBConnection.createStatement();
                deleteTableStmt.executeUpdate(DELETE_TABLE_SQL_FORMAT);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private boolean checkTableExists() {
        connectDB();
        if (mDBConnection != null) {
            try {
                Statement createTableStmt = mDBConnection.createStatement();
                createTableStmt.executeUpdate(CREATE_TABLE_SQL_FORMAT);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean connectServer() {
        if (mServerConnection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                mServerConnection = DriverManager.getConnection(mDBDir, mUserName, mPasswd);
                return true;
            } catch (SQLException se) {
                se.printStackTrace();
                //System.out.println("Fail to connect mysql server");
                //throw my Exception;
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean connectDB() {
        if (mDBConnection != null) {
            return true;
        }

        try {   
            Class.forName("com.mysql.jdbc.Driver");
            mDBConnection = DriverManager.getConnection(mDBUrl, mUserName, mPasswd);
        } catch (SQLException se){   
            System.out.println("Fail to connect to :" + mDBUrl);   
            if (++mTryConnectTimes < MAX_CONNECT_TRIES) {
                System.out.println("Trying to connect Database: " + mDBName + " and connect to it again");   
                connectServer();
                tryCreateDatabase();
                connectDB();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (mDBConnection != null) {
            return true;
        }

        return false;
    }

    private void tryCreateDatabase() /*throw MyException*/{
        if (mServerConnection != null) {
            try {
                Statement createDBStmt = mServerConnection.createStatement();
                String createDBSql = String.format(CREATE_DATABASE_SQL_FORMAT, mDBName);
                createDBStmt.executeUpdate(createDBSql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Try to create Database before connected to Server");
        }
    }

    public void dispose() {
        try {
            if (mDBConnection != null) {
                mDBConnection.close();
            }
        } catch (SQLException e) {
        }
    }

    public void getTopTwoOwnerSharesRecord(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord preRecord) {
        String getNewOwnerSharesRecord = String.format(SELECT_OWNER_SHARES_ITEMS, 0, 10);
        ResultSet newOwnerShareItems = executeQuery(getNewOwnerSharesRecord);
        newRecord = OwnerShareBuilder.buildOwnerSharesRecordFromQueryResult(newRecord, newOwnerShareItems);

        String getPreOwnerSharesRecord = String.format(SELECT_OWNER_SHARES_ITEMS, 10, 10);
        ResultSet preOwnerShareItems = executeQuery(getPreOwnerSharesRecord);
        preRecord = OwnerShareBuilder.buildOwnerSharesRecordFromQueryResult(preRecord, preOwnerShareItems);

        return;
    }

    public void getPreTwoOwnerSharesRecord(OwnerShareBuilder.OwnerSharesRecord newRecord, OwnerShareBuilder.OwnerSharesRecord preRecord) {
        String getNewOwnerSharesRecord = String.format(SELECT_OWNER_SHARES_ITEMS, 10, 10);
        ResultSet newOwnerShareItems = executeQuery(getNewOwnerSharesRecord);
        newRecord = OwnerShareBuilder.buildOwnerSharesRecordFromQueryResult(newRecord, newOwnerShareItems);

        String getPreOwnerSharesRecord = String.format(SELECT_OWNER_SHARES_ITEMS, 20, 10);
        ResultSet preOwnerShareItems = executeQuery(getPreOwnerSharesRecord);
        preRecord = OwnerShareBuilder.buildOwnerSharesRecordFromQueryResult(preRecord, preOwnerShareItems);

        return;
    }

    public ResultSet executeQuery(String sql) {
        connectDB();
        try {
            if (mDBConnection != null) {
                Statement queryStmt = mDBConnection.createStatement();
                return queryStmt.executeQuery(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean executeInsert(OwnerShareBuilder.OwnerSharesRecord ownerSharesRecord) {
        if (!checkTableExists()) {
            return false;
        }
        if (ownerSharesRecord.size() < 1) {
            return false;
        }

        OwnerShareBuilder.OwnerShareItem singleShareItem = null;
        try {
            mDBConnection.setAutoCommit(false);
            PreparedStatement insertStatement = mDBConnection.prepareStatement(INSERT_SQL);
            for (int k = 0; k < ownerSharesRecord.size(); ++k) {
                 singleShareItem = ownerSharesRecord.get(k);
                 insertStatement.setString(1, singleShareItem.mDate);
                 insertStatement.setInt(2, singleShareItem.mRanking);
                 insertStatement.setLong(3, singleShareItem.mShares);
                 insertStatement.setFloat(4, singleShareItem.mShareRatio);
                 insertStatement.setString(5, singleShareItem.mOwnerName);
                 insertStatement.addBatch();
            }

            insertStatement.executeBatch();
            mDBConnection.commit();
            System.out.println(ownerSharesRecord.size() + " records inserted");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean executeDelete(String sql) {
        return false;
    }

    public boolean executeUpdate(String sql) {
        return false;
    }

    private Connection mDBConnection;
    private String mDBDir;
    private String mDBName;
    private String mDBUrl;
    private String mUserName;
    private String mPasswd;
    private int mTryConnectTimes;
};
