import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;



public class AdminManagementPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5296351586349731432L;

	private User current_user;
	private JFrame admin_management_page;
	private JPanel admin_management_panel;
	private JTextField username_field;
	private JLabel username_label;
	private JButton make_admin_button;
	private JButton remove_admin_button;
	private JButton admin_tools_button;
	private JButton main_page_button;

	
	
	private void initComponents() {
		make_admin_button = new JButton("Promote to admin");
		remove_admin_button = new JButton("Demote to customer");
		admin_tools_button = new JButton("Admin Tools");
		main_page_button = new JButton("Main Page");
		username_label = new JLabel("Username: ");
		username_field = new JTextField(15);
		username_field.setEditable(true);
	}
	
	private void initListeners() {
		
		main_page_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				admin_management_page.dispose();
			}
		});

		make_admin_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String username = username_field.getText();
					if(username.equals(current_user.getUsername())) {
						JOptionPane.showMessageDialog(null, 
								"You can not change your own status.");
						return;
					}
					int make_admin_returnvalue = DatabaseManagement.make_admin(username);
					if(make_admin_returnvalue == 0) {
						username_field.setText("");
						JOptionPane.showMessageDialog(null, username 
								+ " is now a admin.");
					}
					else if(make_admin_returnvalue == -1) {
						JOptionPane.showMessageDialog(null, 
								"No user with that username was found.");
					}
					else if(make_admin_returnvalue == -2) {
						JOptionPane.showMessageDialog(null, 
								"User is already a admin.");
					}
				}
				catch(Exception ex) {
					System.out.println("Error in make admin button: "+ e);
				}
			}
		});
		
		remove_admin_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String username = username_field.getText();
					if(username.equals(current_user.getUsername())) {
						JOptionPane.showMessageDialog(null, 
								"You can not change your own status.");
						return;
					}
					int make_admin_returnvalue = DatabaseManagement.make_customer(username);
					if(make_admin_returnvalue == 0) {
						username_field.setText("");
						JOptionPane.showMessageDialog(null, username 
								+ " is now a customer.");
					}
					else if(make_admin_returnvalue == -1) {
						JOptionPane.showMessageDialog(null, 
								"No user with that username was found.");
					}
					else if(make_admin_returnvalue == -2) {
						JOptionPane.showMessageDialog(null, 
								"User is already a customer.");
					}
				}
				catch(Exception ex) {
					System.out.println("Error in make customer button: "+ e);
				}
			}
		});
		
		admin_tools_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				admin_management_page.dispose();
			}
		});
	}
	
	
	public AdminManagementPage(User user) {
		
		current_user = user;
		
		admin_management_page = new JFrame("Admin Management");
		admin_management_page.setLayout(new GridBagLayout());
		admin_management_panel = new JPanel(new MigLayout("wrap 2", "[150!]", "[20!][20!][30!][30!]"));
		
		initComponents();
		initListeners();
		
		admin_management_panel.add(username_label, "span 2, left, grow");
		admin_management_panel.add(username_field, "span 2, left, grow");
		admin_management_panel.add(make_admin_button, "grow");
		admin_management_panel.add(remove_admin_button, "grow");
		admin_management_panel.add(main_page_button, "grow");
		admin_management_panel.add(admin_tools_button, "grow");
		
		admin_management_page.add(admin_management_panel);
		admin_management_page.setSize(350, 250);
		admin_management_page.setLocationRelativeTo(null); //Centers the window
		admin_management_page.setResizable(false);
		admin_management_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		admin_management_page.setVisible(true);
	}
}