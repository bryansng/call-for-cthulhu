package ie.ucd.ui.solver.progress;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class CustomProgressIndicator extends StackPane {
	private Deque<Progress> progressDeque;

	private ProgressBar bar;
	private Label percentage;
	private String percentageSuffix = "%";
	private DecimalFormat df;

	public CustomProgressIndicator() {
		super();
		df = new DecimalFormat("##.#####");
		df.setRoundingMode(RoundingMode.FLOOR);
		progressDeque = new LinkedList<Progress>();

		initLayout();
	}

	private void initLayout() {
		bar = new ProgressBar(0.0);
		bar.setPrefWidth(this.getPrefWidth());

		percentage = new Label(0.0 + percentageSuffix);
		getChildren().addAll(bar, percentage);
	}

	public void setProgress(double progress) {
		bar.setProgress(progress);
		percentage.setText(String.format("%s", df.format(progress * 100)) + percentageSuffix); // convert progress to percentage.
	}

	public void setDone() {
		resetSeries();
		setProgress(1.0);
	}

	public void resetSeries() {
		progressDeque.clear();
	}

	public void reset() {
		progressDeque.clear();
		setProgress(0.0);
	}

	public boolean isDequeEmpty() {
		return progressDeque.isEmpty();
	}

	// SA: numerator = currentTemperature, denominator = minTemperature.
	// SA's end < start.
	// GA: numerator = currentGeneration, denominator maxGeneration.
	// GA's end > start.
	public void addToQueue(double start, double current, double end) {
		progressDeque.add(new Progress(start, current, end));
		updateIndicator();
	}

	public void initOneShotScheduler() {
		updateIndicator();
	}

	public void updateIndicator() {
		Platform.runLater(() -> {
			if (!isDequeEmpty()) {
				try {
					Progress progress = progressDeque.removeFirst();
					double start = progress.getStart();
					double current = progress.getCurrent();
					double end = progress.getEnd();

					double currProgress = 0.0;
					if (start > end) {
						currProgress = (start - current) / (start - end);
					} else if (end > start) {
						currProgress = (current - start) / (end - start);
					} else {
						currProgress = 1.0;
					}

					if (currProgress > 1.0) {
						currProgress = 1.0;
					}
					setProgress(currProgress);
				} catch (NoSuchElementException e) {
				}
			}
		});
	}
}