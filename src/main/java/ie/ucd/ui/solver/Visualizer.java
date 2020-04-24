package ie.ucd.ui.solver;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.Common.SolverType;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class Visualizer extends GridPane implements VisualizerInterface {
	private Deque<Coordinate> coordinateDeque;

	private String yAxisName;
	private String xAxisName;
	private LineChart<Number, Number> lineChart;
	private XYChart.Series<Number, Number> currSeries;
	private XYChart.Series<Number, Number> bestSeries;
	private LocalDateTime startDateTime;

	private SolverType solverType;

	public Visualizer(SolverType solverType) {
		super();
		this.solverType = solverType;
		switch (solverType) {
			case GeneticAlgorithm:
				yAxisName = "Fitness";
				xAxisName = "Number of Generations";
				getStylesheets().add("ui/solver/visualizerGA.css");
				break;
			case SimulatedAnnealing:
			default:
				yAxisName = "Energy";
				xAxisName = "Number of Iterations";
				getStylesheets().add("ui/solver/visualizerSA.css");
				break;
		}
		initLayout();
	}

	private void initLayout() {
		getChildren().add(initLineChart());
	}

	private LineChart<Number, Number> initLineChart() {
		// defining axes.
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(xAxisName);
		xAxis.setAutoRanging(true);
		xAxis.setForceZeroInRange(false);
		yAxis.setLabel(yAxisName);
		yAxis.setAutoRanging(true);
		yAxis.setForceZeroInRange(false);

		// creating the chart.
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle(yAxisName + " over " + xAxisName + " / Time");
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.setAnimated(false);
		if (solverType == SolverType.SimulatedAnnealing)
			lineChart.setCreateSymbols(false);
		lineChart.setPrefWidth(1024);
		lineChart.setPrefHeight(768);
		lineChart.setLegendSide(Side.RIGHT);

		// defining a series, initial series has no data.
		currSeries = new XYChart.Series<Number, Number>();
		bestSeries = new XYChart.Series<Number, Number>();
		setSeriesName(0.0, 0.0);

		// initialize coordinate deque.
		coordinateDeque = new LinkedList<Coordinate>();

		// add series to chart.
		lineChart.getData().add(currSeries);
		lineChart.getData().add(bestSeries);

		return lineChart;
	}

	@Override
	public void addToQueue(double currY, double bestY, int loopNumber) {
		// populating the deque with data, the data in deque will be added to the series at regular intervals.
		LocalDateTime now = LocalDateTime.now();
		long timeElapsed = java.time.Duration.between(startDateTime, now).getNano();

		Coordinate coord = new Coordinate(currY, bestY, timeElapsed, loopNumber);
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
		if (solverType == SolverType.GeneticAlgorithm) {
			for (int i = 0; i < Settings.gaPopulationSize; i++)
				addDataToChart();
		} else {
			addDataToChart();
		}
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
				if (solverType == SolverType.GeneticAlgorithm) {
					// for genetic algorithm, to have legend show the color of the symbol, we cannot use css to set transparent (because legend symbol is based on that css color).
					// so an alternative is to do this, setting each node individually invisible.
					// https://stackoverflow.com/questions/39507491/how-to-remove-symbol-markers-from-only-selected-series-in-javafx-charts
					Data<Number, Number> data = new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY());
					bestSeries.getData().add(data);
					StackPane sp = (StackPane) data.getNode();
					sp.setVisible(false);
				} else {
					bestSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY()));
				}

				// update legend with new y values.
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
				if (solverType == SolverType.GeneticAlgorithm) {
					Data<Number, Number> data = new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY());
					bestSeries.getData().add(data);
					StackPane sp = (StackPane) data.getNode();
					sp.setVisible(false);
				} else {
					bestSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestY()));
				}
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