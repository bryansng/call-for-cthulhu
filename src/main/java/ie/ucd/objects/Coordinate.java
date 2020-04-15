package ie.ucd.objects;

public class Coordinate {
	private double currY;
	private double bestY;
	private long timeElapsed;
	private int loopNumber;

	public Coordinate(double currY, double bestY, long timeElapsed, int loopNumber) {
		this.currY = currY;
		this.bestY = bestY;
		this.timeElapsed = timeElapsed;
		this.loopNumber = loopNumber;
	}

	public double getCurrY() {
		return currY;
	}

	public double getBestY() {
		return bestY;
	}

	public double getTimeElapsed() {
		return timeElapsed;
	}

	public double getLoopNumber() {
		return loopNumber;
	}
}