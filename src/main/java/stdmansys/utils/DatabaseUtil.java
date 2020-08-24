package stdmansys.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static Connection connection = null;

    public static void createNewDatabase(String dbName) {
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static Connection getDBConnection(String dbName) {
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

}