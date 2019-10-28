import java.awt.Component;
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
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;



public class AddDestinationPage extends javax.swing.JFrame{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5494347412874804488L;
	
	private User current_user;
	private JFrame add_destination_page;
	private JPanel add_destination_panel;
	private JTextField destination_field;
	private JLabel destination_label;
	private JLabel countries_label;
	private JButton save_button;
	private JButton cancel_button;
	private JTable countries_table;
	private JScrollPane countries_scrollpane;
	private String selected_country = "";
	private Object[][] countries_list = {{}};
	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		save_button = new JButton("Add Destination");
		destination_field = new JTextField(20);
		destination_label = new JLabel("Name of the new destination: ");
		countries_label = new JLabel("Select the country in which the destination is located: ");
		
		String[] table_titles = {"Country", "Capital", "Inhabitants"};
		
		try {
			countries_list = DatabaseManagement.getCountries();
		} catch (Exception ex) {
			System.out.println("Database error in getCountries addDestinationPage: " + ex);
		}
		countries_table = new JTable(new DefaultTableModel(countries_list, table_titles));
		Formatting.resizeColumnWidth(countries_table);
		countries_table.setPreferredScrollableViewportSize(new Dimension(450,350));
		countries_table.setFillsViewportHeight(true);
		countries_table.setDefaultEditor(Object.class, null);
		countries_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		countries_scrollpane = new JScrollPane(countries_table);
		countries_scrollpane.setPreferredSize(new Dimension(450,350));


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
					String name = destination_field.getText();
					if(name.trim().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter a name for the destination.");
						return;
					}
					if(selected_country.equals("")) {
						JOptionPane.showMessageDialog(null, "Please select the "
								+ "country in which the destination is located.");
						return;
					}
					if(DatabaseManagement.insert_destination(name, selected_country) == 0) {
						JOptionPane.showMessageDialog(null, "Destination added to database!");
						new SupportedThemesPage(current_user, name, selected_country, 0);
						add_destination_page.dispose();
					}
					else {
						JOptionPane.showMessageDialog(null, "Destination already exists.");
						return;
					}
				}
				catch(Exception ex) {
					System.out.println("Database error in save button add destination page: " + e);
				}
			}
		});

		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				add_destination_page.dispose();
			}
		});
	}
	
	
	public AddDestinationPage(User user) {
		current_user = user;
		
		add_destination_page = new JFrame("Add Destination");
		add_destination_page.setLayout(new GridBagLayout());
		add_destination_panel = new JPanel(new MigLayout("wrap 2", "[300!]", 
				"[20!][20!][20!][300!][30!]"));
		
		initComponents();
		initListeners();
		
		add_destination_panel.add(destination_label, "left, wrap, grow");
		add_destination_panel.add(destination_field, "left, span 2, grow");
		add_destination_panel.add(countries_label, "left, wrap, grow");
		add_destination_panel.add(countries_scrollpane, "span 2, grow");
		add_destination_panel.add(save_button, "center, grow");
		add_destination_panel.add(cancel_button, "center, grow");
		
		add_destination_page.add(add_destination_panel);
		add_destination_page.setSize(700, 550);
		add_destination_page.setLocationRelativeTo(null); //Centers the window
		add_destination_page.setResizable(false);
		add_destination_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add_destination_page.setVisible(true);
	}
	
}