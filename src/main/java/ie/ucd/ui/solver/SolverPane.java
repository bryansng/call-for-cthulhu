package ie.ucd.ui.solver;

import ie.ucd.Common.SolverType;
import ie.ucd.ui.common.StudentSheet;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SolverPane extends VBox {
	private Visualizer visualizer;
	private ControlButtons controlButtons;
	private StudentSheet sheet;
	private Button saveToFileButton;

	public SolverPane(SolverType solverType) {
		super();
		initLayout(solverType);
	}

	private void initLayout(SolverType solverType) {
		visualizer = new Visualizer(solverType);
		sheet = new StudentSheet();
		controlButtons = new ControlButtons(visualizer, sheet, solverType);
		saveToFileButton = new Button("Save to file");

		getChildren().add(new Label("Visualization"));
		getChildren().add(visualizer);
		getChildren().add(controlButtons);
		getChildren().add(sheet);
		getChildren().add(saveToFileButton);
	}

	public void stopVisualizerScheduler() {
		visualizer.stopAddToGraphScheduler();
	}
}