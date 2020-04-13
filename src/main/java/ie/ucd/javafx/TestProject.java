package ie.ucd.javafx;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler; 
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
public class TestProject extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Test Project");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }
    
    public void selectSection(String input)	throws Exception{
    	Parent root;
    	Scene scene;
    	Stage primaryStage = new Stage();
    	
    	switch (input)	{
    	case "Setup":
    		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
    		scene = new Scene(root);
            primaryStage.setTitle("Setup");
            primaryStage.setScene(scene);
            primaryStage.show(); 
    		break;
    	case "Random Solver":
    		root = FXMLLoader.load(getClass().getResource("RandomSolver.fxml"));
    		scene = new Scene(root);
            primaryStage.setTitle("Random Solver");
            primaryStage.setScene(scene);
            primaryStage.show(); 
    		break;
    	case "Simulated Annealing Solver":
    		root = FXMLLoader.load(getClass().getResource("AnnealingSolver.fxml"));
    		scene = new Scene(root);
            primaryStage.setTitle("AnnealingSolver");
            primaryStage.setScene(scene);
            primaryStage.show(); 
    		break;
    	case "Genetic Algorithm Solver":
    		root = FXMLLoader.load(getClass().getResource("GeneticSolver.fxml"));
    		scene = new Scene(root);
            primaryStage.setTitle("GeneticSolver");
            primaryStage.setScene(scene);
            primaryStage.show(); 
    		break;
    	default:
    		throw new IllegalArgumentException("Invalid input received: " + input);
    	//Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
    	}
    }
    
    public void browseFiles()	{
    	//Parent root;
    	//Scene scene;
    	Stage primaryStage = new Stage();
    	
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	fileChooser.showOpenDialog(primaryStage);
    }
    
    /*
     * 	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.showOpenDialog(stage);
     */
}