package ie.ucd.ui.sa;

import ie.ucd.solvers.Solver;
import ie.ucd.ui.common.ControlButtons;
import ie.ucd.ui.common.Sheet;
import ie.ucd.ui.common.Visualizer;
import ie.ucd.ui.interfaces.VisualizerInterface;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SAPane extends VBox {
	private Visualizer visualizer;
	private ControlButtons controlButtons;
	private Sheet sheet;
	private Button saveToFileButton;

	public SAPane() {
		super();
		visualizer = new Visualizer();
		sheet = new Sheet();
		controlButtons = new ControlButtons(visualizer, sheet);
		saveToFileButton = new Button("Save to file");
		initLayout();
	}

	private void initLayout() {
		getChildren().add(new Label("Visualization"));
		getChildren().add(visualizer);
		getChildren().add(controlButtons);
		getChildren().add(sheet);
		getChildren().add(saveToFileButton);
	}

	public VisualizerInterface getVisualizer() {
		return visualizer;
	}

	public void stopVisualizerScheduler() {
		visualizer.stopAddToGraphScheduler();
	}

	public void setSolver(Solver solver) {
		controlButtons.setSolver(solver);
	}
}