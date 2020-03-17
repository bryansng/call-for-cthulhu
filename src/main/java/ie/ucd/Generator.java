package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;
// import org.apache.xssf.usemodel;

public class Generator {
	public static void main(String[] args) throws IOException {
		System.out.println("Hello World!");
		ArrayList<SupervisorProject> staffArray = new Parser().generateStaffProjects();
		for (SupervisorProject sp : staffArray) {
			System.out.println(sp);
		}
	}
}
