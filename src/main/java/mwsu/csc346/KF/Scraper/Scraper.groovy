package mwsu.csc346.KF.Scraper;

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class Scraper {

    static def getSections(String semester, PrintWriter writer) {

        ArrayList<Section> sections = new ArrayList<>();

        Section sec = null;

        ArrayList<String> departmentsList = new ArrayList<String>(Arrays.asList("AF","ART","BIO","BUS","CHE","CST","CSMP","CJLS","EPSS","EDU","ET","EFLJ","GS","HPER","HPG","HON","MIL","MUS","NUR","PSY","FINE","CON"));
        String department;

        for(String element : departmentsList) {
            department = element;

            def baseURL = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"

            Document doc = null;

            Connection.Response response = Jsoup.connect(baseURL)
                    .timeout(60 * 1000)
                    .method(Connection.Method.POST)
                    .data("course_number", "")
                    .data("subject", "ALL")
                    .data("department", department)
                    .data("display_closed", "yes")
                    .data("course_type", "ALL")
                    .followRedirects(true)
                    .execute();
            doc = response.parse()

          //  System.out.println(doc)

            Elements rows = doc.select("tr")
            println("There are ${rows.size()} rows")

            if (rows.size() > 4) {
                rows.each { row ->
                    def className = row.attr("class")

                    Elements cells = row.select("td")
                    if (className == "list_row") {
                        if (sec != null) {
                         //   println "Section: $sec"   //Add to database here!
                            writer.println(sec)
                            writer.flush()
                        }
                        def cellCount = cells.size()
                        switch (cellCount) {
                            case 10:
                                //full row
                                sec = new Section()
                                //Going to be some code here
                                sec.department = department
                                sec.crn = cells.get(0).text().trim();  //works in Java or groovy

                                sec.courseID = cells[1].text().trim()  //only works in groovy

                                def s = cells.get(1).text().trim()
                                //println "Course is  $s "

                                sec.discipline = s.take(3)
                                sec.courseNumber = s.drop(3)
                              //  println("Dicsipline is  ${sec.discipline} ${sec.courseNumber} ")

                                sec.instructor = cells[9].text().trim();
                                //println("Instructor  ${sec.instructor} ")

                                sec.room = cells[8].text().trim()

                                sec.section = cells[2].text().trim()
                                //println "Section: ${sec.section}"

                                sec.type = cells[3].text().trim()
                                //println "Type: ${sec.type}"

                                sec.title = cells[4].text().trim()
                                //println "Title: ${sec.title}"

                                sec.hours = cells[5].text().trim()
                                //println "Hours: ${sec.hours}"

                                sec.days = cells[6].text().trim()
                                //println "Days: ${sec.days}"

                                sec.time = cells[7].text().trim()
                                //println "Time: ${sec.time}"

                                sec.semester = semester


                                break;
                            case 5:
                                sec.room += "|" + cells[3].text().trim()
                                sec.time += "|" + cells[2].text().trim()
                                sec.days += "|" + cells[1].text().trim()
                                break;

                            default:
                                println "DID NOT HANDLE ROWS WITH $cellCount cells"
                                System.exit(1)

                        }//end of switch
                    } else if (className == "detail_row") {
                        sec.messages = ""
                        sec.term = ""
                        sec.courseFeesType = ""
                        sec.courseFeesAmount = 0
                        sec.courseFeesTitle = ""

                        Elements tags = row.select("*")
                        tags.each { tag ->
                            className = tag.attr('class').toString()
                            switch (className) {
                                case "course_messages":
                                    sec.messages += tag.text().trim()
                                    break;
                                case "course_ends":
                                    def s = tag.text()
                                    sec.endDate = s.drop("Course Ends: ".length())
                                    sec.endDate = sec.endDate.trim()
                                  //  println "End Date: ${sec.endDate}"
                                    break;
                                case "course_begins":
                                    def s = tag.text()
                                    sec.beginDate = s.drop("Course Begins: ".length())
                                    sec.beginDate = sec.beginDate.trim()
                                    //println "Begin Date: ${sec.beginDate}"
                                    break;
                                case "course_seats":
                                    def contents = tag.text()
                                    contents.replace("<br />", " ")
                                    String[] parts = contents.split(/\s+/)
                                    if (parts.length > 6) {
                                        def max = parts[2]
                                        def available = parts[6];
                                        sec.maximumEnrollment = Integer.parseInt(max)
                                        sec.seatsAvailable = Integer.parseInt(available);
                                      //  println "Seats ${sec.maximumEnrollment} and ${sec.seatsAvailable}"
                                    }
                                    break;
                                case "course_term":
                                    sec.term = tag.text().trim()
                                  //  println "Term: ${sec.term}"
                                    break;
                                case "course_fees"://****************************** NEEDS WORK **********************//

                                    def contents = tag.text()
                                    contents.replace("&nbsp;", " ")
                                    //println "Contents: $contents"
                                    String[] parts = contents.split(/\s\d/,)

                                    def part1 = parts[0]
                                    def part2 = parts[1];

                                  //  println "Part 1: $part1 and Part 2: $part2"

                                    String[] moreParts = part2.split(/\s/)

                                    def part4 = moreParts[0]
                                    def part5 = moreParts[1]

                                    //println "Part 3 NOW: $part4 and Last Part $part5"
                                    def fees = 0
                                    def type = ""
                                    if (part5.contains("CRED")) {
                                        def amount = part4.toDouble()
                                        def hours = (sec.hours).toDouble()
                                       // println hours
                                        def total = amount * hours
                                        fees += total
                                        type = "PER CREDIT "
                                    } else {
                                        fees += part4.toDouble()
                                        type += "PER COURSE "
                                    }
                                    sec.courseFeesTitle += part1
                                    sec.courseFeesAmount += fees
                                    sec.courseFeesType += type

                                    break;
                            }//end of switch

                        }//end tags

                    } else {
                        // println "UNKNOWN ROW CLASS $className"
                        //System.exit(1)
                    }
                    //println "***********Printing"
                    println sec
                    if (sec != null) {
                    //    println sec
                        //   sections.add(sec)//adding section record to arraylist
                        //  writer.println(sec)
                    }
                }

            }//end of if
            //NOTE: def sql = Sql.newInstance("jdbc:sqlite:schedule
        }
        writer.close()

    }//end of getSections
    static def getDepartments(PrintWriter writer) {

        println "Getting all departments"

        Department depart = null;

        def baseURL = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"

        Document doc = null;

        Connection.Response response = Jsoup.connect(baseURL)
                .timeout(60 * 1000)
                .followRedirects(true)
                .execute();
        doc = response.parse()

  //      System.out.println(doc)

        Elements rows = doc.select("select")

        println("There are ${rows.size()} rows")
        def idName = rows.attr("id");

        rows.each { row ->
            Elements tags = row.select("option")
       //     println("There are ${tags.size()} options")

            // Elements tags = row.select("option")
            if ( (tags.size()) > 10 && (tags.size()) <= 50) {
                tags.each { tag ->
                    //  idName = tag.attr('value').toString()
                    depart = new Department()
                    if(tag.attr("value") != "ALL" ) {

                        depart.departmentName = tag.text().trim()
                        depart.departmentID = tag.attr("value")

                      //  println("DepartmentName = ${depart.departmentName} and department ID: ${depart.departmentID}")
                        writer.println(depart)

                    }
                }
            }
        }
        writer.close()
    }//end of getDepartments

    static def getSubjects(PrintWriter writer) {

        //    PrintWriter output = new PrintWriter(new File("subjects.txt"));

        println "Getting all subjects"

        Subject sub = null;

        def baseURL = "https://aps2.missouriwestern.edu/schedule/Default.asp?tck=201910"

        Document doc = null;

        Connection.Response response = Jsoup.connect(baseURL)
                .timeout(60 * 1000)
                .followRedirects(true)
                .execute();
        doc = response.parse()

    //    System.out.println(doc)

        Elements rows = doc.select("select")

   //     println("There are ${rows.size()} rows")
        def idName = rows.attr("id");

        rows.each { row ->
            Elements tags = row.select("option")
     //       println("There are ${tags.size()} options")

            // Elements tags = row.select("option")
            if ( (tags.size()) > 50) {
                tags.each { tag ->
                    //  idName = tag.attr('value').toString()
                    sub = new Subject()
                    if(tag.attr("value") != "ALL" ) {

                        sub.subjectName = tag.text().trim()
                        sub.subjectID = tag.attr("value")

       //                 println("SubjectName = ${sub.subjectName} and department ID: ${sub.subjectID}")
                        writer.println(sub);


                    }
                }

            }
        }
        writer.close()

    }//end of getSubjects

}
