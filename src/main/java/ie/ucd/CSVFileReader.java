package ie.ucd;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CSVFileReader {
    private String separator;

    CSVFileReader() {
        this.separator = ",";
    }

    CSVFileReader(String separator) {
        if (separator.equals("\t") || separator.equals(",") || separator.equals(" "))
            this.separator = separator;
        else
            System.out.println("Invalid separator. Default separator (comma) selected.");
    }

    public ArrayList<Project> readProject(String filename) throws Exception {
        CSVReader csvReader = null;
        ArrayList<Project> projects = new ArrayList<Project>();
        String[] line;
        try {
            csvReader = new CSVReader(new FileReader(filename));
        } catch (Exception e) {
            System.out.println("error creating reader for input file: " + filename);
        }

        int flag = 0;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            if (flag == 0) {
                flag = 1;
                continue;
            }
            projects.add(new Project(line[0], line[1], line[2], Double.parseDouble(line[3])));
        }
        return projects;
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects) throws IOException {
        CSVReader csvReader = null;
        ArrayList<Student> students = new ArrayList<Student>();
        String[] line;
        try {
            csvReader = new CSVReader(new FileReader(filename));
        } catch (Exception e) {
            System.out.println("error creating CSVReader object for input file: " + filename);
        }

        int flag = 0;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            if (flag == 0) {
                flag = 1;
                continue;
            }
            ArrayList<Project> thisStudentsProjects = new ArrayList<Project>();
            for (int i = 4; i < 14; i++) {
                for (Project project : projects) {
                    if (project.getResearchActivity().equals(line[i]))
                        thisStudentsProjects.add(project);
                }
            }
            students.add(new Student(line[0], line[1], Integer.parseInt(line[2]), line[3], thisStudentsProjects));
        }
        return students;
    }
}


