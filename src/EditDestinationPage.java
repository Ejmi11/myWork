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



public class EditDestinationPage extends javax.swing.JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7715715406127898332L;
	
	private User current_user;
	private JFrame edit_destination_page;
	private JPanel edit_destination_panel;
	private JLabel selected_destination_label;
	private JLabel old_country_label;
	private JLabel countries_label;
	private JTextField selected_destination_field;
	private JTextField old_country_field;
	private JButton save_button;
	private JButton cancel_button;
	private JScrollPane countries_scrollpane;
	private JTable countries_table;

	private String selected_destination;
	private String old_country;
	private String selected_country = "";
	private Object[][] countries_list = {{}};
	
	private void initComponents() {
		
		cancel_button = new JButton("Cancel");
		save_button = new JButton("Save");
		selected_destination_label = new JLabel("Selected destination:");
		old_country_label = new JLabel("Old country:");
		countries_label = new JLabel("Select a country in which the destination is located: ");
		selected_destination_field = new JTextField(selected_destination);
		old_country_field = new JTextField(old_country);
		selected_destination_field.setEditable(false);
		old_country_field.setEditable(false);
		
		String[] table_titles = {"Country", "Capital", "Inhabitants"};
		
		try {
			countries_list = DatabaseManagement.getCountries();
		} catch (Exception ex) {
			System.out.println("Database error in getCountries EditDestinationPage: " + ex);
		}
		
		countries_table = new JTable(new DefaultTableModel(countries_list, table_titles));
		Formatting.resizeColumnWidth(countries_table);
		countries_table.setPreferredScrollableViewportSize(new Dimension(450,350));
		countries_table.setFillsViewportHeight(true);
		countries_table.setDefaultEditor(Object.class, null);
		countries_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		countries_scrollpane = new JScrollPane(countries_table);
		countries_scrollpane.setPreferredSize(new Dimension(400,300));
	}
	
	private void initListeners() {
		
		countries_table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = countries_table.getSelectedRow();
				selected_country = (String) countries_list[row][0];
				return;
			}
		});

		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					save_button.setEnabled(false);
					if(selected_country.equals("")) {
						JOptionPane.showMessageDialog(null, "Please select the "
								+ "country in which the destination is located.");
						save_button.setEnabled(true);
						return;
					}
					else {
						DatabaseManagement.update_destination(selected_destination, 
								old_country, selected_country);
						save_button.setEnabled(true);
						JOptionPane.showMessageDialog(null, "Successfully update the destination.");
						new UpdateDestinationPage(current_user);
						edit_destination_page.dispose();
					}
				}
				catch(Exception ex) {
					System.out.println("Error in Edit destination page save button: " + ex);
				}
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UpdateDestinationPage(current_user);
				edit_destination_page.dispose();
			}
		});
		
	}
	
	
	public EditDestinationPage(User user, String selected_destination_, String selected_country_) {
		selected_destination = selected_destination_;
		old_country = selected_country_;
		current_user = user;
		
		edit_destination_page = new JFrame("Edit Destination");
		edit_destination_page.setLayout(new GridBagLayout());
		edit_destination_panel = new JPanel(new MigLayout("wrap 2", "[300!]", 
				"[20!][20!][20!][20!][20!][300!][30!]"));
		
		initComponents();
		initListeners();
		
		edit_destination_panel.add(selected_destination_label, "left, wrap, grow");
		edit_destination_panel.add(selected_destination_field, "left, wrap, grow");
		edit_destination_panel.add(old_country_label, "left, wrap, grow");
		edit_destination_panel.add(old_country_field, "left, wrap, grow");
		edit_destination_panel.add(countries_label, "left, wrap, grow");
		edit_destination_panel.add(countries_scrollpane, "span 2, left, grow");
		edit_destination_panel.add(save_button, "grow");
		edit_destination_panel.add(cancel_button, "grow");
		
		edit_destination_page.add(edit_destination_panel);
		edit_destination_page.setSize(700, 600);
		edit_destination_page.setLocationRelativeTo(null); //Centers the window
		edit_destination_page.setResizable(false);
		edit_destination_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		edit_destination_page.setVisible(true);
	}
}