import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.Parser;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import org.junit.Assert;

public class CSVReaderTest {
	private CSVFileReader dummyReader;
	private Parser parser;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting ProjectTest up!");
		dummyReader = new CSVFileReader();
		parser = new Parser();
	}

	@Test
	@DisplayName("Test readProject with correct csv")
	public void testReadProject() {
		System.out.println("Running: testReadProject");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testCorrectProject.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNotNull("Projects null, expected successful read.", dummyProjects);
		Assert.assertEquals(90, dummyProjects.size());
	}

	@Test
	@DisplayName("Test readProject with Illegal Separator")
	public void testReadProjectIllegalSep() {
		System.out.println("Running: testReadProjectIllegalSep");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongProject-illegalSeparator.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNull("Projects not null, expected unsuccessful read.", dummyProjects);
	}

	@Test
	@DisplayName("Test readProject with Wrong Layout")
	public void testReadProjectWrongLayout() {
		System.out.println("Running: testReadProjectWrongLayout");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongProject-wrongLayout.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNull("Projects not null, expected unsuccessful read.", dummyProjects);
	}

	@Test
	@DisplayName("Test readProject with Wrong Data Type")
	public void testReadProjectWrongDataType() {
		System.out.println("Running: testReadProjectWrongDataType");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongProject-wrongDataType.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNull("Projects not null, expected unsuccessful read.", dummyProjects);
	}

	@Test
	@DisplayName("Test readProject with missing fields")
	public void testReadProjectMissingFields() {
		System.out.println("Running: testReadProjectMissingFields");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongProject-missingFields.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNull("Projects not null, expected unsuccessful read.", dummyProjects);
	}

	@Test
	@DisplayName("Test readStudent with correct csv")
	public void testReadStudent() {
		System.out.println("Running: testReadStudent");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testCorrectProject.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("testCSVs/testCorrectStudent.csv", dummyProjects);
		} catch (Exception e) {
		}
		Assert.assertNotNull("Students null, expected successful read.", dummyStudents);
		Assert.assertEquals(60, dummyStudents.size());
	}

	@Test
	@DisplayName("Test readStudent with Illegal Separator")
	public void testReadStudentIllegalSep() {
		System.out.println("Running: testReadStudentIllegalSep");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongStudent-projects.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("testCSVs/testWrongStudent-illegalSeparator.csv", dummyProjects);
		} catch (Exception e) {
		}
		Assert.assertNull("Students not null, expected unsuccessful read.", dummyStudents);
	}

	@Test
	@DisplayName("Test readStudent with Wrong Layout")
	public void testReadStudentWrongLayout() {
		System.out.println("Running: testReadStudentWrongLayout");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongStudent-projects.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("testCSVs/testWrongStudent-wrongLayout.csv", dummyProjects);
		} catch (Exception e) {
		}
		Assert.assertNull("Students not null, expected unsuccessful read.", dummyStudents);
	}

	@Test
	@DisplayName("Test readStudent with Wrong Data Type")
	public void testReadStudentWrongDataType() {
		System.out.println("Running: testReadStudentWrongDataType");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongStudent-projects.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("testCSVs/testWrongStudent-wrongDataType.csv", dummyProjects);
		} catch (Exception e) {
		}
		Assert.assertNull("Students not null, expected unsuccessful read.", dummyStudents);
	}

	@Test
	@DisplayName("Test readStudent with missing fields")
	public void testReadStudentMissingFields() {
		System.out.println("Running: testReadStudentMissingFields");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("testCSVs/testWrongStudent-projects.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("testCSVs/testWrongStudent-missingFields.csv", dummyProjects);
		} catch (Exception e) {
		}
		Assert.assertNull("Students not null, expected unsuccessful read.", dummyStudents);
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyReader = null;
		parser = null;
		Assert.assertNull(dummyReader);
		Assert.assertNull(parser);
	}
}
