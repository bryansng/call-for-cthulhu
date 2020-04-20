package ie.ucd.ui.common.sheets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.File;
import java.util.ArrayList;
import ie.ucd.Settings;
import ie.ucd.Common.SheetType;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.Parser;
import ie.ucd.ui.setup.SetupPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// class that is able to load and generate random projects/students to Settings.java.
public abstract class SetupSheet<E> extends Sheet<E> {
	private String sheetTypeName;
	private SetupPane setupPane;

	public SetupSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			SheetType sheetType) {
		this(stage, includeLoadFromFileButton, includeSaveToFileButton, sheetType, null);
	}

	public SetupSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			SheetType sheetType, SetupPane setupPane) {
		super(stage, includeSaveToFileButton, sheetType);
		if (includeLoadFromFileButton) {
			this.setupPane = setupPane;
			if (sheetType == SheetType.Project) {
				sheetTypeName = "Projects";
			} else if (sheetType == SheetType.Student) {
				sheetTypeName = "Students";
			}
			initLayout(stage);
		}
	}

	private void initLayout(Stage stage) {
		VBox vBox = new VBox();
		vBox.getChildren().add(initLoadButton(stage));
		vBox.getChildren().add(initGenerateButton());
		getChildren().add(0, vBox);
	}

	private Node initLoadButton(Stage stage) {
		VBox vBox = new VBox();

		TextField filePath = new TextField(System.getProperty("user.dir"));
		filePath.setFocusTraversable(false);
		filePath.setMouseTransparent(true);

		Button loadButton = new Button("Browse");
		loadButton.setFocusTraversable(false);
		loadButton.setMouseTransparent(true);

		HBox hBox = new HBox();
		hBox.setOnMouseClicked((evt) -> {
			handleFileLoading(stage, filePath);
		});
		hBox.getChildren().addAll(filePath, loadButton);
		hBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		hBox.getStylesheets().add("ui/solver/fileLoader.css");
		hBox.getStyleClass().add("file-loader-container");
		vBox.getChildren().addAll(new Label("Load " + sheetTypeName), hBox);

		return vBox;
	}

	private void handleFileLoading(Stage stage, TextField filePath) {
		// handle errors.
		// 1. students loaded and projects loaded incompatible.

		// Show save file dialog.
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			filePath.setText(file.getAbsolutePath());
			loadFromFile(file);
		}
	}

	private Node initGenerateButton() {
		Button generateButton = new Button("Generate Random " + sheetTypeName);
		generateButton.setOnAction(e -> {
			if (sheetType == SheetType.Project) {
				// clear current set first.
				clearStudents();

				Settings.loadedProjects = Settings.setupSolution.generateProjects();
				setAll((ArrayList<E>) Settings.loadedProjects);
				enableStudentSheet();
				setEnableNavigateSolvers(false);
			} else if (sheetType == SheetType.Student) {
				Settings.loadedStudents = Settings.setupSolution.generateStudents();
				setAll((ArrayList<E>) Settings.loadedStudents);
				setEnableNavigateSolvers(true);
			}
		});
		return generateButton;
	}

	private void loadFromFile(File fromFile) {
		CSVFileReader reader = new CSVFileReader();
		try {
			if (sheetType == SheetType.Project) {
				// clear current set first.
				clearStudents();

				Settings.loadedProjects = reader.readProject(null, new Parser().getStaffMembersMap(), fromFile);
				setAll((ArrayList<E>) Settings.loadedProjects);
				enableStudentSheet();
				setEnableNavigateSolvers(false);
			} else if (sheetType == SheetType.Student) {
				Settings.loadedStudents = reader.readStudents(null, Settings.loadedProjects, fromFile);
				setAll((ArrayList<E>) Settings.loadedStudents);
				setEnableNavigateSolvers(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearStudents() {
		if (setupPane != null)
			setupPane.clearStudentsInStudentSheet();
	}

	private void enableStudentSheet() {
		if (setupPane != null)
			setupPane.enableStudentSheet();
	}

	private void setEnableNavigateSolvers(boolean value) {
		if (setupPane != null)
			setupPane.setEnableNavigateSolvers(value);
	}
}