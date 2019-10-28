import java.util.ArrayList;

import javax.swing.JTable;

public class DataFunctions {

	// Gets the data from a jtable and returns it
	public static Object[][] getData(JTable table_) {
		Object[][] updated_data_ = {{}};
		int rows = table_.getRowCount();
		int columns = table_.getColumnCount();
		updated_data_ = new Object[rows][columns];
		for(int row_index = 0; row_index < rows; row_index++) {
			for(int column_index = 0; column_index < columns; column_index++) {
				updated_data_[row_index][column_index] = table_.getValueAt(row_index, column_index);
			}
		}
		return updated_data_;
	}
	
	
	// Compares an old data set to a current one and if there are changes between them they get 
	// 		saved in a new two dimensional Object array
	// returns the Object array;
	public static Object[][] getModelChanges(Object[][] old_data, Object[][] new_data) {
		Object[][] changed_data = {{}};
		ArrayList<Integer> change_indices = getChangedRows(old_data, new_data);
		int row_index = 0;
		int columns = new_data[0].length;
		if(change_indices.size() == 0) {
			return changed_data;
		}
		changed_data = new Object[change_indices.size()][columns];
		for(Integer change_index : change_indices) {
			for(int column_index = 0; column_index < columns; column_index++) {
				if(new_data[change_index][column_index] != null) {
					changed_data[row_index][column_index] = new_data[change_index][column_index];
				}
			}
			row_index++;
		}
		return changed_data;
	}
	
	
	// Returns an Arraylist of the changed row indices after comparing two data sets
	public static ArrayList<Integer> getChangedRows(Object[][] old_data, Object[][] new_data) {
		int rows = old_data.length;
		ArrayList<Integer> change_indices = new ArrayList<Integer>();
		for(int row_index = 0; row_index < rows; row_index++) {
			if(!(old_data[row_index].equals(new_data[row_index]))) {
				change_indices.add(row_index);
			}
			/* For testing
			if((!old_objects[index][1].equals(new_objects[index][1])) 
					| ((old_objects[index][2] == null) & (new_objects[index][2] != null))
					| ((old_objects[index][2] != null) & (new_objects[index][2] == null))) {
				change_indices.add(index);
			}
			*/
		}
		return change_indices;
	}
	
}
