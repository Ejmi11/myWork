import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;


public class StatisticsPage extends javax.swing.JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4923339756245119776L;
	
	private JFrame statistics_page;
	private JPanel statistics_panel;
	private JButton main_page_button;
	private JButton statistics_type_confirm;
	private User current_user;
	private String[] admin_available_stats = new String[] {"Cheapest hotels", "Most expensive hotels",
			"Highest rated hotels", "Destinations supporting interest themes", 
			"Most evaluated hotels", "Worst rated hotels", 
			"Most & least evaluating customers", "Customers interested in destination supporting themes"};
	private String[] customer_available_stats = new String[] {admin_available_stats[0], 
			admin_available_stats[1], admin_available_stats[2], admin_available_stats[3]};
	private JComboBox<String> restricted_stats;
	private String chosen_statistic = "Cheapest hotels";
	
	private void initComponents() {

        statistics_type_confirm = new JButton("Confirm");
 
		main_page_button = new JButton("Main Page");
		
		
		if(current_user.getStatus() == User.UserType.ADMIN)
		{
			restricted_stats = new JComboBox<String>(admin_available_stats);
		}
		else
		{
			restricted_stats = new JComboBox<String>(customer_available_stats);
		}
        
		restricted_stats.setPreferredSize(new Dimension(400, 20));
	}
	
	private void initListeners() {
		
		this.main_page_button.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				statistics_page.dispose();
			}
		});
		
        this.statistics_type_confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new Stats(chosen_statistic, current_user);
				statistics_page.dispose();
			}
		});
        
        this.restricted_stats.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		chosen_statistic = (String) restricted_stats.getSelectedItem(); 
        	}
        });
	}
	
	
	public StatisticsPage(User user) {
		current_user = user;
		
		statistics_page = new JFrame("Statistics");
		statistics_page.setLayout(new GridBagLayout());
		statistics_panel = new JPanel(new MigLayout("wrap", "[300!]","[20!] [20!] [20!] [20!] [20!]" ));
		if(current_user.getStatus() == User.UserType.ADMIN)
		{
			restricted_stats = new JComboBox(admin_available_stats);
		}
		else
		{
			restricted_stats = new JComboBox(customer_available_stats);
		}
	
		initComponents();
		initListeners();
		
		
		statistics_panel.add(statistics_type_confirm, "center, grow, w 200!");
		statistics_panel.add(restricted_stats, "left, grow, span 2, w 400!");
		statistics_panel.add(main_page_button, "center, grow");
		
		statistics_page.add(statistics_panel);
		statistics_page.setSize(700, 550);
		statistics_page.setLocationRelativeTo(null); //Centers the window
		statistics_page.setResizable(false);
		statistics_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		statistics_page.setVisible(true);	
	}
}