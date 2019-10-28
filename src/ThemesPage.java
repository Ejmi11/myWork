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
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class ThemesPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6098057972582024965L;

	private User current_user;
	private JFrame themes_page;
	private JPanel themes_panel;
	private JButton save_button;
	private JButton cancel_button;
	private JLabel theme_label;
	private JLabel radio_label;
	private JLabel radio_description_label;
	private JTable themes_table;
	private JScrollPane themes_scrollpane;
	private Object[][] themes_data = {{}};
	private DefaultTableModel themesInterestModel;
	
	
	private void initComponents() {
		
		save_button = new JButton("Save");
		cancel_button = new JButton("Cancel");
		theme_label = new JLabel("Theme: ");
		radio_description_label = new JLabel("How interested are you in this theme?");
		radio_label = new JLabel("1 (not interested) - 10 (very interested)");

		String[] theme_titles = {"Theme", "Description", "Preference"};
		
		try {
			themes_data = DatabaseManagement.getThemePreferences(current_user.getUsername());
		}
		catch(Exception ex) {
			System.out.println("Database error in themes page: " + ex);
		}
		
		themesInterestModel = new DefaultTableModel(themes_data, theme_titles) {
			private static final long serialVersionUID = -1542324678128213822L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return column == 2 ;
		    }
			@Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                	case 0:
                		return String.class;
                    case 1:
                        return String.class;
                    case 2:
                    	return Integer.class;
                    default:
                        return String.class;
                }
            }
		};
		
		themes_table = new JTable(themesInterestModel);
		Formatting.resizeColumnWidth(themes_table);
		themes_table.setFillsViewportHeight(true);
		themes_scrollpane = new JScrollPane(themes_table);
		DefaultTableModel model = (DefaultTableModel) themes_table.getModel();
		model.setRowCount(themes_data.length);
	}
	
	
	private void initListeners() {
		
		this.save_button.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				save_button.setEnabled(false);
				Object[][] updated_data = DataFunctions.getData(themes_table);
				Object[][] changed_data = DataFunctions.getModelChanges(themes_data, 
						updated_data);
				try {
					if(DatabaseManagement.update_theme_preferences(current_user.getUsername(), 
							changed_data) == 0) {
						JOptionPane.showMessageDialog(null, 
								"Sucessfully updated theme preferences.");
						save_button.setEnabled(true);
						new InterestPage(current_user);
						themes_page.dispose();
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
		
		this.cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new InterestPage(current_user);
				themes_page.dispose();
			}
		});
	}
	
	
	public ThemesPage(User user) {
		
		current_user = user;
		
		themes_page = new JFrame("Update theme preferences");
		themes_page.setLayout(new GridBagLayout());
		
		themes_panel = new JPanel(new MigLayout("wrap 2", "[250!]", "[25!][25!][300!][25!]"));

		initComponents();
		initListeners();
		
		themes_panel.add(theme_label, "grow");
		themes_panel.add(radio_description_label, "grow");
		themes_panel.add(radio_label, "skip 1, grow");
		themes_panel.add(themes_scrollpane, "span 2, grow");
		themes_panel.add(save_button, "grow");
		themes_panel.add(cancel_button, "grow");
		
		themes_page.add(themes_panel);
		themes_page.setSize(600, 500);
		themes_page.setLocationRelativeTo(null); //Centers the window
		themes_page.setResizable(false);
		themes_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		themes_page.setVisible(true);
		
		
	}

}