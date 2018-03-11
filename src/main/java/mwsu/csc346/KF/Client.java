package mwsu.csc346.KF;

import mwsu.csc346.KF.database.DB;
import mwsu.csc346.KF.database.DbConfiguration;

import java.util.Scanner;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Client {

    private static DB TheDB;

    private static Map<String, String> menu;
    private static Map<String, Runnable> commands;

    public Client() {
        TheDB = new DB();
        menu = new HashMap<>();
        commands = new HashMap<>();

        this.init();
        this.run();
    }
    
    public void run() {
        while(true) {
            printMenu();
            readAndRun();
        }
    }

    public static void printMenu() {
        System.out.println("==========================================");
        System.out.println("                   MENU                   ");
        System.out.println("==========================================");
        for(String x: menu.keySet()) {
            System.out.print(x);
            System.out.print(" : ");
            System.out.println(menu.get(x).toString());
        }
        System.out.println("==========================================");
    }

    public static void readAndRun() {
        String input;
        Scanner scan = new Scanner(System.in);

        System.out.println("RUN:?");
        input = scan.nextLine().toString().toLowerCase();

        if(menu.keySet().contains(input)) commands.get(input).run();
    }


    
    public void init() {

        // Populate menu descriptions
        menu.put("a", "Erase and Build Subjects Table");
        menu.put("b", "Erase and Build Departments Table");
        menu.put("c", "Print Subjects Table");
        menu.put("d", "Print Departments Table");
      /*menu.put("e", "Print disciplinesByDepartment Report");
        menu.put("g", "Erase and Build Sections Table (promptDepartments)");
        menu.put("h", "Print sectionsByDeptOrDiscipline Report");
        menu.put("i", "Print facultyAndScheduleByDepartment Report");
        menu.put("j", "Print ctrlBreakSectionsForDept Report");
        menu.put("k", "Print theControlBreakReport");
      */menu.put("q", "Quit");
       

        // Populate (commands menu) options and functions
        commands.put("a", () -> eraseAndBuildTable("Subjects", ""));
        commands.put("b", () -> eraseAndBuildTable("Departments",""));
        commands.put("c", () -> printTable("Subjects"));
        commands.put("d", () -> printTable("Departments"));
      /*commands.put("e", () -> printReport("disciplinesByDepartment"));
        commands.put("g", () -> eraseAndBuildTable("Sections","promptDepartment"));
        commands.put("h", () -> printReport("sectionsByDeptOrDiscipline"));
        commands.put("i", () -> printReport("facultyAndScheduleByDepartment"));
        commands.put("j", () -> printReport("ctrlBreakSectionsForDept"));
        commands.put("k", () -> printReport("theControlBreakReport"));
      */commands.put("q", () -> System.exit(0));
       
    }


    public static void printTable(String tableName) {
        TheDB.runQuery("SELECT * FROM " + tableName);
    }


    public static void eraseAndBuildTable(String tableName, String option) {
        String scriptName = "create" + tableName + "Table";
        String txtFileName = tableName.toLowerCase() + ".txt";

        TheDB.runQuery("DROP TABLE IF EXISTS " + tableName);
        TheDB.runQuery(DbConfiguration.iscripts.get(scriptName).toString());
        TheDB.insertData(txtFileName, tableName, "~");
    }


    public static void main(String[] args) {        
        Client client = new Client();
    }
}
