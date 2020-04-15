package ie.ucd.ui.solver;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.Common.SolverType;
import ie.ucd.objects.Coordinate;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

public class Visualizer extends GridPane implements VisualizerInterface {
	private Deque<Coordinate> coordinateDeque;

	private String yAxisName;
	private LineChart<Number, Number> lineChart;
	private XYChart.Series<Number, Number> currSeries;
	private XYChart.Series<Number, Number> bestSeries;
	private LocalDateTime startDateTime;

	public Visualizer(SolverType solverType) {
		super();
		switch (solverType) {
			case GeneticAlgorithm:
				yAxisName = "Fitness";
				break;
			case SimulatedAnnealing:
			default:
				yAxisName = "Energy";
				break;
		}
		initLayout();
	}

	private void initLayout() {
		getStylesheets().add("ui/solver/visualizer.css");
		getChildren().add(initLineChart());
	}

	private LineChart<Number, Number> initLineChart() {
		// defining axes.
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Loop Number");
		xAxis.setAutoRanging(true);
		xAxis.setForceZeroInRange(false);
		yAxis.setLabel(yAxisName);
		yAxis.setAutoRanging(true);

		// creating the chart.
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle(yAxisName + " over Time / Loop Number");
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(false);
		lineChart.setPrefWidth(1024);
		lineChart.setPrefHeight(768);
		lineChart.setLegendSide(Side.RIGHT);

		// defining a series, initial series has no data.
		currSeries = new XYChart.Series<Number, Number>();
		bestSeries = new XYChart.Series<Number, Number>();
		setSeriesName(null, null);

		// initialize coordinate deque.
		coordinateDeque = new LinkedList<Coordinate>();

		// add series to chart.
		lineChart.getData().add(currSeries);
		lineChart.getData().add(bestSeries);

		return lineChart;
	}

	@Override
	public void addToQueue(double currEnergy, double bestEnergy, int loopNumber) {
		// populating the deque with data, the data in deque will be added to the series at regular intervals.
		LocalDateTime now = LocalDateTime.now();
		long timeElapsed = java.time.Duration.between(startDateTime, now).getNano();

		Coordinate coord = new Coordinate(currEnergy, bestEnergy, timeElapsed, loopNumber);
		coordinateDeque.add(coord);

		if (!Settings.enableAnimation && coordinateDeque.size() > Settings.maximumXAxisTicks) {
			coordinateDeque.removeFirst();
		}
	}

	@Override
	public void newSeries() {
		startDateTime = LocalDateTime.now();
	}

	@Override
	public void resetSeries() {
		currSeries.getData().clear();
		bestSeries.getData().clear();
		coordinateDeque.clear();

		Platform.runLater(() -> {
			setSeriesName(0.0, 0.0);
		});
	}

	public void initOneShotScheduler() {
		addDataToChart();
	}

	public boolean isDequeEmpty() {
		return coordinateDeque.isEmpty();
	}

	public void addDataToChart() {
		if (!isDequeEmpty()) {
			// update the chart.
			try {
				// get data from deque.
				Coordinate coord = coordinateDeque.removeFirst();

				// put y value with current time.
				currSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getCurrY()));
				bestSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY()));
				setSeriesName(coord.getCurrY(), coord.getBestY());

				if (currSeries.getData().size() > Settings.maximumXAxisTicks && Common.CHART_ENABLE_TRUNCATE)
					currSeries.getData().remove(0, Settings.pointsToRemove);
				if (bestSeries.getData().size() > Settings.maximumXAxisTicks && Common.CHART_ENABLE_TRUNCATE)
					bestSeries.getData().remove(0, Settings.pointsToRemove);
			} catch (NoSuchElementException e) {
			}
		}
	}

	public void addLastWindowToChart() {
		// update the chart.
		try {
			for (int i = 0; i < Settings.maximumXAxisTicks && !isDequeEmpty(); i++) {
				// get data from deque.
				Coordinate coord = coordinateDeque.removeFirst();

				// put y value with current time.
				currSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getCurrY()));
				bestSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY()));
				setSeriesName(coord.getCurrY(), coord.getBestY());
			}
		} catch (NoSuchElementException e) {
		}
	}

	private void setSeriesName(Double currEnergy, Double bestEnergy) {
		String currSeriesName = "Current " + yAxisName;
		String bestSeriesName = "Best " + yAxisName;
		int padding = Math.max(currSeriesName.length(), bestSeriesName.length());
		if (currEnergy == null || bestEnergy == null) {
			currSeries.setName(currSeriesName);
			bestSeries.setName(bestSeriesName);
		} else {
			currSeries.setName(String.format("%-" + padding + "s %10.4f", currSeriesName, currEnergy));
			bestSeries.setName(String.format("%-" + padding + "s %10.4f", bestSeriesName, bestEnergy));
		}
	}
}