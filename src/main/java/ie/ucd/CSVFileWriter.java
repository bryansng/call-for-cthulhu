package ie.ucd;

import java.io.*;

import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CSVFileWriter {
    String separator = ",";
    String newLine = "\n";

    public void writeProjects(ArrayList<Project> projects, int numOfStudents) throws IOException {
        final String[] columns = {"Staff Name", "Research Activity", "Stream", "Preferred Probability"};
        try {
            //create file
            File file = new File("ProjectsCSV" + numOfStudents + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            for (Project project : projects) {
                fileWriter.write(project.getProposedBy() + separator + project.getResearchActivity()
                        + separator + project.getStream() + separator + project.getPreferredProbability() + newLine);
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeProjects");
        }
    }

    public void writeStudents(HashMap<Integer, Student> students) throws IOException {
        final String[] columns = {"First Name", "Last Name", "ID", "Stream", "Preference 1", "Preference 2", "Preference 3",
                "Preference 4", "Preference 5", "Preference 6", "Preference 7", "Preference 8", "Preference 9",
                "Preference 10"};
        Collection<Student> studentCollection = students.values();
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
            for (Student student : studentCollection) {
                line = "";
                line += student.getFirstName() + separator + student.getLastName() + separator
                        + student.getId() + separator + student.getStream() + separator;
                for (int i = 0; i < student.preferenceList.size(); i++) {
                    line = line.concat(student.preferenceList.get(i).getResearchActivity());
                    if (i == student.preferenceList.size() - 1)
                        line = line.concat(newLine);
                    else
                        line = line.concat(separator);
                }
                fileWriter.write(line);
                rowNum++;
                if (rowNum == students.size()) break;
            }
            //close writer
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("I/O error in CSVFileWriter.writeStudents");
        }
    }

    public void writeAnalysis(Parser parser) throws IOException {
        String[] columns = {"Research Activity", "Stream", "PDF", "Percentage Distribution"};
        String line = "";
        try {
            //create file
            File file = new File("AnalysisCSV" + parser.numberOfStudents + ".csv");
            file.createNewFile();
            //create writer
            FileWriter fileWriter = new FileWriter(file);
            //write header
            fileWriter.write(columns[0] + separator + columns[1] + separator + columns[2] + separator + columns[3] + newLine);
            //write the details
            for (Project project : parser.someStaffsProjects) {
                String analysis = parser.formatPercentage(project.getNumStudentsAssigned() / parser.totalProjectsAssigned * 100);
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
}
