package mwsu.csc346.KF.database;
 
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * "With this class, you can be sure there will never be two instances of the database connection."
 *        (the only method that currently uses this is DB.runQuery())
 * ADAPTED FROM:   https://community.oracle.com/docs/DOC-918906   
 *    TITLE:  "Effective Ways to Implement and Use the Singleton Design Pattern"
 *    SECTION: "How to Create a Database Connection Class Using a Singleton"     
 */
public class DbSingleton {
    private static String dbfileName;
    private static Statement connection;

    private DbSingleton() {
        this.dbfileName = DbConfiguration.dbfileName;
        /* Creation of an instance of the connection statement*/
        connection = setConnection();
    }
    /* Private method charge to set the connection statement*/
    private static Statement setConnection() {
        try {
            String url = "jdbc:sqlite:" + dbfileName;
            Connection conn = DriverManager.getConnection(url);

            //Creation of the Statement object
            Statement state = conn.createStatement();
            return (Statement) state;
        } catch (SQLException ex) {
            //Logger.getLogger(DbSingleton.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /* Private inner class responsible for instantiating the single instance of the singleton */
    private static class DbSingletonManagerHolder {
        private final static DbSingleton instance = new DbSingleton();
    }

    /**
     * @return
     * Public method, which is the only method allowed to return an instance of 
     * the singleton (the instance here is the database connection statement)
     */
    public static DbSingleton getInstance() {
        try {
            return DbSingletonManagerHolder.instance;
        } catch (ExceptionInInitializerError ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public static Statement getStatement() {
        return connection;
    }
}
