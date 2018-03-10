package mwsu.csc346.FK;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashMap;
import java.util.Map;

/*
 *   A DB object class of db operation methods.
 *   Because it pulls dbfileName and iscripts from DbConfiguration,
 *           it doesn't make sense to make more than one instance of it ever.
 *                                    
 */
public class DB {

    private String dbfileName;
    private String dbUrl;
    private Map scripts;
    
    public DB() {
        this.dbfileName = DbConfiguration.dbfileName;
        this.dbUrl = "jdbc:sqlite:" + dbfileName;
        this.scripts = DbConfiguration.iscripts;

        this.init(dbfileName, scripts);
    }

    //(location of db file is expected to be in root of project
    // - db file will be created in root of project if nonexistent)
    //check for db file, if none create one and run scripts.
    //print info about it
    private void init(String fileName, Map scripts) {
    
        File db = new File(fileName);
        boolean exists = db.exists(); 

        //connect to the database or creates new one if no db file
        try {
           Connection conn = DriverManager.getConnection(dbUrl);
            if (conn != null) {
               DatabaseMetaData meta = conn.getMetaData();
               //System.out.println("The driver name is " + meta.getDriverName());

               if(!exists) {
                    System.out.println();
                    System.out.println("A new database has been created: " + fileName);
                    System.out.println();
                    //call config if there are scripts to run (scripts.keySet())
                    if(scripts.size() > 0) {
                        if(this.config(fileName, scripts)) {
                            printFileMetadata(fileName);
                            printDbMetadata(fileName, conn, meta);
                         }
                    }
               }
               if(exists) { 
                   printFileMetadata(fileName);
                   printDbMetadata(fileName, conn, meta);              
               }
               conn.close();
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private boolean config(String fileName, Map scripts) {
    
    Connection conn = null;
    Statement stmt = null;

        //connect to the database or creates new one if no db file
        try {

            conn = DriverManager.getConnection(dbUrl);
 
            if (conn != null) {
                
                stmt = conn.createStatement();
                //System.out.println(scripts.keySet());
                
                for(Object key: scripts.keySet()) {
                      System.out.println("running script: " + key);// + " - " + scripts.get(key).toString());
                      stmt.executeUpdate(scripts.get(key).toString());
                } 
            stmt.close();
            conn.close();
            }

        } catch (SQLException e) {
            //System.out.println("error");
            System.out.println(e.getMessage());
            return false;
        }
    return true;
    }


    private void printFileMetadata(String fileName) {
        //get file metadata
        Path path = Paths.get(fileName);
        BasicFileAttributes attr = null;

        try {
             attr = Files.readAttributes(path, BasicFileAttributes.class);
             
        } catch (IOException e) {
              System.out.println("oops error! " + e.getMessage());
        }
        System.out.println();
        System.out.println("==========================================");
        System.out.println("The database: " + fileName +" exists.");
        System.out.println("==========================================");
        System.out.println("creationTime     = " + attr.creationTime());
        System.out.println("lastAccessTime   = " + attr.lastAccessTime());
        System.out.println("lastModifiedTime = " + attr.lastModifiedTime());

        //System.out.println("isDirectory      = " + attr.isDirectory());
        //System.out.println("isOther          = " + attr.isOther());
        //System.out.println("isRegularFile    = " + attr.isRegularFile());
        //System.out.println("isSymbolicLink   = " + attr.isSymbolicLink());
        System.out.println("size             = " + attr.size());
        System.out.println();
    }

    public void printDbMetadata(String fileName, Connection connection, DatabaseMetaData metaData) {
        System.out.println("==========================================");
        System.out.println("                  TABLES                  ");
        System.out.println("------------------------------------------");
        try {
            Connection conn = connection;
            DatabaseMetaData meta = metaData;

            String SCHEMA_NAME="${YOUR_SCHEMA_NAME}";
     
            String tableType[] = {"TABLE"};
    
            StringBuilder builder = new StringBuilder();
    
            ResultSet result = meta.getTables(null,SCHEMA_NAME,null,tableType);
            while(result.next())
            {
              String tableName = result.getString(3);
              if(!tableName.equals("sqlite_sequence"))
              {
                builder.append(tableName + "( ");
                ResultSet columns = metaData.getColumns(null,null,tableName,null);
     
                while(columns.next())
                {
                    String columnName = columns.getString(4);
                    builder.append(columnName);
                    builder.append(",");
                }
                builder.deleteCharAt(builder.lastIndexOf(","));
                builder.append(" )");
                builder.append("\n");
                builder.append("---------------------------------------");
                builder.append("\n");
              }
            }
    
            System.out.println(builder.toString());
            System.out.println("=======================================");
   
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        DB theDb = new DB();

    }
}
