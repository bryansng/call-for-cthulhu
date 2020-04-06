package ie.ucd.ui.interfaces;

public interface VisualizerInterface {
	public void addToSeries(double y1Value, double y2Value, int loopNumber);

	public void newSeries();

	public void resetSeries();

	public void stopAddToGraphScheduler();
}