package ie.ucd.ui.common.constraints;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SoftConstraints extends VBox {
	private CheckBox equallyDistributedAcrossSupervisors;
	private CheckBox higherGPAHigherPreferences;

	public SoftConstraints() {
		super();
		initLayout();
	}

	public void initLayout() {
		equallyDistributedAcrossSupervisors = new CheckBox(
				"Projects are more-or-less equally distributed across supervisors.");
		higherGPAHigherPreferences = new CheckBox(
				"Students with higher GPAs tend to get higher preferences than those with lower GPAs, or higher GPA means a greater chance of getting one's preferred projects.");

		getChildren().addAll(new Label("Soft"), equallyDistributedAcrossSupervisors, higherGPAHigherPreferences);
	}
}