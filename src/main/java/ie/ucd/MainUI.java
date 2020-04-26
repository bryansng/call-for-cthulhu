package ie.ucd;

import ie.ucd.Common.SolverType;
import ie.ucd.Settings.Theme;
import ie.ucd.ui.navigation.NavigationPane;
import ie.ucd.ui.setup.SetupPane;
import ie.ucd.ui.solver.SolverPane;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainUI extends GridPane {
	private HBox mainContainer;
	private NavigationPane navPane;
	private SetupPane setupPane;
	private SolverPane saPane;
	private SolverPane gaPane;

	public MainUI(Stage stage) {
		super();
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		mainContainer = new HBox();

		setupPane = new SetupPane(stage, this);
		saPane = new SolverPane(stage, SolverType.SimulatedAnnealing);
		gaPane = new SolverPane(stage, SolverType.GeneticAlgorithm);

		navPane = new NavigationPane(this);

		if (Settings.enableDarkTheme)
			handleThemes(Theme.DARK);
		else
			handleThemes(Theme.ORIGINAL);

		mainContainer.getChildren().addAll(navPane, setupPane);
		add(mainContainer, 0, 0);
		setAlignment(Pos.TOP_CENTER);
	}

	public void handleThemes(Theme theme) {
		switch (theme) {
			case DARK:
				getStylesheets().setAll("ui/solver/constraints.css", "ui/common.css", "ui/modena_dark.css");
				break;
			default:
				getStylesheets().setAll("ui/solver/constraints.css", "ui/common.css");
				break;
		}
	}

	public void showSetupPane() {
		mainContainer.getChildren().set(1, setupPane);
	}

	public void showSAPane() {
		mainContainer.getChildren().set(1, saPane);
	}

	public void showGAPane() {
		mainContainer.getChildren().set(1, gaPane);
	}

	public void setEnableNavigateSolvers(boolean value) {
		navPane.setEnableNavigateSolvers(value);
	}
}