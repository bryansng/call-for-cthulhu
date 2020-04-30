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
import java.io.FileInputStream;
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
                csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(fromFile), "UTF-8"))
                        .withCSVParser(csvParser).build();
                // csvReader = new CSVReaderBuilder(new FileReader(fromFile)).withCSVParser(csvParser).build();
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

            assert !line[0].equals("") && !line[1].equals("") && !line[2]
                    .equals("") : "Unable to read Projects input file, expected column values not empty or null.";

            if (Common.DEBUG_IO_UNICODE && line[0].contains("Pep"))
                System.out.println(line[0] + ": " + allStaffMembers.get(line[0]));

            if (!(line[2].equals("CS") || line[2].equals("CS+DS") || line[2].equals("DS"))
                    || !(line.length != 4 || line.length != 3)
                    || !(!line[0].equals("") && !line[1].equals("") && !line[2].equals(""))) {
                // System.out.println("stuff empty");
                throw new InterruptedIOException("Unable to read Projects input file.");
            }

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
                csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(fromFile), "UTF-8"))
                        .withCSVParser(csvParser).build();
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
            boolean hasProjectAssigned = false;
            String projectAssignedStr = null;
            Project projectAssigned = null;
            int start, end;
            if (Settings.enableGPA && isGPA(line[4])) {
                // if has column for project assigned.
                if (line.length == 16) {
                    start = 6;
                    end = 16;
                    hasProjectAssigned = true;
                    projectAssignedStr = line[5];
                } else {
                    start = 5;
                    end = 15;
                }
            } else {
                // if has column for project assigned.
                if (line.length == 15) {
                    start = 5;
                    end = 15;
                    hasProjectAssigned = true;
                    projectAssignedStr = line[4];
                } else {
                    start = 4;
                    end = 14;
                }
            }

            // find actual Project objects from the strings.
            for (int curr = start; curr < end; curr++) {
                for (Project project : projects) {
                    // if is ProjectAssigned.
                    if (projectAssigned == null && hasProjectAssigned && projectAssignedStr != null
                            && project.getResearchActivity().equals(projectAssignedStr)) {
                        projectAssigned = project;
                    }

                    // if is Project object of preference list.
                    if (project.getResearchActivity().equals(line[curr])) {
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
            if (hasProjectAssigned && projectAssigned != null) {
                students.add(new Student(line[0], line[1], Integer.parseInt(line[2]), line[3],
                        (Settings.enableGPA && isGPA(line[4]))
                                ? (line[4].equals("null")) ? null : Double.parseDouble(line[4])
                                : null,
                        projectAssigned, thisStudentsPreference));
            } else {
                students.add(new Student(line[0], line[1], Integer.parseInt(line[2]), line[3],
                        (Settings.enableGPA && isGPA(line[4]))
                                ? (line[4].equals("null")) ? null : Double.parseDouble(line[4])
                                : null,
                        thisStudentsPreference));
            }
            // System.out.println(aIndex + " " + students.get(students.size() - 1));
            // aIndex += 1;
        }
        return students;
    }

    // is GPA if it is a double, and that double is within 0.0 to 4.2.
    private boolean isGPA(String strVal) {
        if (strVal.equals("null"))
            return true;

        try {
            Double val = Double.parseDouble(strVal);
            if (val >= 0.0 && val <= 4.2) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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
