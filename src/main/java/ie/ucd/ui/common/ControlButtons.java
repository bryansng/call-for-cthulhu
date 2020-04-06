package ie.ucd.ui.common;

import ie.ucd.interfaces.Solver;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlButtons extends HBox {
	private Solver solver;
	private Visualizer visualizer;
	private Sheet sheet;

	private Button play;
	private Button pause;
	private Button clearAndReset;
	private Button step;

	public ControlButtons(Visualizer visualizer, Sheet sheet) {
		super();
		this.visualizer = visualizer;
		this.sheet = sheet;
		initLayout();
	}

	private void initLayout() {
		play = new Button("Play");
		play.setOnAction((event) -> {
			solver.run();
		});

		pause = new Button("Pause");
		clearAndReset = new Button("Clear and Reset");
		step = new Button("Step");

		getChildren().add(play);
		getChildren().add(pause);
		getChildren().add(clearAndReset);
		getChildren().add(step);
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
}