import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class SupportedThemesPage {

	/**
	 * 
	 */
	private User current_user;
	private JFrame supported_themes_page;
	private JPanel supported_themes_panel;
	private JLabel chosen_destination_label;
	private JLabel themes_label;
	private JTextField chosen_destination_field;
	private JButton save_button;
	private JButton cancel_button;
	private JScrollPane themes_scrollpane;
	private JTable themes_table;
	
	private DefaultTableModel supportedThemesModel;

	private String destination_name;
	private String country_name;
	private Object[][] data = {{}};
	private int id;
	private int called_by;
	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		save_button = new JButton("Save");
		chosen_destination_label = new JLabel("Chosen destination:");
		themes_label = new JLabel("Select which themes this destination supports: ");
		chosen_destination_field = new JTextField(destination_name);
		chosen_destination_field.setEditable(false);
		
		String[] table_titles = {"Theme", "Supported?", "Rating(optional, 0 - 10)"};
		try {
			id = DatabaseManagement.getDestinationId(destination_name, country_name);
			data = DatabaseManagement.getThemesForSupportedPage(id);
		} catch (Exception ex) {
			System.out.println("Error in getThemes supported themes page: " + ex);
		}
		// TODO: Fix the bug which "hides" the first theme (from the top) if the theme is supported 
		// 			but not rated (theme shows up as unsupported even though it is in the database)
		//			likely a visual bug, since saving without changing anything actually removes 
		// 			the theme from the supported list
		supportedThemesModel = new DefaultTableModel(data, table_titles) {
			private static final long serialVersionUID = -1542844678128213822L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return (column == 1) | (column == 2) ;
		    }
			@Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return Integer.class;
                }
            }
		};
		themes_table = new JTable(supportedThemesModel);
		Formatting.resizeColumnWidth(themes_table);
		themes_table.setPreferredScrollableViewportSize(new Dimension(400,300));
		themes_table.setFillsViewportHeight(true);
		themes_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		themes_scrollpane = new JScrollPane(themes_table);
		themes_scrollpane.setPreferredSize(new Dimension(400,300));
	}
	
	private void initListeners() {

		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save_button.setEnabled(false);
				Object[][] updated_data = DataFunctions.getData(themes_table);
				Object[][] changed_data = DataFunctions.getModelChanges(data, updated_data);
				
				try {
					if(DatabaseManagement.update_supported_themes(id, changed_data)) {
						JOptionPane.showMessageDialog(null, "Sucessfully updated supported themes.");
						save_button.setEnabled(true);
						if(called_by == 0) {
							new AddDestinationPage(current_user);
							supported_themes_page.dispose();
						}
						else {
							new UpdateDestinationPage(current_user);
							supported_themes_page.dispose();
						}
					}
					else {
						JOptionPane.showMessageDialog(null, "No changes were made. "
								+ "Recheck your entries.");
						save_button.setEnabled(true);
						return;
					}
				}
				catch(Exception ex) {
					System.out.println("Error in supported themes save button: " + ex);
				}	
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(called_by == 0) {
					new AddDestinationPage(current_user);
					supported_themes_page.dispose();
				}
				else {
					new UpdateDestinationPage(current_user);
					supported_themes_page.dispose();
				}
			}
		});
	}
	
	
	// called_by_ is 0 when called by AddDestinationPage, 1 when called by UpdateDestinationPage
	public SupportedThemesPage(User user, String destination_name_, 
			String country_name_, int called_by_) {
		called_by = called_by_;
		current_user = user;
		destination_name = destination_name_;
		country_name = country_name_;
		
		supported_themes_page = new JFrame("Supported Themes");
		supported_themes_page.setLayout(new GridBagLayout());
		supported_themes_panel = new JPanel(new MigLayout("wrap 2", "[300!]", "[20!][20!][20!][300!][30!]"));
		
		initComponents();
		initListeners();
		
		supported_themes_panel.add(chosen_destination_label, "left, wrap, grow");
		supported_themes_panel.add(chosen_destination_field, "left, wrap, grow");
		supported_themes_panel.add(themes_label, "left, wrap, grow");
		supported_themes_panel.add(themes_scrollpane, "span 2, center, grow");
		supported_themes_panel.add(save_button, "grow");
		supported_themes_panel.add(cancel_button, "grow");
		
		supported_themes_page.add(supported_themes_panel);
		supported_themes_page.setSize(700, 550);
		supported_themes_page.setLocationRelativeTo(null); //Centers the window
		supported_themes_page.setResizable(false);
		supported_themes_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		supported_themes_page.setVisible(true);
	}
	

}
