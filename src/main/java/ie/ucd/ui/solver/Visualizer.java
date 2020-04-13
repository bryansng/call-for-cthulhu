package ie.ucd.ui.solver;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import ie.ucd.Common;
import ie.ucd.Common.SolverType;
import ie.ucd.objects.Coordinate;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Visualizer extends GridPane implements VisualizerInterface {
	private final int WINDOW_SIZE = 10000; // 13000
	private final int VISUALIZER_DEQUE_EMPTY_LIMIT = 1000; //  time_to_wait_before_stop_scheduler_in_sec / scheduler_wait_in_sec, i.e. 1/0.001

	private ControlButtons controlButtons;

	private int emptyCount;
	private String yAxisName;
	private LineChart<Number, Number> lineChart;
	private Deque<Coordinate> coordinateDeque;
	private XYChart.Series<Number, Number> currSeries;
	private XYChart.Series<Number, Number> bestSeries;
	private LocalDateTime startDateTime;

	private Timeline addToGraph;

	public Visualizer(SolverType solverType) {
		super();
		switch (solverType) {
			case GeneticAlgorithm:
				yAxisName = "Fitness";
			case SimulatedAnnealing:
			default:
				yAxisName = "Energy";
				break;
		}
		initLayout();

		// setup a scheduled executor to periodically put data into the chart.
		initAddToGraphScheduler();
	}

	private void initAddToGraphScheduler() {
		addToGraph = new Timeline(new KeyFrame(Duration.millis(1), ev -> {
			if (!coordinateDeque.isEmpty()) {
				addDataToChart();
				emptyCount = 0;
			} else {
				emptyCount++;
				if (emptyCount > VISUALIZER_DEQUE_EMPTY_LIMIT) {
					controlButtons.enableOnlyClearAndReset();
					pause();
					System.out.println("visualizer paused");
				}
			}
		}));
		addToGraph.setCycleCount(Animation.INDEFINITE);
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
	}

	@Override
	public void newSeries() {
		startDateTime = LocalDateTime.now();
		emptyCount = 0;
		resetSeries();
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

	public void stop() {
		addToGraph.stop();
	}

	public void pause() {
		addToGraph.pause();
	}

	public void resume() {
		addToGraph.play();
	}

	public void initOneShotScheduler() {
		addDataToChart();
	}

	public boolean isDequeEmpty() {
		return coordinateDeque.isEmpty();
	}

	private void addDataToChart() {
		// update the chart.
		Platform.runLater(() -> {
			try {
				// get data from deque.
				Coordinate coord = coordinateDeque.removeFirst();

				// put y value with current time.
				currSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getCurrEnergy()));
				bestSeries.getData().add(new XYChart.Data<Number, Number>(coord.getLoopNumber(), coord.getBestEnergy()));
				setSeriesName(coord.getCurrEnergy(), coord.getBestEnergy());

				if (currSeries.getData().size() > WINDOW_SIZE && Common.CHART_ENABLE_TRUNCATE)
					currSeries.getData().remove(0);
				if (bestSeries.getData().size() > WINDOW_SIZE && Common.CHART_ENABLE_TRUNCATE)
					bestSeries.getData().remove(0);
			} catch (NoSuchElementException e) {
			}
		});
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

	public void setControlButtons(ControlButtons controlButtons) {
		this.controlButtons = controlButtons;
	}
}