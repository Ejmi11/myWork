import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;



public class EditHotelPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666365024644430802L;
	
	private User current_user;
	private JFrame edit_hotel_page;
	private JPanel edit_hotel_panel;
	private JTextField hotel_name_field;
	private JTextArea hotel_description_textarea;
	private JTextField hotel_price_field;
	private JLabel hotel_name_label;
	private JLabel hotel_description_label;
	private JLabel hotel_price_label;
	private JLabel hotel_activities_label;
	private JButton save_button;
	private JButton cancel_button;
	private JScrollPane hotel_activities_scrollpane;
	private JTable activities_list;

	private int _hotelID;
	
	private void initComponents() {
		
		Object[] hotelData = DatabaseManagement.getHotel(_hotelID);
		cancel_button = new JButton("Cancel");
		save_button = new JButton("Save");
		hotel_name_label = new JLabel("Name of the hotel: ");
		hotel_description_label = new JLabel("Short description: ");
		hotel_price_label = new JLabel("Price per night in EUR: ");
		hotel_activities_label = new JLabel("Which activities does the hotel support ?");
		hotel_name_field = new JTextField(String.valueOf(hotelData[0]));
		hotel_name_field.setEditable(true);
		hotel_description_textarea = new JTextArea(String.valueOf(hotelData[1]));
		hotel_description_textarea.setLineWrap(true);
	    hotel_description_textarea.setWrapStyleWord(true);
	    hotel_description_textarea.setEditable(true);
	    hotel_description_textarea.setPreferredSize(new Dimension(600, 120));
		hotel_price_field = new JTextField(String.valueOf(hotelData[2]));
		hotel_price_field.setEditable(true);
		
		String[] table_titles = {"Activity", "Supported?"};
		String[] activities = null;
		Object[] hotelActivities = null;
		try
		{
			activities = DatabaseManagement.getActivities();
			hotelActivities = DatabaseManagement.getHotelActivities(_hotelID);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Object[][] data = new Object[activities.length][2];
		for(int i = 0; i < activities.length; i++)
		{
			data[i][0] = activities[i];
			if (Arrays.asList(hotelActivities).contains(activities[i]))
				data[i][1] = Boolean.TRUE;
			else
				data[i][1] = Boolean.FALSE;
		}

		activities_list = new JTable(new DefaultTableModel(data, table_titles))
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1628963847744566251L;

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return data[0][columnIndex].getClass();
			}
		};
		activities_list.setPreferredScrollableViewportSize(new Dimension(250,250));
		activities_list.setFillsViewportHeight(true);
		hotel_activities_scrollpane = new JScrollPane(activities_list);
		hotel_activities_scrollpane.setPreferredSize(new Dimension(250,250));
		DefaultTableModel model = (DefaultTableModel) activities_list.getModel();
		model.setRowCount(data.length);
	}
	
	private void initListeners() {
		

		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String activitiesString = "";
				for(int i = 0; i < activities_list.getModel().getRowCount(); i++)
				{
					if (activities_list.getModel().getValueAt(i, 1) == Boolean.TRUE)
						activitiesString += activities_list.getModel().getValueAt(i, 0) + ",";
				}
				activitiesString = activitiesString.substring(0, activitiesString.length() - 1);

				try {
					DatabaseManagement.updateHotel(_hotelID,hotel_name_field.getText(),hotel_description_textarea.getText(),hotel_price_field.getText(),activitiesString);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null,"Hotel updated!");
				new HotelPage(current_user,_hotelID);
				edit_hotel_page.dispose();
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HotelPage(current_user,_hotelID);
				edit_hotel_page.dispose();
			}
		});
		
	}
	
	
	public EditHotelPage(User user, int HotelID) {
		current_user = user;
		_hotelID = HotelID;
		
		edit_hotel_page = new JFrame("Edit Hotel Information");
		edit_hotel_page.setLayout(new GridBagLayout());
		edit_hotel_panel = new JPanel(new MigLayout("wrap 2", "[300!]", "[20!][20!][200!][20!][250!][25!]"));
		
		initComponents();
		initListeners();
		
		edit_hotel_panel.add(hotel_name_label, "left, grow");
		edit_hotel_panel.add(hotel_name_field, "left, grow");
		edit_hotel_panel.add(hotel_price_label, "left, grow");
		edit_hotel_panel.add(hotel_price_field, "left, grow");
		edit_hotel_panel.add(hotel_description_label, "left, top, grow");
		edit_hotel_panel.add(hotel_description_textarea, "left, grow");
		edit_hotel_panel.add(hotel_activities_label, "left, grow, top, wrap");
		edit_hotel_panel.add(hotel_activities_scrollpane, "left, grow, span 2");
		edit_hotel_panel.add(save_button, "grow");
		edit_hotel_panel.add(cancel_button, "grow");
		
		edit_hotel_page.add(edit_hotel_panel);
		edit_hotel_page.setSize(700, 750);
		edit_hotel_page.setLocationRelativeTo(null); //Centers the window
		edit_hotel_page.setResizable(false);
		edit_hotel_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		edit_hotel_page.setVisible(true);
	}
}