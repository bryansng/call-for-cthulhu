package ie.ucd.ui.common;

import ie.ucd.interfaces.Solver;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlButtons extends HBox {
	private Solver solver;
	private Visualizer visualizer;
	private Sheet sheet;

	private boolean isRunning;
	private boolean isPaused;

	private Button play;
	private Button pause;
	private Button clearAndReset;
	private Button step;

	private volatile Thread thread;

	public ControlButtons(Visualizer visualizer, Sheet sheet) {
		super();
		this.visualizer = visualizer;
		this.sheet = sheet;
		resetStates();
		initLayout();
	}

	private void initLayout() {
		play = new Button("Play");
		play.setOnAction((event) -> {
			if (isPaused && isRunning) {
				isPaused = false;
				// visualizer.resumeAddToGraphScheduler();
			} else if (!isRunning) {
				isRunning = true;
				startThread();
			}
		});

		pause = new Button("Pause");
		pause.setOnAction((event) -> {
			if (isRunning && !isPaused) {
				isPaused = true;
				// visualizer.pauseAddToGraphScheduler();
			}
		});

		clearAndReset = new Button("Clear and Reset");
		clearAndReset.setOnAction((event) -> {
			if (isRunning) {
				visualizer.stopAddToGraphScheduler();
				isRunning = false;
				stopThread();
			}
		});

		step = new Button("Step");

		getChildren().add(play);
		getChildren().add(pause);
		getChildren().add(clearAndReset);
		getChildren().add(step);
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	private void resetStates() {
		isRunning = false;
		isPaused = false;
	}

	private void stopThread() {
		thread = null;
	}

	private void startThread() {
		thread = new Thread(() -> {
			solver.run();
		});
		thread.setDaemon(true);
		thread.start();
	}

	private void pauseThread() {

	}
}