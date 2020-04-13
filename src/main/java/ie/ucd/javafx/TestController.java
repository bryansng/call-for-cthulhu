package ie.ucd.javafx;

//import java.util.;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


public class TestController {

	@FXML
	private boolean start = true;
	private Label input;
	private TestProject testproject = new TestProject();
	
	@FXML
	public void selectSection(ActionEvent event)	{
		/*if (start)	{
			input.setText("");
			start = false;
		}*/
		String value = ((Button)event.getSource()).getText();
		//input.setText(input.getText() + value);
		System.out.print(value);
		try	{
		testproject.selectSection(value);
		}
		catch(Exception e)	{
			System.out.print("Error in selecting section");
		}
	}
	
	public void selectBrowse(ActionEvent event)		{
		testproject.browseFiles();
	}

}