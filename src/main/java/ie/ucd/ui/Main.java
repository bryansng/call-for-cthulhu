package ie.ucd.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
		
		//Button button = new Button();
		//button.setText("test");
		
		VBox vbox = new VBox();
		Button buttonSetup = new Button("Setup");
		Button buttonSA = new Button("Simulated Annealing Solver");
		Button buttonGA = new Button("Genetic Algorithm Solver");
		vbox.getChildren().addAll(buttonSetup, buttonSA, buttonGA);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(vbox);
		ScrollPane scrollPane = new ScrollPane();
		borderPane.setCenter(scrollPane);
		
		Scene setupScene = new Scene(borderPane, 600, 400);
		//root.getChildren().add(button);
		primaryStage.setScene(setupScene);
		primaryStage.show();
	}

	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
