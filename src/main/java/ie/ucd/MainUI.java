package ie.ucd;

import ie.ucd.Common.SolverType;
import ie.ucd.ui.navigation.NavigationPane;
import ie.ucd.ui.setup.SetupPane;
import ie.ucd.ui.solver.SolverPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainUI extends BorderPane {
	private NavigationPane navPane;
	private SetupPane setupPane;
	private SolverPane saPane;
	private SolverPane gaPane;

	public MainUI(Stage stage) {
		super();
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		setupPane = new SetupPane(stage);
		saPane = new SolverPane(stage, SolverType.SimulatedAnnealing);
		gaPane = new SolverPane(stage, SolverType.GeneticAlgorithm);

		navPane = new NavigationPane(this);

		getStylesheets().add("ui/solver/constraints.css");
		setLeft(navPane);
		showSetupPane();
	}

	public void showSetupPane() {
		setRight(setupPane);
	}

	public void showSAPane() {
		setRight(saPane);
	}

	public void showGAPane() {
		setRight(gaPane);
	}
}