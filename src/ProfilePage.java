import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;



public class ProfilePage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634373077322785689L;

	private JFrame profile_page;
	private JPanel profile_panel;
	private JButton main_page_button;
	private JButton edit_info_button;
	private JButton change_interests_button;
	private JButton friends_button;
	private JLabel username_label;
	private JLabel first_name_label;
	private JLabel last_name_label;
	private User current_user;
	
	
	private void initComponents() {
		username_label = new JLabel("Username:  " + current_user.getUsername());
		first_name_label = new JLabel("First name:  " + current_user.getFirstName());
		last_name_label = new JLabel("Last name:  " + current_user.getLastName());
		main_page_button = new JButton("Main Page");
		main_page_button.setPreferredSize(new Dimension(300, 20));
		edit_info_button = new JButton("Edit information");
		change_interests_button = new JButton("Update interests");
		friends_button = new JButton("Friends");
		
	}
	
	
	private void initListeners() {
		
		this.main_page_button.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				profile_page.dispose();
			}
		});
		
		this.edit_info_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditProfile(current_user);
				profile_page.dispose();
			}
		});
		
		this.change_interests_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new InterestPage(current_user);
				profile_page.dispose();
			}
		});
		
		this.friends_button.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				new FriendPage(current_user);
				profile_page.dispose();
			}
		});
	}
	
	
	public ProfilePage(User user) {
		current_user = user;
		
		profile_page = new JFrame("My Profile");
		profile_page.setLayout(new GridBagLayout());
		profile_panel = new JPanel(new MigLayout("wrap 2", "[150!]", "[20!][20!][20!]20[30!][30!]"));
		
		initComponents();
		initListeners();
		
		profile_panel.add(username_label, "span 2, growy, center");
		profile_panel.add(first_name_label, "center, span 2, growy");
		profile_panel.add(last_name_label, "center, span 2, growy");
		profile_panel.add(edit_info_button, "grow");
		profile_panel.add(change_interests_button, "wrap, grow");
		profile_panel.add(friends_button, "grow");
		profile_panel.add(main_page_button, "grow");
		
		profile_page.add(profile_panel);
		Formatting.formatPage(profile_page, 350, 250);
		profile_page.setVisible(true);
		
	}

}
