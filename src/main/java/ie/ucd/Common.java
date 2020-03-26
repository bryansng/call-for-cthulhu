package ie.ucd;

import java.util.Random;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Common {
	// debug verbosity.
	public static final boolean SHOW_SA_DEBUG = false;

	// the below are used during calculating of global/local satisfaction.
	public static final double COST_NOT_ASSIGNED_PREFERENCE_PROJECTS = -5.0;
	public static final double COST_UNSUITED_STREAM = -1.0;
	public static final double COST_NONE_OR_MULTIPLE_PROJECTS = -1.0;

	// the below are used during generation of our random candidate solution.
	public static Random rand = new Random();
	public static int numAvgProjectsProposed = 3;
	public static AbstractRealDistribution distr = new NormalDistribution();
	private static double gaussianLimit = 2.5;

	// https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
	public static double getProbability() {
		double aDouble = rand.nextGaussian();
		while (!(aDouble >= -gaussianLimit && aDouble <= gaussianLimit)) {
			aDouble = rand.nextGaussian();
		}
		return distr.density(rand.nextGaussian());
	}

	public static double getMax() {
		return distr.density(0.0);
	}

	public static double getMin() {
		return distr.density(gaussianLimit);
	}

	public static double getRange() {
		return getMax() - getMin();
	}
}