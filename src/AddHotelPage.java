import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class AddHotelPage extends javax.swing.JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5296351586349731432L;

	private User current_user;
	private JFrame add_hotel_page;
	private JPanel add_hotel_panel;
	private JTextField hotel_name_field;
	private JEditorPane hotel_description_field;
	private JTextField hotel_price_field;
	private JLabel hotel_name_label;
	private JLabel hotel_description_label;
	private JLabel hotel_price_label;
	private JLabel hotel_activities_label;
	private JButton save_button;
	private JButton cancel_button;
	private JScrollPane hotel_activities_scrollpane;
	private JTable activities_list;
	private JLabel hotel_destination_label;
	private JTable destinations_table;
	private JScrollPane hotel_destinations_scrollpane;
	
	private void initComponents() {
		
		cancel_button = new JButton("Cancel");
		save_button = new JButton("Add hotel");
		hotel_name_label = new JLabel("Name of the hotel: ");
		hotel_description_label = new JLabel("Short description: ");
		hotel_price_label = new JLabel("Price per night in Euros: ");
		hotel_activities_label = new JLabel("Which activities does the hotel support ?");
		hotel_name_field = new JTextField(15);
		hotel_name_field.setEditable(true);
		hotel_description_field = new JEditorPane();
		hotel_description_field.setEditable(true);
		hotel_price_field = new JTextField(15);
		hotel_price_field.setEditable(true);
		hotel_destination_label = new JLabel("Select the destination the hotel is located in: ");
		
		String[] destination_table_titles = {"Destination"};
		Object[][] destination_data = new Object[0][];
		try {
			destination_data = DatabaseManagement.getDestinations();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i =0; i < destination_data.length; i++)
			destination_data[i][0] += ","+ destination_data[i][1];


		destinations_table = new JTable(new DefaultTableModel(destination_data, 
				destination_table_titles));
		destinations_table.setPreferredScrollableViewportSize(new Dimension(200, 250));
		destinations_table.setFillsViewportHeight(true);
		Formatting.setTableSingleSelectionNoEdits(destinations_table, 200, 250);
		hotel_destinations_scrollpane = new JScrollPane(destinations_table);
		hotel_destinations_scrollpane.setPreferredSize(new Dimension(200, 250));
		DefaultTableModel destination_model = (DefaultTableModel) destinations_table.getModel();
		destination_model.setRowCount(destination_data.length);
		
		String[] table_titles = {"Activity", "Supported?"};
		String[] activities = null;
		try
		{
			activities = DatabaseManagement.getActivities();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Object[][] data = new Object[activities.length][2];
		for(int i = 0; i < activities.length; i++)
		{
			data[i][0] = activities[i];
			data[i][1] = Boolean.FALSE;
		}
		
		activities_list = new JTable(new DefaultTableModel(data, table_titles))
		{
			private static final long serialVersionUID = -1202605436982615227L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return (column == 1);
		    }
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return data[0][columnIndex].getClass();
			}
		};

		activities_list.setPreferredScrollableViewportSize(new Dimension(250,250));
		Formatting.resizeColumnWidth(activities_list);
		activities_list.setFillsViewportHeight(true);
		activities_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hotel_activities_scrollpane = new JScrollPane(activities_list);
		hotel_activities_scrollpane.setPreferredSize(new Dimension(250,250));
		DefaultTableModel model = (DefaultTableModel) activities_list.getModel();
		model.setRowCount(activities.length);
	}
	
	private void initListeners() {	
		
		save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				save_button.setEnabled(false);
				String hotel_name = hotel_name_field.getText();
				String hotel_price = hotel_price_field.getText();
				String hotel_description = hotel_description_field.getText();
				String activitiesString = "";
				if(hotel_name.isEmpty() | hotel_price.isEmpty() | hotel_description.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please fill all of the required fields.");
					save_button.setEnabled(true);
					return;
				}
				for(int i = 0; i < activities_list.getModel().getRowCount(); i++)
				{
					if (activities_list.getModel().getValueAt(i, 1) == Boolean.TRUE)
						activitiesString += activities_list.getModel().getValueAt(i, 0) + ",";
				}
				activitiesString = activitiesString.substring(0, activitiesString.length() - 1);
				int destinationID=0;
				String[] destString = ((String)destinations_table.getModel().getValueAt(destinations_table.getSelectedRow(),0)).split(",");
				try {
					destinationID = DatabaseManagement.getDestinationId(destString[0],destString[1]);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					DatabaseManagement.insert_hotel(hotel_name, hotel_description, hotel_price,
							String.valueOf(destinationID), activitiesString);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "Hotel added to database!");
				new AddHotelPage(current_user);
				add_hotel_page.dispose();
			}
		});

		
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminPage(current_user);
				add_hotel_page.dispose();
			}
		});
		
	}
	
	
	public AddHotelPage(User user) {
		current_user = user;
		
		add_hotel_page = new JFrame("Add Hotel");
		add_hotel_page.setLayout(new GridBagLayout());
		add_hotel_panel = new JPanel(new MigLayout("wrap 2", "[400!]", "[20!][20!][200!][20!][250!][25!]"));
		
		initComponents();
		initListeners();
		
		add_hotel_panel.add(hotel_name_label, "left, grow");
		add_hotel_panel.add(hotel_name_field, "left, grow");
		add_hotel_panel.add(hotel_price_label, "left, grow");
		add_hotel_panel.add(hotel_price_field, "left, grow");
		add_hotel_panel.add(hotel_description_label, "left, top, grow");
		add_hotel_panel.add(hotel_description_field, "left, grow");
		add_hotel_panel.add(hotel_destination_label, "left, grow");
		add_hotel_panel.add(hotel_activities_label, "left, grow, top");
		add_hotel_panel.add(hotel_destinations_scrollpane, "left, grow");
		add_hotel_panel.add(hotel_activities_scrollpane, "left, grow");
		add_hotel_panel.add(save_button, "grow");
		add_hotel_panel.add(cancel_button, "grow");
		
		add_hotel_page.add(add_hotel_panel);
		add_hotel_page.setSize(900, 750);
		add_hotel_page.setLocationRelativeTo(null); //Centers the window
		add_hotel_page.setResizable(false);
		add_hotel_page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add_hotel_page.setVisible(true);
	}
}