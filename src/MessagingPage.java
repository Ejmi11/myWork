import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class MessagingPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725153519236035209L;

	private User current_user;
	private String selected_friend;
	private JFrame messaging_page;
	private JPanel messaging_panel;
	private JLabel messages_label;
	private JButton write_message_button;
	private JButton finish_button;
	private JTable exchanged_messages_table;
	private JScrollPane exchanged_messages_scrollpane;
	private Object[][] exchanged_messages_list = {{}};

	
	private void initComponents() {

		finish_button = new JButton("Finish");
		write_message_button = new JButton("Write message");
		messages_label = new JLabel("These are the messages from and to your friend: ");
		
		String[] messages_titles = {"Messages"};
		
		try {
			exchanged_messages_list = DatabaseManagement.getMessages(current_user.getUsername(), 
					selected_friend);
		} catch (Exception ex) {
			System.out.println("Database error in getMessagess MessagingPage: " + ex);
		}
		exchanged_messages_table = new JTable(new DefaultTableModel(exchanged_messages_list, 
				messages_titles));
		Formatting.resizeColumnWidth(exchanged_messages_table);
		Formatting.setTableSingleSelectionNoEdits(exchanged_messages_table, 450, 350);
		exchanged_messages_table.getColumnModel().getColumn(0).setCellRenderer(new WordWrapCellRenderer());
		exchanged_messages_scrollpane = new JScrollPane(exchanged_messages_table);
		exchanged_messages_scrollpane.setPreferredSize(new Dimension(450,350));
		DefaultTableModel model = (DefaultTableModel) exchanged_messages_table.getModel();
		if(exchanged_messages_list.length < 2) {
			model.setRowCount(0);
		}
		else {
			model.setRowCount(exchanged_messages_list.length);
		}
	}
	
	private void initListeners() {
		
		write_message_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new WriteMessagePage(current_user, selected_friend);
				messaging_page.dispose();
			}
		});
		
		finish_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MessageOverviewPage(current_user);
				messaging_page.dispose();
			}
		});
		
	}
	
	
	public MessagingPage(User user, String selected_user_) {
		current_user = user;
		selected_friend = selected_user_;
		
		messaging_page = new JFrame("Messages");
		messaging_page.setLayout(new GridBagLayout());
		messaging_panel = new JPanel(new MigLayout("wrap 2", "[300!]", "[20!]20[500!][25!]"));
		
		initComponents();
		initListeners();
		
		messaging_panel.add(messages_label, "left, wrap, grow");
		messaging_panel.add(exchanged_messages_scrollpane, "span 2, grow");
		messaging_panel.add(write_message_button, "grow");
		messaging_panel.add(finish_button, "grow");
		
		messaging_page.add(messaging_panel);
		Formatting.formatPage(messaging_page, 700, 700);
		messaging_page.setVisible(true);
	}
}