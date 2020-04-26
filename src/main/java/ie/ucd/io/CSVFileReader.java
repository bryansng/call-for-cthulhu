package ie.ucd.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
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
        return readProject(filename, allStaffMembers, null);
    }

    public ArrayList<Project> readProject(String filename, HashMap<String, StaffMember> allStaffMembers, File fromFile)
            throws Exception {
        CSVParser csvParser = getParser();
        CSVReader csvReader = null;
        ArrayList<Project> projects = new ArrayList<Project>();
        String[] line;
        try {
            if (fromFile == null) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
                csvReader = new CSVReaderBuilder(new InputStreamReader(is)).withCSVParser(csvParser).build();
            } else {
                csvReader = new CSVReaderBuilder(new FileReader(fromFile)).withCSVParser(csvParser).build();
            }
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
            assert line[2].equals("CS") || line[2].equals("CS+DS") || line[2].equals(
                    "DS") : "Unable to read Projects input file, expected values of third column to be either 'CS', 'DS', or 'CS+DS'.";
            assert line.length != 4
                    || line.length != 3 : "Unable to read Projects input file, expected 3 or 4 columns of values.";

            if (line.length == 4)
                projects.add(new Project(allStaffMembers.get(line[0]), line[1], line[2], Double.parseDouble(line[3])));
            else if (line.length == 3)
                projects.add(new Project(allStaffMembers.get(line[0]), line[1], line[2], Common.getProbability()));
        }
        return projects;
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects) throws Exception {
        return readStudents(filename, projects, null);
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects, File fromFile)
            throws IOException {
        CSVReader csvReader = null;
        CSVParser csvParser = getParser();
        ArrayList<Student> students = new ArrayList<Student>();
        String[] line;
        try {
            if (fromFile == null) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
                csvReader = new CSVReaderBuilder(new InputStreamReader(is)).withCSVParser(csvParser).build();
            } else {
                csvReader = new CSVReaderBuilder(new FileReader(fromFile)).withCSVParser(csvParser).build();
            }
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
            ArrayList<Project> thisStudentsPreference = new ArrayList<Project>();
            for (int i = 4; i < 14; i++) {
                for (Project project : projects) {
                    if (project.getResearchActivity().equals(line[i])) {
                        thisStudentsPreference.add(project);
                        break;
                    }
                }
            }
            if (thisStudentsPreference.size() != 10) {
                throw new InterruptedIOException(
                        "Preference list size must be equal to 10, but is %d, check if readStudents() is given the correct ArrayList projects tailored for these students.");
            }
            assert thisStudentsPreference.size() == 10 : String.format(
                    "Preference list size must be equal to 10, but is %d, check if readStudents() is given the correct ArrayList projects tailored for these students.",
                    thisStudentsPreference.size());
            // System.out.println(thisStudentsPreference.size() + " " + (thisStudentsPreference.size() == 10));
            // System.out.println("actual " + (thisStudentsPreference.size() < 10) + ", expected: false");
            // if (Settings.enableGPA)
            // students.add(
            //         new Student(line[0], line[1], Integer.parseInt(line[2]), line[3], line[4], thisStudentsPreference));
            // else
            students.add(
                    new Student(line[0], line[1], Integer.parseInt(line[2]), line[3], 0.0, thisStudentsPreference));
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
            csvReader = new CSVReaderBuilder(new InputStreamReader(is)).withSkipLines(1).withCSVParser(csvParser)
                    .build();
        } catch (Exception e) {
            System.out.println("error creating reader for input file: " + filename);
        }

        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            String researchActivities = line[1].replaceAll(inFieldSeparator, ",");
            String researchAreas = line[2].replaceAll(inFieldSeparator, ",");
            staffMembers.add(new StaffMember(line[0], researchActivities, researchAreas, line[3]));
        }
        return staffMembers;
    }

    private CSVParser getParser() {
        return new CSVParserBuilder().withSeparator(separator.charAt(0)).withIgnoreQuotations(ignoreQuotations).build();
    }
}
