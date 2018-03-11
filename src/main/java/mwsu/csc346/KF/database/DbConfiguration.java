package mwsu.csc346.KF.database;

import java.util.HashMap;
import java.util.Map;


/*
 *  Globals class container.
 *  specify the dbfileName, any table or db schema based scripts individually as strings
 *            and prop up scripts as Map<(String)scriptName, (String)scriptSqlString>
 */
public class DbConfiguration {

    public static final String dbfileName = "ScheduleData.db";

    public static final String createDepartmentsTable = "CREATE TABLE Departments " +
                                "(DepartmentID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                " abbrev TEXT NOT NULL, " +
                                " fullname TEXT NOT NULL) ";

    public static final String createSubjectsTable = "CREATE TABLE Subjects " +
                                "(SubjectID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                " abbrev TEXT NOT NULL, " +
                                " fullname TEXT NOT NULL) ";

    public static final String createSectionsTable = "CREATE TABLE Sections " +
                                "(SectionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                " CourseID TEXT NOT NULL, " +
                                " Department TEXT NOT NULL, " +
                                " crn TEXT NOT NULL, " +
                                " Semester TEXT NOT NULL"
                                " Discipline TEXT NOT NULL, " +
                                " CourseNumber TEXT NOT NULL, " +
                                " Section TEXT, " +
                                " Type TEXT, " +
                                " Title TEXT, " +
                                " Hours TEXT, " +
                                " Days TEXT, " +
                                " Location TEXT, " +
                                " Instructor TEXT, "+
                                " MaxEnrollments TEXT, " +
                                " AvailSeats TEXT, " +
                                " Messages TEXT, " +
                                " Term TEXT,"+
                                " FeeTitle TEXT,"
                                " TotalFees REAL, " +
                                " PerCourseOrPerCredit TEXT, " +
                                " StartDate TEXT, " +
                                " EndDate TEXT) ";

       public static final Map iscripts = createInitMap();
       private static Map<String, String> createInitMap()
       {
           Map<String, String> map = new HashMap<String, String>();
           map.put("createDepartmentsTable", createDepartmentsTable);
           map.put("createSubjectsTable", createSubjectsTable);
           map.put("createSectionsTable", createSectionsTable);
           return map;
       }
}
