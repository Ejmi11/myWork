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
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class PotentialCustomersPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7266565171702757990L;
	
	private User current_user;
	private JFrame potential_customers_page;
	private JPanel potential_customers_panel;
	private JLabel potential_customers_description_label;
	private JButton return_button;
	private JScrollPane potential_customers_scrollpane;
	private JTable potential_customers_list;

	private int _hotelID;

	
	private void initComponents() {
		
		return_button = new JButton("Return to hotel");
		potential_customers_description_label = new JLabel("This is a list of users which chosen interests best "
				+ "match the supported interests of the hotel.");
		String[] table_titles = {"Username", "Name", "Match"};
		Object[][] data = {
				{"testusername", "John test", "84%"},
				{"user2", "Mark A", "72%"}
		};
		potential_customers_list = new JTable(new DefaultTableModel(data, table_titles));
		potential_customers_list.setPreferredScrollableViewportSize(new Dimension(250,250));
		potential_customers_list.setFillsViewportHeight(true);
		potential_customers_list.setDefaultEditor(Object.class, null); //Disables editing of table
		potential_customers_scrollpane = new JScrollPane(potential_customers_list);
		potential_customers_scrollpane.setPreferredSize(new Dimension(300,400));
		DefaultTableModel model = (DefaultTableModel) potential_customers_list.getModel();
		model.setRowCount(2);
	}
	
	private void initListeners() {
		
		return_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HotelPage(current_user,_hotelID);
				potential_customers_page.dispose();
			}
		});
		
	}
	
	
	public PotentialCustomersPage(User user, int HotelID) {
		current_user = user;
		_hotelID = HotelID;
		
		potential_customers_page = new JFrame("Potential Customers");
		potential_customers_page.setLayout(new GridBagLayout());
		potential_customers_panel = new JPanel(new MigLayout("wrap 2", "[300!]", "[20!][400!][25!]"));
		
		initComponents();
		initListeners();
		
		potential_customers_panel.add(potential_customers_description_label, "left, grow, span 2");
		potential_customers_panel.add(potential_customers_scrollpane, "left, grow, span 2");
		potential_customers_panel.add(return_button, "skip 1, grow");
		
		potential_customers_page.add(potential_customers_panel);
		potential_customers_page.setSize(700, 600);
		potential_customers_page.setLocationRelativeTo(null); //Centers the window
		potential_customers_page.setResizable(false);
		potential_customers_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		potential_customers_page.setVisible(true);
	}
}