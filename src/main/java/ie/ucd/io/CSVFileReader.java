package ie.ucd.io;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.exceptions.EmptyPreferenceListException;
import ie.ucd.exceptions.EmptyResearchActivityException;
import ie.ucd.exceptions.InadequatePreferenceListSizeException;
import ie.ucd.exceptions.MissingFieldsException;
import ie.ucd.exceptions.SimilarStudentIDsException;
import ie.ucd.exceptions.UnexpectedStreamException;
import ie.ucd.exceptions.UnknownStaffMemberNameException;
import ie.ucd.exceptions.UnsuitableColumnHeadersException;
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
import java.nio.charset.StandardCharsets;
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
            FileNotFoundException, IOException, UnknownStaffMemberNameException, EmptyResearchActivityException {
        return readProject(filename, allStaffMembers, null);
    }

    public ArrayList<Project> readProject(String filename, HashMap<String, StaffMember> allStaffMembers, File fromFile)
            throws MissingFieldsException, UnexpectedStreamException, UnsupportedEncodingException,
            FileNotFoundException, IOException, UnknownStaffMemberNameException, EmptyResearchActivityException {
        CSVParser csvParser = getParser();
        CSVReader csvReader = null;
        ArrayList<Project> projects = new ArrayList<Project>();

        // for dummy staff members.
        ArrayList<StaffMember> dummyStaffMembers = new ArrayList<>();
        ArrayList<Project> dummyProjects = new ArrayList<>();
        HashMap<String, StaffMember> existingStaffMembers = new HashMap<>();
        HashMap<String, Project> existingProjects = new HashMap<>();
        Common.doesLoadedFileHaveStream = false;

        String[] line;
        if (fromFile == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            csvReader = new CSVReaderBuilder(new InputStreamReader(is, StandardCharsets.UTF_8)).withCSVParser(csvParser)
                    .build();
        } else {
            csvReader = new CSVReaderBuilder(
                    new InputStreamReader(new FileInputStream(fromFile), StandardCharsets.UTF_8))
                            .withCSVParser(csvParser).build();
        }

        Boolean isHeaderLine = true;
        Integer staffNameIndex = null, researchActivityIndex = null, streamIndex = null,
                preferredProbabilityIndex = null;
        int currLine = 1;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            // dynamically get column indexes of data.
            if (isHeaderLine) {
                String[] headers = line;
                for (int i = 0; i < headers.length; i++) {
                    String currHeader = headers[i].toLowerCase();
                    if (currHeader.endsWith("staff name")) {
                        staffNameIndex = i;
                    } else if (currHeader.endsWith("research activity")) {
                        researchActivityIndex = i;
                    } else if (currHeader.endsWith("stream")) {
                        streamIndex = i;
                    } else if (currHeader.endsWith("preferred probability")) {
                        preferredProbabilityIndex = i;
                    }
                }

                // minimum required headers for solvers to work.
                if (staffNameIndex == null || researchActivityIndex == null) {
                    throw new UnsuitableColumnHeadersException(String.format(
                            "ERROR: Error occurred while reading file. Unable to parse CSV's column headers at line %d.\nCurrently parsed staff name at column %d, and research activity at column %d.\nPlease ensure CSV file has the minimum expected column headers.\ni.e. Staff Name, Research Activity",
                            currLine, (staffNameIndex == null) ? null : staffNameIndex + 1,
                            (researchActivityIndex == null) ? null : researchActivityIndex + 1));
                }

                Common.doesLoadedFileHaveStream = (streamIndex == null) ? false : true;
                isHeaderLine = false;
                currLine++;
                continue;
            }

            StaffMember staffMember = null;
            String staffName = null, researchActivity = null, stream = null;
            Double preferredProbability = null;
            try {
                staffName = staffNameIndex != null ? line[staffNameIndex] : null;
                staffMember = staffName != null ? allStaffMembers.get(staffName) : null;
                // if (staffName.startsWith("Tom") && staffName.endsWith("Torquemada")) {
                //     System.out.println("from projects file: " + staffName);

                //     for (String name : allStaffMembers.keySet()) {
                //         if (name.startsWith("Tom") && name.endsWith("Torquemada")) {
                //             System.out.println("from Miskatonic: " + name);
                //         }
                //     }
                // }
                researchActivity = line[researchActivityIndex].trim();
                stream = streamIndex != null ? (line[streamIndex].equals("null")) ? null : line[streamIndex] : null;
                preferredProbability = preferredProbabilityIndex != null
                        ? Double.parseDouble(line[preferredProbabilityIndex])
                        : Common.getProbability();
            } catch (IndexOutOfBoundsException e) {
                throw new MissingFieldsException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure no required fields are missing/empty at line %d.",
                        currLine));
            } catch (NumberFormatException e) {
                if (preferredProbability == null) {
                    throw new NumberFormatException(String.format(
                            "ERROR: Error occurred while reading file. Expected preferred probability to be a double, but got: '%s' at line %d.",
                            line[preferredProbabilityIndex], currLine));
                }
            }

            if (Common.DEBUG_IO_UNICODE && line[0].contains("Pep"))
                System.out.println(line[0] + ": " + allStaffMembers.get(line[0]));

            if (researchActivity.equals("")) {
                throw new EmptyResearchActivityException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure research activity or project is not an empty string at line %d.",
                        currLine));
            }

            if (stream != null && !(stream.equals("CS") || stream.equals("CS+DS") || stream.equals("DS"))) {
                throw new UnexpectedStreamException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure stream values to be either 'CS', 'CS+DS' or 'DS' at line %d.",
                        currLine));
            }

            if (staffNameIndex != null && staffMember == null) {
                throw new UnknownStaffMemberNameException(String.format(
                        "ERROR: Error occurred while reading file. Unable to find staff member name in MiskatonicStaffMembers.xlsx from given CSV file at line %d.",
                        currLine));
            } else if (staffNameIndex == null) {
                Project project = null;

                // update maps.
                // if already exist, we just fetch it from the projects map.
                if (existingStaffMembers.containsKey(researchActivity)
                        && existingProjects.containsKey(researchActivity)) {
                    project = existingProjects.get(researchActivity);
                } else {
                    StaffMember sm = new StaffMember(Integer.toString(existingStaffMembers.size()), researchActivity,
                            "", stream, true);
                    project = sm.getProject();

                    dummyStaffMembers.add(sm);
                    dummyProjects.add(project);
                    existingStaffMembers.put(researchActivity, sm);
                    existingProjects.put(researchActivity, project);
                }

                projects.add(project);
            } else {
                projects.add(new Project(staffMember, researchActivity, stream, preferredProbability));
            }

            currLine++;
        }
        return projects;
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects)
            throws UnsupportedEncodingException, FileNotFoundException, IOException,
            InadequatePreferenceListSizeException, SimilarStudentIDsException, MissingFieldsException,
            UnexpectedStreamException, NumberFormatException, UnsuitableColumnHeadersException,
            EmptyPreferenceListException {
        return readStudents(filename, projects, null);
    }

    public ArrayList<Student> readStudents(String filename, ArrayList<Project> projects, File fromFile)
            throws UnsupportedEncodingException, FileNotFoundException, IOException,
            InadequatePreferenceListSizeException, SimilarStudentIDsException, MissingFieldsException,
            UnexpectedStreamException, NumberFormatException, UnsuitableColumnHeadersException,
            EmptyPreferenceListException {
        CSVReader csvReader = null;
        CSVParser csvParser = getParser();
        ArrayList<Student> students = new ArrayList<Student>();
        HashSet<Integer> studentIDs = new HashSet<>();

        // for dummy staff members.
        ArrayList<StaffMember> dummyStaffMembers = new ArrayList<>();
        ArrayList<Project> dummyProjects = new ArrayList<>();
        HashMap<String, StaffMember> existingStaffMembers = new HashMap<>();
        HashMap<String, Project> existingProjects = new HashMap<>();
        Common.doesLoadedFileHaveStream = false;

        String[] line;
        if (fromFile == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            csvReader = new CSVReaderBuilder(new InputStreamReader(is, StandardCharsets.UTF_8)).withCSVParser(csvParser)
                    .build();
        } else {
            csvReader = new CSVReaderBuilder(
                    new InputStreamReader(new FileInputStream(fromFile), StandardCharsets.UTF_8))
                            .withCSVParser(csvParser).build();
        }

        Boolean isHeaderLine = true, hasProjectAssigned = false;
        Integer firstNameIndex = null, lastNameIndex = null, studentIDIndex = null, streamIndex = null, gpaIndex = null,
                projectAssignedIndex = null, preferenceStartIndex = null, preferenceEndIndex = null;
        // int aIndex = 0;
        int currLine = 1;
        assert csvReader != null;
        while ((line = csvReader.readNext()) != null) {
            // dynamically get column indexes of data.
            if (isHeaderLine) {
                String[] headers = line;
                for (int i = 0; i < headers.length; i++) {
                    // System.out.println(headers[i]);
                    String currHeader = headers[i].toLowerCase();
                    // System.out.println(currHeader);
                    if (currHeader.endsWith("student") || currHeader.endsWith("first name")
                            || currHeader.endsWith("student name")) {
                        firstNameIndex = i;
                    } else if (currHeader.endsWith("last name")) {
                        lastNameIndex = i;
                    } else if (currHeader.endsWith("id") || currHeader.endsWith("student number")) {
                        studentIDIndex = i;
                    } else if (currHeader.endsWith("stream")) {
                        streamIndex = i;
                    } else if (currHeader.endsWith("gpa")) {
                        gpaIndex = i;
                    } else if (currHeader.endsWith("project assigned")) {
                        hasProjectAssigned = true;
                        projectAssignedIndex = i;
                    } else if (currHeader.equals("1") || currHeader.endsWith("preference 1")) {
                        preferenceStartIndex = i;
                        preferenceEndIndex = headers.length - 1;
                        // System.out.println(preferenceStartIndex + " " + preferenceEndIndex);
                        break;
                    }
                }

                // minimum required headers for solvers to work.
                if (firstNameIndex == null || studentIDIndex == null || preferenceStartIndex == null
                        || preferenceEndIndex == null) {
                    throw new UnsuitableColumnHeadersException(String.format(
                            "ERROR: Error occurred while reading file. Unable to parse CSV's column headers at line %d.\nCurrently parsed first name at column %d, student id at column %d, preference start at column %d, and end at column %d.\nPlease ensure CSV file has the minimum expected column headers.\ni.e. First Name, Student ID, Preference 1, ..., Preference 20",
                            currLine, (firstNameIndex == null) ? null : firstNameIndex + 1,
                            (studentIDIndex == null) ? null : studentIDIndex + 1,
                            (preferenceStartIndex == null) ? null : preferenceStartIndex + 1,
                            (preferenceEndIndex == null) ? null : preferenceEndIndex + 1));
                }

                Common.doesLoadedFileHaveStream = (streamIndex == null || !Common.isProjectsPopulated) ? false : true;
                isHeaderLine = false;
                currLine++;
                continue;
            }

            String firstName = null, lastName = null, stream = null, projectAssignedStr = null;
            Integer studentID = null;
            Double gpa = null;
            Project projectAssigned = null;
            try {
                firstName = line[firstNameIndex];
                lastName = lastNameIndex != null ? line[lastNameIndex] : "";
                studentID = Integer.parseInt(line[studentIDIndex]);
                stream = streamIndex != null ? (line[streamIndex].equals("null")) ? null : line[streamIndex] : null;
                gpa = (Settings.enableGPA && gpaIndex != null && isGPA(line[gpaIndex]))
                        ? (line[gpaIndex].equals("null")) ? null : Double.parseDouble(line[gpaIndex])
                        : null;
                projectAssignedStr = projectAssignedIndex != null ? line[projectAssignedIndex].trim() : null;
            } catch (IndexOutOfBoundsException e) {
                throw new MissingFieldsException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure no required fields are missing/empty at line %d.",
                        currLine));
            } catch (NumberFormatException e) {
                if (studentID == null) {
                    throw new NumberFormatException(String.format(
                            "ERROR: Error occurred while reading file. Expected student id to be an integer, but got: '%s' at line %d.",
                            line[studentIDIndex], currLine));
                }
            }

            if (studentIDs.contains(studentID)) {
                throw new SimilarStudentIDsException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure students have unique IDs at line %d.",
                        currLine));
            }
            studentIDs.add(studentID);

            if (stream != null && !(stream.equals("CS") || stream.equals("DS"))) {
                throw new UnexpectedStreamException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure stream values to be either 'CS' or 'DS' at line %d.",
                        currLine));
            }

            ArrayList<Project> thisStudentsPreference = new ArrayList<Project>();
            // if projects not loaded/generated.
            if (!Common.isProjectsPopulated) {
                // if is ProjectAssigned.
                if (projectAssigned == null && hasProjectAssigned && projectAssignedStr != null
                        && !projectAssignedStr.equals("")) {
                    Project project = null;

                    // update maps.
                    // if already exist, we just fetch it from the projects map.
                    if (existingStaffMembers.containsKey(projectAssignedStr)
                            && existingProjects.containsKey(projectAssignedStr)) {
                        project = existingProjects.get(projectAssignedStr);
                    } else {
                        StaffMember sm = new StaffMember(Integer.toString(existingStaffMembers.size()),
                                projectAssignedStr, "", null, true);
                        project = sm.getProject();

                        dummyStaffMembers.add(sm);
                        dummyProjects.add(project);
                        existingStaffMembers.put(projectAssignedStr, sm);
                        existingProjects.put(projectAssignedStr, project);
                    }
                    projectAssigned = project;
                }

                // find actual Project objects from the strings.
                for (int curr = preferenceStartIndex; curr <= preferenceEndIndex; curr++) {
                    String projectStr = line[curr].trim();
                    Project project = null;

                    if (projectStr.equals("")) {
                        continue;
                    }

                    // update maps.
                    // if already exist, we just fetch it from the projects map.
                    if (existingStaffMembers.containsKey(projectStr) && existingProjects.containsKey(projectStr)) {
                        project = existingProjects.get(projectStr);
                    } else {
                        StaffMember sm = new StaffMember(Integer.toString(existingStaffMembers.size()), projectStr, "",
                                null, true);
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
                for (int curr = preferenceStartIndex; curr <= preferenceEndIndex; curr++) {
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

            if (thisStudentsPreference.size() == 0) {
                throw new EmptyPreferenceListException(String.format(
                        "ERROR: Error occurred while reading file. Please ensure student has a non-empty project preference list at line %d.",
                        currLine));
            }

            // if (thisStudentsPreference.size() != 10) {
            //     throw new InadequatePreferenceListSizeException(
            //             "ERROR: Unable to read file. A project in a student's preference list cannot be found in the Loaded projects. Please ensure you have the list of projects allocated for these students, or list of students with these projects.");
            // }
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
            currLine++;
        }
        if (!Common.isProjectsPopulated) {
            Settings.dummyStaffMembers = dummyStaffMembers;
            Settings.loadedProjects = dummyProjects;
        }
        return students;
    }

    public ArrayList<Student> readStudentsV1(String filename, ArrayList<Project> projects, File fromFile)
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
        Common.doesLoadedFileHaveStream = false;

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
                                projectAssignedStr, "", null, true);
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
                                null, true);
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

            // if (thisStudentsPreference.size() != 10) {
            //     throw new InadequatePreferenceListSizeException(
            //             "Preference list size must be equal to 10, but is %d, check if readStudents() is given the correct ArrayList projects tailored for these students.");
            // }
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
