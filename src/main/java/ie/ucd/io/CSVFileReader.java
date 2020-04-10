package ie.ucd.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVFileReader {
    private String separator;
    private boolean ignoreQuotations;
    private String inFieldSeparator;

    public CSVFileReader() {
        this.separator = ",";
        this.ignoreQuotations = false;
        this.inFieldSeparator = ";";
    }

    public CSVFileReader(String separator, boolean ignoreQuotations, String inFieldSeparator) {
        this.ignoreQuotations = ignoreQuotations;
        this.inFieldSeparator = inFieldSeparator;
        if (separator.equals("\t") || separator.equals(",") || separator.equals(" ") || separator.equals("/"))
            this.separator = separator;
        else
            System.out.println("Invalid separator. Default separator (comma) selected.");
    }

    public ArrayList<Project> readProject(String filename, HashMap<String, StaffMember> allStaffMembers)
            throws Exception {
        CSVParser csvParser = getParser();
        CSVReader csvReader = null;
        ArrayList<Project> projects = new ArrayList<Project>();
        String[] line;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        try {
            csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                    .withSkipLines(1)
                    .withCSVParser(csvParser)
                    .build();
            // csvReader = new CSVReader(new FileReader(filename));
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
        CSVParser csvParser = getParser();
        ArrayList<Student> students = new ArrayList<Student>();
        String[] line;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        try {
            csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                    .withSkipLines(1)
                    .withCSVParser(csvParser)
                    .build();
        } catch (Exception e) {
            System.out.println("error creating CSVReader object for input file: " + filename);
        }

        int flag = 0;
        // int aIndex = 0;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            if (flag == 0) {
                flag = 1;
                continue;
            }
            ArrayList<Project> thisStudentsProjects = new ArrayList<Project>();
            for (int i = 4; i < 14; i++) {
                for (Project project : projects) {
                    if (project.getResearchActivity().equals(line[i])) {
                        thisStudentsProjects.add(project);
                        break;
                    }
                }
            }
            assert thisStudentsProjects.size() == 10 : "Preference list size must be equal to 10, but is "
                    + thisStudentsProjects.size()
                    + ", check if readStudents() is given the correct ArrayList projects tailored for these students.";
            // System.out.println(thisStudentsProjects.size() < 10);
            students.add(new Student(line[0], line[1], Integer.parseInt(line[2]), line[3], 0.0, thisStudentsProjects));
            // System.out.println(aIndex + " " + students.get(students.size() - 1));
            // aIndex += 1;
        }
        return students;
    }

    public ArrayList<StaffMember> readStaffMembers(String filename) throws IOException {
        CSVParser csvParser = getParser();
        CSVReader csvReader = null;
        ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
        String[] line;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        try {
            csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                    .withSkipLines(1)
                    .withCSVParser(csvParser)
                    .build();
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
            String researchActivities = line[1].replaceAll(inFieldSeparator, ",");
            String researchAreas = line[2].replaceAll(inFieldSeparator, ",");
            staffMembers.add(new StaffMember(line[0], researchActivities, researchAreas, line[3]));
        }
        return staffMembers;
    }

    private CSVParser getParser() {
        return new CSVParserBuilder()
                .withSeparator(separator.charAt(0))
                .withIgnoreQuotations(ignoreQuotations)
                .build();

    }
}
