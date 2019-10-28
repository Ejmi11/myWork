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

import net.miginfocom.swing.MigLayout;



public class WriteMessagePage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6635159719236035209L;

	private User current_user;
	private String selected_friend;
	private JFrame write_message_page;
	private JPanel write_message_panel;
	private JLabel message_label;
	private JLabel selected_friend_label;
	private JButton send_message_button;
	private JButton cancel_button;
	private JEditorPane message_editorpane;
	private JScrollPane message_scrollpane;
	private String written_message;

	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		send_message_button = new JButton("Send message");
		message_label = new JLabel("Write the message you want to send: ");
		selected_friend_label = new JLabel("Recipient: " + selected_friend);
		message_editorpane = new JEditorPane();
		message_editorpane.setMaximumSize(new Dimension(500, 370));
		message_scrollpane = new JScrollPane(message_editorpane);
		message_scrollpane.setPreferredSize(new Dimension(500, 370));
		message_scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	private void initListeners() {
		
		send_message_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				written_message = message_editorpane.getText();
				if(written_message.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please write something in your message");
					return;
				}
				try {
					DatabaseManagement.insert_message(current_user.getUsername(), 
							selected_friend, written_message);
				}
				catch(Exception ex) {
					System.out.println("Database error in send message button: " + ex);
				}
				new MessagingPage(current_user, selected_friend);
				write_message_page.dispose();
			}
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MessagingPage(current_user, selected_friend);
				write_message_page.dispose();
			}
		});
		
	}
	
	
	public WriteMessagePage(User user, String selected_user_) {
		current_user = user;
		selected_friend = selected_user_;
		
		write_message_page = new JFrame("Write message");
		write_message_page.setLayout(new GridBagLayout());
		write_message_panel = new JPanel(new MigLayout("wrap 2", "[250!]", "[20!]20[20!]10[370!][25!]"));
		
		initComponents();
		initListeners();
		
		write_message_panel.add(selected_friend_label, "left, span 2, grow");
		write_message_panel.add(message_label, "left, span 2, grow");
		write_message_panel.add(message_scrollpane, "span 2, center, grow");
		write_message_panel.add(send_message_button, "grow");
		write_message_panel.add(cancel_button, "grow");
		
		write_message_page.add(write_message_panel);
		Formatting.formatPage(write_message_page, 600, 600);
		write_message_page.setVisible(true);
	}
}