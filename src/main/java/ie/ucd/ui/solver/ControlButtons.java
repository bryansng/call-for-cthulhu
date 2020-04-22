package ie.ucd.ui.solver;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.Common.SolverType;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.solvers.GeneticAlgorithm;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.solvers.Solver;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlButtons extends HBox {
	private SolverType solverType;
	private Solver solver;
	private SolverPane solverPane;

	private boolean isRunning;
	private boolean isPaused;

	private Button play;
	private Button pause;
	private Button clearAndReset;
	private Button step;

	private volatile Thread thread;

	public ControlButtons(SolverPane solverPane, SolverType solverType) {
		super();
		this.solverPane = solverPane;
		this.solverType = solverType;
		resetStates();
		initLayout();
	}

	private void initLayout() {
		play = new Button("Play");
		play.setOnAction((event) -> {
			if (isPaused && isRunning) {
				isPaused = false;
				solverPane.resume();
				play.setDisable(true);
				pause.setDisable(false);
				step.setDisable(true);
			} else if (!isRunning) {
				isRunning = true;
				solverPane.resume();
				solverPane.resetSeries();
				solverPane.resetSheetsEvaluation();
				startThread();
				play.setDisable(true);
				clearAndReset.setDisable(false);
				step.setDisable(true);
				if (Settings.enableAnimation) {
					pause.setDisable(false);
				} else {
					pause.setDisable(true);
				}
			}
		});

		pause = new Button("Pause");
		pause.setOnAction((event) -> {
			if (isRunning && !isPaused) {
				isPaused = true;
				solverPane.pause();
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
				solverPane.pause();
				solverPane.resetSeries();
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
				if (solverPane.isDequeEmpty()) {
					solver.oneStep();
					// System.out.println("visualizer deque empty");
				}
				solverPane.initOneShotScheduler();
				// System.out.println("one shot stepping");
			}
		});

		getChildren().add(play);
		getChildren().add(pause);
		getChildren().add(clearAndReset);
		getChildren().add(step);

		play.setDisable(false);
		play.requestFocus();
		pause.setDisable(true);
		clearAndReset.setDisable(true);
		step.setDisable(true);
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
		// update number of constraints toggled by user before running solver.
		// this is required to calculate Strength correctly.
		Settings.updateNumEnabledConstraints();

		Solver solver;
		Parser parser;
		CandidateSolution startingSolution;
		if (Common.IS_DEBUGGING_SOLVERS) {
			parser = new Parser();
			startingSolution = new CandidateSolution(Settings.numberOfStudents, parser.getStaffMembers(), parser.getNames(),
					null, null);
			startingSolution.generateProjects();
			startingSolution.generateStudents();
		}
		switch (solverType) {
			case GeneticAlgorithm:
				if (Common.IS_DEBUGGING_SOLVERS) {
					solver = new GeneticAlgorithm(startingSolution, solverPane);
				} else {
					solver = new GeneticAlgorithm(Settings.gaMutationChance, Settings.gaCrossoverChance,
							Settings.gaNumberOfGeneration, Settings.gaPopulationSize, Settings.gaPickFittestParentsChance,
							Settings.setupSolution, solverPane);
				}
				break;
			case SimulatedAnnealing:
			default:
				if (Common.IS_DEBUGGING_SOLVERS) {
					solver = new SimulatedAnnealing(startingSolution, solverPane);
				} else {
					solver = new SimulatedAnnealing(Settings.saStartTemperature, Settings.saCoolingRate,
							Settings.saMinTemperature, Settings.saMaxIteration, Settings.setupSolution, solverPane);
				}
				break;
		}
		this.solver = solver;
		return solver;
	}
}