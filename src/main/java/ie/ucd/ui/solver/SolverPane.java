package ie.ucd.ui.solver;

import ie.ucd.Common.SolverType;
import ie.ucd.ui.common.sheets.Sheets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SolverPane extends ScrollPane {
	private Visualizer visualizer;
	private ControlButtons controlButtons;
	private Sheets sheets;

	public SolverPane(Stage stage, SolverType solverType) {
		super();
		visualizer = new Visualizer(solverType);
		sheets = new Sheets(stage);
		controlButtons = new ControlButtons(visualizer, sheets, solverType);
		visualizer.setControlButtons(controlButtons);
		initLayout();
	}

	private void initLayout() {
		VBox vBox = new VBox();
		vBox.getChildren().add(new Label("Visualization"));
		vBox.getChildren().add(visualizer);
		vBox.getChildren().add(controlButtons);
		vBox.getChildren().add(sheets);

		setContent(vBox);
	}

	public void stopVisualizerScheduler() {
		visualizer.stop();
	}
}