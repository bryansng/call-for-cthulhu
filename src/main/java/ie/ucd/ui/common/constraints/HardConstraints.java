package ie.ucd.ui.common.constraints;

import ie.ucd.Common;
import ie.ucd.Settings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HardConstraints extends VBox {
	private CheckBox studentAssignedPreferredProject;
	private CheckBox sameStream;
	private CheckBox studentAssignedOneProject;
	private CheckBox projectAssignedToOneStudent;
	private String studentAssignedPreferredProjectText;
	private String sameStreamText;
	private String studentAssignedOneProjectText;
	private String projectAssignedToOneStudentText;

	public HardConstraints() {
		this(false, false);
	}

	public HardConstraints(boolean enableToggle, boolean enableToggleSettings) {
		super();
		initLayout(enableToggle, enableToggleSettings);
	}

	public void initLayout(boolean enableToggle, boolean enableToggleSettings) {
		studentAssignedPreferredProjectText = "Each student is assigned exactly one of their preferred projects.";
		sameStreamText = "Each student is assigned projects with the same stream as the student.";
		studentAssignedOneProjectText = "Each student is assigned exactly one project.";
		projectAssignedToOneStudentText = "Each project is assigned to at most one student.";

		studentAssignedPreferredProject = new CheckBox(studentAssignedPreferredProjectText);
		studentAssignedPreferredProject.setWrapText(true);
		sameStream = new CheckBox(sameStreamText);
		sameStream.setWrapText(true);
		studentAssignedOneProject = new CheckBox(studentAssignedOneProjectText);
		studentAssignedOneProject.setWrapText(true);
		projectAssignedToOneStudent = new CheckBox(projectAssignedToOneStudentText);
		projectAssignedToOneStudent.setWrapText(true);

		if (!enableToggle) {
			studentAssignedPreferredProject.setDisable(true);
			sameStream.setDisable(true);
			studentAssignedOneProject.setDisable(true);
			projectAssignedToOneStudent.setDisable(true);
		}
		if (enableToggleSettings) {
			studentAssignedPreferredProject.setOnAction((evt) -> {
				Settings.enableStudentAssignedPreferredProject = studentAssignedPreferredProject.isSelected();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(
							String.format("%b %b %b %b", Settings.enableStudentAssignedPreferredProject, Settings.enableSameStream,
									Settings.enableStudentAssignedOneProject, Settings.enableProjectAssignedToOneStudent));
			});

			sameStream.setOnAction((evt) -> {
				Settings.enableSameStream = sameStream.isSelected();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(
							String.format("%b %b %b %b", Settings.enableStudentAssignedPreferredProject, Settings.enableSameStream,
									Settings.enableStudentAssignedOneProject, Settings.enableProjectAssignedToOneStudent));
			});

			studentAssignedOneProject.setOnAction((evt) -> {
				Settings.enableStudentAssignedOneProject = studentAssignedOneProject.isSelected();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(
							String.format("%b %b %b %b", Settings.enableStudentAssignedPreferredProject, Settings.enableSameStream,
									Settings.enableStudentAssignedOneProject, Settings.enableProjectAssignedToOneStudent));
			});

			projectAssignedToOneStudent.setOnAction((evt) -> {
				Settings.enableProjectAssignedToOneStudent = projectAssignedToOneStudent.isSelected();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(
							String.format("%b %b %b %b", Settings.enableStudentAssignedPreferredProject, Settings.enableSameStream,
									Settings.enableStudentAssignedOneProject, Settings.enableProjectAssignedToOneStudent));
			});

			studentAssignedPreferredProject.setSelected(Settings.enableStudentAssignedPreferredProject);
			sameStream.setSelected(Settings.enableSameStream);
			studentAssignedOneProject.setSelected(Settings.enableStudentAssignedOneProject);
			projectAssignedToOneStudent.setSelected(Settings.enableProjectAssignedToOneStudent);
		}

		getChildren().addAll(new Label("Hard"), studentAssignedPreferredProject, sameStream, studentAssignedOneProject,
				projectAssignedToOneStudent);
	}

	public void updateStudentAssignedPreferredProject(boolean isNotViolated, Integer violationCount, Integer total) {
		if (Settings.enableStudentAssignedPreferredProject) {
			studentAssignedPreferredProject.setIndeterminate(false);
			studentAssignedPreferredProject.setSelected(isNotViolated);
			studentAssignedPreferredProject.setText(studentAssignedPreferredProjectText
					+ (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
		}
	}

	public void updateSameStream(boolean isNotViolated, Integer violationCount, Integer total) {
		if (Settings.enableSameStream) {
			sameStream.setIndeterminate(false);
			sameStream.setSelected(isNotViolated);
			sameStream.setText(sameStreamText + (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
		}
	}

	public void updateStudentAssignedOneProject(boolean isNotViolated, Integer violationCount, Integer total) {
		if (Settings.enableStudentAssignedOneProject) {
			studentAssignedOneProject.setIndeterminate(false);
			studentAssignedOneProject.setSelected(isNotViolated);
			studentAssignedOneProject.setText(
					studentAssignedOneProjectText + (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
		}
	}

	public void updateProjectAssignedToOneStudent(boolean isNotViolated, Integer violationCount, Integer total) {
		if (Settings.enableProjectAssignedToOneStudent) {
			projectAssignedToOneStudent.setIndeterminate(false);
			projectAssignedToOneStudent.setSelected(isNotViolated);
			projectAssignedToOneStudent.setText(
					projectAssignedToOneStudentText + (violationCount == null ? "" : " (" + violationCount + "/" + total + ")"));
		}
	}

	public void reset() {
		studentAssignedPreferredProject.setIndeterminate(true);
		studentAssignedPreferredProject.setSelected(false);
		studentAssignedPreferredProject.setText(studentAssignedPreferredProjectText);
		sameStream.setIndeterminate(true);
		sameStream.setSelected(false);
		sameStream.setText(sameStreamText);
		studentAssignedOneProject.setIndeterminate(true);
		studentAssignedOneProject.setSelected(false);
		studentAssignedOneProject.setText(studentAssignedOneProjectText);
		projectAssignedToOneStudent.setIndeterminate(true);
		projectAssignedToOneStudent.setSelected(false);
		projectAssignedToOneStudent.setText(projectAssignedToOneStudentText);
	}
}