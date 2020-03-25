package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import ie.ucd.objects.Student;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.objects.Project;

public class Generator {
	public static void main(String[] args) throws IOException, InvalidFormatException {
		System.out.println("Running application...");
		generateRequiredFiles();

		// playingNormalDistribution();
	}

	public static void generateRequiredFiles() throws IOException, InvalidFormatException {
		Parser parser;
		ExcelWriter writer = new ExcelWriter();
		ArrayList<Project> projects;
		HashMap<Integer, Student> students;

		System.out.println("Generating for 60 students...");
		parser = new Parser(60);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(projects, 60);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		new SimulatedAnnealing(projects, students);
		// System.out.println(parser.ProjectDistributionPercentage());
		// System.out.println(parser.Project1stPreferencePercentage());

		// System.out.println("Generating for 120 students...");
		// parser = new Parser(120);
		// projects = parser.generateStaffProjects();
		// students = parser.generateStudents();
		// writer.writeProjects(projects, 120);
		// writer.writeStudents(students);
		// writer.writeAnalysis(parser);
		// System.out.println(parser.CSDSPercentage());
		// // System.out.println(parser.ProjectDistributionPercentage());

		// System.out.println("Generating for 240 students...");
		// parser = new Parser(240);
		// projects = parser.generateStaffProjects();
		// students = parser.generateStudents();
		// writer.writeProjects(projects, 240);
		// writer.writeStudents(students);
		// writer.writeAnalysis(parser);
		// System.out.println(parser.CSDSPercentage());
		// // System.out.println(parser.ProjectDistributionPercentage());

		// System.out.println("Generating for 500 students...");
		// parser = new Parser(500);
		// projects = parser.generateStaffProjects();
		// students = parser.generateStudents();
		// writer.writeProjects(projects, 500);
		// writer.writeStudents(students);
		// writer.writeAnalysis(parser);
		// System.out.println(parser.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());
		System.out.println("All done");
	}

	public static void playingNormalDistribution() {
		Random rand = new Random();
		Double maxx = Double.NEGATIVE_INFINITY;
		Double minn = Double.POSITIVE_INFINITY;

		// why use TreeMap, tldr; entries are sorted automatically:
		// https://stackoverflow.com/questions/571388/how-can-i-sort-the-keys-of-a-map-in-java
		Map<Double, Double> hm = new TreeMap<Double, Double>();
		for (int i = 0; i < 100; i++) {
			double aDouble = rand.nextGaussian();
			while (isOutlier(aDouble)) {
				aDouble = rand.nextGaussian();
			}

			if (aDouble > maxx) {
				maxx = aDouble;
			}
			if (aDouble < minn) {
				minn = aDouble;
			}

			// String formattedDouble = String.format("%.2f", aDouble);
			// aDouble = Double.parseDouble(formattedDouble);
			if (!hm.containsKey(aDouble)) {
				hm.put(aDouble, 0.0);
			}
			hm.replace(aDouble, hm.get(aDouble) + 1.0);
		}
		for (Map.Entry<Double, Double> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}
		System.out.println("Min: " + minn);
		System.out.println("Max: " + maxx);

		// normalize distribution to be from 0 to 100.
		// minn = Math.abs(minn);
		// maxx += minn;
		// for (Double key : hm.keySet()) {
		// 	hm.replace(key, ((key + minn) / maxx) * 100);
		// }

		// for (Map.Entry<Double, Double> entry : hm.entrySet()) {
		// 	System.out.println(entry.getKey() + " - " + entry.getValue());
		// }

		AbstractRealDistribution distr = new NormalDistribution();
		for (Map.Entry<Double, Double> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " - " + distr.density(entry.getKey()));
		}
	}

	public static boolean isOutlier(double value) {
		return !(value >= -2.0 && value <= 2.0);
	}
}
