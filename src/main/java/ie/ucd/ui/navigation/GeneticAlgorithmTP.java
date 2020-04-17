package ie.ucd.ui.navigation;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class GeneticAlgorithmTP extends TitledPane {
	public GeneticAlgorithmTP() {
		super();
		initLayout();
	}

	private void initLayout() {
		setText("Genetic Algorithm");
		setContent(initContent());
	}

	private Node initContent() {
		return null;
	}
}