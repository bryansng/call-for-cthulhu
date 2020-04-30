package ie.ucd.ui.navigation;

import ie.ucd.Common;
import ie.ucd.Settings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GeneticAlgorithmTP extends TitledPane {
	private TextField numberOfGeneration;
	private TextField populationSize;
	private Slider crossoverChance;
	private Slider mutationChance;
	private Slider cullChance;
	private Label numberOfGenerationWarning;
	private Label populationSizeWarning;

	public GeneticAlgorithmTP() {
		super();
		initLayout();
	}

	private void initLayout() {
		setText("Genetic Algorithm");
		setContent(initContent());
	}

	private Node initContent() {
		VBox container = new VBox(5);
		container.getChildren().addAll(initNumberOfGeneration(), initPopulationSize(), initCrossoverChance(),
				initMutationChance());
		container.getChildren().add(0, initDefaultConfigButton());
		return container;
	}

	// relies on all TextField to be initialized first,
	// so call this after calling all the other inits.
	private Button initDefaultConfigButton() {
		Button defaultConfig = new Button("DEFAULT CONFIG");
		defaultConfig.setOnMouseClicked((evt) -> {
			numberOfGeneration.setText(Settings.GA_DEFAULT_NUMBER_OF_GENERATION.toString());
			populationSize.setText(Settings.GA_DEFAULT_POPULATION_SIZE.toString());
			crossoverChance.setValue(Settings.GA_DEFAULT_CROSSOVER_CHANCE);
			mutationChance.setValue(Settings.GA_DEFAULT_MUTATION_CHANCE);
			// cullChance.setValue(Settings.GA_DEFAULT_CULL_CHANCE);

			Settings.gaNumberOfGeneration = Settings.GA_DEFAULT_NUMBER_OF_GENERATION;
			Settings.gaPopulationSize = Settings.GA_DEFAULT_POPULATION_SIZE;
			Settings.gaCrossoverChance = Settings.GA_DEFAULT_CROSSOVER_CHANCE;
			Settings.gaMutationChance = Settings.GA_DEFAULT_MUTATION_CHANCE;
			// Settings.gaCullChance = Settings.GA_DEFAULT_CULL_CHANCE;

			numberOfGenerationWarning.setText("");
			populationSizeWarning.setText("");
		});
		return defaultConfig;
	}

	private Node initNumberOfGeneration() {
		numberOfGenerationWarning = new Label();
		numberOfGenerationWarning.getStyleClass().add("warning-label");
		numberOfGeneration = new TextField(Settings.gaNumberOfGeneration.toString());
		numberOfGeneration.setOnKeyReleased((evt) -> {
			String newConfig = numberOfGeneration.getText();
			try {
				if (newConfig.equals("")) {
					Settings.gaNumberOfGeneration = Settings.GA_DEFAULT_NUMBER_OF_GENERATION;
					numberOfGenerationWarning.setText("");
				} else {
					Integer val = Integer.parseInt(newConfig);
					if (val <= 0) {
						Settings.gaNumberOfGeneration = Settings.GA_DEFAULT_NUMBER_OF_GENERATION;
						numberOfGenerationWarning.setText("WARNING: Must be greater than 0.");
					} else if (val < 15) {
						Settings.gaNumberOfGeneration = Settings.GA_DEFAULT_NUMBER_OF_GENERATION;
						numberOfGenerationWarning.setText("WARNING: Must be greater than 14 for diversity.");
					} else {
						Settings.gaNumberOfGeneration = val;
						numberOfGenerationWarning.setText("");
					}
				}
			} catch (NumberFormatException e) {
				numberOfGenerationWarning.setText("WARNING: Must be a number.");
				Settings.gaNumberOfGeneration = Settings.GA_DEFAULT_NUMBER_OF_GENERATION;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%d %d %f %f %f", Settings.gaNumberOfGeneration, Settings.gaPopulationSize,
						Settings.gaCrossoverChance, Settings.gaMutationChance, Settings.gaCullChance));
		});
		return new VBox(new VBox(2.5, new Label("Number of generation"), numberOfGeneration), numberOfGenerationWarning);
	}

	private Node initPopulationSize() {
		populationSizeWarning = new Label();
		populationSizeWarning.getStyleClass().add("warning-label");
		populationSize = new TextField(Settings.gaPopulationSize.toString());
		populationSize.setOnKeyReleased((evt) -> {
			String newConfig = populationSize.getText();
			try {
				if (newConfig.equals("")) {
					Settings.gaPopulationSize = Settings.GA_DEFAULT_POPULATION_SIZE;
					populationSizeWarning.setText("");
				} else {
					Integer val = Integer.parseInt(newConfig);
					if (val <= 0) {
						Settings.gaPopulationSize = Settings.GA_DEFAULT_POPULATION_SIZE;
						populationSizeWarning.setText("WARNING: Must be greater than 0.");
					} else if (val < 10) {
						Settings.gaPopulationSize = Settings.GA_DEFAULT_POPULATION_SIZE;
						populationSizeWarning.setText("WARNING: Must be greater than 9 for diversity.");
					} else {
						Settings.gaPopulationSize = val;
						populationSizeWarning.setText("");
					}
				}
			} catch (NumberFormatException e) {
				populationSizeWarning.setText("WARNING: Must be a number.");
				Settings.gaPopulationSize = Settings.GA_DEFAULT_POPULATION_SIZE;
			} catch (NullPointerException e) {
			}
			if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
				System.out.println(String.format("%d %d %f %f %f", Settings.gaNumberOfGeneration, Settings.gaPopulationSize,
						Settings.gaCrossoverChance, Settings.gaMutationChance, Settings.gaCullChance));
		});
		return new VBox(new VBox(2.5, new Label("Population size"), populationSize), populationSizeWarning);
	}

	private Node initCrossoverChance() {
		Label textValue = new Label(String.format("%.4f", Settings.gaCrossoverChance));
		crossoverChance = new Slider(0.0001, 1.0, Settings.gaCrossoverChance);
		crossoverChance.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				textValue.setText(String.format("%.4f", newValue));
				Settings.gaCrossoverChance = newValue.doubleValue();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(String.format("%d %d %f %f %f", Settings.gaNumberOfGeneration, Settings.gaPopulationSize,
							Settings.gaCrossoverChance, Settings.gaMutationChance, Settings.gaCullChance));
			}
		});
		return new VBox(2.5, new Label("Cross-over chance"), new HBox(3, crossoverChance, textValue));
	}

	private Node initMutationChance() {
		Label textValue = new Label(String.format("%.4f", Settings.gaMutationChance));
		mutationChance = new Slider(0.0, 1.0, Settings.gaMutationChance);
		mutationChance.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				textValue.setText(String.format("%.4f", newValue));
				Settings.gaMutationChance = newValue.doubleValue();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(String.format("%d %d %f %f %f", Settings.gaNumberOfGeneration, Settings.gaPopulationSize,
							Settings.gaCrossoverChance, Settings.gaMutationChance, Settings.gaCullChance));
			}
		});
		return new VBox(2.5, new Label("Mutation chance"), new HBox(3, mutationChance, textValue));
	}

	private Node initCullChance() {
		Label textValue = new Label(String.format("%.4f", Settings.gaCullChance));
		cullChance = new Slider(0.0, 1.0, Settings.gaCullChance);
		cullChance.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				textValue.setText(String.format("%.4f", newValue));
				Settings.gaCullChance = newValue.doubleValue();
				if (Common.DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE)
					System.out.println(String.format("%d %d %f %f %f", Settings.gaNumberOfGeneration, Settings.gaPopulationSize,
							Settings.gaCrossoverChance, Settings.gaMutationChance, Settings.gaCullChance));
			}
		});
		return new VBox(2.5, new Label("Cull chance"), new HBox(3, cullChance, textValue));
	}
}