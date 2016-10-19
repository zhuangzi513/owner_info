package origin.DBHelper;

import java.sql.*;

public interface MySQLDBHelper {
    public connect(String dir, String dbName, String user, String passwd);
    public ResultSet executeQuery(String sql);
    public boolean executeInsert(String sql);
    public boolean executeDelete(String sql);
    public boolean executeUpdate(String sql);
};
