package ie.ucd.ui;

import java.io.File;

//import java.awt.Checkbox;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application //implements EventHandler
{
	//Stage window;

	public static void main(String[] args) {
		launch(args);

	}
	
	@Override
	public void start(Stage primaryStage) throws Exception	{
		// window = primaryStage;
		primaryStage.setTitle("Call for Cthulhu - KPMG Come Get Us");
		
		Button button = new Button();
		button.setText("test");
		
		//left pane create buttons
		VBox vboxLeftPane = new VBox();
		Button buttonSetup = new Button("Setup");
		Button buttonSA = new Button("Simulated Annealing Solver");
		Button buttonGA = new Button("Genetic Algorithm Solver");
		vboxLeftPane.getChildren().addAll(buttonSetup, buttonSA, buttonGA);
		
		//scroll pane/border pane create
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(vboxLeftPane);
		ScrollPane scrollPane = new ScrollPane();
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		//hard/soft/other constraints create
		VBox vboxHardConstraints = new VBox();
		
		CheckBox hardOne = new CheckBox("Each student is assigned exactly one of their preferred projects");
		CheckBox hardTwo= new CheckBox("Each project is assigned to at most one student");
		CheckBox hardThree = new CheckBox("Each student belongs to the stream designated for their assigned project");
		CheckBox softOne = new CheckBox("higher GPA means a greater chance of getting one's preferred project");
		CheckBox softTwo = new CheckBox("projects are distributed roughly equally between supervisors");
		Text headingOne = new Text("1. Settings");
		
		Slider gpaSlider = new Slider(0, 1, 0);
		gpaSlider.setShowTickMarks(true);
		gpaSlider.setShowTickLabels(true);
		gpaSlider.setMajorTickUnit(0.25f);
		
		vboxHardConstraints.getChildren().addAll(headingOne, hardOne, hardTwo, 
		hardThree, softOne, softTwo, gpaSlider);
		grid.setConstraints(vboxHardConstraints, 0, 0);
		grid.setConstraints(hardOne, 1, 0);
		grid.setConstraints(hardOne, 2, 0);
		grid.setConstraints(hardOne, 3, 0);
		grid.setConstraints(softOne, 4, 0);
		grid.setConstraints(softOne, 5, 0);
		grid.getChildren().addAll(vboxHardConstraints);
		scrollPane.setContent(grid);
		//scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		borderPane.setCenter(scrollPane);
		
		//project section create
		Text headingTwo = new Text("2. Load/Generate Projects");
		FileChooser fileChooserOne = new FileChooser();
		Button browseProjectButton= new Button("Browse for Project File");
		browseProjectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = fileChooserOne.showOpenDialog(primaryStage);
                if (file != null) {
                    //openFile(file);		if file is valid, display name in field
                }
            }
        });
		Button generateRandomProject = new Button("Generate Random Project");
		vboxHardConstraints.getChildren().addAll(headingTwo, browseProjectButton, generateRandomProject);
		
		
		//student section create
		Text headingThree= new Text("3. Load/Generate Students");
		FileChooser fileChooserTwo = new FileChooser();
		Button browseStudentButton= new Button("Browse for Student File");
		browseStudentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = fileChooserTwo.showOpenDialog(primaryStage);
                if (file != null) {
                    //openFile(file);		if file is valid, display name in field
                }
            }
        });
		Button generateRandomStudent= new Button("Generate Random Project");
		vboxHardConstraints.getChildren().addAll(headingThree, browseStudentButton, generateRandomStudent);	
		
		
		
		
		 
		 //gpaSlider.setBlockIncrement(0.1f);
		
		//vboxHardConstraints.getChildren().addAll()
		
	
		
		Scene setupScene = new Scene(borderPane, 600, 400);
		//root.getChildren().add(button);
		primaryStage.setScene(setupScene);
		primaryStage.show();
	}

	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
