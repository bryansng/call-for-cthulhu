package ie.ucd.ui.common.constraints;

import ie.ucd.Settings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HardConstraints extends VBox {
	private CheckBox studentAssignedPreferredProject;
	private CheckBox sameStream;
	private CheckBox studentAssignedOneProject;
	private CheckBox projectAssignedToOneStudent;

	public HardConstraints() {
		this(false, false);
	}

	public HardConstraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		studentAssignedPreferredProject = new CheckBox("Each student is assigned exactly one of their preferred projects.");
		sameStream = new CheckBox("Each student is assigned projects with the same stream as the student.");
		studentAssignedOneProject = new CheckBox("Each student is assigned exactly one project.");
		projectAssignedToOneStudent = new CheckBox("Each project is assigned to at most one student.");

		if (!enableToggle) {
			studentAssignedPreferredProject.setDisable(true);
			sameStream.setDisable(true);
			studentAssignedOneProject.setDisable(true);
			projectAssignedToOneStudent.setDisable(true);
		}
		if (enableToggleSettings) {
			studentAssignedPreferredProject.setOnAction((evt) -> {
				Settings.enableStudentAssignedPreferredProject = studentAssignedPreferredProject.isSelected();
			});

			sameStream.setOnAction((evt) -> {
				Settings.enableSameStream = sameStream.isSelected();
			});

			studentAssignedOneProject.setOnAction((evt) -> {
				Settings.enableStudentAssignedOneProject = studentAssignedOneProject.isSelected();
			});

			projectAssignedToOneStudent.setOnAction((evt) -> {
				Settings.enableProjectAssignedToOneStudent = projectAssignedToOneStudent.isSelected();
			});
		}

		getChildren().addAll(new Label("Hard"), studentAssignedPreferredProject, sameStream, studentAssignedOneProject,
				projectAssignedToOneStudent);
	}

	public void setSelected(boolean studentAssignedPreferredProjectValue, boolean sameStreamValue,
			boolean studentAssignedOneProjectValue, boolean projectAssignedToOneStudentVale) {
		studentAssignedPreferredProject.setSelected(studentAssignedPreferredProjectValue);
		sameStream.setSelected(sameStreamValue);
		studentAssignedOneProject.setSelected(studentAssignedOneProjectValue);
		projectAssignedToOneStudent.setSelected(projectAssignedToOneStudentVale);
	}
}