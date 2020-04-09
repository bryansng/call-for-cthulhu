package ie.ucd.ui.common;

import java.util.ArrayList;

import ie.ucd.objects.Project;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProjectSheet extends Sheet {
	private TableView<Project> tableView;
	private SearchBox searchBox;

	public ProjectSheet() {
		super();
	}

	public boolean add(Project student) {
		return tableView.getItems().add(student);
	}

	public boolean clearThenAddAll(ArrayList<Project> students) {
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
		searchBox = new SearchBox("Search by Staff Member name / Project Name / Stream", this);

		getChildren().add(searchBox);
		getChildren().add(tableView);
	}

	protected void initTableView() {
		tableView = new TableView<Project>();
		tableView.setPlaceholder(new Label("No projects to display."));

		TableColumn<Project, Integer> columnStaffMember = new TableColumn<Project, Integer>("Staff Member");
		columnStaffMember.setCellValueFactory(new PropertyValueFactory<>("staffMember"));

		TableColumn<Project, String> columnResearchActivity = new TableColumn<Project, String>("Project Name");
		columnResearchActivity.setCellValueFactory(new PropertyValueFactory<>("researchActivity"));

		TableColumn<Project, String> columnStream = new TableColumn<Project, String>("Stream");
		columnStream.setCellValueFactory(new PropertyValueFactory<>("stream"));

		tableView.getColumns().add(columnStaffMember);
		tableView.getColumns().add(columnResearchActivity);
		tableView.getColumns().add(columnStream);
	}
}