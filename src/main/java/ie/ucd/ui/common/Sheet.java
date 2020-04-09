package ie.ucd.ui.common;

import javafx.scene.layout.VBox;

public abstract class Sheet extends VBox {
	public Sheet() {
		initTableView();
		initLayout();
	}

	public abstract void search(String searchTerm);

	protected abstract void initLayout();

	protected abstract void initTableView();
}