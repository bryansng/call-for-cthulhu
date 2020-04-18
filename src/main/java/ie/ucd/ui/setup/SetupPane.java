package ie.ucd.ui.setup;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.constraints.Constraints;
import ie.ucd.ui.common.sheets.ProjectSheet;
import ie.ucd.ui.common.sheets.Sheet;
import ie.ucd.ui.common.sheets.StudentSheet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SetupPane extends ScrollPane {
	private Constraints constraints;
	private Slider importanceOfGPA;
	private Sheet<Project> projectSheet;
	private Sheet<Student> studentSheet;

	// 1. settings.
	// constraints (updates settings)
	// other settings. (also updates settings)
	// Importance of GPA

	// 2. load projects.
	// Load from file
	// Randomly Generate

	// 3. load students.
	// Load from file.
	// Randomly Generate

	public SetupPane(Stage stage) {
		super();
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		VBox vBox = new VBox();
		Label labelSettings = new Label("1. Settings");
		Label sublabelOthers = new Label("Others");
		Label labelProjects = new Label("2. Load/Generate Projects");
		Label labelStudents = new Label("3. Load/Generate Students");
		constraints = new Constraints(true, true);
		projectSheet = new ProjectSheet(stage, true, true);
		studentSheet = new StudentSheet(stage, true, true, false);
		vBox.getChildren().addAll(labelSettings, constraints, sublabelOthers, initImportanceOfGPA(), initEnableAnimation(),
				labelProjects, projectSheet, labelStudents, studentSheet);
		setContent(vBox);
	}

	private Node initImportanceOfGPA() {
		Label textValue = new Label(String.format("%.4f", Settings.importanceOfGPA));
		importanceOfGPA = new Slider(0.0, 1.0, Settings.importanceOfGPA);
		importanceOfGPA.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				textValue.setText(String.format("%.4f", newValue));
				Settings.importanceOfGPA = newValue.doubleValue();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(String.format("%f", Settings.importanceOfGPA));
			}
		});
		return new VBox(2.5, new Label("Importance of GPA"), new HBox(3, importanceOfGPA, textValue));
	}

	private Node initEnableAnimation() {
		CheckBox enableAnimation = new CheckBox("Enable Animation (slower)");

		enableAnimation.setOnAction((evt) -> {
			Settings.enableAnimation = enableAnimation.isSelected();
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%b", Settings.enableAnimation));
		});

		enableAnimation.setSelected(Settings.enableAnimation);
		return enableAnimation;
	}
}