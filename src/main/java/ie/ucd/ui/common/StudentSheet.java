package ie.ucd.ui.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

import ie.ucd.io.CSVFileWriter;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StudentSheet extends Sheet {
	private TableView<Student> tableView;
	private ObservableList<Student> actualList;
	private FilteredList<Student> filteredList;
	private SearchBox searchBox;
	private Button saveButton;

	public StudentSheet(Stage stage) {
		this(stage, false);
	}

	public StudentSheet(Stage stage, boolean includeSaveToFileButton) {
		super();
		if (includeSaveToFileButton) {
			initSaveButton(stage);
		}
	}

	private void saveStudentsToFile(File file) {
		CSVFileWriter writer = new CSVFileWriter();
		ArrayList<Student> students = new ArrayList<Student>(tableView.getItems());
		try {
			writer.writeStudents(students, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean add(Student student) {
		return tableView.getItems().add(student);
	}

	public boolean clearThenAddAll(ArrayList<Student> students) {
		return actualList.setAll(students);
	}

	public void search(String searchTerm) {
		if (searchTerm.equals("")) {
			tableView.setItems(actualList);
		} else {
			filteredList = new FilteredList<Student>(actualList);
			tableView.setItems(filteredList);
			filteredList.setPredicate(new Predicate<Student>() {
				public boolean test(Student student) {
					return student.matchSearchTerm(searchTerm);
				}
			});
		}
		// tableView.getItems().stream().filter(item -> item.matchSearchTerm(searchTerm)).findAny().ifPresent(item -> {
		// 	tableView.getSelectionModel().select(item);
		// 	tableView.scrollTo(item);
		// });
	}

	protected void initLayout() {
		searchBox = new SearchBox("Search by ID / First name / Last name / Stream / GPA / Project Assigned", this);

		getChildren().add(searchBox);
		getChildren().add(tableView);
	}

	private void initSaveButton(Stage stage) {
		saveButton = new Button("Save to File");
		saveButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();

			// Set extension filter for text files.
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog.
			File file = fileChooser.showSaveDialog(stage);

			if (file != null) {
				saveStudentsToFile(file);
			}
		});
		getChildren().add(saveButton);
	}

	protected void initTableView() {
		tableView = new TableView<Student>();
		actualList = tableView.getItems();
		tableView.setPlaceholder(new Label("No students to display."));

		TableColumn<Student, Integer> columnId = new TableColumn<Student, Integer>("ID");
		columnId.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Student, String> columnFirstName = new TableColumn<Student, String>("First Name");
		columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

		TableColumn<Student, String> columnLastName = new TableColumn<Student, String>("Last Name");
		columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

		TableColumn<Student, String> columnStream = new TableColumn<Student, String>("Stream");
		columnStream.setCellValueFactory(new PropertyValueFactory<>("stream"));

		TableColumn<Student, Double> columnGPA = new TableColumn<Student, Double>("GPA");
		columnGPA.setCellValueFactory(new PropertyValueFactory<>("gpa"));

		TableColumn<Student, Project> columnProject = new TableColumn<Student, Project>("Project Assigned");
		columnProject.setCellValueFactory(new PropertyValueFactory<>("project"));

		TableColumn<Student, String> columnPreferenceProject1 = new TableColumn<Student, String>("Preference 1");
		columnPreferenceProject1.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(0).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject2 = new TableColumn<Student, String>("Preference 2");
		columnPreferenceProject2.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(1).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject3 = new TableColumn<Student, String>("Preference 3");
		columnPreferenceProject3.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(2).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject4 = new TableColumn<Student, String>("Preference 4");
		columnPreferenceProject4.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(3).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject5 = new TableColumn<Student, String>("Preference 5");
		columnPreferenceProject5.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(4).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject6 = new TableColumn<Student, String>("Preference 6");
		columnPreferenceProject6.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(5).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject7 = new TableColumn<Student, String>("Preference 7");
		columnPreferenceProject7.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(6).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject8 = new TableColumn<Student, String>("Preference 8");
		columnPreferenceProject8.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(7).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject9 = new TableColumn<Student, String>("Preference 9");
		columnPreferenceProject9.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(8).getResearchActivity()));

		TableColumn<Student, String> columnPreferenceProject10 = new TableColumn<Student, String>("Preference 10");
		columnPreferenceProject10.setCellValueFactory(
				data -> new SimpleStringProperty(data.getValue().getPreferenceList().get(9).getResearchActivity()));

		tableView.getColumns().add(columnId);
		tableView.getColumns().add(columnFirstName);
		tableView.getColumns().add(columnLastName);
		tableView.getColumns().add(columnStream);
		tableView.getColumns().add(columnGPA);
		tableView.getColumns().add(columnProject);
		tableView.getColumns().add(columnPreferenceProject1);
		tableView.getColumns().add(columnPreferenceProject2);
		tableView.getColumns().add(columnPreferenceProject3);
		tableView.getColumns().add(columnPreferenceProject4);
		tableView.getColumns().add(columnPreferenceProject5);
		tableView.getColumns().add(columnPreferenceProject6);
		tableView.getColumns().add(columnPreferenceProject7);
		tableView.getColumns().add(columnPreferenceProject8);
		tableView.getColumns().add(columnPreferenceProject9);
		tableView.getColumns().add(columnPreferenceProject10);
	}
}