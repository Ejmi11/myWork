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

import net.miginfocom.swing.MigLayout;



public class SupportPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725132719236035209L;

	private User current_user;
	private JFrame support_page;
	private JPanel support_panel;
	private JLabel message_label;
	private JEditorPane message_editorpane;
	private JScrollPane message_scrollpane;
	private JButton send_message_button;
	private JButton cancel_button;
	private String written_message = "";

	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		send_message_button = new JButton("Send message");
		message_label = new JLabel("<html>Please write a short message "
				+ "describing your problem or situation: </html>");
		message_editorpane = new JEditorPane();
		message_scrollpane = new JScrollPane(message_editorpane);
		message_scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	private void initListeners() {
		
		send_message_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				written_message = message_editorpane.getText();
				if(written_message.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please write something in your message.");
					return;
				}
				try {
					if (DatabaseManagement.insert_admin_message(current_user.getUsername(), 
							written_message) == 0) {
						JOptionPane.showMessageDialog(null, "Message successfully sent. " 
							+ "An admin will review the message soon and contact you if needed.");
					}	
				}
				catch(Exception ex) {
					System.out.println("Database error in add support page send message button: " 
							+ ex);
				}
				new MainPage(current_user);
				support_page.dispose();
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				support_page.dispose();
			}
		});
		
	}
	
	
	public SupportPage(User user) {
		current_user = user;
		
		support_page = new JFrame("Support Center");
		support_page.setLayout(new GridBagLayout());
		support_panel = new JPanel(new MigLayout("wrap 2", "[170!]", "[40!]20[300!][25!]"));
		
		initComponents();
		initListeners();
		
		support_panel.add(message_label, "left, span 2, grow");
		support_panel.add(message_scrollpane, "span 2, grow");
		support_panel.add(send_message_button, "grow");
		support_panel.add(cancel_button, "grow");
		support_page.add(support_panel);
		Formatting.formatPage(support_page, 450, 550);
		support_page.setVisible(true);
	}
}