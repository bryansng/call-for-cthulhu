package ie.ucd.ui.common.sheets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.Common.SheetType;
import ie.ucd.exceptions.EmptyPreferenceListException;
import ie.ucd.exceptions.EmptyResearchActivityException;
import ie.ucd.exceptions.InadequatePreferenceListSizeException;
import ie.ucd.exceptions.InsufficientSuitableProjectsException;
import ie.ucd.exceptions.MissingFieldsException;
import ie.ucd.exceptions.ProjectsNullException;
import ie.ucd.exceptions.SimilarStudentIDsException;
import ie.ucd.exceptions.StudentsNullException;
import ie.ucd.exceptions.UnexpectedStreamException;
import ie.ucd.exceptions.UnknownStaffMemberNameException;
import ie.ucd.exceptions.UnsuitableColumnHeadersException;
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
	private Label onSuccessfulLoad;
	private Label onErrorLoad;
	private Label onSuccessfulGenerate;
	private Label onErrorGenerate;

	public SetupSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			SheetType sheetType) {
		this(stage, includeLoadFromFileButton, includeSaveToFileButton, sheetType, null);
	}

	public SetupSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			SheetType sheetType, SetupPane setupPane) {
		super(stage, includeLoadFromFileButton, includeSaveToFileButton, sheetType);
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

		Label buffer = new Label("---\nor\n---");
		buffer.setWrapText(true);
		buffer.setMinHeight(Label.USE_PREF_SIZE);

		onSuccessfulLoad = new Label();
		onSuccessfulLoad.setWrapText(true);
		onSuccessfulLoad.getStyleClass().add("successful-label");
		onSuccessfulLoad.setMinHeight(Label.USE_PREF_SIZE);
		onSuccessfulLoad.setMaxWidth(1000);

		onErrorLoad = new Label();
		onErrorLoad.setWrapText(true);
		onErrorLoad.getStyleClass().add("warning-label");
		onErrorLoad.setMinHeight(Label.USE_PREF_SIZE);
		onErrorLoad.setMaxWidth(1000);

		onSuccessfulGenerate = new Label();
		onSuccessfulGenerate.setWrapText(true);
		onSuccessfulGenerate.getStyleClass().add("successful-label");
		onSuccessfulGenerate.setMinHeight(Label.USE_PREF_SIZE);
		onSuccessfulGenerate.setMaxWidth(1000);

		onErrorGenerate = new Label();
		onErrorGenerate.setWrapText(true);
		onErrorGenerate.getStyleClass().add("warning-label");
		onErrorGenerate.setMinHeight(Label.USE_PREF_SIZE);
		onErrorGenerate.setMaxWidth(1000);

		setEnableSuccessfulLoad(false, "");
		setEnableErrorLoad(false, "");
		setEnableSuccessGenerate(false, "");
		setEnableErrorGenerate(false, "");

		allParts.getChildren().addAll(new VBox(initLoadButton(stage), onSuccessfulLoad, onErrorLoad), buffer,
				initGenerateButton(), onSuccessfulGenerate, onErrorGenerate);
		getChildren().add(0, allParts);
	}

	protected Node initClearButton(Boolean includeLoadFromFileButton) {
		if (includeLoadFromFileButton) {
			clearButton = new Button("Clear Table");
			clearButton.setOnAction(e -> {
				setupPane.setEnableStreamHardConstraint(Settings.userSpecifiedSameStream);
				if (!Common.isProjectsPopulated) {
					Settings.loadedStudents = null;
					Settings.loadedProjects = null;
					Settings.dummyStaffMembers = null;
					clearStudents();
					clearProjects();
				} else {
					if (sheetType == SheetType.Project) {
						clearStudents();
						clearProjects();
						setEnableNavigateSolvers(false);
						Common.isProjectsPopulated = false;
						if (Common.DEBUG_SHOW_IS_PROJECTS_POPULATED)
							System.out.println("Set isProjectsPopulated to " + Common.isProjectsPopulated);
					} else if (sheetType == SheetType.Student) {
						clearStudents();
						setEnableNavigateSolvers(false);
					}
				}
			});
			return clearButton;
		}
		return super.initClearButton(includeLoadFromFileButton);
	}

	private Node initLoadButton(Stage stage) {
		VBox vBox = new VBox();

		Label loadLabel = new Label("Load " + sheetTypeName);
		loadLabel.getStyleClass().add("sub-label");

		TextField filePath = new TextField(Settings.dialogDirectory);
		filePath.setFocusTraversable(false);
		filePath.setMouseTransparent(true);
		filePath.setMaxWidth(1024);
		HBox.setHgrow(filePath, Priority.ALWAYS);

		Button loadButton = new Button("Browse");
		loadButton.setFocusTraversable(false);
		loadButton.setMouseTransparent(true);
		loadButton.setDisable(false);

		HBox hBox = new HBox(5);
		hBox.setOnMouseClicked((evt) -> {
			handleFileLoading(stage, filePath);
		});
		hBox.getStyleClass().add("file-loader-container");
		hBox.getStylesheets().add("ui/fileLoader.css");
		hBox.getChildren().addAll(filePath, loadButton);
		vBox.getChildren().addAll(loadLabel, hBox);

		return vBox;
	}

	private void handleFileLoading(Stage stage, TextField filePath) {
		fileChooser.setInitialDirectory(new File(Settings.dialogDirectory));

		// Show save file dialog.
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			handleUpdateDialogDirectory(file);
			filePath.setText(file.getAbsolutePath());
			loadFromFile(file);
		}
	}

	private Node initGenerateButton() {
		VBox vBox = new VBox();

		Label loadLabel = new Label("Generate " + sheetTypeName);
		loadLabel.getStyleClass().add("sub-label");

		Button generateButton = new Button("Generate Random " + sheetTypeName);
		generateButton.setOnAction(evt -> {
			setupPane.setEnableStreamHardConstraint(Settings.userSpecifiedSameStream);
			if (sheetType == SheetType.Project) {
				// clear current set first.
				clearStudents();

				Settings.loadedProjects = Settings.setupSolution.generateProjects();
				setAll((ArrayList<E>) Settings.loadedProjects);
				// enableStudentSheet();
				setEnableSuccessGenerate(true,
						String.format("SUCCESS: Generated %d %s.", Settings.loadedProjects.size(), sheetTypeName.toLowerCase()));
				Common.isProjectsPopulated = true;
				if (Common.DEBUG_SHOW_IS_PROJECTS_POPULATED)
					System.out.println("Set isProjectsPopulated to " + Common.isProjectsPopulated);
			} else if (sheetType == SheetType.Student) {
				try {
					if (!Common.isProjectsPopulated) {
						clearStudents();
						clearProjects();
						Settings.prepareSetupSolutionForGenerator();

						// if projects not populated, we auto generate projects and populate the project sheets.
						Settings.loadedProjects = Settings.setupSolution.generateProjects();
						setupPane.setAllProjectSheet(Settings.loadedProjects);
						Settings.loadedStudents = Settings.setupSolution.generateStudents();
						setEnableSuccessGenerate(true,
								String.format("SUCCESS: Generated %d %s, and automatically generated %d projects.",
										Settings.loadedStudents.size(), sheetTypeName.toLowerCase(), Settings.loadedProjects.size()));
					} else {
						Settings.loadedStudents = Settings.setupSolution.generateStudents();
						setEnableSuccessGenerate(true, String.format("SUCCESS: Generated %d %s.", Settings.loadedStudents.size(),
								sheetTypeName.toLowerCase()));
					}
					Settings.loadedStudents = Settings.setupSolution.generateStudents();
					setAll((ArrayList<E>) Settings.loadedStudents);
					setEnableNavigateSolvers(true);
				} catch (InsufficientSuitableProjectsException e) {
					setEnableErrorGenerate(true,
							"ERROR: Unable to generate students. Insufficient suitable projects (i.e. incompatible stream, etc) during generation of student preference list. Please try increasing the number of students in 1. Settings.");
				}
			}
		});
		vBox.getChildren().addAll(loadLabel, generateButton);
		return vBox;
	}

	private void loadFromFile(File fromFile) {
		CSVFileReader reader = new CSVFileReader();
		setupPane.setEnableStreamHardConstraint(Settings.userSpecifiedSameStream);
		if (sheetType == SheetType.Project) {
			try {
				// clear current set first.
				clearStudents();

				Settings.loadedProjects = reader.readProject(null, new Parser().getStaffMembersMap(), fromFile);

				if (Settings.loadedProjects == null || Settings.loadedProjects.size() == 0) {
					throw new ProjectsNullException("Projects read is null.");
				}

				Settings.prepareSetupSolutionForStudentGenerator();
				setAll((ArrayList<E>) Settings.loadedProjects);
				setupPane.setEnableStreamHardConstraint(Common.doesLoadedFileHaveStream);
				// enableStudentSheet();
				if (!Common.doesLoadedFileHaveStream) {
					setEnableSuccessfulLoad(true, String.format(
							"SUCCESS: Loaded %d %s.\nAlso, sameStream hard constraint has been disabled because no stream column is detected, or projects have no stream.",
							Settings.loadedProjects.size(), sheetTypeName.toLowerCase()));
				} else {
					setEnableSuccessfulLoad(true,
							String.format("SUCCESS: Loaded %d %s.", Settings.loadedProjects.size(), sheetTypeName.toLowerCase()));
				}
				Common.isProjectsPopulated = true;
				if (Common.DEBUG_SHOW_IS_PROJECTS_POPULATED)
					System.out.println("Set isProjectsPopulated to " + Common.isProjectsPopulated);
			} catch (EmptyResearchActivityException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (UnknownStaffMemberNameException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (UnexpectedStreamException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (UnsupportedEncodingException e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. Encoding UTF-8 not supported.");
			} catch (FileNotFoundException e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. File not found exception raised.");
			} catch (IOException e) {
				setEnableErrorLoad(true,
						"ERROR: Unable to finish reading file. Input/Output error occurred while trying to read the file.");
			} catch (MissingFieldsException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (Exception e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. Please ensure you are loading a file for " + sheetTypeName
						+ ".\nExpected format is: Staff Name (String),Research Activity (String),Stream (String: CS, CS, CS+DS)");
			}
		} else if (sheetType == SheetType.Student) {
			try {
				if (!Common.isProjectsPopulated) {
					clearStudents();
					clearProjects();

					Settings.loadedStudents = reader.readStudents(null, Settings.loadedProjects, fromFile);
					setupPane.setAllProjectSheet(Settings.loadedProjects);
					setupPane.setEnableStreamHardConstraint(Common.doesLoadedFileHaveStream);
				} else {
					Settings.loadedStudents = reader.readStudents(null, Settings.loadedProjects, fromFile);
				}

				if (Settings.loadedStudents == null || Settings.loadedStudents.size() == 0) {
					throw new StudentsNullException("Students read is null.");
				}

				setAll((ArrayList<E>) Settings.loadedStudents);
				setEnableNavigateSolvers(true);

				if (!Common.isProjectsPopulated) {
					if (!Common.doesLoadedFileHaveStream) {
						setEnableSuccessfulLoad(true, String.format(
								"SUCCESS: Loaded %d %s, and automatically speculated %d projects.\nAlso, sameStream hard constraint has been disabled because no stream column is detected, or projects have no stream.",
								Settings.loadedStudents.size(), sheetTypeName.toLowerCase(), Settings.loadedProjects.size()));
					} else {
						setEnableSuccessfulLoad(true,
								String.format("SUCCESS: Loaded %d %s, and automatically speculated %d projects.",
										Settings.loadedStudents.size(), sheetTypeName.toLowerCase(), Settings.loadedProjects.size()));
					}
				} else {
					setEnableSuccessfulLoad(true,
							String.format("SUCCESS: Loaded %d %s.", Settings.loadedStudents.size(), sheetTypeName.toLowerCase()));
				}
			} catch (UnsuitableColumnHeadersException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (EmptyPreferenceListException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (SimilarStudentIDsException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (UnexpectedStreamException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (MissingFieldsException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (NumberFormatException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (InadequatePreferenceListSizeException e) {
				setEnableErrorLoad(true, e.getMessage());
			} catch (UnsupportedEncodingException e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. Encoding UTF-8 not supported.");
			} catch (FileNotFoundException e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. File not found exception raised.");
			} catch (IOException e) {
				setEnableErrorLoad(true,
						"ERROR: Unable to finish reading file. Input/Output error occurred while trying to read the file.");
			} catch (Exception e) {
				setEnableErrorLoad(true, "ERROR: Unable to read file. Please ensure you are loading a file for " + sheetTypeName
						+ ".\nExpected format is:\n- First Name,Last Name,ID,Stream,GPA,Project Assigned,Preference 1,...,Preference 10, or\n- First Name,Last Name,ID,Stream,Project Assigned,Preference 1,...,Preference 10, or\n- First Name,Last Name,ID,Stream,Preference 1,...,Preference 10");
			}
		}
	}

	private void clearStudents() {
		if (setupPane != null)
			setupPane.clearStudentsInStudentSheet();
	}

	private void clearProjects() {
		if (setupPane != null)
			setupPane.clearProjectsInProjectSheet();
	}

	private void enableStudentSheet() {
		if (setupPane != null)
			setupPane.enableStudentSheet();
	}

	private void setEnableNavigateSolvers(boolean value) {
		if (setupPane != null)
			setupPane.setEnableNavigateSolvers(value);
	}

	private void setEnableSuccessfulLoad(boolean value, String msg) {
		resetSuccessErrorLabels();
		onSuccessfulLoad.setText(msg);
		onSuccessfulLoad.setVisible(value);
		onSuccessfulLoad.setManaged(value);
	}

	private void setEnableErrorLoad(boolean value, String msg) {
		resetSuccessErrorLabels();
		onErrorLoad.setText(msg);
		onErrorLoad.setVisible(value);
		onErrorLoad.setManaged(value);
	}

	private void setEnableSuccessGenerate(boolean value, String msg) {
		resetSuccessErrorLabels();
		onSuccessfulGenerate.setText(msg);
		onSuccessfulGenerate.setVisible(value);
		onSuccessfulGenerate.setManaged(value);
	}

	private void setEnableErrorGenerate(boolean value, String msg) {
		resetSuccessErrorLabels();
		onErrorGenerate.setText(msg);
		onErrorGenerate.setVisible(value);
		onErrorGenerate.setManaged(value);
	}

	public void resetSuccessErrorLabels() {
		onSuccessfulLoad.setText("");
		onSuccessfulLoad.setVisible(false);
		onSuccessfulLoad.setManaged(false);
		onErrorLoad.setText("");
		onErrorLoad.setVisible(false);
		onErrorLoad.setManaged(false);
		onSuccessfulGenerate.setText("");
		onSuccessfulGenerate.setVisible(false);
		onSuccessfulGenerate.setManaged(false);
		onErrorGenerate.setText("");
		onErrorGenerate.setVisible(false);
		onErrorGenerate.setManaged(false);
	}
}