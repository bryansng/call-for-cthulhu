package ie.ucd.ui.interfaces;

public interface VisualizerInterface {
	public void addToQueue(double currEnergy, double bestEnergy, int loopNumber);

	public void newSeries();

	public void resetSeries();

	public void stop();

	public void setDoneProcessing(boolean isDone);
}