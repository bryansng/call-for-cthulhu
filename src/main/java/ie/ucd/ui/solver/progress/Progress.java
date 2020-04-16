package ie.ucd.ui.solver.progress;

public class Progress {
	private double start;
	private double current;
	private double end;

	public Progress(double start, double current, double end) {
		this.start = start;
		this.current = current;
		this.end = end;
	}

	public double getStart() {
		return start;
	}

	public double getCurrent() {
		return current;
	}

	public double getEnd() {
		return end;
	}
}