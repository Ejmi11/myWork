import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;



public class EvaluateHotelPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8104791968605225263L;
	
	private User current_user;
	private JFrame evaluate_hotel_page;
	private JPanel evaluate_hotel_panel;
	private JLabel hotel_name_label;
	private JLabel evaluation_label;
	private JLabel nights_stayed_label;
	private JLabel rating_label;
	private JLabel rating_description_label;
	private JTextField nights_stayed_field;
	private JEditorPane evaluation_pane;
	private ButtonGroup rating_group;
	private JRadioButton rating_one;
	private JRadioButton rating_two;
	private JRadioButton rating_three;
	private JRadioButton rating_four;
	private JRadioButton rating_five;
	private JButton confirm_button;
	private JButton cancel_button;

	private int _hotelID;
	
	private void initComponents() {
		
		confirm_button = new JButton("Confirm Evaluation");
		cancel_button = new JButton("Cancel");
		hotel_name_label = new JLabel("Hotel test");
		nights_stayed_label = new JLabel("Please include the nights you spent at the hotel (Optional): ");
		evaluation_label = new JLabel("Please write a review of your experience staying at the hotel: (Optional) ");
		rating_label = new JLabel("Please select how satisfied you were with the hotel: ");
		rating_description_label = new JLabel(" 1 (Not satisfied) - 5 (extremely satisfied) ");
		nights_stayed_field = new JTextField(10);
		evaluation_pane = new JEditorPane();
		evaluation_pane.setEditable(true);
		rating_group = new ButtonGroup();
		rating_one = new JRadioButton("1 ");
		rating_two = new JRadioButton("2 ");
		rating_three = new JRadioButton("3 ");
		rating_four = new JRadioButton("4 ");
		rating_five = new JRadioButton("5 ");
		rating_group.add(rating_one);
		rating_group.add(rating_two);
		rating_group.add(rating_three);
		rating_group.add(rating_four);
		rating_group.add(rating_five);
		
	}
	
	private void initListeners() {
		
		confirm_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String rating = "";
					if (rating_one.isSelected())
						rating = "1";
					else if (rating_two.isSelected())
						rating = "2";
					else if (rating_three.isSelected())
						rating = "3";
					else if (rating_four.isSelected())
						rating = "4";
					else if (rating_five.isSelected())
						rating = "5";
					else {
						JOptionPane.showMessageDialog(null, "Please select a rating for the hotel.");
						return;
					}
					String nights_stayed = nights_stayed_field.getText();
					if(nights_stayed.isEmpty()) {
						nights_stayed = "0";
					}
					
					DatabaseManagement.AddHotelEvaluation(_hotelID, nights_stayed, 
							evaluation_pane.getText(), rating, current_user);
					JOptionPane.showMessageDialog(null,"Successfully evaluated the hotel.");
					new HotelPage(current_user, _hotelID);
					evaluate_hotel_page.dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HotelPage(current_user,_hotelID);
				evaluate_hotel_page.dispose();
			}
		});

	}
	
	public EvaluateHotelPage(User user, int HotelID) {
		current_user = user;
		_hotelID = HotelID;
		
		evaluate_hotel_page = new JFrame("Evaluate Hotel");
		evaluate_hotel_page.setLayout(new GridBagLayout());
		evaluate_hotel_panel = new JPanel(new MigLayout("wrap 5", "[80!]", "[20!][20!][20!][20!][20!][20!][20!][200!][25!]"));
		
		initComponents();
		initListeners();
		
		evaluate_hotel_panel.add(hotel_name_label, "left, span 5, grow");
		evaluate_hotel_panel.add(rating_label, "left, span 5, grow");
		evaluate_hotel_panel.add(rating_description_label, "center, span 5, grow");
		evaluate_hotel_panel.add(rating_one, "grow");
		evaluate_hotel_panel.add(rating_two, "grow");
		evaluate_hotel_panel.add(rating_three, "grow");
		evaluate_hotel_panel.add(rating_four, "grow");
		evaluate_hotel_panel.add(rating_five, "grow");
		evaluate_hotel_panel.add(nights_stayed_label, "left, span 5, grow");
		evaluate_hotel_panel.add(nights_stayed_field, "left, wrap, grow");
		evaluate_hotel_panel.add(evaluation_label, "left, span 5, grow");
		evaluate_hotel_panel.add(evaluation_pane, "span 5, grow");
		evaluate_hotel_panel.add(confirm_button, "span 3, grow");
		evaluate_hotel_panel.add(cancel_button, "span 2, grow");
		
		evaluate_hotel_page.add(evaluate_hotel_panel);
		evaluate_hotel_page.setSize(500, 550);
		evaluate_hotel_page.setLocationRelativeTo(null); //Centers the window
		evaluate_hotel_page.setResizable(false);
		evaluate_hotel_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		evaluate_hotel_page.setVisible(true);
	}
}