package ie.ucd.ui.navigation;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class NavigationPane extends Accordion {
	private TitledPane setupTP;
	private TitledPane saTP;
	private TitledPane gaTP;

	public NavigationPane() {
		super();
		initLayout();
	}

	private void initLayout() {
		setupTP = new SetupTP();
		saTP = new SimulatedAnnealingTP();
		gaTP = new GeneticAlgorithmTP();
		getPanes().addAll(setupTP, saTP, gaTP);
	}
}