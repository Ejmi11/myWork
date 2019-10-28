import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.WindowEvent;

import javax.print.attribute.standard.PresentationDirection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class Stats extends javax.swing.JFrame{
	
	private JFrame stats_page_;
	private JButton back_button_;
	private JToolBar destinations_;
	private JComboBox<String[]> destinations_box_; 
	private String chosen_destination_ = "";
	private JPanel stats_panel_;
	private User current_user_;
	private Object[][] hotel_list_;
	private JTable hotels_table_;
	private JScrollPane hotels_scrollpane;
	private int row;
	public WindowListener listener = new WindowAdapter() {
			//@Override
			public void windowClosing(WindowEvent w) {
			new StatisticsPage(current_user_);
			//stats_page.dispose();
		}
		};

	private void initComponents() {	
		back_button_ = new JButton("Back");
		destinations_ = new JToolBar();
		destinations_.setRollover(true);
		destinations_box_ = new JComboBox(new String[] { "overall", "Graz", "Eisenstadt"});
		
        destinations_.add(destinations_box_);
        try {
			hotel_list_ = DatabaseManagement.getHotels();
		} catch (Exception ex) {
			System.out.println("Database error in getHotels Stats: " + ex);
		}
	}
	private void initListeners() {
		
		
		this.stats_page_.addWindowListener(listener);
		this.back_button_.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				new StatisticsPage(current_user_);
				stats_page_.dispose();
			}
		});
		/*this.statistics_type_confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new Stats(chosen_statistic, current_user);
				statistics_page.dispose();
			}
		});*/
        
        this.destinations_box_.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		stats_panel_= new JPanel(new MigLayout("wrap", "[500!]", "[25!][100!][25!][500!][25!]"));
        		if((String) destinations_box_.getSelectedItem() == "overall")
        		{
        			chosen_destination_ = "";
        		}
        		else {
        		chosen_destination_ = (String) destinations_box_.getSelectedItem();
        		}
        		expensiviestHotel(true);
        		/*stats_panel_.revalidate();
        		stats_panel_.repaint();*/
        		/*stats_page_.revalidate();
        		stats_page_.repaint();*/
        	}
        });
		
		if(hotels_table_ != null)
		{
			this.hotels_table_.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) { 
				if (e.getClickCount() == 2) {
					row = hotels_table_.rowAtPoint(e.getPoint());
					int col = hotels_table_.columnAtPoint(e.getPoint());
					int row_before = row;
					int id = DatabaseManagement.getHotelID((String)hotels_table_.getModel().getValueAt(hotels_table_.getSelectedRow(),0),(String)hotels_table_.getModel().getValueAt(hotels_table_.getSelectedRow(),1),String.valueOf(hotels_table_.getModel().getValueAt(hotels_table_.getSelectedRow(),3)));
					if(row_before == row && row >= 0 && col >= 0)
					{
						//new HotelInterface(main_page,hotel_ids.get(row));
						new HotelPage(current_user_,id);
						stats_page_.dispose();
					}
				}
			}
		});
			
		}
	}
	private void cheapestHotel()
	{


		String[] table_titles = {"ID", "Name", "Price", "Destination", "Rating"};
		
		/*try {
			hotel_list_ = DatabaseManagement.getHotels();
		} catch (Exception ex) {
			System.out.println("Database error in getHotels Stats: " + ex);
		}*/
		
		// remove when there is hotel_data
		Object[][] hotel_list_ = {
				{"Hotel test", "Graz", "162", "32", "Golf"},
				{"Hotel test", "Graz", "50", "32", "Golf"},
				{"Hotel test", "Graz", "49", "32", "Golf"},
				{"Hotel test", "Graz", "78", "32", "Golf"},
				{"Hotel test", "Graz", "555", "32", "Golf"},
		};
		//
		//Sorting according to lowest price
		int erase_index = 0;
		Object[][] sorted = new Object[5][5];
		ArrayList<Object[]> hots = new ArrayList<Object[]>(Arrays.asList(hotel_list_));
		for(int i = 0; i<5; i++)
		{
			int new_lowest = 10000;
			int index = 0;
			for (Object[] temp : hots) {
				
				if(new_lowest >= Integer.parseInt((String)temp[2]))
				{
					sorted[i] = temp;
					new_lowest = Integer.parseInt((String)temp[2]);
					erase_index = index;
				}
				index ++;
			}
			hots.remove(erase_index);			
		}
		//
		
		hotels_table_ = new JTable(new DefaultTableModel(sorted, table_titles));
		Formatting.resizeColumnWidth(hotels_table_);
		hotels_table_.setPreferredScrollableViewportSize(new Dimension(450,350));
		hotels_table_.setFillsViewportHeight(true);
		hotels_table_.setDefaultEditor(Object.class, null);
		hotels_table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hotels_scrollpane = new JScrollPane(hotels_table_);
		hotels_scrollpane.setPreferredSize(new Dimension(100,150));
		stats_panel_.add(hotels_scrollpane, "center, span 10, grow");
	}
	private void expensiviestHotel(Boolean first_call)
	{
		String[] table_titles = {"ID", "Name", "Price", "Destination", "Rating"};
		ArrayList<Object[]> presorted = new ArrayList<Object[]>();
		// remove following when there is hotel_data
		Object[][] hotel_list_ = {
				{"1", "Hotel test", "162", "Graz", "Golf"},
				{"2", "Hotel test", "44", "Salzburg", "Golf"},
				{"4", "Hotel test", "32", "Judendorf", "Golf"},
				{"3", "Hotel test", "222", "Eisenerz", "Golf"},
				{"5", "Hotel test", "55", "Eisenstadt", "Golf"},
				{"6", "Hotel test", "162", "Graz", "Golf"},
				{"7", "Hotel test", "66", "Salzburg", "Golf"},
				{"8", "Hotel test", "33", "Judendorf", "Golf"},
				{"9", "Hotel test", "55", "Eisenerz", "Golf"},
				{"10", "Hotel test", "666", "Eisenstadt", "Golf"}
				};
		//
		//presort the list according to destinations
		if(chosen_destination_ != "")
		{
			for (Object[] temp : hotel_list_) {
				
				if(chosen_destination_ == (String)temp[3])
				{
					presorted.add(temp);
				}
			}
		}
		else
		{
			presorted = new ArrayList<Object[]>(Arrays.asList(hotel_list_));
		}
		//
		//Sorting according to highest price
		int erase_index = 0;
		Object[][] sorted = new Object[presorted.size()][5];
		
		for(int i = 0; i<3; i++)
		{
			if(i==presorted.size())
			{
				break;
			}
			int new_highest = 0;
			int index = 0;
			for (Object[] temp : presorted) {
				
				if(new_highest <= Integer.parseInt((String)temp[2]))
				{
					sorted[i] = temp;
					new_highest = Integer.parseInt((String)temp[2]);
					erase_index = index;
				}
				index ++;
			}
				presorted.remove(erase_index);			
		}
		//
		System.out.println((String)sorted[0][2]+ "presorted.size() = "+presorted.size());
		System.out.println((String)sorted[1][2]);
		
		hotels_table_ = new JTable(new DefaultTableModel(sorted, table_titles));
		Formatting.resizeColumnWidth(hotels_table_);
		hotels_table_.setPreferredScrollableViewportSize(new Dimension(450,350));
		hotels_table_.setFillsViewportHeight(true);
		hotels_table_.setDefaultEditor(Object.class, null);
		hotels_table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hotels_scrollpane = new JScrollPane(hotels_table_);
		hotels_scrollpane.setPreferredSize(new Dimension(100,150));	
		stats_panel_.add(destinations_, "wrap, center, grow");
		stats_panel_.add(hotels_scrollpane, "center, span 10, grow");		
	}
	private void highestAmountCustomerEval()
	{
		JToolBar destinations = new JToolBar();
		destinations.setRollover(true);
		JComboBox<String[]> destination_array = new JComboBox(new String[] { "overall", "dest1"});
        destinations.add(destination_array);
        stats_panel_.add(destinations, "wrap, center, grow");
	}
	private void hotelWithBestOverall()
	{
		
	}
	private void hotelWithWorstOverall()
	{
		
	}
	private void highLowNumberEval()
	{
		
	}
	private void customerWithHighInterest()
	{
		
	}
	private void destinationWithInterest()
	{
		
	}
	
	
	public Stats(String stat, User user) {
		current_user_ = user;
		stats_page_ = new JFrame(stat);
		stats_page_.setLayout(new GridBagLayout());
		stats_page_.setSize(550, 550);
		stats_panel_ = new JPanel();
		
		initComponents();
		
		switch(stat) {
		case "Cheapest hotels": 						
				stats_panel_= new JPanel(new MigLayout("wrap", "[500!]", "100 [150!][25!][500!][25!]")); 
				cheapestHotel();
			break;
		case "Most expensive hotels": 					
			stats_panel_= new JPanel(new MigLayout("wrap", "[500!]", "[25!][100!][25!][500!][25!]"));
			expensiviestHotel(true);
			break;
		case "Most evaluated hotels": 					highestAmountCustomerEval();
			break;
		case "Highest rated hotels":		hotelWithBestOverall();
			break;
		case "Worst rated hotels":	hotelWithWorstOverall();
			break;
		case "Most & least evaluating customers":	highLowNumberEval();
			break;
		case "Customers interested in destination supporting themes":
														customerWithHighInterest();
			break;
		case "Destinations supporting interest themes": destinationWithInterest();
			break;
		default:
				break;
		}
		initListeners();
		//stats_panel.add(main_page_button, "span 2, center, grow");
		stats_panel_.add(back_button_, "wrap, center, grow");
		stats_page_.add(stats_panel_);
		
		
		stats_page_.setLocationRelativeTo(null); //Centers the window
		stats_page_.setResizable(false);
		stats_page_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		stats_page_.setVisible(true);	
		
	}
}
