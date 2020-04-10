package ie.ucd.ui.solver;

import ie.ucd.Common.SolverType;
import ie.ucd.ui.common.StudentSheet;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SolverPane extends VBox {
	private Visualizer visualizer;
	private ControlButtons controlButtons;
	private StudentSheet sheet;

	public SolverPane(Stage stage, SolverType solverType) {
		super();
		visualizer = new Visualizer(solverType);
		sheet = new StudentSheet(stage, true);
		controlButtons = new ControlButtons(visualizer, sheet, solverType);
		initLayout();
	}

	private void initLayout() {
		getChildren().add(new Label("Visualization"));
		getChildren().add(visualizer);
		getChildren().add(controlButtons);
		getChildren().add(sheet);
	}

	public void stopVisualizerScheduler() {
		visualizer.stopAddToGraphScheduler();
	}
}