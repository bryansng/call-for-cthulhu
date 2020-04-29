package ie.ucd.ui.navigation;

import ie.ucd.Common;
import ie.ucd.Settings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class SimulatedAnnealingTP extends TitledPane {
	private TextField startTemperature;
	private TextField coolingRate;
	private TextField minTemperature;
	private TextField maxIteration;
	private Label startTemperatureWarning;
	private Label coolingRateWarning;
	private Label minTemperatureWarning;
	private Label maxIterationWarning;

	public SimulatedAnnealingTP() {
		super();
		initLayout();
	}

	private void initLayout() {
		setText("Simulated Annealing");
		setContent(initContent());
	}

	private Node initContent() {
		VBox container = new VBox(5);
		container.getChildren().addAll(initStartTemperature(), startTemperatureWarning, initCoolingRate(),
				coolingRateWarning, initMinTemperature(), minTemperatureWarning, initMaxIteration(), maxIterationWarning);
		container.getChildren().add(0, initDefaultConfigButton());
		return container;
	}

	// relies on all TextField to be initialized first,
	// so call this after calling all the other inits.
	private Button initDefaultConfigButton() {
		Button defaultConfig = new Button("DEFAULT CONFIG");
		defaultConfig.setOnMouseClicked((evt) -> {
			startTemperature.setText(Settings.SA_DEFAULT_START_TEMPERATURE.toString());
			coolingRate.setText(Settings.SA_DEFAULT_COOLING_RATE.toString());
			minTemperature.setText(Settings.SA_DEFAULT_MIN_TEMPERATURE.toString());
			maxIteration.setText(Settings.SA_DEFAULT_MAX_ITERATION.toString());

			Settings.saStartTemperature = Settings.SA_DEFAULT_START_TEMPERATURE;
			Settings.saCoolingRate = Settings.SA_DEFAULT_COOLING_RATE;
			Settings.saMinTemperature = Settings.SA_DEFAULT_MIN_TEMPERATURE;
			Settings.saMaxIteration = Settings.SA_DEFAULT_MAX_ITERATION;

			startTemperatureWarning.setText("");
			coolingRateWarning.setText("");
			minTemperatureWarning.setText("");
			maxIterationWarning.setText("");
		});
		return defaultConfig;
	}

	private Node initStartTemperature() {
		startTemperatureWarning = new Label();
		startTemperatureWarning.getStyleClass().add("warning-label");
		startTemperature = new TextField(Settings.saStartTemperature.toString());
		startTemperature.setOnKeyReleased((evt) -> {
			String newConfig = startTemperature.getText();
			try {
				if (newConfig.equals("")) {
					Settings.saStartTemperature = Settings.SA_DEFAULT_START_TEMPERATURE;
				} else {
					Settings.saStartTemperature = Double.parseDouble(newConfig);
				}
				startTemperatureWarning.setText("");
			} catch (NumberFormatException e) {
				startTemperatureWarning.setText("WARNING: Must be a number.");
				Settings.saStartTemperature = Settings.SA_DEFAULT_START_TEMPERATURE;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%f %f %f %f", Settings.saStartTemperature, Settings.saCoolingRate,
						Settings.saMinTemperature, Settings.saMaxIteration));
		});
		return new VBox(new VBox(2.5, new Label("Starting Temperature"), startTemperature), startTemperatureWarning);
	}

	private Node initCoolingRate() {
		coolingRateWarning = new Label();
		coolingRateWarning.getStyleClass().add("warning-label");
		coolingRate = new TextField(Settings.saCoolingRate.toString());
		coolingRate.setOnKeyReleased((evt) -> {
			String newConfig = coolingRate.getText();
			try {
				if (newConfig.equals("")) {
					Settings.saCoolingRate = Settings.SA_DEFAULT_COOLING_RATE;
					coolingRateWarning.setText("");
				} else {
					Double val = Double.parseDouble(newConfig);
					if (val <= 0) {
						Settings.saCoolingRate = Settings.SA_DEFAULT_COOLING_RATE;
						coolingRateWarning.setText("WARNING: Must be greater than 0.");
					} else {
						Settings.saCoolingRate = val;
						coolingRateWarning.setText("");
					}
				}
			} catch (NumberFormatException e) {
				coolingRateWarning.setText("WARNING: Must be a number.");
				Settings.saCoolingRate = Settings.SA_DEFAULT_COOLING_RATE;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%f %f %f %f", Settings.saStartTemperature, Settings.saCoolingRate,
						Settings.saMinTemperature, Settings.saMaxIteration));
		});
		return new VBox(new VBox(2.5, new Label("Cooling Rate"), coolingRate), coolingRateWarning);
	}

	private Node initMinTemperature() {
		minTemperatureWarning = new Label();
		minTemperatureWarning.getStyleClass().add("warning-label");
		minTemperature = new TextField(Settings.saMinTemperature.toString());
		minTemperature.setOnKeyReleased((evt) -> {
			String newConfig = minTemperature.getText();
			try {
				if (newConfig.equals("")) {
					Settings.saMinTemperature = Settings.SA_DEFAULT_MIN_TEMPERATURE;
				} else {
					Settings.saMinTemperature = Double.parseDouble(newConfig);
				}
				minTemperatureWarning.setText("");
			} catch (NumberFormatException e) {
				minTemperatureWarning.setText("WARNING: Must be a number.");
				Settings.saMinTemperature = Settings.SA_DEFAULT_MIN_TEMPERATURE;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%f %f %f %f", Settings.saStartTemperature, Settings.saCoolingRate,
						Settings.saMinTemperature, Settings.saMaxIteration));
		});
		return new VBox(new VBox(2.5, new Label("Minimum Temperature"), minTemperature), minTemperatureWarning);
	}

	private Node initMaxIteration() {
		maxIterationWarning = new Label();
		maxIterationWarning.getStyleClass().add("warning-label");
		maxIteration = new TextField(Settings.saMaxIteration.toString());
		maxIteration.setOnKeyReleased((evt) -> {
			String newConfig = maxIteration.getText();
			try {
				if (newConfig.equals("")) {
					Settings.saMaxIteration = Settings.SA_DEFAULT_MAX_ITERATION;
					maxIterationWarning.setText("");
				} else {
					Double val = Double.parseDouble(newConfig);
					if (val <= 0) {
						System.out.println("smaller than zero");
						Settings.saMaxIteration = Settings.SA_DEFAULT_MAX_ITERATION;
						maxIterationWarning.setText("WARNING: Must be greater than 0.");
					} else {
						Settings.saMaxIteration = val;
						maxIterationWarning.setText("");
					}
				}
			} catch (NumberFormatException e) {
				maxIterationWarning.setText("WARNING: Must be a number.");
				Settings.saMaxIteration = Settings.SA_DEFAULT_MAX_ITERATION;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%f %f %f %f", Settings.saStartTemperature, Settings.saCoolingRate,
						Settings.saMinTemperature, Settings.saMaxIteration));
		});
		return new VBox(new VBox(2.5, new Label("Max Iteration"), maxIteration), maxIterationWarning);
	}
}