import java.util.ArrayList;
import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudentTest {
	private Student dummyStudent;
	private Project dummyProjectAssigned;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting StudentTest up!");
		dummyStudent = new Student("James", "Bond", 11110000, "CS", 0.0, setupPreferenceList());
	}

	@Test
	@DisplayName("Test Initialization")
	public void testInitialization() {
		System.out.println("Running: testInitialization");
		Assert.assertEquals(10, dummyStudent.getPreferenceList().size());
		Assert.assertEquals("James", dummyStudent.getFirstName());
		Assert.assertEquals("Bond", dummyStudent.getLastName());
		Assert.assertEquals("CS", dummyStudent.getStream());
		Assert.assertEquals(Double.valueOf("0.0"), dummyStudent.getGpa());
		Assert.assertEquals(dummyProjectAssigned, dummyStudent.getProjectAssigned().get(0));
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyStudent = null;
		Assert.assertNull(dummyStudent);
	}

	protected ArrayList<Project> setupPreferenceList() {
		ArrayList<Project> preferenceList = new ArrayList<>();
		dummyProjectAssigned = new Project(new StaffMember("Wolfgang Amadeus Mozart",
				"composing classical music, playing the piano", "Classical music", "CS"), "playing the piano", "CS",
				Common.getProbability());

		preferenceList.add(dummyProjectAssigned);
		preferenceList.add(new Project(
				new StaffMember("Kenny G", "playing the saxophone,", "Easy Listening, Light Jazz,  Saxophone music", "CS"),
				"playing the saxophone", "CS", Common.getProbability()));
		preferenceList
				.add(new Project(new StaffMember("Professor Hans Zarkov", "building rocket ships", "Flash Gordon", "DS"),
						"building rocket ships", "CS", Common.getProbability()));
		preferenceList.add(new Project(
				new StaffMember("George McFly", "standing up to bullies, spying on girls", "Back To The Future", "DS"),
				"standing up to bullies", "DS", Common.getProbability()));
		preferenceList.add(new Project(
				new StaffMember("Leonardo Da Vinci",
						"pioneering new technologies, developing new technologies, painting realistic pictures",
						"the Renaissance, Renaissance art, Florence", "CS"),
				"pioneering new technologies", "CS", Common.getProbability()));
		preferenceList.add(new Project(new StaffMember("Forrest Gump", "fishing for shrimps, eating a box of chocolates",
				"Greenbow Alabama, the American South", "DS"), "fishing for shrimps", "CS", Common.getProbability()));
		preferenceList
				.add(new Project(
						new StaffMember("George W. Bush", " launching foreign wars",
								"the Right, the Republicans, American politics", "CS"),
						"launching foreign wars", "CS", Common.getProbability()));
		preferenceList.add(new Project(
				new StaffMember("Mikhail Gorbachev", "coasting on past glories", "the Soviet Union, the Cold War", "CS"),
				"coasting on past glories", "CS", Common.getProbability()));
		preferenceList
				.add(new Project(
						new StaffMember("Gordon Ramsay", "cursing at kitchen staff, winning Michelin stars, cooking fancy food",
								"Cooking, the Kitchen, Haute Cuisine", "CS"),
						"cursing at kitchen staff", "CS", Common.getProbability()));
		preferenceList
				.add(new Project(new StaffMember("Al Pacino", "shouting in Hollywood movies", "Acting, Hollywood", "CS"),
						"shouting in Hollywood movies", "CS", Common.getProbability()));
		return preferenceList;
	}
}
