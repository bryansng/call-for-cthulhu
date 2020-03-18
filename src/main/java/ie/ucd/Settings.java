package ie.ucd;

import java.util.Random;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Settings {
	public static Random rand = new Random();
	public static int numAvgProjectsProposed = 3;
	public static AbstractRealDistribution distr = new NormalDistribution();
	private static double gaussian = 2.5;

	// https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
	public static double getProbability() {
		double aDouble = rand.nextGaussian();
		while (!(aDouble >= -gaussian && aDouble <= gaussian)) {
			aDouble = rand.nextGaussian();
		}
		return distr.density(rand.nextGaussian());
	}

	public static double getMax() {
		return distr.density(0.0);
	}

	public static double getMin() {
		return distr.density(gaussian);
	}

	public static double getRange() {
		return getMax() - getMin();
	}
}