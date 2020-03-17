package ie.ucd;

import java.io.IOException;
// import org.apache.xssf.usemodel;
import java.util.HashMap;

public class Generator {
	public static void main(String[] args) throws IOException {
		System.out.println("Hello World!");
		HashMap<String, Project> projects = new Parser().parseProjects();
		System.out.println(projects.get("lecturing about climate change"));
	}
}
