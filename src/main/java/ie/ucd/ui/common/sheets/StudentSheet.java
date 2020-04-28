package ie.ucd.ui.common.sheets;

import ie.ucd.Settings;
import ie.ucd.Common.SheetType;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.constraints.Constraints;
import ie.ucd.ui.common.constraints.HardConstraints;
import ie.ucd.ui.common.constraints.SoftConstraints;
import ie.ucd.ui.interfaces.StudentSheetInterface;
import ie.ucd.ui.setup.SetupPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class StudentSheet extends SetupSheet<Student> implements StudentSheetInterface {
	private Strength strength;
	private Constraints constraints;

	private Deque<CandidateSolution> solutionDeque;

	public StudentSheet(Stage stage) {
		this(stage, false, false, false, null);
	}

	public StudentSheet(Stage stage, boolean includeLoadFromFileButton, boolean includeSaveToFileButton,
			boolean includeQualityEvaluation, SetupPane setupPane) {
		super(stage, includeLoadFromFileButton, includeSaveToFileButton, SheetType.Student, setupPane);

		if (includeQualityEvaluation) {
			initSolutionQualityLayout();
		}
		solutionDeque = new LinkedList<CandidateSolution>();
	}

	private void initSolutionQualityLayout() {
		VBox allParts = new VBox();
		allParts.getStyleClass().addAll("smaller-main-container");

		Label qualityLabel = new Label("Quality");
		qualityLabel.getStyleClass().add("main-label");

		strength = new Strength();
		constraints = new Constraints(false, false);

		VBox everythingElse = new VBox();
		everythingElse.getStyleClass().addAll("smaller-sub-container");
		everythingElse.getChildren().addAll(strength, constraints);

		allParts.getChildren().addAll(qualityLabel, everythingElse);
		getChildren().add(0, allParts);
	}

	public void resetSeries() {
		solutionDeque.clear();
	}

	public void resetEvaluation() {
		constraints.reset();
		strength.reset();
	}

	public void initOneShotScheduler() {
		updateSheetAndStrengthAndConstraints();
	}

	public boolean isDequeEmpty() {
		return solutionDeque.isEmpty();
	}

	public void addToQueue(CandidateSolution solution) {
		solutionDeque.add(solution);
	}

	public void updateSheetAndStrengthAndConstraints() {
		updateSheetAndStrengthAndConstraints(null);
	}

	public void updateSheetAndStrengthAndConstraints(CandidateSolution lastSolution) {
		if (!isDequeEmpty() || lastSolution != null) {
			try {
				// get data from deque.
				CandidateSolution solution;
				if (lastSolution == null)
					solution = solutionDeque.removeFirst();
				else
					solution = lastSolution;

				// update sheet.
				setAll(solution.getStudents());

				// update constraints.
				HardConstraints hard = constraints.getHardConstraints();
				SoftConstraints soft = constraints.getSoftConstraints();
				double hardCost = 0.0;
				double softCost = 0.0;

				// hard
				if (Settings.enableStudentAssignedPreferredProject) {
					if (solution.getViolationsStudentAssignedPreferredProject() > 0) {
						hardCost += (1.0 * solution.getViolationsStudentAssignedPreferredProject()) / solution.getNumberOfStudents()
								* Settings.COST_PER_HARD_VIOLATION;

						hard.updateStudentAssignedPreferredProject(false, solution.getViolationsStudentAssignedPreferredProject(),
								solution.getNumberOfStudents());
					} else {
						hard.updateStudentAssignedPreferredProject(true, null, null);
					}
				}

				if (Settings.enableSameStream) {
					if (solution.getViolationsSameStream() > 0) {
						hardCost += (1.0 * solution.getViolationsSameStream()) / solution.getNumberOfStudents()
								* Settings.COST_PER_HARD_VIOLATION;

						hard.updateSameStream(false, solution.getViolationsSameStream(), solution.getNumberOfStudents());
					} else {
						hard.updateSameStream(true, null, null);
					}
				}

				if (Settings.enableStudentAssignedOneProject) {
					if (solution.getViolationsStudentAssignedOneProject() > 0) {
						hardCost += (1.0 * solution.getViolationsStudentAssignedOneProject()) / solution.getNumberOfStudents()
								* Settings.COST_PER_HARD_VIOLATION;

						hard.updateStudentAssignedOneProject(false, solution.getViolationsStudentAssignedOneProject(),
								solution.getNumberOfStudents());
					} else {
						hard.updateStudentAssignedOneProject(true, null, null);
					}
				}

				if (Settings.enableProjectAssignedToOneStudent) {
					if (solution.getViolationsProjectAssignedToOneStudent() > 0) {
						hardCost += (1.0 * solution.getViolationsProjectAssignedToOneStudent()) / solution.getProjects().size()
								* Settings.COST_PER_HARD_VIOLATION;

						hard.updateProjectAssignedToOneStudent(false, solution.getViolationsProjectAssignedToOneStudent(),
								solution.getProjects().size());
					} else {
						hard.updateProjectAssignedToOneStudent(true, null, null);
					}
				}

				// soft
				if (Settings.enableEquallyDistributedAcrossSupervisors) {
					if (solution.getViolationsEquallyDistributedAcrossSupervisors() > 0) {
						softCost += (1.0 * solution.getViolationsEquallyDistributedAcrossSupervisors())
								/ solution.getStaffMembers().size() * Settings.COST_PER_SOFT_VIOLATION;

						soft.updateEquallyDistributedAcrossSupervisors(false,
								solution.getViolationsEquallyDistributedAcrossSupervisors(), solution.getStaffMembers().size());
					} else {
						soft.updateEquallyDistributedAcrossSupervisors(true, null, null);
					}
				}

				if (Settings.enableHigherGPAHigherPreferences && Settings.enableGPA) {
					if (solution.getViolationsHigherGPAHigherPreferences() > 0) {
						softCost += (1.0 * solution.getViolationsHigherGPAHigherPreferences()) / solution.getNumberOfStudents()
								* Settings.COST_PER_SOFT_VIOLATION;

						soft.updateHigherGPAHigherPreferences(false, solution.getViolationsHigherGPAHigherPreferences(),
								solution.getNumberOfStudents());
					} else {
						soft.updateHigherGPAHigherPreferences(true, null, null);
					}
				}

				// update strength.
				strength.setProgressBar(Settings.TOTAL_POINTS - hardCost - softCost);
			} catch (NoSuchElementException e) {
			}
		}
	}

	public void addLastCandidateSolutionToSheet() {
		if (!isDequeEmpty())
			updateSheetAndStrengthAndConstraints(solutionDeque.removeLast());
	}

	protected Node initTableView() {
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

		// checks if the current row, i.e. student has hard constraint violations, if so, color row red, else green.
		tableView.setRowFactory(new Callback<TableView<Student>, TableRow<Student>>() {
			@Override
			public TableRow<Student> call(TableView<Student> tableView) {
				final TableRow<Student> row = new TableRow<Student>() {
					@Override
					protected void updateItem(Student student, boolean empty) {
						super.updateItem(student, empty);

						if (!empty && student.isViolationCalculated()) {
							if (student.hasHardConstraintViolation()) {
								getStyleClass().removeAll(Collections.singleton("row-good-allocation"));
								getStyleClass().add("row-bad-allocation");
							} else {
								getStyleClass().removeAll(Collections.singleton("row-bad-allocation"));
								getStyleClass().add("row-good-allocation");
							}
						}
					}
				};
				return row;
			}
		});

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

		return tableView;
	}
}