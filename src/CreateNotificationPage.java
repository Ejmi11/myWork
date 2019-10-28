import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class CreateNotificationPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6325126719236035209L;

	private User current_user;
	private JFrame create_notification_page;
	private JPanel create_notification_panel;
	private JLabel create_notification_label;
	private JButton create_button;
	private JButton cancel_button;
	private JEditorPane notification_pane;
	
	private void initComponents() {
		create_button = new JButton("Create Notification");
		cancel_button = new JButton("Cancel");
		create_notification_label = new JLabel("Write a notification to be pushed to all users: ");
		notification_pane = new JEditorPane();
		notification_pane.setEditable(true);
	}
	
	private void initListeners() {
		
		create_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					String notification_content = notification_pane.getText();
					if(notification_content.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter your notification text "
								+ "in the textfield");
						return;
					}
					DatabaseManagement.new_notification(notification_content);
					JOptionPane.showMessageDialog(null, "Successfully pushed the new notification!");
					current_user.setCheckedNotifications(0);
					create_button.setEnabled(false);
					new AdminPage(current_user);
					create_notification_page.dispose();
				}
				catch(Exception ex) {
					System.out.println("Database error in create notification page "
							+ "create_button: " + ex);
				}
			}
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				create_notification_page.dispose();
			}
		});
		
	}
	
	
	public CreateNotificationPage(User user) {
		current_user = user;
		
		create_notification_page = new JFrame("Create Notification");
		create_notification_page.setLayout(new GridBagLayout());
		create_notification_panel = new JPanel(new MigLayout("wrap 2", "[200!]", 
				"[20!]10[420!][25!]"));
		
		initComponents();
		initListeners();
		
		create_notification_panel.add(create_notification_label, "left, wrap, grow");
		create_notification_panel.add(notification_pane, "grow, span 2");
		create_notification_panel.add(create_button, "grow");
		create_notification_panel.add(cancel_button, "grow");
		
		create_notification_page.add(create_notification_panel);
		Formatting.formatPage(create_notification_page, 500, 600);
		create_notification_page.setVisible(true);
	}
}