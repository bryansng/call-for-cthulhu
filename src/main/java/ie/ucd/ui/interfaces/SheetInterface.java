package ie.ucd.ui.interfaces;

import java.util.ArrayList;

public interface SheetInterface<E> {
	public void setAll(ArrayList<E> elements);

	public void search(String searchTerm);
}