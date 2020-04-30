package ie.ucd.ui.solver;

import ie.ucd.Settings;
import ie.ucd.Common.SolverType;
import ie.ucd.ui.common.sheets.Sheets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ie.ucd.ui.interfaces.VisualizerInterface;
import ie.ucd.ui.solver.progress.CustomProgressIndicator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * A class that represents the Solver UI Controller.
 */
public class SolverPane extends ScrollPane {
	private Visualizer visualizer;
	private CustomProgressIndicator progressIndicator;
	private ControlButtons controlButtons;
	private Sheets sheets;

	private boolean isDoneProcessing;
	private Timeline updateUI;

	public SolverPane(Stage stage, SolverType solverType) {
		super();
		visualizer = new Visualizer(solverType);
		progressIndicator = new CustomProgressIndicator();
		sheets = new Sheets(stage, solverType);
		controlButtons = new ControlButtons(this, solverType);
		initLayout();

		// setup a scheduled executor to periodically put data into the chart.
		initUpdateUIScheduler();
	}

	private void initLayout() {
		VBox allParts = new VBox();
		allParts.getStyleClass().addAll("standard-main-container", "solver-pane");

		Label visualizerLabel = new Label("Visualization");
		visualizerLabel.getStyleClass().add("main-label");

		VBox everythingElse = new VBox();
		everythingElse.getStyleClass().addAll("smaller-sub-container");
		everythingElse.getChildren().addAll(visualizer,
				new HBox(5, new Label("Processing thread progress:"), progressIndicator), controlButtons, sheets);

		allParts.getChildren().addAll(new VBox(visualizerLabel, everythingElse));
		setContent(allParts);
	}

	private void initUpdateUIScheduler() {
		updateUI = new Timeline(new KeyFrame(Duration.millis(1), ev -> {
			if (!sheets.isDequeEmpty() || !visualizer.isDequeEmpty()) {
				initOneShotScheduler();
			} else if (isDoneProcessing && isDequeEmpty()) {
				pause();
				progressIndicator.setDone();
				controlButtons.enableOnlyClearAndReset();
				System.out.println("scheduler paused");

				if (!Settings.enableAnimation)
					forcePause();
			}
		}));
		updateUI.setCycleCount(Animation.INDEFINITE);
	}

	public void newSeries() {
		visualizer.newSeries();
	}

	public void resetSeries() {
		visualizer.resetSeries();
		sheets.resetSeries();
		progressIndicator.reset();
	}

	public void resetSheetsEvaluation() {
		sheets.resetEvaluation();
	}

	public void stop() {
		if (Settings.enableAnimation) {
			updateUI.stop();
		}
	}

	public void pause() {
		if (Settings.enableAnimation) {
			updateUI.pause();
		}
	}

	public void forcePause() {
		updateUI.pause();
	}

	public void resume() {
		if (Settings.enableAnimation) {
			updateUI.play();
		}
	}

	public void setDoneProcessing(boolean isDone) {
		isDoneProcessing = isDone;
		if (isDone && !Settings.enableAnimation) {
			Platform.runLater(() -> {
				visualizer.addLastWindowToChart();
				sheets.addLastCandidateSolutionToSheet();
				progressIndicator.setDone();
				controlButtons.enableOnlyClearAndReset();
			});
		}
	}

	public void initOneShotScheduler() {
		if (Settings.enableAnimation) {
			Platform.runLater(() -> {
				visualizer.initOneShotScheduler();
				sheets.initOneShotScheduler();
				// progressIndicator.initOneShotScheduler();
			});
		}
	}

	public boolean isDequeEmpty() {
		return visualizer.isDequeEmpty() || sheets.isDequeEmpty();
	}

	public VisualizerInterface getVisualizer() {
		return visualizer;
	}

	public CustomProgressIndicator getProgressIndidactor() {
		return progressIndicator;
	}

	public Sheets getSheets() {
		return sheets;
	}
}