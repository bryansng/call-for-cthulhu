package ie.ucd.ui.common;

import ie.ucd.solvers.Solver;
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
	private volatile boolean isThreadSuspended;

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
				solver.resume();
				visualizer.resumeAddToGraphScheduler();
			} else if (!isRunning) {
				isRunning = true;
				solver.restart();
				startThread();
			}
		});

		pause = new Button("Pause");
		pause.setOnAction((event) -> {
			if (isRunning && !isPaused) {
				isPaused = true;
				visualizer.pauseAddToGraphScheduler();
				solver.suspend();
			}
		});

		clearAndReset = new Button("Clear and Reset");
		clearAndReset.setOnAction((event) -> {
			if (isRunning) {
				resetStates();
				visualizer.pauseAddToGraphScheduler();
				solver.stop();
			}
		});

		step = new Button("Step");
		step.setOnAction((event) -> {
			if (isRunning && isPaused) {
				// if visualizer.deque empty, step through solver, then visualizer.
				// else, just take the data from queue. (because processing is faster than visualizer can animate)
				if (visualizer.isDequeEmpty()) {
					solver.oneStep();
				}
				visualizer.initOneShotScheduler();
			}
		});

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
		isThreadSuspended = false;
	}

	private void startThread() {
		System.out.println("Starting thread");
		thread = new Thread(solver);
		thread.setDaemon(true);
		thread.start();
	}

	private synchronized void stopThread() {
		System.out.println("Stopping thread");
		thread = null;
	}

	private void runThread() {
		System.out.println("Running thread");
		// Thread thisThread = Thread.currentThread();
		// while (thread == thisThread) {
		// 	try {
		// 		System.out.println("sleeping");

		// 		synchronized (this) {
		// 			while (isThreadSuspended && thread == thisThread)
		// 				thread.wait();
		// 		}
		// 	} catch (InterruptedException e) {
		// 	}
		// }
		while (true) {
			try {
				System.out.println("sleeping");

				if (isThreadSuspended) {
					synchronized (this) {
						while (isThreadSuspended) {
							thread.wait();
							System.out.println("processing thread waiting");
						}
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private synchronized void checkIfSuspendedThenNotify() {
		System.out.println("Checking if suspended, then notify thread");
		isThreadSuspended = !isThreadSuspended;

		if (!isThreadSuspended) {
			notify();
			System.out.println("UI thread notifying");
		}
	}
}