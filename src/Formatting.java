import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class Formatting {

	// Sets a tables width to fit the content
	public static void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        int width = 45; // Minimum width
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
	// Sets a jtable to a given size, disallows editing and sets the mode to single selection
	public static void setTableSingleSelectionNoEdits(JTable table, int width, int height) {
		table.setPreferredScrollableViewportSize(new Dimension(width, height));
		table.setFillsViewportHeight(true);
		table.setDefaultEditor(Object.class, null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	// Formats a jframe to a given size and sets its attributes
	public static void formatPage(JFrame frame, int width, int height) {
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null); //Centers the window
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
