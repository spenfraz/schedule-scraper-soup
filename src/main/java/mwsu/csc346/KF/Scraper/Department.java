package mwsu.csc346.KF.Scraper;

public class Department {

    String departmentID;
    String departmentName;


    @Override
    public String toString() {
        return
                departmentID + "|" +
                        departmentName ;
    }
}
