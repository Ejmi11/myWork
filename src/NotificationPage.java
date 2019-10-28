import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;


public class NotificationPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6325199719236035209L;

	private User current_user;
	private JFrame notification_page;
	private JPanel notification_panel;
	private JTextPane notification_pane;
	private JLabel notification_label;
	private JButton read_notification_button;
	private JButton finish_button;
	private String notification_content = "";
	
	private void initComponents() {

		finish_button = new JButton("Finish");
		read_notification_button = new JButton("Mark as read");
		if(current_user.getCheckedNotifications() == 1) {
			read_notification_button.setEnabled(false);
		}
		notification_label = new JLabel("Weekly notification: ");
		try {
			notification_content = DatabaseManagement.getLatestNotification();
		} catch (Exception e) {
			System.out.println("Database error in Notification Page initComponents: " + e);
		}
		notification_pane = new JTextPane();
		notification_pane.setText(notification_content);
		notification_pane.setEditable(false);
		notification_pane.setMaximumSize(new Dimension(400, 420));
	}
	
	private void initListeners() {
		
		read_notification_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					current_user.setCheckedNotifications(1);
					DatabaseManagement.update_user_notification(current_user.getUsername(), 1);
					read_notification_button.setEnabled(false);
				}
				catch(Exception ex) {
					System.out.println("Database error in notification page "
							+ "read_notification_button: " + ex);
				}
			}
		});
		
		finish_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				notification_page.dispose();
			}
		});
		
	}
	
	
	public NotificationPage(User user) {
		current_user = user;
		
		notification_page = new JFrame("Notifications");
		notification_page.setLayout(new GridBagLayout());
		notification_panel = new JPanel(new MigLayout("wrap 2", "[200!]", "[20!]10[420!][25!]"));
		
		initComponents();
		initListeners();
		
		notification_panel.add(notification_label, "left, wrap, grow");
		notification_panel.add(notification_pane, "left, span 2, grow");
		notification_panel.add(read_notification_button, "grow");
		notification_panel.add(finish_button, "grow");
		
		notification_page.add(notification_panel);
		Formatting.formatPage(notification_page, 500, 600);
		notification_page.setVisible(true);
	}
}