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
import javafx.stage.Stage;

public class Setup extends Application{

	public static void main(String[] args) {
		launch(args);

	}
	
	@Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Setup.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Call for Cthulhu program");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }

}

/*
public class HelloWorld extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("My Title");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }
}
*/