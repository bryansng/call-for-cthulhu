package ie.ucd.io;

import java.io.*;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;

import java.util.ArrayList;
import java.util.Arrays;

public class CSVFileWriter {
    private String separator = ",";
    private String newLine = "\n";

    public void writeProjects(ArrayList<Project> projects) throws IOException {
        writeProjects(projects, null);
    }

    public void writeProjects(ArrayList<Project> projects, File toFile) throws IOException {
        final String[] columns = { "Staff Name", "Research Activity", "Stream" };
        try {
            int numOfStudents = projects.size() / 3 * 2;

            // create file.
            File file;
            if (toFile == null) {
                file = new File("src/main/resources/CSVs/ProjectsCSV" + numOfStudents + ".csv");
                file.createNewFile();
            } else {
                file = toFile;
            }

            // create writer
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");

            // write header
            fileWriter.write(columns[0] + separator + columns[1] + separator + columns[2] + newLine);
            // write the details
            for (Project project : projects) {
                if (Common.DEBUG_IO_UNICODE) {
                    System.out.println("\nproject: " + project);
                    System.out.println("staffMember: " + project.getStaffMember());
                    System.out.println("staffMember proposedby: " + project.getStaffMember().getProposedBy());
                }
                fileWriter.write(project.getStaffMember().getProposedBy() + separator + project.getResearchActivity()
                        + separator + project.getStream() + newLine);
            }
            // close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeProjects");
            e.printStackTrace();
        }
    }

    public void writeStudents(ArrayList<Student> students) throws IOException {
        writeStudents(students, null);
    }

    public void writeStudents(ArrayList<Student> students, File toFile) throws IOException {
        ArrayList<String> columns = null;

        if (Settings.enableGPA)
            columns = new ArrayList<String>(Arrays.asList("First Name", "Last Name", "ID", "Stream", "GPA",
                    "Project Assigned", "Preference 1", "Preference 2", "Preference 3", "Preference 4", "Preference 5",
                    "Preference 6", "Preference 7", "Preference 8", "Preference 9", "Preference 10"));
        else
            columns = new ArrayList<String>(Arrays.asList("First Name", "Last Name", "ID", "Stream", "Project Assigned",
                    "Preference 1", "Preference 2", "Preference 3", "Preference 4", "Preference 5", "Preference 6",
                    "Preference 7", "Preference 8", "Preference 9", "Preference 10"));
        String line = "";
        try {
            File file;
            if (toFile == null) {
                file = new File("src/main/resources/CSVs/StudentsCSV" + students.size() + ".csv");
                file.createNewFile();
            } else {
                file = toFile;
            }

            // create writer
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");

            // write header
            for (int i = 0; i < columns.size(); i++) {
                line = line.concat(columns.get(i));
                if (i == columns.size() - 1)
                    line = line.concat(newLine);
                else
                    line = line.concat(separator);
            }
            fileWriter.write(line);

            // write the details
            int rowNum = 1;
            for (Student student : students) {
                line = "";
                line += student.getFirstName() + separator + student.getLastName() + separator + student.getId()
                        + separator + student.getStream()
                        + (Settings.enableGPA ? separator + student.getGpa() + separator : separator)
                        + student.getProject() + separator;
                for (int i = 0; i < student.getPreferenceList().size(); i++) {
                    line = line.concat(student.getPreferenceList().get(i).getResearchActivity());
                    if (i == student.getPreferenceList().size() - 1)
                        line = line.concat(newLine);
                    else
                        line = line.concat(separator);
                }
                fileWriter.write(line);
                rowNum++;
                if (rowNum == students.size() + 1)
                    break;
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeStudents");
        }
    }

    public void writeAnalysis(CandidateSolution solution) throws IOException {
        String[] columns = { "Research Activity", "Stream", "Preferred Percentage", "Percentage Distribution" };
        String line = "";
        try {
            //create file
            File file = new File("src/main/resources/CSVs/AnalysisCSV" + solution.getNumberOfStudents() + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(
                    columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            for (Project project : solution.getProjects()) {
                String analysis = solution
                        .formatPercentage(project.getNumStudentsAssigned() / solution.getTotalProjectsAssigned() * 100);
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
            File file = new File("src/main/resources/CSVs/StaffMembersCSV.csv");
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
