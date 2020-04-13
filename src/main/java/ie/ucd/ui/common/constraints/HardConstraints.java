package ie.ucd.ui.common.constraints;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HardConstraints extends VBox {
	private CheckBox studentAssignedPreferredProject;
	private CheckBox sameStream;
	private CheckBox studentAssignedOneProject;
	private CheckBox projectAssignedToOneStudent;

	public HardConstraints() {
		this(false);
	}

	public HardConstraints(boolean enableToggle) {
		super();
		initLayout(enableToggle);
	}

	public void initLayout(boolean enableToggle) {
		studentAssignedPreferredProject = new CheckBox("Each student is assigned exactly one of their preferred projects.");
		sameStream = new CheckBox("Each student is assigned projects with the same stream as the student.");
		studentAssignedOneProject = new CheckBox("Each student is assigned exactly one project.");
		projectAssignedToOneStudent = new CheckBox("Each project is assigned to at most one student.");

		if (!enableToggle) {
			studentAssignedPreferredProject.setDisable(true);
			sameStream.setDisable(true);
			sameStream.setSelected(true);
			studentAssignedOneProject.setDisable(true);
			projectAssignedToOneStudent.setDisable(true);
		}

		getChildren().addAll(new Label("Hard"), studentAssignedPreferredProject, sameStream, studentAssignedOneProject,
				projectAssignedToOneStudent);
	}
}