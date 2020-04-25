package ie.ucd.ui.common.sheets;

import ie.ucd.ui.interfaces.SheetInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class SearchBox<E> extends TextField {
	private boolean isWaiting;
	private Timeline waitForInput;
	private SheetInterface<E> sheet;

	public SearchBox(String promptText, SheetInterface<E> sheet) {
		this.sheet = sheet;
		resetStates();
		initTimeline();

		setMaxWidth(600);
		setPromptText(promptText);
		setOnKeyReleased(keyEvent -> {
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