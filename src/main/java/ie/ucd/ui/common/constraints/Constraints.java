package ie.ucd.ui.common.constraints;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Constraints extends GridPane {
	private HardConstraints hard;
	private SoftConstraints soft;

	public Constraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		getStylesheets().add("ui/solver/constraints.css");

		Label label = new Label("Constraints");
		label.getStyleClass().add("sub-label");

		hard = new HardConstraints(enableToggle, enableToggleSettings);
		soft = new SoftConstraints(enableToggle, enableToggleSettings);

		hard.setMaxWidth(500);
		hard.setSpacing(5);
		soft.setMaxWidth(500);
		soft.setSpacing(5);

		setHgap(30);

		add(label, 0, 0);
		add(hard, 0, 1);
		add(soft, 1, 1);
	}

	public HardConstraints getHardConstraints() {
		return hard;
	}

	public SoftConstraints getSoftConstraints() {
		return soft;
	}

	public void reset() {
		hard.reset();
		soft.reset();
	}
}