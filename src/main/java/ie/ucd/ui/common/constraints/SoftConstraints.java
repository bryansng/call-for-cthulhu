package ie.ucd.ui.common.constraints;

import ie.ucd.Settings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SoftConstraints extends VBox {
	private CheckBox equallyDistributedAcrossSupervisors;
	private CheckBox higherGPAHigherPreferences;

	public SoftConstraints() {
		this(false, false);
	}

	public SoftConstraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		equallyDistributedAcrossSupervisors = new CheckBox(
				"Projects are more-or-less equally distributed across supervisors.");
		higherGPAHigherPreferences = new CheckBox(
				"Students with higher GPAs tend to get higher preferences than those with lower GPAs, or higher GPA means a greater chance of getting one's preferred projects.");

		if (!enableToggle) {
			equallyDistributedAcrossSupervisors.setDisable(true);
			higherGPAHigherPreferences.setDisable(true);
		}
		if (enableToggleSettings) {
			equallyDistributedAcrossSupervisors.setOnAction((evt) -> {
				Settings.enableEquallyDistributedAcrossSupervisors = equallyDistributedAcrossSupervisors.isSelected();
			});

			higherGPAHigherPreferences.setOnAction((evt) -> {
				Settings.enableHigherGPAHigherPreferences = higherGPAHigherPreferences.isSelected();
			});
		}

		getChildren().addAll(new Label("Soft"), equallyDistributedAcrossSupervisors, higherGPAHigherPreferences);
	}

	public void setSelected(boolean equallyDistributedAcrossSupervisorsValue, boolean higherGPAHigherPreferencesValue) {
		equallyDistributedAcrossSupervisors.setSelected(equallyDistributedAcrossSupervisorsValue);
		higherGPAHigherPreferences.setSelected(higherGPAHigherPreferencesValue);
	}
}