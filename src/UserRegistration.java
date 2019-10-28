import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// MigLayout for better layout management
// http://www.miglayout.com/QuickStart.pdf
// http://www.miglayout.com/
import net.miginfocom.swing.MigLayout;

public class UserRegistration extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387903736483433013L;

	private Toolkit kit = Toolkit.getDefaultToolkit();
	JFrame registration_window;	
	private JTextField user_username;
	private JTextField user_first_name;
	private JTextField user_last_name;
	private JButton register_button;
	private JButton cancel_button;
	private JPasswordField user_password;
	private JPasswordField user_password_confirmation;
	private JLabel label_first_name;
	private JLabel label_last_name;
	private JLabel label_username;
	private JLabel label_password;
	private JLabel label_password_confirmation;
	private JPanel registration_panel;
	
	class CustomKeyListener implements KeyListener{
		
	      public void keyTyped(KeyEvent e) {
	    	  
	      }
	      public void keyPressed(KeyEvent e) {
	    	  if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        	 register_button.doClick();
		         }
	      }
	      public void keyReleased(KeyEvent e) {
		  		
	      }   
	   }
	
	
	private void initListeners()
	{
		this.register_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					register_button.setEnabled(false);
					String first_name = user_first_name.getText();
					String last_name = user_last_name.getText();
					String username = user_username.getText();
					String password = String.valueOf(user_password.getPassword());
					String password_repeat = String.valueOf(user_password_confirmation.getPassword());
					int user_id = 0;
					// TODO: Adapt functions to accept char[] as using array is bad security practice
					// TODO: Check for bad inputs (multiple words, only alphabet characters, ...)
					if(first_name.equals("") | last_name.equals("") 
							| username.equals("") | password.equals("")) {
						JOptionPane.showMessageDialog(null, "Please fill out all fields!");
						register_button.setEnabled(true);
						return;
					}
					if(password.length() < 5) {
						JOptionPane.showMessageDialog(null, 
								"Your password must include at least 5 characters!");
						register_button.setEnabled(true);
						return;
					}
					if(password.compareTo(password_repeat) != 0)
					{
						JOptionPane.showMessageDialog(null, "Passwords do not match!");
						user_password.setText("");
						user_password_confirmation.setText("");
						register_button.setEnabled(true);
						return;
					}
					if(DatabaseManagement.first_user_check()) {
						DatabaseManagement.insert_user(first_name, last_name, username, 
								password, "admin");
						user_id = 1;
					}
					else if(DatabaseManagement.unique_username_check(username) == false) {
						JOptionPane.showMessageDialog(null, 
								"Username is already in use. Please choose another one.");
						register_button.setEnabled(true);
						return;
					}
					else {
						DatabaseManagement.insert_user(first_name, last_name, username, 
								password, "customer");
						user_id = DatabaseManagement.last_id("users");
					}
					User current_user = DatabaseManagement.create_user(user_id);
					JOptionPane.showMessageDialog(null,"Welcome "+ first_name);
					register_button.setEnabled(true);
					new MainPage(current_user);
					registration_window.dispose();
				}
				catch(Exception ex) {
					System.out.println("Error in register button: " + e);
					register_button.setEnabled(true);
				}
			}
		});
		this.user_password_confirmation.addKeyListener(new CustomKeyListener());
		this.cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TREC();
				registration_window.dispose();
			}
		});
	}
	
	
	private void initComponents()
	{
		user_first_name = new JTextField(15);
		user_first_name.setEditable(true);
		user_last_name = new JTextField(15);
		user_last_name.setEditable(true);
		user_username = new JTextField(15);
		user_username.setEditable(true);
		user_password = new JPasswordField(15);
		user_password.setEditable(true);
		user_password_confirmation = new JPasswordField(15);
		user_password_confirmation.setEditable(true);
		register_button = new JButton("Register");
		register_button.setSize(20, 20);
		cancel_button = new JButton("Cancel");
		cancel_button.setSize(20, 20);
		label_first_name = new JLabel("First name: ");
		label_last_name = new JLabel("Last name: ");
		label_username = new JLabel("Username: ");
		label_password = new JLabel("Password: ");
		label_password_confirmation = new JLabel("Repeat password: ");
	}
	
	
	public UserRegistration() {	
		registration_window = new JFrame("T-REC Registration");
		registration_window.setLayout(new GridBagLayout());
		registration_panel = new JPanel(new MigLayout("wrap 2", "[100!, center]", "[20!]"));	
		
		initComponents();
		initListeners();
		
		registration_panel.add(label_first_name, "span 2, growy");
		registration_panel.add(user_first_name, "span 2, growy");
		registration_panel.add(label_last_name, "span 2, growy");
		registration_panel.add(user_last_name, "span 2, growy");
		registration_panel.add(label_username, "span 2, growy");
		registration_panel.add(user_username, "span 2, growy");
		registration_panel.add(label_password, "span 2, growy");
		registration_panel.add(user_password, "span 2, growy");
		registration_panel.add(label_password_confirmation, "span 2, growy");
		registration_panel.add(user_password_confirmation, "span 2, growy");
		registration_panel.add(register_button, "grow");
		registration_panel.add(cancel_button, "wrap, grow");
		
		registration_window.add(registration_panel);
		registration_window.setSize(300, 400);
		registration_window.setLocationRelativeTo(null); //Centers the window
		registration_window.setResizable(false);
		registration_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registration_window.setVisible(true);
	}
}
