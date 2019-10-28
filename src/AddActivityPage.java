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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class AddActivityPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725199719666035209L;

	private User current_user;
	private JFrame add_activity_page;
	private JPanel add_activity_panel;
	private JTextField activity_field;
	private JLabel activity_label;
	private JButton save_button;
	private JButton cancel_button;

	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		save_button = new JButton("Add Activity");
		activity_field = new JTextField(20);
		activity_label = new JLabel("Name of the new activity: ");
	}
	
	private void initListeners() {
		
		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String activity = activity_field.getText();
					if(activity.trim().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter the activity you want to add.");
						return;
					}
					if(DatabaseManagement.insert_activity(activity) == 0) {
						JOptionPane.showMessageDialog(null, "Activity added to database!");
						new AddActivityPage(current_user);
						add_activity_page.dispose();
					}
					else {
						JOptionPane.showMessageDialog(null, "Activity already exists.");
						return;
					}
				}
				catch(Exception ex) {
					System.out.println("Database error in save button add activity page: " + ex);
				}
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				add_activity_page.dispose();
			}
		});
		
	}
	
	
	public AddActivityPage(User user) {
		current_user = user;
		
		add_activity_page = new JFrame("Add Activity");
		add_activity_page.setLayout(new GridBagLayout());
		add_activity_panel = new JPanel(new MigLayout("wrap 2", "[100!]", "[20!][20!][30!]"));
		
		initComponents();
		initListeners();
		
		add_activity_panel.add(activity_label, "left, wrap, grow");
		add_activity_panel.add(activity_field, "left, span 2, grow");
		add_activity_panel.add(save_button, "grow");
		add_activity_panel.add(cancel_button, "grow");
		
		add_activity_page.add(add_activity_panel);
		add_activity_page.setSize(300, 200);
		add_activity_page.setLocationRelativeTo(null); //Centers the window
		add_activity_page.setResizable(false);
		add_activity_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add_activity_page.setVisible(true);
	}
}