package ie.ucd.ui.common.sheets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

import ie.ucd.Settings;
import ie.ucd.Common.SheetType;
import ie.ucd.interfaces.SearchMatchable;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.io.Parser;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.interfaces.SheetInterface;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class Sheet<E> extends VBox implements SheetInterface<E> {
	protected TableView<E> tableView;
	protected ObservableList<E> actualList;
	protected FilteredList<E> filteredList;
	protected SearchBox<E> searchBox;
	protected SheetType sheetType;
	protected String sheetTypeName;
	protected FileChooser fileChooser;

	public Sheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton, SheetType sheetType) {
		super();
		this.sheetType = sheetType;
		if (sheetType == SheetType.Project) {
			sheetTypeName = "Projects";
		} else if (sheetType == SheetType.Student) {
			sheetTypeName = "Students";
		}
		initLayout(stage, includeLoadFromFileButton, includeSaveToFileButton);
	}

	private void initLayout(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton) {
		initFileChooser();
		if (includeLoadFromFileButton) {
			initLoadButton(stage);
		}
		initTableView();
		initSearchBox();
		getChildren().add(tableView);
		if (includeSaveToFileButton) {
			initSaveButton(stage);
		}
	}

	protected abstract void initTableView();

	private void initFileChooser() {
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

		// Set extension filter for text files.
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT and CSV files (*.txt, *.csv)", "*.txt",
				"*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
	}

	private void initLoadButton(Stage stage) {
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

		getChildren().add(vBox);
	}

	private void handleFileLoading(Stage stage, TextField filePath) {
		// Show save file dialog.
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			filePath.setText(file.getAbsolutePath());
			loadFromFile(file);
			if (sheetType == SheetType.Project) {
				setAll((ArrayList<E>) Settings.loadedProjects);
			} else if (sheetType == SheetType.Student) {
				setAll((ArrayList<E>) Settings.loadedStudents);
			}
		}
	}

	private void initSaveButton(Stage stage) {
		Button saveButton = new Button("Save to File");
		saveButton.setOnAction(e -> {
			// Show save file dialog.
			File file = fileChooser.showSaveDialog(stage);

			if (file != null) {
				saveToFile(file);
			}
		});
		getChildren().add(saveButton);
	}

	public void search(String searchTerm) {
		if (searchTerm.equals("")) {
			tableView.setItems(actualList);
		} else {
			filteredList = new FilteredList<E>(actualList);
			tableView.setItems(filteredList);
			filteredList.setPredicate(new Predicate<E>() {
				public boolean test(E element) {
					if (element instanceof SearchMatchable)
						return ((SearchMatchable) element).matchSearchTerm(searchTerm);
					return false;
				}
			});
		}
	}

	public boolean add(E element) {
		return tableView.getItems().add(element);
	}

	public void setAll(ArrayList<E> elements) {
		actualList.setAll(elements);
	}

	protected void initSearchBox() {
		switch (sheetType) {
			case Student:
				searchBox = new SearchBox<E>("Search by ID / First name / Last name / Stream / GPA / Project Assigned", this);
				break;
			case Project:
				searchBox = new SearchBox<E>("Search by Staff Member name / Project Name / Stream", this);
			default:
				break;
		}

		getChildren().add(searchBox);
	}

	private void saveToFile(File toFile) {
		CSVFileWriter writer = new CSVFileWriter();
		ArrayList<E> elements = new ArrayList<E>(actualList);
		try {
			if (!elements.isEmpty()) {
				if (elements.get(0) instanceof Student) {
					writer.writeStudents((ArrayList<Student>) elements, toFile);
				} else if (elements.get(0) instanceof Project) {
					writer.writeProjects((ArrayList<Project>) elements, toFile);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFromFile(File fromFile) {
		CSVFileReader reader = new CSVFileReader();
		try {
			if (sheetType == SheetType.Project) {
				Settings.loadedProjects = reader.readProject(null, new Parser().getStaffMembersMap(), fromFile);
			} else if (sheetType == SheetType.Student) {
				Settings.loadedStudents = reader.readStudents(null, Settings.loadedProjects, fromFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}