import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;



public class InterestPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6665335975618713058L;

	private JFrame interests_page;
	private JPanel interests_panel;
	private JButton themes_button;
	private JButton activities_button;
	private JButton profile_button;
	private JLabel themes_label;
	private JLabel activities_label;
	private JLabel profile_label;
	private User current_user;

	
	private void initComponents() {
		
		themes_label = new JLabel("Update your prefered themes:  ");
		activities_label = new JLabel("Update your prefered activities:  ");
		profile_label = new JLabel("Return to your profile:  ");
		
		themes_button = new JButton("Themes");
		themes_button.setPreferredSize(new Dimension(100, 20));
		activities_button = new JButton("Activites");
		activities_button.setPreferredSize(new Dimension(100, 20));
		profile_button = new JButton("Profile");
		profile_button.setPreferredSize(new Dimension(100, 20));
		
	}
	
	
	private void initListeners() {
		
		themes_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ThemesPage(current_user);
				interests_page.dispose();
			}
		});
		
		activities_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ActivitiesPage(current_user);
				interests_page.dispose();
			}
		});
		
		profile_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ProfilePage(current_user);
				interests_page.dispose();
			}
		});
		
	}
	
	
	public InterestPage(User user) {
		
		current_user = user;
		
		interests_page = new JFrame("Update Interests");	
		interests_page.setLayout(new GridBagLayout());
		interests_panel = new JPanel(new MigLayout("wrap 2", "[200!, left]", "[30!][30!][30!]"));
		
		initComponents();
		initListeners();
		
		interests_panel.add(themes_label, "grow");
		interests_panel.add(themes_button, "grow");
		interests_panel.add(activities_label, "grow");
		interests_panel.add(activities_button, "grow");
		interests_panel.add(profile_label, "grow");
		interests_panel.add(profile_button, "grow");
		
		
		interests_page.add(interests_panel);
		
		interests_page.setSize(450, 200);
		interests_page.setLocationRelativeTo(null); //Centers the window
		interests_page.setResizable(false);
		interests_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		interests_page.setVisible(true);
		
	}

}