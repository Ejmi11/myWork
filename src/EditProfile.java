import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class EditProfile extends javax.swing.JFrame{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1572169421336964400L;

	private JFrame edit_profile_page;
	private JPanel edit_panel;
	private JButton save_button;
	private JButton cancel_button;
	private JPasswordField password_pfield;
	private JTextField first_name_field;
	private JTextField last_name_field;
	private JPasswordField new_password_pfield;
	private JPasswordField new_password_confirmation_pfield;
	private JLabel first_name_label;
	private JLabel last_name_label;
	private JLabel new_password_label;
	private JLabel new_password_confirmation_label;
	private JLabel password_label;
	private User current_user;
	
	private void initComponents()
	{
		
		save_button = new JButton("Save");
		cancel_button = new JButton("Cancel");
		first_name_field = new JTextField(15);
		first_name_field.setEditable(true);
		first_name_field.setText(current_user.getFirstName());
		last_name_field = new JTextField(15);
		last_name_field.setEditable(true);
		last_name_field.setText(current_user.getLastName());
		password_pfield = new JPasswordField(15);
		password_pfield.setEditable(true);
		new_password_pfield = new JPasswordField(15);
		new_password_pfield.setEditable(true);
		new_password_confirmation_pfield = new JPasswordField(15);
		new_password_confirmation_pfield.setEditable(true);

		first_name_label = new JLabel("First name: ");
		last_name_label = new JLabel("Last name: ");
		password_label = new JLabel("Current password: ");
		new_password_label = new JLabel("New password (optional): ");
		new_password_confirmation_label = new JLabel("Repeat new password: ");
		
		
	}
	
	private void initListeners()
	{
		this.save_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String first_name = first_name_field.getText();
					String last_name = last_name_field.getText();
					String password = String.valueOf(password_pfield.getPassword());
					String new_password = String.valueOf(new_password_pfield.getPassword());
					String new_password_repeat 
						= String.valueOf(new_password_confirmation_pfield.getPassword());
					if(first_name.equals("") | last_name.equals("") | password.equals("")) {
						JOptionPane.showMessageDialog(null, "Please fill out all required fields!");
						return;
					}
					if(!password.equals(current_user.getPassword())) {
						password_pfield.setText("");
						new_password_pfield.setText("");
						new_password_confirmation_pfield.setText("");
						JOptionPane.showMessageDialog(null, "Incorrect password!");
						return;
					}
					if(!new_password.equals("") | !new_password_repeat.equals("")) {
						if(!new_password.equals(new_password_repeat)) {
							password_pfield.setText("");
							new_password_pfield.setText("");
							new_password_confirmation_pfield.setText("");
							JOptionPane.showMessageDialog(null, "Passwords do not match.");
							return;
						}
						if(new_password.length() < 5) {
							password_pfield.setText("");
							new_password_pfield.setText("");
							new_password_confirmation_pfield.setText("");
							JOptionPane.showMessageDialog(null, 
									"Your password must include at least 5 characters!");
							return;
						}
						DatabaseManagement.update_user_information(current_user.getUsername(), 
								first_name, last_name, new_password);
						current_user.update_information(first_name, last_name, new_password);
					}
					else {
						DatabaseManagement.update_user_information(current_user.getUsername(), 
								first_name, last_name, password);
						current_user.update_information(first_name, last_name, password);
					}
					JOptionPane.showMessageDialog(null, "Successfully updated your information.");
					new ProfilePage(current_user);
					edit_profile_page.dispose();
				}
				catch(Exception ex) {
					System.out.println("Database error in edit profile save button: " + e);
				}
			}
			
		});
		
		this.cancel_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ProfilePage(current_user);
				edit_profile_page.dispose();
			}
		});
		
	}
	
	public EditProfile(User user_)
	{
		edit_profile_page = new JFrame("Edit Profile");
		edit_profile_page.setLayout(new GridBagLayout());
		current_user = user_;
		
		edit_panel = new JPanel(new MigLayout("wrap 2", "[150!, center]", "[20!][20!][20!][20!][20!][30!]"));
	
		initComponents();
		initListeners();
		
		edit_panel.add(first_name_label, "grow");
		edit_panel.add(first_name_field, "grow");
		edit_panel.add(last_name_label, "grow");
		edit_panel.add(last_name_field, "grow");
		edit_panel.add(password_label, "grow");
		edit_panel.add(password_pfield, "grow");
		edit_panel.add(new_password_label, "grow");
		edit_panel.add(new_password_pfield, "grow");
		edit_panel.add(new_password_confirmation_label, "grow");
		edit_panel.add(new_password_confirmation_pfield, "grow");
		edit_panel.add(save_button, "grow");
		edit_panel.add(cancel_button, "grow");
		
		edit_profile_page.add(edit_panel);
		
		edit_profile_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		edit_profile_page.setSize(400, 250);
		edit_profile_page.setLocationRelativeTo(null); //Centers the window
		edit_profile_page.setResizable(false);
		edit_profile_page.setVisible(true);
	}
	
}
