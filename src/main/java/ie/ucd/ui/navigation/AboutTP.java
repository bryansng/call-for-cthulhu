package ie.ucd.ui.navigation;

import javafx.scene.control.TitledPane;

public class AboutTP extends TitledPane {
	public AboutTP() {
		super();
		initLayout();
	}

	private void initLayout() {
		setText("About");
	}
}