package ie.ucd.ui.common.constraints;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Constraints extends VBox {
	private HardConstraints hard;
	private SoftConstraints soft;

	public Constraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		getStylesheets().add("ui/solver/constraints.css");

		getChildren().add(new Label("Constraints"));

		hard = new HardConstraints(enableToggle, enableToggleSettings);
		soft = new SoftConstraints(enableToggle, enableToggleSettings);
		HBox hBox = new HBox(hard, soft);
		getChildren().add(hBox);
	}

	public HardConstraints getHardConstraints() {
		return hard;
	}

	public SoftConstraints getSoftConstraints() {
		return soft;
	}
}