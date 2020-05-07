package ie.ucd.ui.common.sheets;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;
import ie.ucd.Settings;
import ie.ucd.Common.SheetType;
import ie.ucd.interfaces.SearchMatchable;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.interfaces.SheetInterface;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// class handles the sheet view, search box and save button.
public abstract class Sheet<E> extends VBox implements SheetInterface<E> {
	protected TableView<E> tableView;
	protected ArrayList<E> elements;
	protected ObservableList<E> actualList;
	protected FilteredList<E> filteredList;
	protected SearchBox<E> searchBox;
	protected SheetType sheetType;
	protected FileChooser fileChooser;
	protected Button clearButton;

	public Sheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton, SheetType sheetType) {
		super();
		this.sheetType = sheetType;
		initLayout(stage, includeLoadFromFileButton, includeSaveToFileButton);
	}

	private void initLayout(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton) {
		initFileChooser();

		getStyleClass().add("more-smaller-main-container");
		getChildren().addAll(initSearchBox(), initTableView());
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		HBox bottom = new HBox(15);
		if (includeSaveToFileButton) {
			bottom.getChildren().add(initSaveButton(stage));
		}
		bottom.getChildren().add(initClearButton(includeLoadFromFileButton));
		getChildren().add(bottom);
	}

	protected abstract Node initTableView();

	private void initFileChooser() {
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Settings.dialogDirectory));

		// Set extension filter for text files.
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT and CSV files (*.txt, *.csv)", "*.txt",
				"*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
	}

	private Node initSaveButton(Stage stage) {
		Button saveButton = new Button("Save to File");
		saveButton.setOnAction(e -> {
			fileChooser.setInitialDirectory(new File(Settings.dialogDirectory));

			// Show save file dialog.
			File file = fileChooser.showSaveDialog(stage);

			if (file != null) {
				handleUpdateDialogDirectory(file);
				saveToFile(file);
			}
		});
		return saveButton;
	}

	protected Node initClearButton(Boolean includeLoadFromFileButton) {
		return new Region();
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
		this.elements = elements;
		actualList.setAll(elements);
	}

	public void clear() {
		this.elements = null;
		actualList.clear();
	}

	protected Node initSearchBox() {
		switch (sheetType) {
			case Student:
				searchBox = new SearchBox<E>("Search by ID / First name / Last name / Stream / GPA / Project Assigned", this);
				break;
			case Project:
				searchBox = new SearchBox<E>("Search by Staff Member name / Project Name / Stream", this);
			default:
				break;
		}
		return searchBox;
	}

	private void saveToFile(File toFile) {
		CSVFileWriter writer = new CSVFileWriter();
		try {
			if (elements != null && !elements.isEmpty()) {
				if (elements.get(0) instanceof Student) {
					writer.writeStudents((ArrayList<Student>) elements, toFile);
				} else if (elements.get(0) instanceof Project) {
					writer.writeProjects((ArrayList<Project>) elements, toFile);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// used previously when we didn't have a variable to store the ArrayList<E> elements.
		// the below is when we thought we wanted to implement modify/edit tableView.
		// CSVFileWriter writer = new CSVFileWriter();
		// ArrayList<E> elements = new ArrayList<E>(actualList);
		// try {
		// 	if (!elements.isEmpty()) {
		// 		if (elements.get(0) instanceof Student) {
		// 			writer.writeStudents((ArrayList<Student>) elements, toFile);
		// 		} else if (elements.get(0) instanceof Project) {
		// 			writer.writeProjects((ArrayList<Project>) elements, toFile);
		// 		}
		// 	}
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
	}

	protected void handleUpdateDialogDirectory(File file) {
		Settings.dialogDirectory = file.getParent();
	}
}