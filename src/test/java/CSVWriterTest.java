import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.io.Parser;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;

public class CSVWriterTest {
	private CSVFileReader dummyReader;
	private CSVFileWriter dummyWriter;
	private Parser parser;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting ProjectTest up!");
		dummyReader = new CSVFileReader();
		dummyWriter = new CSVFileWriter();
		parser = new Parser(100);
	}

	@Test
	@DisplayName("Test writeProject")
	public void testWriteProject() {
		System.out.println("Running: testWriteProject");

		Boolean hasException = false;
		try {
			dummyWriter.writeProjects(parser.generateStaffProjects(), parser.numberOfStudents);
		} catch (IOException e) {
			hasException = true;
		}
		Assert.assertEquals("Exception raised, expected no exceptions.", false, hasException);
	}

	@Test
	@DisplayName("Test writeStudent")
	public void testWriteStudent() throws InterruptedException {
		System.out.println("Running: testWriteStudent");

		Boolean hasException = false;
		try {
			dummyWriter.writeProjects(parser.generateStaffProjects(), parser.numberOfStudents);
			dummyWriter.writeStudents(parser.generateStudents());
		} catch (IOException e) {
			hasException = true;
		}
		Assert.assertEquals("Exception raised, expected no exceptions.", false, hasException);
	}

	@Test
	@DisplayName("Test readWrittenProject")
	public void testReadWrittenProject() {
		System.out.println("Running: testReadWrittenProject");

		ArrayList<Project> dummyProjects = null;
		try {
			dummyProjects = dummyReader.readProject("CSVs/ProjectsCSV100.csv", parser.allStaffMembers);
		} catch (Exception e) {
		}
		Assert.assertNotNull("Projects null, expected successful write then read.", dummyProjects);
		Assert.assertEquals(150, dummyProjects.size());
	}

	@Test
	@DisplayName("Test readWrittenStudent")
	public void testReadWrittenStudent() throws InterruptedException {
		System.out.println("Running: testReadWrittenStudent");

		ArrayList<Project> dummyProjects = null;
		ArrayList<Student> dummyStudents = null;
		try {
			dummyProjects = dummyReader.readProject("CSVs/ProjectsCSV100.csv", parser.allStaffMembers);
			dummyStudents = dummyReader.readStudents("CSVs/StudentsCSV100.csv", dummyProjects);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		Assert.assertNotNull("Students null, expected successful write then read.", dummyStudents);
		Assert.assertEquals(100, dummyStudents.size());
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyReader = null;
		dummyWriter = null;
		parser = null;
		Assert.assertNull(dummyReader);
		Assert.assertNull(dummyWriter);
		Assert.assertNull(parser);
	}
}
