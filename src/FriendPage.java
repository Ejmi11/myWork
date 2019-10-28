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
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class FriendPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725199719236035209L;

	private User current_user;
	private JFrame friend_page;
	private JPanel friend_panel;
	private JTextField friend_field;
	private JLabel friend_label;
	private JButton add_friend_button;
	private JButton finish_button;
	private JButton remove_friend_button;
	private JTable current_friends_table;
	private JScrollPane current_friends_scrollpane;
	private String selected_friend = "";
	private Object[][] current_friends_list = {{}};

	
	private void initComponents() {

		finish_button = new JButton("Finish");
		add_friend_button = new JButton("Add friend");
		remove_friend_button = new JButton("Remove friend");
		friend_field = new JTextField(20);
		friend_label = new JLabel("Name of the friend you want to add: ");
		
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
		
		add_friend_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected_friend = friend_field.getText();
				if(selected_friend.equals(current_user.getUsername())) {
					JOptionPane.showMessageDialog(null, "You cannot add yourself as a friend!");
					return;
				}
				if(selected_friend.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter the name of the "
							+ "friend you want to add.");
					return;
				}
				try {
					if(DatabaseManagement.insert_friend(current_user.getUsername(), 
							selected_friend) != 0) {
						JOptionPane.showMessageDialog(null, "Username not found. "
								+ "Please recheck your entry.");
						return;
					}
					else {
						JOptionPane.showMessageDialog(null, "Successfully added " + selected_friend 
								+ " as a friend!");
						new FriendPage(current_user);
						friend_page.dispose();
					}
				}
				catch(Exception ex) {
					System.out.println("Database error in add friend button: " + ex);
				}
			}
		});

		remove_friend_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected_friend = friend_field.getText();
				if(selected_friend.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter the name of the "
							+ "friend you want to remove.");
					return;
				}
				try {
					if(DatabaseManagement.delete_friend(current_user.getUsername(), 
							selected_friend) != 0) {
						JOptionPane.showMessageDialog(null, "Username not found. "
								+ "Please recheck your entry.");
						return;
					}
					else {
						JOptionPane.showMessageDialog(null, "Successfully removed " + selected_friend 
								+ " from your friends list!");
						new FriendPage(current_user);
						friend_page.dispose();
					}
				}
				catch(Exception ex) {
					System.out.println("Database error in remove friend button: " + ex);
				}
			}
		});
		
		finish_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ProfilePage(current_user);
				friend_page.dispose();
			}
		});
		
	}
	
	
	public FriendPage(User user) {
		current_user = user;
		
		friend_page = new JFrame("Friends");
		friend_page.setLayout(new GridBagLayout());
		friend_panel = new JPanel(new MigLayout("wrap 2", "[150!]", "[20!][20!][350!][25!][25!]"));
		
		initComponents();
		initListeners();
		
		friend_panel.add(friend_label, "left, wrap, grow");
		friend_panel.add(friend_field, "left, span 2, grow");
		friend_panel.add(current_friends_scrollpane, "span 2, grow");
		friend_panel.add(add_friend_button, "grow");
		friend_panel.add(remove_friend_button, "grow");
		friend_panel.add(finish_button, "span 2, grow");
		
		friend_page.add(friend_panel);
		Formatting.formatPage(friend_page, 400, 600);
		friend_page.setVisible(true);
	}
}