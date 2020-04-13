package ie.ucd.ui.common.sheets;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Strength extends VBox {
	private ProgressBar bar;
	private Label label;

	public Strength() {
		super();
		initLayout();
	}

	public void initLayout() {
		getChildren().add(new Label("Strength"));

		bar = new ProgressBar(0.0);
		label = new Label("-");
		HBox hBox = new HBox(bar, label);
		getChildren().add(hBox);
	}

	public void setProgressBar(Double progress) {
		bar.setProgress(progress); // between 0.0 and 1.0
		setLabelText(progress.toString());
	}

	public void setLabelText(String text) {
		label.setText(text);
	}
}