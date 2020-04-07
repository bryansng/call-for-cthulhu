package ie.ucd.ui.interfaces;

public interface VisualizerInterface {
	public void addToSeries(double currEnergy, double bestEnergy, int loopNumber);

	public void newSeries();

	public void resetSeries();

	public void stopAddToGraphScheduler();
}