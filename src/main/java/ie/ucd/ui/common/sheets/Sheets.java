package ie.ucd.ui.common.sheets;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Sheets extends VBox {
	private StudentSheet currSheet;
	private StudentSheet bestSheet;

	public Sheets(Stage stage) {
		super();
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		getChildren().add(new Label("Solutions"));

		currSheet = new StudentSheet(stage, true, true);
		bestSheet = new StudentSheet(stage, true, true);

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new Tab("Current Solution", currSheet));
		tabPane.getTabs().add(new Tab("Best Solution", bestSheet));
		getChildren().add(tabPane);
	}

	public StudentSheet getCurrentSheet() {
		return currSheet;
	}

	public StudentSheet getBestSheet() {
		return bestSheet;
	}
}