package ie.ucd.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.exceptions.InadequatePreferenceListSizeException;
import ie.ucd.exceptions.MissingFieldsException;
import ie.ucd.exceptions.SimilarStudentIDsException;
import ie.ucd.exceptions.UnexpectedStreamException;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
            throws MissingFieldsException, UnexpectedStreamException, UnsupportedEncodingException,
            FileNotFoundException, IOException {
        return readProject(filename, allStaffMembers, null);
    }

    public ArrayList<Project> readProject(String filename, HashMap<String, StaffMember> allStaffMembers, File fromFile)
            throws MissingFieldsException, UnexpectedStreamException, UnsupportedEncodingException,
            FileNotFoundException, IOException {
        CSVParser csvParser = getParser();
        CSVReader csvReader = null;
        ArrayList<Project> projects = new ArrayList<Project>();
        String[] line;
        if (fromFile == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            csvReader = new CSVReaderBuilder(new InputStreamReader(is)).withCSVParser(csvParser).build();
        } else {
            csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(fromFile), "UTF-8"))
                    .withCSVParser(csvParser).build();
            // csvReader = new CSVReaderBuilder(new FileReader(fromFile)).withCSVParser(csvParser).build();
        }

        int flag = 0;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            if (flag == 0) {
                flag = 1;
                continue;
            }

            if (Common.DEBUG_IO_UNICODE && line[0].contains("Pep"))
                System.out.println(line[0] + ": " + allStaffMembers.get(line[0]));

            if (!(line[2].equals("CS") || line[2].equals("CS+DS") || line[2].equals("DS"))) {
                throw new UnexpectedStreamException(
                        "Error occurred while reading projects. Expected stream info to be either 'CS', 'CS+DS' or 'DS'.");
            }

            if (!(line.length != 4 || line.length != 3)
                    || !(!line[0].equals("") && !line[1].equals("") && !line[2].equals(""))) {
                // System.out.println("stuff empty");
                throw new MissingFieldsException(
                        "Error occurred while reading Projects. Missing fields exception raised.");
            }

            if (line.length == 4)
                projects.add(new Project(allStaffMembers.get(line[0]), line[1], line[2], Double.parseDouble(line[3])));
            else if (line.length == 3)
                projects.add(new Project(allStaffMembers.get(line[0]), line[1], line[2], Common.getProbability()));
        }
        return projects;
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects)
            throws UnsupportedEncodingException, FileNotFoundException, IOException,
            InadequatePreferenceListSizeException, SimilarStudentIDsException, MissingFieldsException,
            UnexpectedStreamException, NumberFormatException {
        return readStudents(filename, projects, null);
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects, File fromFile)
            throws UnsupportedEncodingException, FileNotFoundException, IOException,
            InadequatePreferenceListSizeException, SimilarStudentIDsException, MissingFieldsException,
            UnexpectedStreamException, NumberFormatException {
        CSVReader csvReader = null;
        CSVParser csvParser = getParser();
        ArrayList<Student> students = new ArrayList<Student>();
        HashSet<Integer> studentIDs = new HashSet<>();

        // for dummy staff members.
        ArrayList<StaffMember> dummyStaffMembers = new ArrayList<>();
        ArrayList<Project> dummyProjects = new ArrayList<>();
        HashMap<String, StaffMember> existingStaffMembers = new HashMap<>();
        HashMap<String, Project> existingProjects = new HashMap<>();

        String[] line;
        if (fromFile == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            csvReader = new CSVReaderBuilder(new InputStreamReader(is)).withCSVParser(csvParser).build();
        } else {
            csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(fromFile), "UTF-8"))
                    .withCSVParser(csvParser).build();
        }

        int flag = 0;
        // int aIndex = 0;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            if (flag == 0) {
                flag = 1;
                continue;
            }

            String firstName;
            String lastName;
            Integer studentID;
            String stream;
            Double gpa = (Settings.enableGPA && isGPA(line[4]))
                    ? (line[4].equals("null")) ? null : Double.parseDouble(line[4])
                    : null;

            try {
                firstName = line[0];
                lastName = line[1];
                studentID = Integer.parseInt(line[2]);
                stream = line[3];
            } catch (IndexOutOfBoundsException e) {
                throw new MissingFieldsException(
                        "Error occurred while reading Students. Missing fields exception raised.");
            } catch (NumberFormatException e) {
                throw new NumberFormatException(
                        "Error occurred while reading Students. Expected student id to be an integer number.");
            }

            if (studentIDs.contains(studentID)) {
                throw new SimilarStudentIDsException(
                        "Error occurred while reading Students. Expected students to have unique IDs.");
            }
            studentIDs.add(studentID);

            if (!(stream.equals("CS") || stream.equals("DS"))) {
                throw new UnexpectedStreamException(
                        "Error occurred while reading Students. Expected stream info to be either 'CS' or 'DS'.");
            }

            ArrayList<Project> thisStudentsPreference = new ArrayList<Project>();
            boolean hasProjectAssigned = false;
            String projectAssignedStr = null;
            Project projectAssigned = null;
            int start, end;
            if (Settings.enableGPA && isGPA(line[4])) {
                // if have column for project assigned.
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
                // if have column for project assigned.
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

            // if projects not loaded/generated.
            if (!Common.isProjectsPopulated) {
                // if is ProjectAssigned.
                if (projectAssigned == null && hasProjectAssigned && projectAssignedStr != null) {
                    Project project = null;

                    // update maps.
                    // if already exist, we just fetch it from the projects map.
                    if (existingStaffMembers.containsKey(projectAssignedStr)
                            && existingProjects.containsKey(projectAssignedStr)) {
                        project = existingProjects.get(projectAssignedStr);
                    } else {
                        StaffMember sm = new StaffMember(Integer.toString(existingStaffMembers.size()),
                                projectAssignedStr, "", stream, true);
                        project = sm.getProject();

                        dummyStaffMembers.add(sm);
                        dummyProjects.add(project);
                        existingStaffMembers.put(projectAssignedStr, sm);
                        existingProjects.put(projectAssignedStr, project);
                    }
                    projectAssigned = project;
                }

                // find actual Project objects from the strings.
                for (int curr = start; curr < end; curr++) {
                    String projectStr = line[curr].trim();
                    Project project = null;

                    // update maps.
                    // if already exist, we just fetch it from the projects map.
                    if (existingStaffMembers.containsKey(projectStr) && existingProjects.containsKey(projectStr)) {
                        project = existingProjects.get(projectStr);
                    } else {
                        StaffMember sm = new StaffMember(Integer.toString(existingStaffMembers.size()), projectStr, "",
                                stream, true);
                        project = sm.getProject();

                        dummyStaffMembers.add(sm);
                        dummyProjects.add(project);
                        existingStaffMembers.put(projectStr, sm);
                        existingProjects.put(projectStr, project);
                    }

                    // if is Project object of preference list.
                    thisStudentsPreference.add(project);
                }
            } else {
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
            }

            if (thisStudentsPreference.size() != 10) {
                throw new InadequatePreferenceListSizeException(
                        "Preference list size must be equal to 10, but is %d, check if readStudents() is given the correct ArrayList projects tailored for these students.");
            }
            // System.out.println(thisStudentsPreference.size() + " " + (thisStudentsPreference.size() == 10));
            // System.out.println("actual " + (thisStudentsPreference.size() < 10) + ", expected: false");

            if (hasProjectAssigned && projectAssigned != null) {
                students.add(new Student(firstName, lastName, studentID, stream, gpa, projectAssigned,
                        thisStudentsPreference));
            } else {
                students.add(new Student(firstName, lastName, studentID, stream, gpa, thisStudentsPreference));
            }
            // System.out.println(aIndex + " " + students.get(students.size() - 1));
            // aIndex += 1;
        }
        if (!Common.isProjectsPopulated) {
            Settings.dummyStaffMembers = dummyStaffMembers;
            Settings.loadedProjects = dummyProjects;
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
