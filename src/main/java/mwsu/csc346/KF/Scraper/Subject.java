package mwsu.csc346.KF.Scraper;

public class Subject {

    String subjectID;
    String subjectName;

    @Override
    public String toString() {
        return subjectID + "~" +
                subjectName;
    }
}
