package mwsu.csc346.KF;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashMap;
import java.util.Map;

/*
 *   A DB object class of db operation methods.
 *   Because it pulls dbfileName and iscripts from DbConfiguration,
 *           it doesn't make sense to make more than one instance of it
 *                                           (w/o modification).
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
                    //and if config() returns successful (true)
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

    boolean success = false;

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
        
        success = true;

        } catch (SQLException e) {
            //System.out.println("error");
            System.out.println(e.getMessage());
            return success;
        }
    return success;
    }


    //uses DbSingleton, just to try it out
    public void runQuery(String sqlString) {
       System.out.println("running:   " + sqlString);
       try {
        /* Creation of the statement instance */
        DbSingleton single = DbSingleton.getInstance();
        Statement state = DbSingleton.getStatement();
        //execute sqlString param query
        ResultSet rs = state.executeQuery(sqlString);
        //get metadata for resultset
        ResultSetMetaData rsmd = rs.getMetaData();

        int columnsNumber = rsmd.getColumnCount();
        
        //print:  columnName1, columnName2, columnName3...
        for (int i = 1; i <= columnsNumber; i++) {
                String columnValue = rs.getString(i);
                System.out.print(rsmd.getColumnName(i) + ", ");
        }
        System.out.println();
        //print:  
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                String columnValue = rs.getString(i);
                System.out.print(columnValue + "   ");
            }
            System.out.println();
        }
        System.out.println("");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println();
    }

    
    //departments.txt  and  subjects.txt in root of project
    //Constructs the query of the form:
    //  
    //   INSERT INTO tableName(column1,column2,...) VALUES (PK, column1Val, column2Val,...);
    //    
    // from db DatabaseMetadata ( "getMetaData()" called on the Connection)
    public void insertData(String fileName, String tableName, String delimiter) {
        try {   
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement();

            //so that committing is done at the end of inserting values, not each insert
            conn.setAutoCommit(false);

            DatabaseMetaData meta = conn.getMetaData();
            
            //final expected form:  tableName(column1.name, column2.name,...)
            StringBuilder nameAndCols = new StringBuilder();
    
            //get table metadata
            ResultSet result = meta.getTables(null,null,"%",null);
            while(result.next())
            { 
              //when current metadata is for tableName
              //create the String of form:   tableName(column1.name, column2.name,...)
              if(tableName.equals(result.getString(3)))
              {
                nameAndCols.append(tableName + "( ");
                ResultSet columns = meta.getColumns(null,null,tableName,null);
     
                while(columns.next())
                {
                    String columnName = columns.getString(4);
                    nameAndCols.append(columnName);
                    nameAndCols.append(", ");
                }
                nameAndCols.deleteCharAt(nameAndCols.lastIndexOf(","));
                nameAndCols.append(")");
                System.out.println(nameAndCols.toString());
              }
            }
            System.out.println("inserting into " + tableName + " table");
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            int pk = 1;
            while ( (line=br.readLine()) != null)
            {
                StringBuilder valueListStr = new StringBuilder("");
                String[] values = line.split(delimiter);    //your seperator
                for(String str: values) {
                     valueListStr.append(str + ",");
                }
                valueListStr.deleteCharAt(valueListStr.lastIndexOf(","));
                //System.out.println(valueListStr.toString());
                String sql = "INSERT INTO " + nameAndCols.toString() +
                             "VALUES ("+ pk +"," + valueListStr.toString() + ");";
                pk += 1;
                stmt.executeUpdate(sql);  
            }
            br.close();
            stmt.close();
            conn.commit();
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("failed insert");
        }     
        System.out.println("insert success");
        System.out.println();
        System.out.println(); 
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

            StringBuilder builder = new StringBuilder();
    
            ResultSet result = meta.getTables(null,null,null,null);
            while(result.next())
            {
              String tableName = result.getString(3);
              //exclude from printed report
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

}
