package ie.ucd.ui.common.constraints;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Constraints extends VBox {
	private HardConstraints hard;
	private SoftConstraints soft;

	public Constraints() {
		super();
		initLayout();
	}

	public void initLayout() {
		getStylesheets().add("ui/solver/constraints.css");

		getChildren().add(new Label("Constraints"));

		hard = new HardConstraints();
		soft = new SoftConstraints();
		HBox hBox = new HBox(hard, soft);
		getChildren().add(hBox);
	}
}