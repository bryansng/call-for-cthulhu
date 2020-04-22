package ie.ucd.ui.common.sheets;

import ie.ucd.Common.SolverType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Sheets extends VBox {
	private StudentSheet currSheet;
	private StudentSheet bestSheet;
	private String infix = " ";

	public Sheets(Stage stage, SolverType solverType) {
		super();
		if (solverType == SolverType.GeneticAlgorithm)
			infix = " Fittest ";
		initLayout(stage);
	}

	public void initLayout(Stage stage) {
		getChildren().add(new Label("Solutions"));

		currSheet = new StudentSheet(stage, false, true, true, null);
		bestSheet = new StudentSheet(stage, false, true, true, null);

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new Tab("Current" + infix + "Solution", currSheet));
		tabPane.getTabs().add(new Tab("Best Solution", bestSheet));
		getChildren().add(tabPane);
	}

	public StudentSheet getCurrentSheet() {
		return currSheet;
	}

	public StudentSheet getBestSheet() {
		return bestSheet;
	}

	public void resetSeries() {
		currSheet.resetSeries();
		bestSheet.resetSeries();
	}

	public void resetEvaluation() {
		currSheet.resetEvaluation();
		bestSheet.resetEvaluation();
	}

	public void initOneShotScheduler() {
		currSheet.initOneShotScheduler();
		bestSheet.initOneShotScheduler();
	}

	public void addLastCandidateSolutionToSheet() {
		currSheet.addLastCandidateSolutionToSheet();
		bestSheet.addLastCandidateSolutionToSheet();
	}

	public boolean isDequeEmpty() {
		return currSheet.isDequeEmpty() || bestSheet.isDequeEmpty();
	}
}