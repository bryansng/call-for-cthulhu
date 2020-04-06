package ie.ucd.objects;

public class Coordinate {
	private double currEnergy;
	private double bestEnergy;
	private long timeElapsed;
	private int loopNumber;

	public Coordinate(double currEnergy, double bestEnergy, long timeElapsed, int loopNumber) {
		this.currEnergy = currEnergy;
		this.bestEnergy = bestEnergy;
		this.timeElapsed = timeElapsed;
		this.loopNumber = loopNumber;
	}

	public double getCurrEnergy() {
		return currEnergy;
	}

	public double getBestEnergy() {
		return bestEnergy;
	}

	public double getTimeElapsed() {
		return timeElapsed;
	}

	public double getLoopNumber() {
		return loopNumber;
	}
}