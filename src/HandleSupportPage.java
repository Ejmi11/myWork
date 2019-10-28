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



public class HandleSupportPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6711153519236035209L;

	private User current_user;
	private JFrame handle_support_page;
	private JPanel handle_support_panel;
	private JLabel support_messages_label;
	private JButton save_button;
	private JButton cancel_button;
	private JTable support_messages_table;
	private JScrollPane support_messages_scrollpane;
	private Object[][] support_messages_list = {{}};
	private DefaultTableModel supportMessagesModel;
	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		save_button = new JButton("Save changes");
		support_messages_label = new JLabel("These are the support messages from the users: ");
		
		String[] messages_titles = {"ID", "Messages", "Resolved"};
		
		try {
			support_messages_list = DatabaseManagement.getSupportMessages();
		} catch (Exception ex) {
			System.out.println("Database error in getSupportMessagess HandleSupportPage: " + ex);
		}
		
		supportMessagesModel = new DefaultTableModel(support_messages_list, 
				messages_titles) {
			private static final long serialVersionUID = -1542844678128213822L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return column == 2 ;
		    }
			@Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                	case 0:
                		return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
		};
		
		support_messages_table = new JTable(supportMessagesModel);
		Formatting.resizeColumnWidth(support_messages_table);
		support_messages_table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
		support_messages_table.setFillsViewportHeight(true);
		support_messages_scrollpane = new JScrollPane(support_messages_table);
		DefaultTableModel model = (DefaultTableModel) support_messages_table.getModel();
		model.setRowCount(support_messages_list.length);
	}
	
	private void initListeners() {
		
		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save_button.setEnabled(false);
				Object[][] updated_data = DataFunctions.getData(support_messages_table);
				Object[][] changed_data = DataFunctions.getModelChanges(support_messages_list, 
						updated_data);
				try {
					if(DatabaseManagement.update_support_messages(changed_data) == 0) {
						JOptionPane.showMessageDialog(null, "Sucessfully updated support messages.");
						save_button.setEnabled(true);
						new AdminPage(current_user);
						handle_support_page.dispose();
					}
					else {
						JOptionPane.showMessageDialog(null, "No changes were made. "
								+ "Recheck your entries.");
						save_button.setEnabled(true);
						return;
					}
				}
				catch(Exception ex) {
					System.out.println("Error in supported themes save button: " + ex);
				}	
			}
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				handle_support_page.dispose();
			}
		});
		
	}
	
	
	public HandleSupportPage(User user) {
		current_user = user;
		
		handle_support_page = new JFrame("Support Handling");
		handle_support_page.setLayout(new GridBagLayout());
		handle_support_panel = new JPanel(new MigLayout("wrap 2", "[300!]", "[20!]20[500!][25!]"));
		
		initComponents();
		initListeners();
		
		handle_support_panel.add(support_messages_label, "left, wrap, grow");
		handle_support_panel.add(support_messages_scrollpane, "span 2, grow");
		handle_support_panel.add(save_button, "grow");
		handle_support_panel.add(cancel_button, "grow");
		
		handle_support_page.add(handle_support_panel);
		Formatting.formatPage(handle_support_page, 700, 700);
		handle_support_page.setVisible(true);
	}
}