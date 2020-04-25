package ie.ucd.ui.common.sheets;

import ie.ucd.Common.SheetType;
import ie.ucd.objects.Project;
import ie.ucd.ui.setup.SetupPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ProjectSheet extends SetupSheet<Project> {
	public ProjectSheet(Stage stage) {
		this(stage, false, false, null);
	}

	public ProjectSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			SetupPane setupPane) {
		super(stage, includeLoadFromFileButton, includeSaveToFileButton, SheetType.Project, setupPane);
	}

	protected Node initTableView() {
		tableView = new TableView<Project>();
		actualList = tableView.getItems();
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

		return tableView;
	}
}