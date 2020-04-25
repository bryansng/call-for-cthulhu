package ie.ucd.ui.common.sheets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.InterruptedIOException;
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
	private Label errorWarning;

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
		VBox allParts = new VBox();
		allParts.getStyleClass().addAll("smaller-sub-container");

		VBox buffer = new VBox(new Label("---\nor\n---"));

		errorWarning = new Label();
		errorWarning.getStyleClass().add("warning-label");
		setEnableErrorWarning(false, "");

		allParts.getChildren().addAll(new VBox(initLoadButton(stage), errorWarning), buffer, initGenerateButton());
		getChildren().add(0, allParts);
	}

	private Node initLoadButton(Stage stage) {
		VBox vBox = new VBox();

		Label loadLabel = new Label("Load " + sheetTypeName);
		loadLabel.getStyleClass().add("sub-label");

		TextField filePath = new TextField(System.getProperty("user.dir"));
		filePath.setFocusTraversable(false);
		filePath.setMouseTransparent(true);
		filePath.setMaxWidth(1024);
		HBox.setHgrow(filePath, Priority.ALWAYS);

		Button loadButton = new Button("Browse");
		loadButton.setFocusTraversable(false);
		loadButton.setMouseTransparent(true);

		HBox hBox = new HBox(5);
		hBox.setOnMouseClicked((evt) -> {
			handleFileLoading(stage, filePath);
		});
		hBox.getChildren().addAll(filePath, loadButton);
		hBox.getStylesheets().add("ui/fileLoader.css");
		hBox.getStyleClass().add("file-loader-container");
		vBox.getChildren().addAll(loadLabel, hBox);

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
		if (sheetType == SheetType.Project) {
			try {
				// clear current set first.
				clearStudents();

				Settings.loadedProjects = reader.readProject(null, new Parser().getStaffMembersMap(), fromFile);

				if (Settings.loadedProjects == null || Settings.loadedProjects.size() == 0) {
					throw new NullPointerException("Projects read is null.");
				}

				setAll((ArrayList<E>) Settings.loadedProjects);
				enableStudentSheet();
				setEnableNavigateSolvers(false);
				setEnableErrorWarning(false, "");
			} catch (AssertionError e) {
				setEnableErrorWarning(true, "ERROR: Unable to read file. Please ensure you are loading a file for "
						+ sheetTypeName + ".\nExpected format is: Staff Name,Research Activity,Stream");
			} catch (Exception e) {
				setEnableErrorWarning(true, "ERROR: Unable to read file. Please ensure you are loading a file for "
						+ sheetTypeName + ".\nExpected format is: Staff Name,Research Activity,Stream");
			}
		} else if (sheetType == SheetType.Student) {
			try {
				Settings.loadedStudents = reader.readStudents(null, Settings.loadedProjects, fromFile);

				if (Settings.loadedStudents == null || Settings.loadedStudents.size() == 0) {
					throw new NullPointerException("Students read is null.");
				}

				setAll((ArrayList<E>) Settings.loadedStudents);
				setEnableNavigateSolvers(true);
				setEnableErrorWarning(false, "");
			} catch (AssertionError e) {
				setEnableErrorWarning(true,
						"ERROR: A student's preference list project cannot be found in the Loaded projects.");
			} catch (InterruptedIOException e) {
				setEnableErrorWarning(true,
						"ERROR: A student's preference list project cannot be found in the Loaded projects.");
			} catch (Exception e) {
				setEnableErrorWarning(true, "ERROR: Unable to read file. Please ensure you are loading a file for "
						+ sheetTypeName
						+ ".\nExpected format is: First Name,Last Name,ID,Stream,Preference 1,Preference 2,Preference 3,Preference 4,Preference 5,Preference 6,Preference 7,Preference 8,Preference 9,Preference 10");
			}
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

	private void setEnableErrorWarning(boolean value, String errorMsg) {
		errorWarning.setText(errorMsg);
		errorWarning.setVisible(value);
		errorWarning.setManaged(value);
	}
}