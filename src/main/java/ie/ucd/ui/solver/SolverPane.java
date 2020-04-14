package ie.ucd.ui.solver;

import ie.ucd.Settings;
import ie.ucd.Common.SolverType;
import ie.ucd.ui.common.sheets.Sheets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

public class SolverPane extends ScrollPane {
	private Visualizer visualizer;
	private ControlButtons controlButtons;
	private Sheets sheets;

	private boolean isDoneProcessing;
	private Timeline updateUI;

	public SolverPane(Stage stage, SolverType solverType) {
		super();
		visualizer = new Visualizer(solverType);
		sheets = new Sheets(stage);
		controlButtons = new ControlButtons(this, solverType);
		initLayout();

		// setup a scheduled executor to periodically put data into the chart.
		initUpdateUIScheduler();
	}

	private void initLayout() {
		VBox vBox = new VBox();
		vBox.getChildren().add(new Label("Visualization"));
		vBox.getChildren().add(visualizer);
		vBox.getChildren().add(controlButtons);
		vBox.getChildren().add(sheets);

		setContent(vBox);
	}

	private void initUpdateUIScheduler() {
		updateUI = new Timeline(new KeyFrame(Duration.millis(1), ev -> {
			if (!sheets.isDequeEmpty() || !visualizer.isDequeEmpty()) {
				initOneShotScheduler();
			} else if (isDoneProcessing && isDequeEmpty()) {
				pause();
				controlButtons.enableOnlyClearAndReset();
				System.out.println("scheduler paused");
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

	public void resume() {
		if (Settings.enableAnimation) {
			updateUI.play();
		}
	}

	public void setDoneProcessing(boolean isDone) {
		if (Settings.enableAnimation) {
			isDoneProcessing = isDone;
		} else {
			Platform.runLater(() -> {
				visualizer.addLastWindowToChart();
				sheets.addLastCandidateSolutionToSheet();
			});
		}
	}

	public void initOneShotScheduler() {
		if (Settings.enableAnimation) {
			Platform.runLater(() -> {
				visualizer.initOneShotScheduler();
				sheets.initOneShotScheduler();
			});
		}
	}

	public boolean isDequeEmpty() {
		return visualizer.isDequeEmpty() || sheets.isDequeEmpty();
	}

	public VisualizerInterface getVisualizer() {
		return visualizer;
	}

	public Sheets getSheets() {
		return sheets;
	}
}