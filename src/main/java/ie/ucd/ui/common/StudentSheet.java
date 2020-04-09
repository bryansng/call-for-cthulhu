package ie.ucd.ui.common;

import java.util.ArrayList;

import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentSheet extends Sheet {
	private TableView<Student> tableView;
	private SearchBox searchBox;

	public StudentSheet() {
		super();
	}

	public boolean add(Student student) {
		return tableView.getItems().add(student);
	}

	public boolean clearThenAddAll(ArrayList<Student> students) {
		tableView.getItems().clear();
		return tableView.getItems().addAll(students);
	}

	public void search(String searchTerm) {
		tableView.getItems().stream().filter(item -> item.matchSearchTerm(searchTerm)).findAny().ifPresent(item -> {
			tableView.getSelectionModel().select(item);
			tableView.scrollTo(item);
		});
	}

	protected void initLayout() {
		searchBox = new SearchBox("Search by ID / First name / Last name / Stream / GPA / Project Assigned", this);

		getChildren().add(searchBox);
		getChildren().add(tableView);
	}

	protected void initTableView() {
		tableView = new TableView<Student>();
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