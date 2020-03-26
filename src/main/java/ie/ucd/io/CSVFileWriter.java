package ie.ucd.io;

import java.io.*;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFileWriter {
    private String separator = ",";
    private String newLine = "\n";

    public void writeProjects(ArrayList<Project> projects, int numOfStudents) throws IOException {
        final String[] columns = { "Staff Name", "Research Activity", "Stream", "Preferred Probability" };
        try {
            //create file
            File file = new File("ProjectsCSV" + numOfStudents + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(
                    columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            for (Project project : projects) {
                fileWriter.write(project.getStaffMember().getProposedBy() + separator + project.getResearchActivity()
                        + separator + project.getStream() + separator + project.getPreferredProbability() + newLine);
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeProjects");
        }
    }

    public void writeStudents(ArrayList<Student> students) throws IOException {
        final String[] columns = { "First Name", "Last Name", "ID", "Stream", "Preference 1", "Preference 2",
                "Preference 3", "Preference 4", "Preference 5", "Preference 6", "Preference 7", "Preference 8",
                "Preference 9", "Preference 10" };
        String line = "";
        try {
            File file = new File("StudentsCSV" + students.size() + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);

            //write header
            for (int i = 0; i < columns.length; i++) {
                line = line.concat(columns[i]);
                if (i == columns.length - 1)
                    line = line.concat(newLine);
                else
                    line = line.concat(separator);
            }
            fileWriter.write(line);

            //write the details
            int rowNum = 1;
            for (Student student : students) {
                line = "";
                line += student.getFirstName() + separator + student.getLastName() + separator + student.getId()
                        + separator + student.getStream() + separator;
                for (int i = 0; i < student.getPreferenceList().size(); i++) {
                    line = line.concat(student.getPreferenceList().get(i).getResearchActivity());
                    if (i == student.getPreferenceList().size() - 1)
                        line = line.concat(newLine);
                    else
                        line = line.concat(separator);
                }
                fileWriter.write(line);
                rowNum++;
                if (rowNum == students.size())
                    break;
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeStudents");
        }
    }

    public void writeAnalysis(Parser parser) throws IOException {
        String[] columns = { "Research Activity", "Stream", "Preferred Percentage", "Percentage Distribution" };
        String line = "";
        try {
            //create file
            File file = new File("AnalysisCSV" + parser.numberOfStudents + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(
                    columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            for (Project project : parser.someStaffsProjects) {
                String analysis = parser
                        .formatPercentage(project.getNumStudentsAssigned() / parser.totalProjectsAssigned * 100);
                line = project.getResearchActivity() + separator + project.getStream() + separator
                        + project.getPreferredProbability() + separator + analysis + newLine;
                fileWriter.write(line);
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeAnalysis");
        }
    }

    public void writeStaffMembers(ArrayList<StaffMember> staffMembers) {
        String[] columns = { "Staff Member", "Research Activity", "Research Area", "Special Focus" };
        String line = "";
        try {
            //create file
            File file = new File("StaffMembersCSV.csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(
                    columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            int skipFirst = 0;
            for (StaffMember staffMember : staffMembers) {
                if (skipFirst == 0) {
                    skipFirst++;
                    continue;
                }
                String[] researchActivities = staffMember.getResearchActivities();
                String[] researchAreas = staffMember.getResearchAreas();

                //build row to write to file
                line = staffMember.getProposedBy() + separator;
                int flag = 0;
                for (String researchActivity : researchActivities) {
                    if (flag == researchActivities.length - 1) {
                        line += researchActivity + separator;
                    } else {
                        line += researchActivity + ";";
                        flag++;
                    }
                }
                flag = 0;
                for (String researchArea : researchAreas) {
                    if (flag == researchAreas.length - 1) {
                        line += researchArea + separator;
                    } else {
                        line += researchArea + ";";
                        flag++;
                    }
                }
                line += staffMember.getStream() + newLine;

                //write line to file
                fileWriter.write(line);
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeStaffMembers");
        }
    }
}
