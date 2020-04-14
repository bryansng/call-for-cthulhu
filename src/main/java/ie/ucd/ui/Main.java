package ie.ucd.ui;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
		
		//hard/soft constraints create
		VBox vboxHardConstraints = new VBox();
		
		CheckBox hardOne = new CheckBox("Each student is assigned exactly one of their preferred projects");
		CheckBox hardTwo= new CheckBox("Each project is assigned to at most one student");
		CheckBox hardThree = new CheckBox("Each student belongs to the stream designated for their assigned project");
		CheckBox softOne = new CheckBox("higher GPA means a greater chance of getting one's preferred project");
		CheckBox softTwo = new CheckBox("projects are distributed roughly equally between supervisors");
		Text headingOne = new Text("1. Settings");

		//TilePane tilePane = new TilePane();
		//tilePane.getChildren().add(hardOne);
		
		vboxHardConstraints.getChildren().addAll(headingOne, hardOne, hardTwo, hardThree, softOne, softTwo);
		grid.setConstraints(vboxHardConstraints, 0, 0);
		grid.setConstraints(hardOne, 1, 0);
		grid.setConstraints(hardOne, 2, 0);
		grid.setConstraints(hardOne, 3, 0);
		grid.setConstraints(softOne, 4, 0);
		grid.setConstraints(softOne, 5, 0);
		grid.getChildren().addAll(vboxHardConstraints);
		scrollPane.setContent(grid);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		borderPane.setCenter(scrollPane);
		
		//vboxHardConstraints.getChildren().addAll()
		
	
		//constraints create
		Scene setupScene = new Scene(borderPane, 600, 400);
		//root.getChildren().add(button);
		primaryStage.setScene(setupScene);
		primaryStage.show();
	}

	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
