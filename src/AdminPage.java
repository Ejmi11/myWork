import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;



public class AdminPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2622412489178178934L;

	private User current_user;
	private JFrame admin_page;
	private JPanel admin_panel;
	private JButton admin_management_button;
	private JButton add_hotel_button;
	private JButton add_activity_button;
	private JButton add_theme_button;
	private JButton add_destination_button;
	private JButton update_destination_button;
	private JButton main_page_button;
	private JButton new_notification_button;
	private JButton check_support_messages_button;
	
	private void initComponents() {
		
		admin_management_button = new JButton("Admin Management");
		add_hotel_button = new JButton("Add Hotel");
		add_theme_button = new JButton("Add Interest Theme");
		add_activity_button = new JButton("Add Activity");
		add_destination_button = new JButton("Add Desination");
		update_destination_button = new JButton("Update Destination");
		main_page_button = new JButton("Main Page");	
		new_notification_button = new JButton("New Notification");
		check_support_messages_button = new JButton("Check support messages");
	}
	
	private void initListeners() {
		
		main_page_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				admin_page.dispose();
			}
		});

		admin_management_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminManagementPage(current_user);
				admin_page.dispose();
			}
		});
		
		add_hotel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddHotelPage(current_user);
				admin_page.dispose();
			}
		});
		
		add_activity_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddActivityPage(current_user);
				admin_page.dispose();
			}
		});
		
		add_theme_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddThemePage(current_user);
				admin_page.dispose();
			}
		});
		
		add_destination_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddDestinationPage(current_user);
				admin_page.dispose();
			}
		});
		
		update_destination_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UpdateDestinationPage(current_user);
				admin_page.dispose();
			}
		});
		
		new_notification_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CreateNotificationPage(current_user);
				admin_page.dispose();
			}
		});
		
		check_support_messages_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HandleSupportPage(current_user);
				admin_page.dispose();
			}
		});
		
		
	}
	
	
	public AdminPage(User user) {
		
		current_user = user;
		
		admin_page = new JFrame("Admin Tools");
		admin_page.setLayout(new GridBagLayout());
		admin_panel = new JPanel(new MigLayout("wrap 2", "[180!]", "[30!]"));
		
		initComponents();
		initListeners();
		
		admin_panel.add(admin_management_button, "grow");
		admin_panel.add(add_hotel_button, "grow");
		admin_panel.add(add_destination_button, "grow");
		admin_panel.add(update_destination_button, "grow");
		admin_panel.add(add_activity_button, "grow");
		admin_panel.add(add_theme_button, "grow");
		admin_panel.add(new_notification_button, "grow");
		admin_panel.add(check_support_messages_button, "grow");
		admin_panel.add(main_page_button, "grow, span 2");
		
		admin_page.add(admin_panel);
		admin_page.setSize(450, 300);
		admin_page.setLocationRelativeTo(null); //Centers the window
		admin_page.setResizable(false);
		admin_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		admin_page.setVisible(true);
	}
}