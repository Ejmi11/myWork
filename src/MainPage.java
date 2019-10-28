import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class MainPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7055134515015855716L;
	
	private JFrame main_page;
	private JLabel welcome_label;
	private JButton search_button;
	private JButton profile_button;
	private JButton logout_button;
	private JButton admin_button;
	private JButton statistics_button;
	private JButton messages_button;
	private JButton contact_admin_button;
	private JButton notifications_button;
	private JComboBox<String> filter_dropdown;
	private JTextField search_field;
	private User current_user;
	private JPanel main_panel;
	private JTable hotel_list;
	private JLabel empty_space;
	private int row = -10;
	private int selected_sort = 0;
	
	private ArrayList<Integer> hotel_ids = new ArrayList<Integer>(); 
	//stores hotel-ids from current hotels in the table (after user pressed find-button)
	
	private void initListeners() {
		//meni1_m1_menuitem1
		this.search_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) hotel_list.getModel();
				String[] table_titles = {"Name", "Location", "Rating", "Price", "Interests"};
				Object[][] data = null;
				String sort_by = "";
				selected_sort = filter_dropdown.getSelectedIndex();
				switch(selected_sort) {
					case 0:
						sort_by = "ORDER BY hotels.price ASC";
						break;
					case 1:
						sort_by = "ORDER BY hotels.price DESC";
						break;
					case 2:
						sort_by = "ORDER BY hotels.rating ASC";
						break;
					case 3:
						sort_by = "ORDER BY hotels.rating DESC";
						break;
					default:
						sort_by = "ORDER BY hotels.price ASC";
						break;
				}
				try {
					if (search_field.getText().length() == 0)
						data = DatabaseManagement.getHotelsAndOrder(sort_by);
					else {
						data = DatabaseManagement.getHotelsBySearchAndOrder(search_field.getText(), 
								sort_by);
					}
					model.setDataVector(data, table_titles);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				model.setRowCount(data.length);
				hotel_ids.clear();
				hotel_list.getColumnModel().getColumn(4).setCellRenderer(new WordWrapCellRenderer());
				Formatting.resizeColumnWidth(hotel_list);
			}
		});
		
		this.profile_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ProfilePage(current_user);
				main_page.dispose();
			}
		});
		
		
		this.admin_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				main_page.dispose();
			}
		});
		
		
		this.logout_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"Sucessfully logged out.");
				new TREC();
				main_page.dispose();
				
			}
		});
		
		hotel_list.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) { 
				if (e.getClickCount() == 2) {
					row = hotel_list.rowAtPoint(e.getPoint());
					int col = hotel_list.columnAtPoint(e.getPoint());
					int row_before = row;
					int id = DatabaseManagement.getHotelID((String)hotel_list.getModel().getValueAt(hotel_list.getSelectedRow(),0),(String)hotel_list.getModel().getValueAt(hotel_list.getSelectedRow(),1),String.valueOf(hotel_list.getModel().getValueAt(hotel_list.getSelectedRow(),3)));
					if(row_before == row && row >= 0 && col >= 0)
					{

						//new HotelInterface(main_page,hotel_ids.get(row));
						new HotelPage(current_user,id);
						main_page.dispose();
					}
				}
			}
		});
		
		statistics_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new StatisticsPage(current_user);
				main_page.dispose();	
			}
		});
		
		contact_admin_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SupportPage(current_user);
				main_page.dispose();
			}
		});
		
		messages_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MessageOverviewPage(current_user);
				main_page.dispose();
			}
		});
		
		notifications_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new NotificationPage(current_user);
				main_page.dispose();
			}
		});
		
	}
	
	private void initComponents(User current_user) {
		
		search_field = new JTextField(15);
		search_field.setEditable(true);
		admin_button = new JButton("Admin Tools");
		search_button = new JButton("Search");
		profile_button = new JButton("Profile");
		logout_button = new JButton("Logout");
		statistics_button = new JButton("Statistics");
		messages_button = new JButton("Messages");
		contact_admin_button = new JButton("Contact admin");
		notifications_button = new JButton("Notifications");
		if(current_user.getCheckedNotifications() == 0) {
			notifications_button.setForeground(Color.RED);
		}
		welcome_label = new JLabel("Welcome to T-REC " + current_user.getFirstName() + "!");
		empty_space = new JLabel("");
		
		String[] table_titles = {"Name", "Location", "Rating", "Price", "Interests"};
		Object[][] data = new Object[0][];
		try {
			data = DatabaseManagement.getHotelsToDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
		hotel_list = new JTable(new DefaultTableModel(data, table_titles));
		hotel_list.setPreferredScrollableViewportSize(new Dimension(600,500));
		hotel_list.setFillsViewportHeight(true);
		hotel_list.getColumnModel().getColumn(4).setCellRenderer(new WordWrapCellRenderer());
		Formatting.setTableSingleSelectionNoEdits(hotel_list, 600, 500);
		Formatting.resizeColumnWidth(hotel_list);
		JScrollPane scrollPane = new JScrollPane(hotel_list);
		scrollPane.setPreferredSize(new Dimension(600,500));
		
		String[] filter_choices = {"Price (Ascending)", "Price (Descending)", "Rating (Ascending)", 
				"Rating (Descending)"};
		filter_dropdown = new JComboBox<String>(filter_choices);
		
		main_panel.add(notifications_button, "skip 4, grow");
		main_panel.add(welcome_label, "left, span 2,  grow");
		main_panel.add(empty_space, "grow");
		main_panel.add(profile_button, "right, grow");
		main_panel.add(messages_button, "right, grow");
		main_panel.add(search_button, "left, grow");
		main_panel.add(search_field, "left, span 2, grow");
		main_panel.add(filter_dropdown, "span 2, grow");
		
		main_panel.add(scrollPane, "center, span 5, grow");
		main_panel.add(statistics_button, "grow");
		main_panel.add(contact_admin_button, "skip 1, grow");
		main_panel.add(logout_button, "skip 1, grow");
		if(current_user.getStatus() == User.UserType.ADMIN) {
			main_panel.add(admin_button, "left, grow");
		}	
	}
	
	public MainPage(User user_)
	{
		main_page = new JFrame("T-REC Main Page");
		main_page.setLayout(new GridBagLayout());
		
		main_panel = new JPanel(new MigLayout("wrap 5", "[120!]", "[25!][25!][25!][500!][25!][25!]"));
		
		current_user = user_;
		
		initComponents(current_user);
		initListeners();
		
		main_page.add(main_panel);
		Formatting.formatPage(main_page, 700, 780);
		main_page.setVisible(true);	
		System.out.println(current_user.getId());
	}
}
