package mwsu.csc346.KF.Scraper;

public class Section {

    String courseID;
    String department;
    String semester;
    String crn;
    String discipline;
    String courseNumber;
    String section;
    String type;
    String title;
    String hours;
    String days;
    String time;
    String room;
    String instructor;
    String maximumEnrollment;
    String seatsAvailable;
    String messages;
    String term;
    String courseFeesTitle;
    Double courseFeesAmount;
    String courseFeesType;
    String beginDate;
    String endDate;
    String url;


    //constructor
    public Section(){
        messages = "";//initialize message in order to concatenate

    }

    @Override
    public String toString() {
        return  courseID + "~" +
                department + "~" +
                crn + "~" +
                semester + "~" +
                discipline + "~" +
                courseNumber + "~" +
                section + "~" +
                type + "~" +
                title + "~" +
                hours + "~" +
                days + "~" +
                time + "~" +
                room + "~" +
                instructor + "~" +
                maximumEnrollment + "~" +
                seatsAvailable + "~" +
                messages + "~" +
                term + "~" +
                courseFeesTitle + "~" +
                courseFeesAmount + "~" +
                courseFeesType + "~" +
                beginDate + "~" +
                endDate + "~" +
                url + "~";
    }
}



