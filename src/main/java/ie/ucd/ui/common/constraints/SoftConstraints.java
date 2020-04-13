package ie.ucd.ui.common.constraints;

import ie.ucd.Settings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SoftConstraints extends VBox {
	private CheckBox equallyDistributedAcrossSupervisors;
	private CheckBox higherGPAHigherPreferences;
	private String equallyDistributedAcrossSupervisorsText;
	private String higherGPAHigherPreferencesText;

	public SoftConstraints() {
		this(false, false);
	}

	public SoftConstraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		equallyDistributedAcrossSupervisorsText = "Projects are more-or-less equally distributed across supervisors.";
		higherGPAHigherPreferencesText = "Students with higher GPAs tend to get higher preferences than those with lower GPAs, or higher GPA means a greater chance of getting one's preferred projects.";

		equallyDistributedAcrossSupervisors = new CheckBox(equallyDistributedAcrossSupervisorsText);
		higherGPAHigherPreferences = new CheckBox(higherGPAHigherPreferencesText);

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

	public void updateEquallyDistributedAcrossSupervisors(boolean isNotViolated, Integer violationCount, Integer total) {
		equallyDistributedAcrossSupervisors.setSelected(isNotViolated);
		equallyDistributedAcrossSupervisors.setText(equallyDistributedAcrossSupervisorsText
				+ (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
	}

	public void updateHigherGPAHigherPreferences(boolean isNotViolated, Integer violationCount, Integer total) {
		higherGPAHigherPreferences.setSelected(isNotViolated);
		higherGPAHigherPreferences.setText(
				higherGPAHigherPreferencesText + (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
	}
}