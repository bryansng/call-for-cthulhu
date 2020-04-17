package ie.ucd;

import ie.ucd.Common.SolverType;
import ie.ucd.ui.navigation.NavigationPane;
import ie.ucd.ui.solver.SolverPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainUI extends BorderPane {
	private NavigationPane navPane;
	private SolverPane solverPane;

	public MainUI(Stage stage) {
		super();
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		navPane = new NavigationPane();

		// solverPane = new SolverPane(stage, SolverType.GeneticAlgorithm);
		solverPane = new SolverPane(stage, SolverType.SimulatedAnnealing);
		solverPane.getStylesheets().add("ui/solver/constraints.css");

		setLeft(navPane);
		setRight(solverPane);
	}
}