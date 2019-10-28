import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
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



public class UpdateDestinationPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5999561611037833565L;
	
	private User current_user;
	private JFrame update_destination_page;
	private JPanel update_destination_panel;
	private JLabel update_destination_label;
	private JButton finish_button;
	private JButton country_button;
	private JButton themes_button;
	private JScrollPane destinations_scrollpane;
	private JTable destinations_table;

	private Object[][] destination_list = {{}};
	private String selected_destination = "";
	private String selected_country;
	
	private void initComponents() {
		

		country_button = new JButton("Change country");
		themes_button = new JButton("Change supported themes");
		finish_button = new JButton("Finish");
		update_destination_label = new JLabel("Choose which destination you want to update: ");
		
		String[] table_titles = {"Destination", "Country"};
		
		
		try {
			destination_list = DatabaseManagement.getDestinations();
		} catch (Exception ex) {
			System.out.println("Error in get destinations update destination page: " + ex);
		}
		
		destinations_table = new JTable(new DefaultTableModel(destination_list, table_titles));
		destinations_table.setPreferredScrollableViewportSize(new Dimension(450,450));
		Formatting.resizeColumnWidth(destinations_table);
		destinations_table.setFillsViewportHeight(true);
		destinations_table.setDefaultEditor(Object.class, null);
		destinations_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		destinations_scrollpane = new JScrollPane(destinations_table);
		destinations_scrollpane.setPreferredSize(new Dimension(450,450));
	}
	
	private void initListeners() {
		
		destinations_table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				try {
					int row = destinations_table.getSelectedRow();
					selected_destination = (String) destination_list[row][0];
					selected_country = (String) destination_list[row][1];
					return;
				}
				catch(Exception ex) {
					// Catches Exception if user clicks in the empty space of the table
				}
			}
		});

		finish_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				update_destination_page.dispose();
			}
		});

		
		country_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditDestinationPage(current_user, selected_destination, selected_country);
				update_destination_page.dispose();
			}
		});
		
		themes_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selected_destination.equals("")) {
					JOptionPane.showMessageDialog(null, "Please select a destination.");
					return;
				}
				new SupportedThemesPage(current_user, selected_destination, selected_country, 1);
				update_destination_page.dispose();
			}
		});
		
	}
	
	
	public UpdateDestinationPage(User user) {
		current_user = user;
		
		update_destination_page = new JFrame("Update Destination");
		update_destination_page.setLayout(new GridBagLayout());
		update_destination_panel = new JPanel(new MigLayout("wrap 3", "[200!, center]", "[20!][450!][25!]"));
		
		initComponents();
		initListeners();
		
		update_destination_panel.add(update_destination_label, "left, wrap, grow");
		update_destination_panel.add(destinations_scrollpane, "span 3, left, grow");
		update_destination_panel.add(country_button, "grow");
		update_destination_panel.add(themes_button, "grow");
		update_destination_panel.add(finish_button, "grow");
		
		
		update_destination_page.add(update_destination_panel);
		update_destination_page.setSize(650, 650);
		update_destination_page.setLocationRelativeTo(null); //Centers the window
		update_destination_page.setResizable(false);
		update_destination_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		update_destination_page.setVisible(true);
	}
}