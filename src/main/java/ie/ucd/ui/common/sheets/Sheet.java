package ie.ucd.ui.common.sheets;

import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;
import ie.ucd.Common.SheetType;
import ie.ucd.interfaces.SearchMatchable;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.interfaces.SheetInterface;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// class handles the sheet view, search box and save button.
public abstract class Sheet<E> extends VBox implements SheetInterface<E> {
	protected TableView<E> tableView;
	protected ObservableList<E> actualList;
	protected FilteredList<E> filteredList;
	protected SearchBox<E> searchBox;
	protected SheetType sheetType;
	protected FileChooser fileChooser;

	public Sheet(Stage stage, boolean includeSaveToFileButton, SheetType sheetType) {
		super();
		this.sheetType = sheetType;
		initLayout(stage, includeSaveToFileButton);
	}

	private void initLayout(Stage stage, boolean includeSaveToFileButton) {
		initFileChooser();
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

	public void clear() {
		actualList.clear();
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
}