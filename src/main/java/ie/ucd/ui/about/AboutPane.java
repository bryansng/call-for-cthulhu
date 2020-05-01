package ie.ucd.ui.about;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class AboutPane extends ScrollPane {
	public AboutPane() {
		super();
		initLayout();
	}

	public void initLayout() {
		VBox outerContainer = new VBox();
		outerContainer.getStyleClass().add("standard-padding");

		VBox innerContainer = new VBox();
		innerContainer.getStyleClass().add("smaller-main-container");

		Label aboutLabel = new Label("About");
		aboutLabel.getStyleClass().add("main-label");

		Label expectationsLabel = new Label("What the application does");
		expectationsLabel.getStyleClass().add("sub-label");
		Label expectationsText = new Label("...");

		Label howSAWorksLabel = new Label("How SA works");
		howSAWorksLabel.getStyleClass().add("sub-label");
		Label howSAWorksText = new Label("...");

		Label howGAWorksLabel = new Label("How GA works");
		howGAWorksLabel.getStyleClass().add("sub-label");
		Label howGAWorksText = new Label("...");

		innerContainer.getChildren().addAll(new VBox(expectationsLabel, expectationsText),
				new VBox(howSAWorksLabel, howSAWorksText), new VBox(howGAWorksLabel, howGAWorksText));
		outerContainer.getChildren().addAll(aboutLabel, innerContainer);
		setContent(outerContainer);
	}
}