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

		Label expectationLabel = new Label("What the Application does");
		expectationLabel.getStyleClass().add("sub-label");
		Label expectationText = new Label(
				"Call-for-Cthulu is a project that aims at employing machine learning algorithms to assign final year projects to college students based on the students' preference list and GPA, as well as certain constraints as are listed on the Setup page.");

		Label settingsLabel = new Label("Settings");
		settingsLabel.getStyleClass().add("sub-label");
		Label settingsText = new Label(
				"Each constraint can be toggled on or off here. You can also choose whether or not the program should consider the GPA. If this is on, students with better GPA tend to get their preffered projects more easily and the weight given to the GPA can also be chosen along with the theme and the animations for the graph (for when either solver is used).");

		Label gettingStartedLabel = new Label("Getting Started");
		gettingStartedLabel.getStyleClass().add("sub-label");
		Label gettingStartedText = new Label(
				"Project and student information can either be randomly generated or loaded into the program using CSVs on the Setup page under the 'Load/Generate Projects' and 'Load/Generate Students'. Valid CSV formats are as follows:\n\nProjects: <Staff Member> <Project Name> <Stream> <Preferred Probability (optional)>\nStudents: <First Name> <Last Name> <ID> <Stream> <GPA> <Project Preference #1> ... <Project Preference #20>");

		Label howSAWorksLabel = new Label("Simulated Annealing");
		howSAWorksLabel.getStyleClass().add("sub-label");
		Label howSAWorksText = new Label(
				"A thermodynamics-inspired stochastic machine learning algorithm used for optimization problems. It models the physical process of heating a material and then slowly reducing its temperature to decrease defects, minimizing the system's energy in the process.");

		Label howGAWorksLabel = new Label("Genetic Algorithm");
		howGAWorksLabel.getStyleClass().add("sub-label");
		Label howGAWorksText = new Label(
				"A genetic algorithm is a search heuristic that is inspired by Charles Darwin's theory of natural evolution. This algorithm reflects the process of natural selection where the fittest solutions are selected for reproduction in order to produce offsprings of the next generation hence using the process of natural selection to evolve solution strength over time.");

		Label solutionsLabel = new Label("Solutions");
		solutionsLabel.getStyleClass().add("sub-label");
		Label solutionsText = new Label(
				"Once the selected solver has finished simulating, you can view the strength, fitness and the actual project assignments below the graph. The assignments that meet constraints are highlighted in green while ones that violate it are in red. These can be exported as a CSV file to your hard drive.");

		expectationText.setMaxWidth(500);
		settingsText.setMaxWidth(500);
		gettingStartedText.setMaxWidth(500);
		howSAWorksText.setMaxWidth(500);
		howGAWorksText.setMaxWidth(500);
		solutionsText.setMaxWidth(500);
		setMinWidth(600);

		innerContainer.getChildren().addAll(new VBox(expectationLabel, expectationText),
				new VBox(settingsLabel, settingsText), new VBox(gettingStartedLabel, gettingStartedText),
				new VBox(howSAWorksLabel, howSAWorksText), new VBox(howGAWorksLabel, howGAWorksText),
				new VBox(solutionsLabel, solutionsText));
		outerContainer.getChildren().addAll(aboutLabel, innerContainer);
		setContent(outerContainer);
	}
}