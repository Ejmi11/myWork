import java.awt.GridBagLayout;
import java.awt.event.*;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class TREC extends javax.swing.JFrame{


	/**
	 * 
	 */
	private static final long serialVersionUID = -7055134515015855716L;
	private JFrame login_window;
	private JButton login_button;
	private JButton register_button;
	private JTextField user_name;
	private JPasswordField user_password;
	private JLabel label_username;
	private JLabel label_password;
	private JPanel login_panel;
	class CustomKeyListener implements KeyListener{
		
	      public void keyTyped(KeyEvent e) {
	    	  
	      }
	      public void keyPressed(KeyEvent e) {
	    	  if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        	 login_button.doClick();
		         }
	      }
	      public void keyReleased(KeyEvent e) {
		  		
	      }   
	   }
	private void initListeners()
	{
		this.login_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login_button.setEnabled(false);
				String name = user_name.getText();
				String password = String.valueOf(user_password.getPassword());	
				// TODO: Adapt functions to accept char[] since using array is bad security practice
				int user_id = 0;
				try {
					user_id = DatabaseManagement.check_login(name, password);
					if(user_id != 0)
					{
						
						User current_user = DatabaseManagement.create_user(user_id);
						login_button.setEnabled(true);
						JOptionPane.showMessageDialog(null, "Welcome back " 
								+ current_user.getFirstName() + "!");
						new MainPage(current_user);
						login_window.dispose();
					}
					else
					{
						user_password.setText("");
						login_button.setEnabled(true);
						JOptionPane.showMessageDialog(null, 
								"User does not exist or password is incorrect.");	
						return;
					}
				}
				catch (Exception e1) {
					System.out.println("Database error in check_login: " + e1);
				}
			}
		});
		
		//this.user_name.addKeyListener(new CustomKeyListener());
		this.user_password.addKeyListener(new CustomKeyListener());
		this.register_button.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				new UserRegistration();
				login_window.dispose();
			}
		});
	}
	
	private void initComponents()
	{
		user_name = new JTextField(14);
		user_name.setEditable(true);
		user_password = new JPasswordField(14);
		user_password.setEditable(true);
		login_button = new JButton("Login");
		register_button = new JButton("Register");
		label_username = new JLabel("Username: ");
		label_password = new JLabel("Password: ");
	}
	
	public TREC()
	{
		login_window = new JFrame("Login");
		login_window.setLayout(new GridBagLayout());
		login_panel = new JPanel(new MigLayout("wrap 2", "[90!]", "[15:20:30]"));

		initComponents();
		initListeners();
		
		login_panel.add(label_username, "span 2, center");
		login_panel.add(user_name, "span 2, growx");
		login_panel.add(label_password, "span 2, center");
		login_panel.add(user_password, "span 2, growx");
		login_panel.add(login_button, "growx, growy");
		login_panel.add(register_button, "growx, growy");
		
		login_window.add(login_panel);
		login_window.setSize(250, 230);
		login_window.setLocationRelativeTo(null); //Centers the window
		login_window.setResizable(false);
		login_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_window.setVisible(true);
	}
	
	
	public static void main(String[] args) throws Exception {

		//DatabaseManagement.resetTables(); // only use if you know what you are doing
		//DatabaseManagement.createTable();
		//DatabaseManagement.insert_countries(); // only run this once
		//DatabaseManagement.insert_hotels();// only run this once
	
		
		new TREC();
	}
}
	
