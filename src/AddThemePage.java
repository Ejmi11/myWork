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



public class AddThemePage extends javax.swing.JFrame{


	/**
	 * 
	 */
	private static final long serialVersionUID = 6269040575119946031L;
	
	private User current_user;
	private JFrame add_theme_page;
	private JPanel add_theme_panel;
	private JTextField theme_field;
	private JEditorPane theme_description_pane;
	private JLabel theme_description_label;
	private JLabel theme_label;
	private JButton save_button;
	private JButton cancel_button;

	
	private void initComponents() {

		cancel_button = new JButton("Cancel");
		save_button = new JButton("Add Interest Theme");
		theme_field = new JTextField(20);
		theme_label = new JLabel("Name of the new interest theme: ");
		theme_description_label = new JLabel("Short description of the new theme: ");
		theme_description_pane = new JEditorPane();
		
	}
	
	private void initListeners() {
		
		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String theme = theme_field.getText();
					String description = theme_description_pane.getText();
					if(theme.trim().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter the theme you want to add.");
						return;
					}
					else if(description.trim().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please enter a short description of the theme.");
						return;
					}
					if(DatabaseManagement.insert_theme(theme, description) == 0) {
						JOptionPane.showMessageDialog(null, "Theme added to database!");
						new AddThemePage(current_user);
						add_theme_page.dispose();
					}
					else {
						JOptionPane.showMessageDialog(null, "Theme already exists.");
						return;
					}
				}
				catch(Exception ex) {
					System.out.println("Database error in save button add theme page: " + ex);
				}
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				add_theme_page.dispose();
			}
		});
		
	}
	
	
	public AddThemePage(User user) {
		current_user = user;
		
		add_theme_page = new JFrame("Add Interest Theme");
		add_theme_page.setLayout(new GridBagLayout());
		add_theme_panel = new JPanel(new MigLayout("wrap 2", "[150!]", "[20!][20!][20!][150!][30!]"));
		
		initComponents();
		initListeners();
		
		add_theme_panel.add(theme_label, "left, wrap, grow");
		add_theme_panel.add(theme_field, "left, span 2, grow");
		add_theme_panel.add(theme_description_label, "left, wrap, grow");
		add_theme_panel.add(theme_description_pane, "left, span 2, grow");
		add_theme_panel.add(save_button, "grow");
		add_theme_panel.add(cancel_button, "grow");
		
		add_theme_page.add(add_theme_panel);
		add_theme_page.setSize(400, 400);
		add_theme_page.setLocationRelativeTo(null); //Centers the window
		add_theme_page.setResizable(false);
		add_theme_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add_theme_page.setVisible(true);
	}
}