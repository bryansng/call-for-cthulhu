package ie.ucd.ui.common;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ie.ucd.Common;
import ie.ucd.objects.Coordinate;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

public class Visualizer extends GridPane implements VisualizerInterface {
	private final int WINDOW_SIZE = 10000; // 13000
	private final int VISUALIZER_DEQUE_EMPTY_LIMIT = 5000; //  time_to_wait_before_stop_scheduler_in_sec / scheduler_wait_in_sec, i.e. 5/0.001

	private boolean isRunning;

	private int emptyCount;
	private LineChart<Number, Number> lineChart;
	private Deque<Coordinate> coordinateDeque;
	private XYChart.Series<Number, Number> currSeries;
	private XYChart.Series<Number, Number> bestSeries;
	private LocalDateTime startDateTime;

	private ScheduledExecutorService scheduledExecutorService;
	private Future<?> future;

	public Visualizer() {
		super();
		resetStates();
		initLayout();

		// setup a scheduled executor to periodically put data into the chart.
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	}

	private void initLayout() {
		getChildren().add(initLineChart());
	}

	private LineChart<Number, Number> initLineChart() {
		// defining axes.
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Loop Number");
		xAxis.setAutoRanging(true);
		xAxis.setForceZeroInRange(false);
		yAxis.setLabel("Energy");
		yAxis.setAutoRanging(true);

		// creating the chart.
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle("Energy over Time / Loop Number");
		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(false);
		lineChart.setPrefWidth(1024);
		lineChart.setPrefHeight(768);
		lineChart.setLegendSide(Side.RIGHT);

		// defining a series, initial series has no data.
		currSeries = new XYChart.Series<Number, Number>();
		currSeries.setName("Current Energy");
		bestSeries = new XYChart.Series<Number, Number>();
		bestSeries.setName("Best Energy");

		// initialize coordinate deque.
		coordinateDeque = new LinkedList<Coordinate>();

		// add series to chart.
		lineChart.getData().add(currSeries);
		lineChart.getData().add(bestSeries);

		return lineChart;
	}

	@Override
	public void addToSeries(double currEnergy, double bestEnergy, int loopNumber) {
		// populating the deque with data, the data in deque will be added to the series at regular intervals.
		LocalDateTime now = LocalDateTime.now();
		long timeElapsed = java.time.Duration.between(startDateTime, now).getNano();

		Coordinate coord = new Coordinate(currEnergy, bestEnergy, timeElapsed, loopNumber);
		coordinateDeque.add(coord);
	}

	@Override
	public void newSeries() {
		resetStates();
		if (!isRunning) {
			isRunning = true;
			startDateTime = LocalDateTime.now();
			emptyCount = 0;
			resetSeries();
			initScheduler();
		}
	}

	@Override
	public void resetSeries() {
		currSeries.getData().clear();
		bestSeries.getData().clear();
		coordinateDeque.clear();
	}

	public void stopAddToGraphScheduler() {
		scheduledExecutorService.shutdownNow();
	}

	// https://stackoverflow.com/questions/4205327/scheduledexecutorservice-start-stop-several-times
	public void pauseAddToGraphScheduler() {
		future.cancel(true);
	}

	public void resumeAddToGraphScheduler() {
		initScheduler();
	}

	private void initScheduler() {
		// put data onto graph every.
		future = scheduledExecutorService.scheduleAtFixedRate(() -> {
			addDataToChart();
		}, 0, 1, TimeUnit.MILLISECONDS); // 0.001 second
		// }, 0, 500, TimeUnit.MICROSECONDS);	// 0.000 5 second
	}

	public void initOneShotScheduler() {
		scheduledExecutorService.schedule(() -> {
			addDataToChart();
		}, 0, TimeUnit.MILLISECONDS);
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

				if (currSeries.getData().size() > WINDOW_SIZE && Common.CHART_ENABLE_TRUNCATE)
					currSeries.getData().remove(0);
				if (bestSeries.getData().size() > WINDOW_SIZE && Common.CHART_ENABLE_TRUNCATE)
					bestSeries.getData().remove(0);
				emptyCount = 0;
			} catch (NoSuchElementException e) {
				emptyCount++;

				if (emptyCount > VISUALIZER_DEQUE_EMPTY_LIMIT)
					stopAddToGraphScheduler();
			}
		});
	}

	private void resetStates() {
		isRunning = false;
	}
}