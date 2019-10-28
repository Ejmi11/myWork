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
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class MessageOverviewPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725159719236035209L;

	private User current_user;
	private JFrame message_overview_page;
	private JPanel message_overview_panel;
	private JLabel message_label;
	private JButton message_button;
	private JButton finish_button;
	private JTable current_friends_table;
	private JScrollPane current_friends_scrollpane;
	private String selected_friend = "";
	private Object[][] current_friends_list = {{}};

	
	private void initComponents() {

		finish_button = new JButton("Finish");
		message_button = new JButton("See conversation");
		message_label = new JLabel("Select the friend you want to message: ");
		
		String[] current_friends_titles = {"Username", "First Name"};
		
		try {
			current_friends_list = DatabaseManagement.getFriendsAndNames(current_user.getUsername());
		} catch (Exception ex) {
			System.out.println("Database error in getFriends FriendPage: " + ex);
		}
		current_friends_table = new JTable(new DefaultTableModel(current_friends_list, 
				current_friends_titles));
		Formatting.resizeColumnWidth(current_friends_table);
		Formatting.setTableSingleSelectionNoEdits(current_friends_table, 450, 350);
		current_friends_scrollpane = new JScrollPane(current_friends_table);
		current_friends_scrollpane.setPreferredSize(new Dimension(450,350));
		DefaultTableModel model = (DefaultTableModel) current_friends_table.getModel();
		model.setRowCount(current_friends_list.length);
	}
	
	private void initListeners() {
		
		message_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected_friend = ((String) 
						current_friends_table.getModel().getValueAt(
						current_friends_table.getSelectedRow(), 0));
				if(selected_friend == null) {
					JOptionPane.showMessageDialog(null, "Please select the friend you want "
							+ "to message.");
					return;
				}
				else if(selected_friend.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please select the friend you want "
							+ "to message.");
					return;
				}
				new MessagingPage(current_user, selected_friend);
				message_overview_page.dispose();
			}
		});
		
		finish_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				message_overview_page.dispose();
			}
		});
		
	}
	
	
	public MessageOverviewPage(User user) {
		current_user = user;
		
		message_overview_page = new JFrame("Messages");
		message_overview_page.setLayout(new GridBagLayout());
		message_overview_panel = new JPanel(new MigLayout("wrap 2", "[150!]", "[20!]20[350!][25!]"));
		
		initComponents();
		initListeners();
		
		message_overview_panel.add(message_label, "left, wrap, grow");
		message_overview_panel.add(current_friends_scrollpane, "span 2, grow");
		message_overview_panel.add(message_button, "grow");
		message_overview_panel.add(finish_button, "grow");
		
		message_overview_page.add(message_overview_panel);
		Formatting.formatPage(message_overview_page, 400, 550);
		message_overview_page.setVisible(true);
	}
}