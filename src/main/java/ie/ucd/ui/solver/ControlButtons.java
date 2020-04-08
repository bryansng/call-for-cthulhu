package ie.ucd.ui.solver;

import java.io.IOException;

import ie.ucd.Common.SolverType;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.solvers.GeneticAlgorithm;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.solvers.Solver;
import ie.ucd.ui.common.Sheet;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlButtons extends HBox {
	private SolverType solverType;
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

	public ControlButtons(Visualizer visualizer, Sheet sheet, SolverType solverType) {
		super();
		this.visualizer = visualizer;
		this.sheet = sheet;
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
				// visualizer.resumeAddToGraphScheduler();
				pause.setDisable(false);
				play.setDisable(true);
				step.setDisable(true);
			} else if (!isRunning) {
				isRunning = true;
				// visualizer.resumeAddToGraphScheduler();
				startThread();
				clearAndReset.setDisable(false);
				pause.setDisable(false);
				play.setDisable(true);
				step.setDisable(true);
			}
		});

		pause = new Button("Pause");
		pause.setOnAction((event) -> {
			if (isRunning && !isPaused) {
				isPaused = true;
				// visualizer.pauseAddToGraphScheduler();
				solver.suspend();
				pause.setDisable(true);
				play.setDisable(false);
				step.setDisable(false);
			}
		});

		clearAndReset = new Button("Clear and Reset");
		clearAndReset.setOnAction((event) -> {
			if (isRunning) {
				resetStates();
				// visualizer.pauseAddToGraphScheduler();
				// visualizer.resetSeries();
				solver.stop();
				clearAndReset.setDisable(true);
				pause.setDisable(true);
				play.setDisable(false);
				step.setDisable(true);
			}
		});

		step = new Button("Step");
		step.setOnAction((event) -> {
			if (isRunning && isPaused) {
				// if visualizer.deque empty, step through solver, then visualizer.
				// else, just take the data from queue. (because processing is faster than visualizer can animate)
				if (visualizer.isDequeEmpty()) {
					solver.oneStep();
					System.out.println("visualizer deque empty");
				}
				visualizer.initOneShotScheduler();
				System.out.println("one shot stepping");
			}
		});

		getChildren().add(play);
		getChildren().add(pause);
		getChildren().add(clearAndReset);
		// getChildren().add(step);
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	private void resetStates() {
		isRunning = false;
		isPaused = false;
	}

	private void startThread() {
		System.out.println("Starting thread");
		thread = new Thread(createNewSolver());
		thread.setDaemon(true);
		thread.start();
	}

	private Solver createNewSolver() {
		try {
			Solver solver;
			Parser parser = new Parser();
			CandidateSolution solution = new CandidateSolution(500, parser.allStaffsProjects, parser.allNames, null, null);
			solution.generateProjects();
			solution.generateStudents();
			switch (solverType) {
				case GeneticAlgorithm:
					solver = new GeneticAlgorithm(solution);
				case SimulatedAnnealing:
				default:
					//! simulated annealing here should take in the updated parameters.
					solver = new SimulatedAnnealing(solution, visualizer);
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