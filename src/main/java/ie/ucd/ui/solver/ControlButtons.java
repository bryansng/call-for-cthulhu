package ie.ucd.ui.solver;

import java.io.IOException;
import ie.ucd.Common.SolverType;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.solvers.GeneticAlgorithm;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.solvers.Solver;
import ie.ucd.ui.common.sheets.Sheets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlButtons extends HBox {
	private SolverType solverType;
	private Solver solver;
	private Visualizer visualizer;
	private Sheets sheets;

	private boolean isRunning;
	private boolean isPaused;

	private Button play;
	private Button pause;
	private Button clearAndReset;
	private Button step;

	private volatile Thread thread;

	public ControlButtons(Visualizer visualizer, Sheets sheets, SolverType solverType) {
		super();
		this.visualizer = visualizer;
		this.sheets = sheets;
		this.solverType = solverType;
		resetStates();
		initLayout();
	}

	private void initLayout() {
		play = new Button("Play");
		play.setOnAction((event) -> {
			if (isPaused && isRunning) {
				isPaused = false;
				solver.resume();
				visualizer.resume();
				sheets.resume();
				play.setDisable(true);
				pause.setDisable(false);
				step.setDisable(true);
			} else if (!isRunning) {
				isRunning = true;
				visualizer.resume();
				visualizer.resetSeries();
				sheets.resume();
				sheets.resetSeries();
				startThread();
				play.setDisable(true);
				pause.setDisable(false);
				clearAndReset.setDisable(false);
				step.setDisable(true);
			}
		});

		pause = new Button("Pause");
		pause.setOnAction((event) -> {
			if (isRunning && !isPaused) {
				isPaused = true;
				visualizer.pause();
				sheets.pause();
				solver.suspend();
				play.setDisable(false);
				play.requestFocus();
				pause.setDisable(true);
				step.setDisable(false);
			}
		});

		clearAndReset = new Button("Clear and Reset");
		clearAndReset.setOnAction((event) -> {
			if (isRunning) {
				resetStates();
				visualizer.pause();
				visualizer.resetSeries();
				sheets.pause();
				sheets.resetSeries();
				solver.stop();
				play.setDisable(false);
				play.requestFocus();
				pause.setDisable(true);
				clearAndReset.setDisable(true);
				step.setDisable(true);
			}
		});

		step = new Button("Step");
		step.setOnAction((event) -> {
			if (isRunning && isPaused) {
				// if visualizer.deque empty, step through solver, then visualizer.
				// else, just take the data from queue. (because processing is faster than visualizer can animate)
				if (visualizer.isDequeEmpty() || sheets.isDequeEmpty()) {
					solver.oneStep();
					// System.out.println("visualizer deque empty");
				}
				visualizer.initOneShotScheduler();
				sheets.initOneShotScheduler();
				// System.out.println("one shot stepping");
			}
		});

		getChildren().add(play);
		getChildren().add(pause);
		getChildren().add(clearAndReset);
		getChildren().add(step);
	}

	// used when visualizer one animating.
	public void enableOnlyClearAndReset() {
		if (isRunning) {
			play.setDisable(true);
			pause.setDisable(true);
			clearAndReset.setDisable(false);
			step.setDisable(true);
			System.out.println("buttons reset");
		}
	}

	private void resetStates() {
		isRunning = false;
		isPaused = false;
	}

	private void startThread() {
		// System.out.println("Starting thread");
		thread = new Thread(createNewSolver());
		thread.setDaemon(true);
		thread.start();
	}

	private Solver createNewSolver() {
		try {
			//! should take in projects and students from setup pane.
			Solver solver;
			Parser parser = new Parser();
			CandidateSolution solution = new CandidateSolution(500, parser.getStaffMembers(), parser.getNames(), null, null);
			solution.generateProjects();
			solution.generateStudents();
			switch (solverType) {
				case GeneticAlgorithm:
					solver = new GeneticAlgorithm(solution);
				case SimulatedAnnealing:
				default:
					//! simulated annealing here should take in the updated parameters.
					solver = new SimulatedAnnealing(solution, visualizer, sheets);
					break;
			}
			this.solver = solver;
			return solver;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}