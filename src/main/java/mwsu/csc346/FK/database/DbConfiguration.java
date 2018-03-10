package mwsu.csc346.FK;

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
                                " Department TEXT NOT NULL, " +
                                " CRN INTEGER NOT NULL, " +
                                " CourseCode TEXT NOT NULL, " +
                                " CourseUrl TEXT, " +
                                " SectionNum INTEGER NOT NULL, " +
                                " ClassType TEXT NOT NULL, " +
                                " CreditHours INTEGER NOT NULL, " +
                                " WeekDays TEXT NOT NULL, " +
                                " Times TEXT NOT NULL, " +
                                " Location TEXT NOT NULL, " +
                                " Instructor TEXT NOT NULL, " +
                                " MaxEnrollments INTEGER NOT NULL, " +
                                " AvailSeats INTEGER NOT NULL, " +
                                " Details TEXT, " +
                                " Messages TEXT, " +
                                " TotalFees REAL NOT NULL, " +
                                " FeeTitles TEXT NOT NULL, " +
                                " PerCourseOrPerCredit TEXT NOT NULL, " +
                                " CourseTerm TEXT NOT NULL, " +
                                " StartDate DATE NOT NULL, " +
                                " EndDate DATE NOT NULL) ";   

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
