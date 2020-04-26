package ie.ucd.ui.setup;

import ie.ucd.Common;
import ie.ucd.MainUI;
import ie.ucd.Settings;
import ie.ucd.Settings.Theme;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.constraints.Constraints;
import ie.ucd.ui.common.sheets.ProjectSheet;
import ie.ucd.ui.common.sheets.SetupSheet;
import ie.ucd.ui.common.sheets.StudentSheet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SetupPane extends ScrollPane {
	private MainUI mainUI;
	private TextField numStudents;
	private Constraints constraints;
	private Slider importanceOfGPA;
	private SetupSheet<Project> projectSheet;
	private SetupSheet<Student> studentSheet;
	private Tab tab3;

	public SetupPane(Stage stage, MainUI mainUI) {
		super();
		this.mainUI = mainUI;
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		Label labelSettings = new Label("1. Settings");
		labelSettings.getStyleClass().add("main-label");

		Label sublabelOthers = new Label("Others");
		sublabelOthers.getStyleClass().add("sub-label");

		Label labelProjects = new Label("2. Load/Generate Projects");
		labelProjects.getStyleClass().add("main-label");

		Label labelStudents = new Label("3. Load/Generate Students");
		labelStudents.getStyleClass().add("main-label");

		constraints = new Constraints(true, true);
		projectSheet = new ProjectSheet(stage, true, true, this);
		studentSheet = new StudentSheet(stage, true, true, false, this);

		VBox innerSubPart = new VBox(initImportanceOfGPA(), initEnableAnimation(), initDarkTheme());
		innerSubPart.getStyleClass().add("standard-sub-sub-container");

		VBox middleSubPart = new VBox(constraints, new VBox(sublabelOthers, innerSubPart));
		middleSubPart.getStyleClass().add("standard-sub-container");

		VBox outerSubPart = new VBox(initNumStudents(), middleSubPart);
		outerSubPart.getStyleClass().add("smaller-main-container");

		VBox part1 = new VBox(labelSettings, outerSubPart);
		part1.getStyleClass().add("standard-padding");

		VBox part2 = new VBox(labelProjects, projectSheet);
		part2.getStyleClass().add("standard-padding");

		VBox part3 = new VBox(labelStudents, studentSheet);
		part3.getStyleClass().add("standard-padding");

		Tab tab1 = new Tab("1. Settings", part1);
		Tab tab2 = new Tab("2. Load/Generate Projects", part2);
		tab3 = new Tab("3. Load/Generate Students", part3);

		disableStudentSheet();

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(tab1);
		tabPane.getTabs().add(tab2);
		tabPane.getTabs().add(tab3);
		setContent(tabPane);
	}

	private Node initNumStudents() {
		Label numStudentsWarning = new Label();
		numStudentsWarning.getStyleClass().add("warning-label");

		numStudents = new TextField(Settings.numberOfStudents.toString());
		numStudents.setMaxWidth(200);
		numStudents.setOnKeyReleased((evt) -> {
			String newConfig = numStudents.getText();
			try {
				if (newConfig.equals("")) {
					Settings.numberOfStudents.setValue(Settings.DEFAULT_NUMBER_OF_STUDENTS);
				} else {
					Settings.numberOfStudents.setValue(Integer.parseInt(newConfig));
				}
				numStudentsWarning.setText("");
			} catch (NumberFormatException e) {
				numStudentsWarning.setText("WARNING: Must be a number.");
				Settings.numberOfStudents.setValue(Settings.DEFAULT_NUMBER_OF_STUDENTS);
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%d", Settings.numberOfStudents.getValue()));
		});
		return new VBox(new VBox(2.5, new Label("Number of Students (used by generator)"), numStudents),
				numStudentsWarning);
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

	private Node initDarkTheme() {
		CheckBox enableDarkTheme = new CheckBox("Enable Dark Theme");

		enableDarkTheme.setOnAction((evt) -> {
			Settings.enableDarkTheme = enableDarkTheme.isSelected();

			if (Settings.enableDarkTheme) {
				mainUI.handleThemes(Theme.DARK);
			} else {
				mainUI.handleThemes(Theme.ORIGINAL);
			}

			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%b", Settings.enableDarkTheme));
		});

		enableDarkTheme.setSelected(Settings.enableDarkTheme);
		return enableDarkTheme;
	}

	private Node initEnableAnimation() {
		CheckBox enableAnimation = new CheckBox(
				"Enable Animation (slower) (If disabled, data will be populated once calculation is complete)");

		enableAnimation.setOnAction((evt) -> {
			Settings.enableAnimation = enableAnimation.isSelected();
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%b", Settings.enableAnimation));
		});

		enableAnimation.setSelected(Settings.enableAnimation);
		return enableAnimation;
	}

	public void clearStudentsInStudentSheet() {
		studentSheet.clear();
	}

	public void enableStudentSheet() {
		studentSheet.setDisable(false);
		tab3.setDisable(false);
	}

	public void disableStudentSheet() {
		studentSheet.setDisable(true);
		tab3.setDisable(true);
	}

	public void setEnableNavigateSolvers(boolean value) {
		mainUI.setEnableNavigateSolvers(value);
	}
}