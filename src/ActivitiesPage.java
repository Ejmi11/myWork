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



public class ActivitiesPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8900237893543269975L;

	private User current_user;
	private JFrame activities_page;
	private JPanel activities_panel;
	private JButton save_button;
	private JButton cancel_button;
	private JLabel activity_label;
	private JLabel radio_label;
	private JLabel radio_description_label;
	private JTable activity_table;
	private JScrollPane activity_scrollpane;
	private Object[][] activity_data = {{}};
	private DefaultTableModel activityInterestModel;
	
	private void initComponents() {
		
		save_button = new JButton("Save");
		cancel_button = new JButton("Cancel");
		activity_label = new JLabel("Activity: ");
		radio_description_label = new JLabel("How interested are you in this activity?");
		radio_label = new JLabel("1 (not interested) - 10 (very interested)");
		
		String[] activity_titles = {"Activity", "Preference"};
		
		try {
			activity_data = DatabaseManagement.getActivityPreferences(current_user.getUsername());
		}
		catch(Exception ex) {
			System.out.println("Database error in activities page: " + ex);
		}
		
		activityInterestModel = new DefaultTableModel(activity_data, activity_titles) {
			private static final long serialVersionUID = -1542824678128213822L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return column == 1 ;
		    }
			@Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                	case 0:
                		return String.class;
                    case 1:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
		};
		
		activity_table = new JTable(activityInterestModel);
		Formatting.resizeColumnWidth(activity_table);
		activity_table.setFillsViewportHeight(true);
		activity_scrollpane = new JScrollPane(activity_table);
		DefaultTableModel model = (DefaultTableModel) activity_table.getModel();
		model.setRowCount(activity_data.length);
	}
	
	
	private void initListeners() {
		
		this.save_button.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				save_button.setEnabled(false);
				Object[][] updated_data = DataFunctions.getData(activity_table);
				Object[][] changed_data = DataFunctions.getModelChanges(activity_data, 
						updated_data);
				try {
					if(DatabaseManagement.update_activity_preferences(current_user.getUsername(), 
							changed_data) == 0) {
						JOptionPane.showMessageDialog(null, 
								"Sucessfully updated activity preferences.");
						save_button.setEnabled(true);
						new InterestPage(current_user);
						activities_page.dispose();
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
				activities_page.dispose();
			}
		});
	}
	
	
	public ActivitiesPage(User user) {
		
		current_user = user;
		
		activities_page = new JFrame("Update activitiy preferences");
		activities_page.setLayout(new GridBagLayout());
		
		activities_panel = new JPanel(new MigLayout("wrap 2", "[250!]", "[25!][25!][300!][25!]"));

		initComponents();
		initListeners();
		
		activities_panel.add(activity_label, "grow");
		activities_panel.add(radio_label, "grow");
		activities_panel.add(radio_description_label, "skip 1, grow");
		activities_panel.add(activity_scrollpane, "span 2, grow");
		activities_panel.add(save_button, "grow");
		activities_panel.add(cancel_button, "grow");
		
		activities_page.add(activities_panel);
		activities_page.setSize(600, 500);
		activities_page.setLocationRelativeTo(null); //Centers the window
		activities_page.setResizable(false);
		activities_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		activities_page.setVisible(true);
		
		
	}

}