package ie.ucd.ui.solver;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class CustomProgressIndicator extends StackPane {
	private ProgressBar bar;
	private Text percentage;
	private String percentageSuffix = "%";

	public CustomProgressIndicator() {
		super();
		initLayout();
	}

	private void initLayout() {
		bar = new ProgressBar(0.0);
		percentage = new Text(0.0 + percentageSuffix);
		getChildren().addAll(bar, percentage);
	}

	public void setProgress(double progress) {
		bar.setProgress(progress);
		percentage.setText(String.format("%.2f", progress * 100) + percentageSuffix);
	}
}