package ie.ucd.io;

import com.opencsv.CSVReader;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVFileReader {
    private String separator;

    public CSVFileReader() {
        this.separator = ",";
    }

    public CSVFileReader(String separator) {
        if (separator.equals("\t") || separator.equals(",") || separator.equals(" "))
            this.separator = separator;
        else
            System.out.println("Invalid separator. Default separator (comma) selected.");
    }

    public ArrayList<Project> readProject(String filename, HashMap<String, StaffMember> allStaffMembers)
            throws Exception {
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
            projects.add(new Project(allStaffMembers.get(line[0]), line[1], line[2], Double.parseDouble(line[3])));
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
            students.add(new Student(line[0], line[1], Integer.parseInt(line[2]), line[3], 0.0, thisStudentsProjects));
        }
        return students;
    }

    public ArrayList<StaffMember> readStaffMembers(String filename) throws IOException {
        CSVReader csvReader = null;
        ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
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
            String researchActivities = line[1].replaceAll(";", ",");
            String researchAreas = line[2].replaceAll(";", ",");
            staffMembers.add(new StaffMember(line[0], researchActivities, researchAreas, line[3]));
        }
        return staffMembers;
    }
}
