package ie.ucd.ui.navigation;

import ie.ucd.Common;
import ie.ucd.MainUI;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class NavigationPane extends Accordion {
	private TitledPane setupTP;
	private TitledPane saTP;
	private TitledPane gaTP;
	private TitledPane aboutTP;

	public NavigationPane(MainUI mainUI) {
		super();
		initLayout(mainUI);
	}

	private void initLayout(MainUI mainUI) {
		setupTP = new SetupTP();
		setupTP.setOnMouseClicked((evt) -> {
			mainUI.showSetupPane();
		});

		saTP = new SimulatedAnnealingTP();
		saTP.setOnMouseClicked((evt) -> {
			mainUI.showSAPane();
		});

		gaTP = new GeneticAlgorithmTP();
		gaTP.setOnMouseClicked((evt) -> {
			mainUI.showGAPane();
		});

		aboutTP = new AboutTP();
		aboutTP.setOnMouseClicked((evt) -> {
			mainUI.showAboutPane();
		});

		if (!Common.IS_DEBUGGING_SOLVERS) {
			saTP.setDisable(true);
			gaTP.setDisable(true);
		}
		getPanes().addAll(setupTP, saTP, gaTP, aboutTP);
	}

	public void setEnableNavigateSolvers(boolean value) {
		saTP.setDisable(!value);
		gaTP.setDisable(!value);
	}
}