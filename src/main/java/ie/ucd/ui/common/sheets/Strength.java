package ie.ucd.ui.common.sheets;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Strength extends HBox {
	private ProgressBar bar;
	private Label value;
	private Label rating;

	public Strength() {
		super();
		initLayout();
	}

	public void initLayout() {
		Label label = new Label("Strength");
		label.getStyleClass().add("sub-label");
		getChildren().add(label);

		bar = new ProgressBar(0.0);
		value = new Label("-");
		rating = new Label("-");

		StackPane valueOnBar = new StackPane(bar, value);

		setSpacing(5);
		getChildren().addAll(valueOnBar, rating);
	}

	public void setProgressBar(Double progress) {
		setProgressBar(progress, false);
	}

	public void setProgressBar(Double progress, Boolean hasHardConstraintViolations) {
		bar.setProgress(progress); // between 0.0 and 1.0

		if (hasHardConstraintViolations) {
			if (progress < 0.5) {
				setLabelText(String.format("%.04f", progress), "Unusable");
				rating.setStyle("-fx-background-color: #5e0101; -fx-font-weight: bold; -fx-text-fill: #b5b5b5;");
			} else if (progress >= 0.5 && progress < 0.85) {
				setLabelText(String.format("%.04f", progress), "Horrible");
				rating.setStyle("-fx-background-color: #a40000; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.85 && progress < 0.95) {
				setLabelText(String.format("%.04f", progress), "Weak");
				rating.setStyle("-fx-background-color: #ef2929; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.95) {
				setLabelText(String.format("%.04f", progress), "Fair");
				rating.setStyle("-fx-background-color: #fce94f; -fx-font-weight: bold; -fx-text-fill: black;");
			}
		} else {
			if (progress < 0.25) {
				setLabelText(String.format("%.04f", progress), "Horrible");
				rating.setStyle("-fx-background-color: #a40000; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.25 && progress < 0.5) {
				setLabelText(String.format("%.04f", progress), "Weak");
				rating.setStyle("-fx-background-color: #ef2929; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.5 && progress < 0.85) {
				setLabelText(String.format("%.04f", progress), "Fair");
				rating.setStyle("-fx-background-color: #fce94f; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.85 && progress < 0.95) {
				setLabelText(String.format("%.04f", progress), "Good");
				rating.setStyle("-fx-background-color: #8ae234; -fx-font-weight: bold; -fx-text-fill: black;");
			} else if (progress >= 0.95) {
				setLabelText(String.format("%.04f", progress), "Excellent");
				rating.setStyle("-fx-background-color: #4e9a06; -fx-font-weight: bold; -fx-text-fill: black;");
			}
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