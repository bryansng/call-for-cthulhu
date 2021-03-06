package ie.ucd;

import ie.ucd.Common.SolverType;
import ie.ucd.Settings.Theme;
import ie.ucd.ui.about.AboutPane;
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
	private AboutPane aboutPane;

	public MainUI(Stage stage) {
		super();
		stage.setMaximized(true);
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		mainContainer = new HBox();

		setupPane = new SetupPane(stage, this);
		saPane = new SolverPane(stage, SolverType.SimulatedAnnealing);
		gaPane = new SolverPane(stage, SolverType.GeneticAlgorithm);
		aboutPane = new AboutPane();
		// setupPane.setMinWidth(1400);
		// saPane.setMinWidth(1400);
		// gaPane.setMinWidth(1400);
		// aboutPane.setMinWidth(1400);

		navPane = new NavigationPane(this);
		navPane.setMinWidth(230);
		// navPane.setMaxWidth(250);

		if (Settings.enableDarkTheme)
			handleThemes(Theme.DARK);
		else
			handleThemes(Theme.ORIGINAL);

		mainContainer.getChildren().addAll(navPane, setupPane);
		add(mainContainer, 0, 0);
		setAlignment(Pos.TOP_LEFT);
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

	public void showAboutPane() {
		mainContainer.getChildren().set(1, aboutPane);
	}

	public void setEnableNavigateSolvers(boolean value) {
		navPane.setEnableNavigateSolvers(value);
	}
}