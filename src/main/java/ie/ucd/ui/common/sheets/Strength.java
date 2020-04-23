package ie.ucd.ui.common.sheets;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Strength extends VBox {
	private ProgressBar bar;
	private Label value;
	private Label rating;

	public Strength() {
		super();
		initLayout();
	}

	public void initLayout() {
		getChildren().add(new Label("Strength"));

		bar = new ProgressBar(0.0);
		value = new Label("-");
		rating = new Label("-");
		HBox hBox = new HBox(5.0, bar, value, rating);
		getChildren().add(hBox);
	}

	public void setProgressBar(Double progress) {
		bar.setProgress(progress); // between 0.0 and 1.0

		if (progress < 0.25) {
			setLabelText(String.format("%.04f", progress), "Horrible");
			rating.setStyle("-fx-background-color: #a40000; -fx-font-weight: bold;");
		} else if (progress >= 0.25 && progress < 0.5) {
			setLabelText(String.format("%.04f", progress), "Weak");
			rating.setStyle("-fx-background-color: #ef2929; -fx-font-weight: bold;");
		} else if (progress >= 0.5 && progress < 0.85) {
			setLabelText(String.format("%.04f", progress), "Fair");
			rating.setStyle("-fx-background-color: #fce94f; -fx-font-weight: bold;");
		} else if (progress >= 0.85 && progress < 0.95) {
			setLabelText(String.format("%.04f", progress), "Good");
			rating.setStyle("-fx-background-color: #8ae234; -fx-font-weight: bold;");
		} else if (progress >= 0.95) {
			setLabelText(String.format("%.04f", progress), "Excellent");
			rating.setStyle("-fx-background-color: #4e9a06; -fx-font-weight: bold;");
		}
	}

	public void setLabelText(String valueText, String ratingText) {
		value.setText(valueText);
		rating.setText(ratingText);
	}

	public void reset() {
		bar.setProgress(0.0);
		value.setText("-");
		rating.setText("-");
		rating.setStyle("");
	}
}