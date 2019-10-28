import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class HotelPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2868830487558836627L;
	
	private User current_user;
	private JFrame hotel_interface_page;
	private JPanel hotel_interface_panel;
	private JLabel hotel_name_label;
	private JLabel hotel_location_label;
	private JTextArea hotel_description_textarea;
	private JLabel hotel_price_label;
	private JLabel hotel_rating_label;
	private JLabel reviews_description_label;
	private JButton main_page_button;
	private JButton book_hotel_button;
	private JButton evaluate_hotel_button;
	private JButton delete_hotel_button;
	private JButton edit_information_button;
	private JButton potential_customers_button;
	private JButton delete_review_button;
	private JTable evaluations_table;
	private JScrollPane evaluations_scrollpane;
	private JLabel current_weather_label;
	private Object[][] evaluation_data = {{}};
	private int _hotelID;
	private int selected_evaluation_row = 0;
	
	private void initComponents() {

		book_hotel_button = new JButton("Book hotel");
		main_page_button = new JButton("Main Page");
		evaluate_hotel_button = new JButton("Evalute hotel");
		delete_review_button = new JButton("Delete Review");
		try {
			if(DatabaseManagement.hasUserEvaluatedHotel(_hotelID, current_user.getUsername())) {
				evaluate_hotel_button.setEnabled(false);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		potential_customers_button = new JButton("Potential Customers");
		delete_hotel_button = new JButton("Delete Hotel");
		edit_information_button = new JButton("Edit Hotel Information");
		delete_hotel_button.setForeground(Color.RED);

		Object[] HotelData = DatabaseManagement.getHotel(_hotelID);

		hotel_name_label = new JLabel("Hotel " + HotelData[0]);
		hotel_price_label = new JLabel("Price per night: " + HotelData[2] + "EUR");
		try {
			hotel_rating_label = new JLabel("Rating: " + String.valueOf(HotelData[3]) + "/5 (Evaluated by " + String.valueOf(DatabaseManagement.countHotelEvaluations(_hotelID)) + " Customers)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		hotel_location_label = new JLabel("The hotel is located in " + HotelData[4] + ", " + HotelData[5] + " (Capital: " + HotelData[6] + ", " + HotelData[7] + " Inhabitants)");
		reviews_description_label = new JLabel("Reviews:");
		String current_weather = WeatherAPI.getWeather(HotelData[4].toString());
		current_weather_label = new JLabel("");
		if (!current_weather.equals("")) {
			current_weather_label = new JLabel("Current weather in " + HotelData[4] + ": " + current_weather);
		}
		hotel_description_textarea = new JTextArea(String.valueOf(HotelData[1]));
		hotel_description_textarea.setLineWrap(true);
		hotel_description_textarea.setWrapStyleWord(true);
		hotel_description_textarea.setEditable(false);
		hotel_description_textarea.setPreferredSize(new Dimension(600, 120));
		
		String[] table_titles = {"Reviewer", "Rating", "Nights", "Review"};
		
		
		try {
			evaluation_data = DatabaseManagement.getHotelEvaluations(_hotelID);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		evaluations_table = new JTable(new DefaultTableModel(evaluation_data, 
				table_titles));
		Formatting.resizeColumnWidth(evaluations_table);
		Formatting.setTableSingleSelectionNoEdits(evaluations_table, 600, 400);
		evaluations_scrollpane = new JScrollPane(evaluations_table);
		evaluations_scrollpane.setPreferredSize(new Dimension(600, 400));
		DefaultTableModel model = (DefaultTableModel) evaluations_table.getModel();
		model.setRowCount(evaluation_data.length);

	}
	
	private void initListeners() {
		
		main_page_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MainPage(current_user);
				hotel_interface_page.dispose();
			}
		});

		
		book_hotel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"Redirecting you to the hotels booking site.");
			}
		});
		
		evaluate_hotel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EvaluateHotelPage(current_user,_hotelID);
				hotel_interface_page.dispose();
			}
		});
		
		edit_information_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EditHotelPage(current_user,_hotelID);
				hotel_interface_page.dispose();
			}
		});
		
		delete_hotel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.YES_NO_OPTION;
				int confirm_result = JOptionPane.showConfirmDialog(null, 
						"Are you absolutely sure you want to delete this hotel? \n"
						+ "This will also delete all reviews associated with it. \n"
						+ "This is permanent!", "Confirm hotel deletion",  confirm, 
						JOptionPane.WARNING_MESSAGE);
				if(confirm_result == JOptionPane.YES_OPTION) {
					try {
						DatabaseManagement.deleteHotel(_hotelID);
						new MainPage(current_user);
						hotel_interface_page.dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		potential_customers_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PotentialCustomersPage(current_user,_hotelID);
				hotel_interface_page.dispose();
			}
		});
		
		
		evaluations_table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) { 
				selected_evaluation_row = evaluations_table.rowAtPoint(e.getPoint());
			}
		});
		
		
		delete_review_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DatabaseManagement.deleteEvaluation(_hotelID, 
							(String) evaluation_data[selected_evaluation_row][0]);
				}
				catch(Exception ex) {
					System.out.println("Database error in delete review button: " + ex);
				}
				new HotelPage(current_user, _hotelID);
				hotel_interface_page.dispose();
			}
		});
		
	}
	
	
	public HotelPage(User user_, int hotelID) {
		current_user = user_;
		_hotelID = hotelID;
		
		hotel_interface_page = new JFrame("Hotel Interface");
		hotel_interface_page.setLayout(new GridBagLayout());
		hotel_interface_panel = new JPanel(new MigLayout("wrap 3", "[200!, center]", 
				"[20!][20!][20!][20!][20!]10[120!][25!]40[20!][400!][20!]"));
		
		initComponents();
		initListeners();
		
		if(current_user.getStatus() == User.UserType.ADMIN) {
			hotel_interface_panel.add(hotel_name_label, "left, span 2, grow");
			hotel_interface_panel.add(delete_hotel_button, "grow");
		}
		else {
			hotel_interface_panel.add(hotel_name_label, "left, span 3, grow");
		}
		hotel_interface_panel.add(hotel_rating_label, "left, span 3, grow");
		hotel_interface_panel.add(hotel_price_label, "left, span 3, grow");
		hotel_interface_panel.add(hotel_location_label, "left, span 3, grow");
		hotel_interface_panel.add(current_weather_label, "left, span 3, grow");
		hotel_interface_panel.add(hotel_description_textarea, "span 3, left, grow");
		hotel_interface_panel.add(book_hotel_button, "grow");
		hotel_interface_panel.add(evaluate_hotel_button, "grow");
		hotel_interface_panel.add(main_page_button, "grow");
		hotel_interface_panel.add(reviews_description_label, "skip 1, center, wrap");
		hotel_interface_panel.add(evaluations_scrollpane, "span 3, grow");
		if(current_user.getStatus() == User.UserType.ADMIN) {
			hotel_interface_panel.add(edit_information_button, "grow");
			hotel_interface_panel.add(delete_review_button, "grow");
			hotel_interface_panel.add(potential_customers_button, "grow");
		}
		
		hotel_interface_page.add(hotel_interface_panel);
		hotel_interface_page.setSize(700, 900);
		hotel_interface_page.setLocationRelativeTo(null); //Centers the window
		hotel_interface_page.setResizable(false);
		hotel_interface_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hotel_interface_page.setVisible(true);
	}
}