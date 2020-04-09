package ie.ucd.ui.common;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class SearchBox extends TextField {
	private boolean isWaiting;
	private Timeline waitForInput;
	private Sheet sheet;

	public SearchBox(String promptText, Sheet sheet) {
		this.sheet = sheet;
		resetStates();
		initTimeline();

		// setMaxWidth(800);
		setPromptText(promptText);
		setOnKeyTyped(keyEvent -> {
			if (!isWaiting) {
				isWaiting = true;
				waitForInput.play();
			} else {
				waitForInput.stop();
				waitForInput.play();
			}
		});
	}

	private void initTimeline() {
		waitForInput = new Timeline(new KeyFrame(Duration.millis(500), e -> {
			sheet.search(getText());
			resetStates();
			waitForInput.stop();
		}));
		waitForInput.setCycleCount(1);
	}

	private void resetStates() {
		isWaiting = false;
	}
}