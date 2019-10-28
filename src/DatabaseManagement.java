//Adapted from: https://www.youtube.com/watch?v=KRhv4iPgzHE

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseManagement {

	// Updates a user updates their information
	public static void update_user_information(String username, String first_name, 
			String last_name, String new_password) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE users "
					+ "SET first_name = '" + first_name + "', "
					+ "last_name = '" + last_name + "', "
					+ "password = '" + new_password + "' WHERE username = '" + username + "'");
			statement.executeUpdate();
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in update_user_information: " + e);
		}
		return;
	}
	
	
	// Updates the users notification read status
	public static void update_user_notification(String username, int new_status) 
			throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE users SET notification = "
					+ "'" + new_status + "' WHERE username = '" + username + "'");
			statement.executeUpdate();
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in update_user_notification_status: " + e);
			throw e;
		}
	}
	
	
	// Inserts a new notification in the database and resets the notification status of all users
	public static void new_notification(String content) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO "
					+ "notifications(content, created) VALUES ('" + content + "', CURDATE())");
			statement.executeUpdate();
			statement = con.prepareStatement("UPDATE users SET notification = 0");
			statement.executeUpdate();
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in new_notification: " + e);
		}
	}
	
	
	// Returns the latest (by date) notification in the database
	// Returns an empty string if no notification is found or an error occured
	public static String getLatestNotification() throws Exception {
		String notification_content = "";
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM notifications");
			ResultSet results = statement.executeQuery();
			if(results.next() == false){
				return notification_content;
			}
			results.last();
			notification_content = results.getString("content");
			con.close();
			return notification_content;
		}
		catch(Exception e) {
			System.out.println("Database error in getLatestNotification: " + e);
		}
		return notification_content;
	}
	
	
	// Login check
	// returns users id
	public static int check_login(String username, String password) throws Exception {
		try {
			PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM users "
					+ "WHERE (username LIKE '" 
					+ username + "' AND password LIKE '" + password + "')");
			ResultSet results = statement.executeQuery();
		
			if(results.next()) {
				int result = results.getInt("id");
				statement.close();
				return result;
			}
			else {
				statement.close();
				return 0;
			}	
		}
		catch(Exception e) {
			System.out.println("Database error in check_login: " + e);
		}
		return 0;
	}
	
	
	// Insert a user into the database
	// returns 0 if the user was added successfully 
	// returns -1 if there was an error
	public static int insert_user(String first_name, String last_name, String username, 
			String password, String status) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO users(first_name, last_name, username, password, status, "
					+ "notification) VALUES ('" + first_name + "','" + last_name + "','" 
					+ username + "','" + password + "','" + status + "','0')");
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception e) {
			System.out.println("Database error in insert_user: " + e);
		}
		return -1;
	}
	
	// Insert a hotel into the database
	// returns 0 if the hotel was added successfully 
	// returns -1 if there was an error
	public static int insert_hotel(String name, String description, String price, 
			String destination_id, String activities) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO hotels(name, description, price, destination_id) "
					+ "VALUES ('" + name + "', '" + description + "', '" + price + "', '" 
					+ destination_id + "');");
			statement.executeUpdate();

			statement = con.prepareStatement("SELECT `id` FROM `hotels` WHERE `name` ='"+name+"' AND `destination_id`='"+destination_id+"';");
			ResultSet results = statement.executeQuery();
			results.next();
			int id = results.getInt("id");
			String[] acts = activities.split(",");
			String query = "INSERT INTO `supported_activities` (`hotel_id`,`activity`) VALUES ";
			for(int i =0; i < acts.length; i++)
			{
				query += "('" + id + "','" + acts[i] + "')";
				if (i == acts.length -1) query += ";";
				else query +=",";
			}
			statement = con.prepareStatement(query);
			statement.executeUpdate();

			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in insert_hotel: " + e);
		}
		return -1;
	}
	
	
	//Create the database tables if they aren't created already
	// TODO: encrypt user passwords 
	public static void createTable() throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement;
			//country table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS countries("
					+ "id INT NOT NULL AUTO_INCREMENT, name varchar(255) NOT NULL, "
					+ "population BIGINT NOT NULL, capital varchar(255) NOT NULL, "
					+ "PRIMARY KEY(id), UNIQUE KEY(name)) ENGINE=INNODB");
			statement.executeUpdate();
			//destinations table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS destinations("
					+ "id INT NOT NULL AUTO_INCREMENT, name varchar(255) NOT NULL, "
					+ "country varchar(255) NOT NULL, PRIMARY KEY(id), INDEX (country), "
					+ "FOREIGN KEY (country) REFERENCES countries(name) ON DELETE CASCADE)"
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			//users table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "users(id INT NOT NULL AUTO_INCREMENT, first_name varchar(255) NOT NULL, "
					+ "last_name varchar(255) NOT NULL, username varchar(255) NOT NULL, "
					+ "password varchar(255) NOT NULL, status varchar(64) NOT NULL, "
					+ "notification TINYINT(1) NOT NULL, "
					+ "PRIMARY KEY(id), UNIQUE KEY(username)) ENGINE=INNODB");
			statement.executeUpdate();
			//hotels table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS hotels("
					+ "id INT NOT NULL AUTO_INCREMENT, name varchar(255) NOT NULL, "
					+ "description TEXT NOT NULL, price INT NOT NULL, "
					+ "destination_id INT, rating INT, PRIMARY KEY(id), "
					+ "FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE SET NULL) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			//evaluations table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS evaluations("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "username varchar(255) NOT NULL, hotel_id INT NOT NULL, "
					+ "rating INT NOT NULL, nights_stayed INT, description TEXT, "
					+ "PRIMARY KEY(id), "
					+ "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, "
					+ "FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			//activities table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS activities("
					+ "name varchar(255) NOT NULL, PRIMARY KEY(name)) ENGINE=INNODB");
			statement.executeUpdate();
			//themes table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS themes("
					+ "name varchar(255) NOT NULL, description TEXT, "
					+ "PRIMARY KEY(name)) ENGINE=INNODB");
			statement.executeUpdate();
			//supported activites
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS supported_activities("
					+ "hotel_id INT NOT NULL, activity varchar(255) NOT NULL, "
					+ "CONSTRAINT no_duplicate_activities UNIQUE (hotel_id, activity), "
					+ "INDEX (hotel_id), INDEX (activity), "
					+ "FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (activity) REFERENCES activities(name) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			//supported themes table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS supported_themes("
					+ "destination_id INT NOT NULL, theme varchar(255) NOT NULL, "
					+ "rating INT, "
					+ "CONSTRAINT no_duplicate_themes UNIQUE (destination_id, theme), "
					+ "INDEX (destination_id), INDEX (theme), "
					+ "FOREIGN KEY (destination_id) REFERENCES destinations(id) "
					+ "ON DELETE CASCADE, "
					+ "FOREIGN KEY (theme) REFERENCES themes(name) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// activity evaluations table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS activity_evaluations("
					+ "evaluation_id INT NOT NULL, activity varchar(255) NOT NULL, "
					+ "username varchar(255) NOT NULL, rating INT NOT NULL,"
					+ "CONSTRAINT one_activity_evaluation UNIQUE (evaluation_id, activity, username), "
					+ "INDEX (evaluation_id), INDEX (activity), INDEX(username), "
					+ "FOREIGN KEY (evaluation_id) REFERENCES evaluations(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (activity) REFERENCES activities(name) ON DELETE CASCADE, "
					+ "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// notifications table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ "notifications(id INT NOT NULL AUTO_INCREMENT, content TEXT, "
					+ "created DATE NOT NULL, PRIMARY KEY(id)) ENGINE=INNODB");
			statement.executeUpdate();
			// messages table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS messages(id INT "
					+ "NOT NULL AUTO_INCREMENT, sender varchar(255) NOT NULL, "
					+ "recipient varchar(255) NOT NULL, sent DATE NOT NULL, content TEXT NOT NULL, "
					+ "PRIMARY KEY(id), INDEX (sender), INDEX (recipient), "
					+ "FOREIGN KEY(sender) REFERENCES users(username) ON DELETE CASCADE, "
					+ "FOREIGN KEY(recipient) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// admin/support messages table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS admin_messages("
					+ "id INT NOT NULL AUTO_INCREMENT, sender varchar(255) NOT NULL, "
					+ "content TEXT NOT NULL, sent DATE NOT NULL, resolved TINYINT(1) NOT NULL, "
					+ "PRIMARY KEY(id), INDEX (sender), "
					+ "FOREIGN KEY(sender) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// friends list table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS friends("
					+ "friend1 varchar(255) NOT NULL, friend2 varchar(255) NOT NULL, "
					+ "CONSTRAINT one_friend_pair UNIQUE (friend1, friend2), INDEX (friend1), "
					+ "INDEX (friend2), "
					+ "FOREIGN KEY(friend1) REFERENCES users(username) ON DELETE CASCADE, "
					+ "FOREIGN KEY(friend2) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// activity preferences table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS activity_preference("
					+ "activity varchar(255) NOT NULL, user varchar(255) NOT NULL, "
					+ "preference INT NOT NULL, "
					+ "CONSTRAINT one_activity_preference_per_user UNIQUE (activity, user), "
					+ "INDEX (activity), INDEX (user), "
					+ "FOREIGN KEY(activity) REFERENCES activities(name) ON DELETE CASCADE, "
					+ "FOREIGN KEY(user) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			// theme preferences table
			statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS theme_preference("
					+ "theme varchar(255) NOT NULL, user varchar(255) NOT NULL, "
					+ "preference INT NOT NULL, "
					+ "CONSTRAINT one_theme_preference_per_user UNIQUE (theme, user), "
					+ "INDEX (theme), INDEX (user), "
					+ "FOREIGN KEY(theme) REFERENCES themes(name) ON DELETE CASCADE, "
					+ "FOREIGN KEY(user) REFERENCES users(username) ON DELETE CASCADE) "
					+ "ENGINE=INNODB");
			statement.executeUpdate();
			con.close();
			System.out.println("Sucessfully created:");
		} 
		catch (Exception e) {
			System.out.println("Database error in create table: " + e);
		}
	}
	
	//Drops the old user/hotel tables in the database
	//Only call this if there is a significant change in the database structure
	public static void resetTables() throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SET FOREIGN_KEY_CHECKS = 0");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS users");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS hotels");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS evaluations");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS activities");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS themes");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS destinations");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS expert_evaluations");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS supported_activities");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS countries");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS supported_themes");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS activity_evaluations");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS notifications");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS messages");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS friends");
			statement.executeUpdate();
			statement = con.prepareStatement("DROP TABLE IF EXISTS admin_messages");
			statement.executeUpdate();
			statement = con.prepareStatement("SET FOREIGN_KEY_CHECKS = 1");
			statement.executeUpdate();
			con.close();
			System.out.println("Sucessfully dropped tables:");
		}
		catch(Exception e) {
			System.out.println("Database error in drop old tables: " + e);
		}
	}
	//Inserts a list of 75 hotels into the database (only run this once)
		public static void insert_hotels() throws Exception {
			try {
				Connection con = getConnection();
				PreparedStatement statement = con.prepareStatement(
						"INSERT INTO hotels (name, description, price, destination_id, rating) VALUES" + 
								"('Hotel B&B Graz', 'Das im Jahr 2016 renovierte B&B Graz begrüßt Sie in zentraler Lage in Graz, nur 3 Gehminuten vom Hauptbahnhof Graz sowie 10 Gehminuten von der Altstadt und dem Kunsthaus entfernt.', '70', '2', '4.5'), "+ 
								

								"(' NH Graz City', 'Neueröffnung im August 2018. Das im Zentrum von Graz gelegene NH Graz City bietet"
								+ " klimatisierte Zimmer mit Privatparkplätzen und Zimmerservice', '69', '2', '4.9'), "+
								
				
								"('Hotel Strasser', 'Dieses traditionsreiche Hotel liegt nur 3 Gehminuten "
								+ "vom Grazer Hauptbahnhof und einen 10-minütigen Spaziergang von der Altstadt entfernt', '69', '2', '3.9'), " +
								
								"('Grand Hotel Wiesler', 'Das Hotel Wiesler Graz liegt direkt am Mur in der Altstadt von Graz und verfügt "
								+ "über eine Sauna, einen Fitnessbereich und eine Laden, in dem man sich den Bart rasieren lassen kann. ',"
								+ " '81', '2', '3.8'), " +
								
								"('Hotel Goldener Hirsch', 'as Hotel Goldener Hirsch - a Luxury Collection Hotel, Salzburg befindet sich in "
								+ "der berühmten Getreidegasse der Salzburger Altstadt, direkt gegenüber vom Festspielhaus.', '100', '3', '4.9'), " +
								
								"('Altstadt Hotel Hofwirt Salzburg', 'Das 1878 erbaute Altstadt Hotel Hofwirt begrüßt Sie direkt neben der"
								+ "Linzergasse, einer Fußgängerzone im Zentrum von Salzburg.', '90', '3', '4'), " +
								
								"('Pension Jahn', 'Die familiengeführte Pension Jahn befindet sich nur 300 m vom Salzburger Hauptbahnhof und"
								+ " einem 10-minütigen Spaziergang vom Schloss Mirabell und dem Kongresszentrum entfernt.', '85', '3', '3.9'), " +
								
								"('Best Western Premier Hotel Astoria', 'In einem ruhigen Viertel mitten im Zentrum von Zagreb und nur 300 m "
								+ "vom Hauptbahnhof entfernt erwarten Sie im eleganten Hotel Astoria kostenfreie Parkplätze und kostenfreies WLAN. '"
								+ ", '55', '5', '4'), " +
								
								"('Hotel Center', 'Das vorteilhaft gelegene Hotel Central trennen nur wenige Schritte vom Hauptbahnhof in Zagreb."
								+ "Es bietet klimatisierte Zimmer mit kostenfreiem WLAN.', '66', '5', '3.5'), " +
								
								"('Hotel Dubrovnik', 'Am Hauptplatz im Herzen von Zagreb empfängt Sie das traditionsreiche Hotel Dubrovnik, "
								+ "das seit 1929 seine Gäste begrüßt.', '77', '5', '3'), " +
								
								"('Star Inn Hotel Linz', 'Das Star Inn Hotel Linz Promenadengalerien, by Comfort erwartet Sie direkt im Zentrum "
								+ "von Linz, nur 200 m vom Mariendom entfernt. Die Zimmer verfügen alle über einen Flachbild-TV und ein eigenes Bad.',"
								+ " '77', '6', '5'), " +
								
								"('Courtyard by Marriott Linz', 'Dieses 4-Sterne-Superior-Hotel befindet sich neben dem Design Center (Kongresshalle) "
								+ "und einen 15-minütigen Spaziergang von der Altstadt, der Donau sowie dem Linzer Hauptbahnhof entfernt. ', "
								+ "'88', '6', '4.5')," +
								
								"('Austria Trend Hotel Schillerpark Linz', 'Im Zentrum von Linz empfängt Sie das Austria Trend Hotel Schillerpark"
								+ " Linz 12 Gehminuten vom Hauptplatz, 10 Gehminuten vom Linzer Hauptbahnhof und 300 m vom Musiktheater entfernt. ', "
								+ "'55', '6', '3')," +
								
								"('Trans World Hotel Donauwelle', 'Das im August 2018 neu renovierte Hotel Donauwelle liegt am Ufer der Donau "
								+ "und bietet einen Panoramablick. Alle Zimmer verfügen über einen LCD-TV und kostenfreien Zugang zum Spa- "
								+ "und Fitnesscenter.', '65', '6', '4.5')," +
								
								
								"('Hotel Emonec', 'Located in the heart of Ljubljana’s pedestrian area, Hotel Emonec offers rooms with free WiFi. "
								+ "Facilities include self-service laundry and ironing. Prešeren Square is just 150 metres away.', '55', '7', '5')," +
								
								"('City Hotel Ljubljana', 'Das City Hotel Ljubljana begrüßt Sie im Herzen von Ljubljana, nur 300 m vom zentralen "
								+ "Prešerenplatz entfernt und bietet ein à-la-carte-Restaurant und eine Lobbybar.', '44', '7', '4.8')," +
								
								"('Austria Trend Hotel Ljubljana', 'Das Austria Trend Hotel Ljubljana erwartet Sie mit einem Spa und Wellnesscenter, "
								+ "hauseigenen Restaurants sowie elegant eingerichteten, geräumigen Zimmern in Ljubljana, nur 500 m von der "
								+ "Autobahnausfahrt Ljubljana-Bežigrad und 2,5 km von der Messe Ljubljana entfernt.', '56', '7', '4')," +
								
								"('Best Western Premier Hotel Slon', 'Im Stadtzentrum von Ljubljana bietet Ihnen das Best Western Premier "
								+ "Hotel Slon helle, farbenfroh gestaltete Zimmer, einen Wellnessbereich, ein elegantes Restaurant sowie "
								+ "eine bekannte Konditorei.', '57', '7', '3')," +
								
								"('City Hotel Ljubljana', 'Das City Hotel Ljubljana begrüßt Sie im Herzen von Ljubljana, nur 300 m vom zentralen "
								+ "Prešerenplatz entfernt und bietet ein à-la-carte-Restaurant und eine Lobbybar.', '57', '7', '2.8')," +
								
								"(' Aparthotel Meriton Suites Zetland', 'Das Meriton Suites Zetland bietet Ihnen luxuriöse Unterkünfte zur "
								+ "Selbstverpflegung nur 5 Fahrminuten von den Cafés der lebhaften Danks Street entfernt.', '85', '8', '5')," +
								
								"('Hyatt Regency Sydney', 'Das Hyatt Regency Sydney begrüßt Sie neben dem Hafenviertel Darling Harbour im Herzen"
								+ " des zentralen Geschäftsviertels von Sydney. Sie wohnen hier im größten gehobenen Hotel Australiens."
								+ "', '95', '8', '4.8')," +
								
								"('Siesta Sydney', 'Das Hostel Siesta Sydney erwartet Sie im zentralen Geschäftsviertel von Sydney, 15 Gehminuten"
								+ " von den wichtigsten Sehenswürdigkeiten von Sydney entfernt', '100', '8', '4.5')," +
								
								"('Meriton Suites Mascot Central', 'Das Meriton Suites Mascot Central begrüßt Sie nur 10 Fahrminuten vom "
								+ "Inlandsflughafen und 15 Fahrminuten vom internationalen Flughafen entfernt.', '80', '8', '3.5')," +
								
								"('Park Inn by Radisson Berlin Alexanderplatz', 'Dieses 4-Sterne-Superior-Hotel erwartet Sie direkt am "
								+ "Alexanderplatz mit 3 Restaurants, einem Wellnessbereich sowie klimatisierten Zimmern mit einem Flachbild-TV.'"
								+ ", '90', '9', '5')," +
								
								"('H2 Hotel Berlin-Alexanderplatz', 'Dieses moderne Hotel begrüßt Sie in idealer Lage im Herzen von Berlin, "
								+ "nur 3 Gehminuten vom Alexanderplatz und dem kultigen Fernsehturm entfernt.', '80', '9', '4')," +
								
								"('RIU Plaza Berlin am Kurfürstendamm', 'Dieses Hotel befindet sich in einem modernen 18-stöckigen Gebäude"
								+ " am Berliner Kurfürstendamm. ', '75', '9', '3.5')," +
								
								"('Hotel Gat Point Charlie', 'Dieses Designhotel begrüßt Sie im zentralen Berliner Stadtteil Mitte, "
								+ "nur 50 m vom Checkpoint Charlie und der Einkaufsmeile Friedrichstraße entfernt.', '70', '9', '3.6')," +
								
								"('Leonardo Hotel Berlin Mitte', 'Dieses neu eröffnete Hotel begrüßt Sie im Herzen von Berlin, nur 100 m"
								+ " vom Bahnhof Berlin Friedrichstraße entfernt.', '70', '9', '3.8')," +
								
								"('Hilton Los Angeles Airport', 'Dieses Hotel bietet einen großen Außenpool und einen Whirlpool sowie "
								+ "einen kostenfreien 24-Stunden-Shuttleservice zum Flughafen Los Angeles International.', '100', '10', '5')," +
								
								"('Sheraton Gateway Los Angeles', 'Dieses moderne Hotel empfängt Sie mit einem kostenfreien 24-Stunden-"
								+ "Shuttleservice zum nur 3 Fahrminuten entfernten internationalen Flughafen Los Angeles.', '90', '10', '4.5')," +
								
								"('Four Points by Sheraton Los Angeles International Airport', 'Nur 1,5 km vom internationalen Flughafen Los "
								+ "Angeles entfernt begrüßt Sie dieses Hotel, das Ihnen einen kostenfreien 24-Stunden-Flughafentransfer bietet.'"
								+ ", '55', '10', '4')," +
								
								"('Walk of Fame Hostel', 'Das Walk of Fame Hostel begrüßt Sie am Walk of Fame des Hollywood Boulevards. "
								+ "Freuen Sie sich auf Unterkünfte mit kostenfreiem WLAN.', '65', '10', '3.6')," +
								
								"(' Lotte City Hotel Myeongdong', 'Das Lotte City Hotel Myeongdong liegt 3 Gehminuten vom Ausgang 1 des "
								+ "U-Bahnhofs Euljiro3-ga (Linien 2 und 3) entfernt und bietet ein hauseigenes Fitnesscenter sowie ein Café.'"
								+ ", '60', '11', '5')," +
								
								"('Lotte Hotel Seoul', 'Das Luxushotel ist mit der U-Bahn-Station Euljiro 1ga (Linie 2) und dem Lotte "
								+ "Department Store verbunden. Freuen Sie sich auf 10 gastronomische Einrichtungen sowie Zimmer mit "
								+ "einem LCD-TV. ', '100', '11', '4')," +
								
								"('Ramada Hotel & Suites by Wyndham Seoul Namdaemun', 'Das Ramada Hotel genießt eine verkehrsgünstige "
								+ "Lage im Stadtteil Jung-Gu und bietet geräumige Zimmer mit einem luxuriösen Bad.', '99', '11', '4.5')," +
								
								"('Nine Tree Premier Hotel Myeongdong 2', 'Mit einem hauseigenen Buffetrestaurant, einer Bar und einem "
								+ "Fitnesscenter empfängt Sie das Nine Tree Premier Hotel Myeongdong 2 in einer günstigen Lage.', "
								+ "'88', '11', '4.6')," +
								
								"('Hotel WBF Gojo Omiya', 'Das Hotel WBF Gojo Omiya in Kyoto liegt 2,6 km vom internationalen Manga-"
								+ "Museum Kyoto entfernt und bietet Ihnen klimatisierte Zimmer.', '100', '12', '5')," +
								
								"('Centurion Cabin & Spa Kyoto', 'Das im Jahre 2016 eröffnete Centurion Cabin & Spa Kyoto erwartet "
								+ "Sie im Herzen von Kyoto. Es bietet Ihnen Kapselzimmer sowie geräumige, rund um die Uhr "
								+ "geöffnete öffentliche Bäder mit einer Sauna.', '98', '12', '4.6')," +
								
								"('Hotel Keihan Kyoto Grande', 'Das Hotel Keihan Kyoto ist durch eine Unterführung direkt mit dem JR-"
								+ "Bahnhof Kyoto verbunden. Es bietet Massagen und eine 24-Stunden-Rezeption. Die modernen Zimmer "
								+ "umfassen kostenfreies WLAN.', '80', '12', '4.6')," +
								
								"('APA Hotel Kyoto-eki Horikawa-Dori', 'Das APA Hotel Kyoto-eki Horikawa-Dori befindet sich in "
								+ "einer günstigen Lage, 10 Gehminuten vom Bahnhof Kyoto und 5 Fahrminuten von den Tempeln Higashi "
								+ "und Nishi Hongan-ji entfernt.', '81', '12', '3')," +
								
								"('Fairmont Nile City', 'Dieses 5-Sterne-Hotel in den Nile City Towers am Nil bietet Ihnen eine "
								+ "Dachterrasse mit einem Pool und einer spektakulären Aussicht über Kairo bis hin zu den Pyramiden.'"
								+ ", '150', '13', '5')," +
								
								"('Le Meridien Pyramids Hotel & Spa', 'Das Le Méridien Pyramids Hotel & Spa begrüßt Sie mit einem"
								+ " unvergleichlichen Ausblick auf die majestätischen Pyramiden von Gizeh, die nur 1 km entfernt sind.'"
								+ ", '100', '13', '4')," +
								
								"('Marriott Mena House, Cairo', 'Das Marriott Mena House, Cairo bietet Blick auf die Großen "
								+ "Pyramiden von Gizeh und ist von einem 40 Hektar großen, grünen Garten umgeben. Freuen Sie sich "
								+ "auf ein Spa, ein Fitnesscenter und einen Pool.', '160', '13', '4.5')," +
								
								"('Safir Hotel Cairo', 'Das Safir Hotel in Kairos Innenstadt ist 10 Gehminuten vom Nildamm entfernt"
								+ " und bietet luxuriöse Zimmer mit Balkon, einen Außenpool und kostenfreies WLAN in den öffentlichen "
								+ "Bereichen.', '100', '13', '4.5')," +
								
								"('Cabinn City', 'Nur 5 Gehminuten vom Kopenhagener Hauptbahnhof und dem Vergnügungspark Tivoli "
								+ "entfernt erwartet Sie dieses Budget-Hotel mit kostenfreiem WLAN.', '80', '15', '5')," +
								
								"('AC Hotel by Marriott Bella Sky Copenhagen', 'Dieses Hotel befindet sich neben dem Kongresszentrum "
								+ "Bella Center in Kopenhagen und bietet Ihnen eine unverwechselbare und moderne Architektur.', "
								+ "'89', '15', '4')," +
								
								"('First Hotel Twentyseven', 'Nur 3 Gehminuten vom Vergnügungs- und Erholungspark Tivoli entfernt"
								+ " bietet dieses Boutique-Hotel in der beliebten Stadt Kopenhagen Designerzimmer mit einem Flachbild-"
								+ "TV.', '70', '15', '4')," +
								
								"('Tivoli Hotel', 'Dieses Hotel liegt am Kopenhagener Kanal und nur 10 Gehminuten vom Hauptbahnhof "
								+ "Kopenhagen entfernt.', '80', '15', '3')," +
								
								"('Ez Aclimação Hotel', 'Nur 1 km von São Paulos Park Aclimação und dem Einkaufszentrum Pátio Paulista"
								+ " entfernt erwartet Sie das Ez Aclimação mit einem Pool, einer Sauna, einem Fitnesscenter, "
								+ "2 Restaurants und einer Bar.', '60', '16', '4.4')," +
								
								"('Bourbon Convention Ibirapuera', 'Das Bourbon Convention Ibirapuera erwartet Sie mit Agilität"
								+ "und qualitativem Service.', '70', '16', '4')," +
								
								"('La Residence Paulista', 'Im Herzen von Jardins, dem vornehmsten Viertel von São Paulo, "
								+ "erwartet Sie nur 2 Häuserblöcke von der Avenida Paulista entfernt das La Residence.'"
								+ ", '80', '16', '3.5')," +
								
								"('Hotel Boulevard Inn São Paulo', 'Das Hotel Boulevard Inn São Paulo erwartet Sie in São Paulo "
								+ "als eine Unterkunft mit kostenfreiem WLAN. Vom Zentrum der Stadt São Paulo trennen Sie 400 m.',"
								+ " '90', '16', '3')," +
								
								"('Grand Millennium Beijing', 'Das luxuriöse Grand Millennium befindet sich in eleganter Lage am "
								+ "Beijing Fortune Plaza nahe dem neuen CCTV-Hauptbüro. Es verfügt über einen Innenpool,"
								+ " Spa-Service und 4 Restaurants.', '100', '17', '4')," +
								
								"('New World Beijing Hotel', 'Das New World Beijing Hotel liegt etwa 15 Gehminuten vom "
								+ "Himmelstempel entfernt und bietet Zimmer mit WLAN.', '99', '17', '3')," +
								
								"('Nanluogu Lane CitiGO Court Hotel in Beijing', 'Das Nanluogu Lane citigo Court Hotel in "
								+ "Peking bietet klimatisierte Zimmer mit Flachbild-TV im Viertel Dongcheng in Peking.', '80', '17', '2')," +
								
								"('CHAO Sanlitun Beijing', 'Die Unterkunft CHAO Sanlitun Beijing ist ein hervorragendes Refugium mit "
								+ "ausgezeichneten Unterkünften und empfängt Sie im Herzen von Sanlitun', '85', '17', '4.4')," +
								
								"('Fairmont Monte Carlo', 'Das 4-Sterne-Hotel Fairmont Monte Carlo mit einer Dachterrasse mit Aussicht "
								+ "auf die Rennstrecke Circuit de Monaco begrüßt Sie zwischen dem Mittelmeer und dem Casino von Monte Carlo.',"
								+ " '110', '21', '4')," +
								
								"('Le Méridien Beach Plaza', 'Das Le Méridien Beach Plaza begrüßt Sie mit Blick auf Monte Carlo und das"
								+ " Mittelmeer.', '100', '21', '3')," +
								
								"('Port Palace', 'Das Port Palace ist ein 4-Sterne-Boutique-Hotel in Monaco mit Blick auf den Hafen Port "
								+ "Hercule.', '90', '21', '4')," +
								
								"('Monte Carlo View ', 'Das Monte Carlo View begrüßt Sie in Monte Carlo, 500 m vom Casino Monte Carlo und "
								+ "14 Gehminuten vom Palast des Prinzen von Monaco entfernt in einer Gegend, in der Sie wandern können.',"
								+ " '90', '21', '3')");
				statement.executeUpdate();
				con.close();
				System.out.println("Success!");
			}
			catch(Exception e) {
				System.out.println("Database error in insert_hotels: " + e);
			}
		}
	//Inserts a list of all countries into the database (only run this once)
	public static void insert_countries() throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO countries (name, population, capital) VALUES" + 
					"('Afghanistan', '29121286', 'Kabul'), " + 
					"('Albania', '2986952', 'Tirana'), " + 
					"('Algeria', '34586184', 'Algiers'), " + 
					"('American Samoa', '57881', 'Pago Pago'), " + 
					"('Andorra', '84000', 'Andorra la Vella'), " + 
					"('Angola', '13068161', 'Luanda'), " + 
					"('Anguilla', '13254', 'The Valley'), " + 
					"('Antigua and Barbuda', '86754', 'St. Johns'), " + 
					"('Argentina', '41343201', 'Buenos Aires'), " + 
					"('Armenia', '2968000', 'Yerevan'), " + 
					"('Aruba', '71566', 'Oranjestad'), " + 
					"('Australia', '21515754', 'Canberra'), " + 
					"('Austria', '8205000', 'Vienna'), " + 
					"('Azerbaijan', '8303512', 'Baku'), " + 
					"('Bahamas', '301790', 'Nassau'), " + 
					"('Bahrain', '738004', 'Manama'), " + 
					"('Bangladesh', '156118464', 'Dhaka'), " + 
					"('Barbados', '285653', 'Bridgetown'), " + 
					"('Belarus', '9685000', 'Minsk'), " + 
					"('Belgium', '10403000', 'Brussels'), " + 
					"('Belize', '314522', 'Belmopan'), " + 
					"('Benin', '9056010', 'Porto-Novo'), " + 
					"('Bermuda', '65365', 'Hamilton'), " + 
					"('Bhutan', '699847', 'Thimphu'), " + 
					"('Bolivia', '9947418', 'Sucre'), " + 
					"('Bonaire', '18012', 'Kralendijk'), " + 
					"('Bosnia and Herzegovina', '4590000', 'Sarajevo'), " + 
					"('Botswana', '2029307', 'Gaborone'), " + 
					"('Brazil', '201103330', 'Brasilia'), " + 
					"('British Virgin Islands', '21730', 'Road Town'), " + 
					"('Brunei', '395027', 'Bandar Seri Begawan'), " + 
					"('Bulgaria', '7148785', 'Sofia'), " + 
					"('Burkina Faso', '16241811', 'Ouagadougou'), " + 
					"('Burundi', '9863117', 'Bujumbura'), " + 
					"('Cambodia', '14453680', 'Phnom Penh'), " + 
					"('Cameroon', '19294149', 'Yaounde'), " + 
					"('Canada', '33679000', 'Ottawa'), " + 
					"('Cape Verde', '508659', 'Praia'), " + 
					"('Cayman Islands', '44270', 'George Town'), " + 
					"('Central African Republic', '4844927', 'Bangui'), " + 
					"('Chad', '10543464', 'NDjamena'), " + 
					"('Chile', '16746491', 'Santiago'), " + 
					"('China', '1330044000', 'Beijing'), " + 
					"('Christmas Island', '1500', 'Flying Fish Cove'), " + 
					"('Cocos [Keeling] Islands', '628', 'West Island'), " + 
					"('Colombia', '47790000', 'Bogota'), " + 
					"('Comoros', '773407', 'Moroni'), " + 
					"('Cook Islands', '21388', 'Avarua'), " + 
					"('Costa Rica', '4516220', 'San Jose'), " + 
					"('Croatia', '4284889', 'Zagreb'), " + 
					"('Cuba', '11423000', 'Havana'), " + 
					"('Curacao', '141766', 'Willemstad'), " + 
					"('Cyprus', '1102677', 'Nicosia'), " + 
					"('Czechia', '10476000', 'Prague'), " + 
					"('Democratic Republic of the Congo', '70916439', 'Kinshasa'), " + 
					"('Denmark', '5484000', 'Copenhagen'), " + 
					"('Djibouti', '740528', 'Djibouti'), " + 
					"('Dominica', '72813', 'Roseau'), " + 
					"('Dominican Republic', '9823821', 'Santo Domingo'), " + 
					"('East Timor', '1154625', 'Dili'), " + 
					"('Ecuador', '14790608', 'Quito'), " + 
					"('Egypt', '80471869', 'Cairo'), " + 
					"('El Salvador', '6052064', 'San Salvador'), " + 
					"('Equatorial Guinea', '1014999', 'Malabo'), " + 
					"('Eritrea', '5792984', 'Asmara'), " + 
					"('Estonia', '1291170', 'Tallinn'), " + 
					"('Ethiopia', '88013491', 'Addis Ababa'), " + 
					"('Falkland Islands', '2638', 'Stanley'), " + 
					"('Faroe Islands', '48228', 'Torshavn'), " + 
					"('Fiji', '875983', 'Suva'), " + 
					"('Finland', '5244000', 'Helsinki'), " + 
					"('France', '64768389', 'Paris'), " + 
					"('French Guiana', '195506', 'Cayenne'), " + 
					"('French Polynesia', '270485', 'Papeete'), " + 
					"('French Southern Territories', '140', 'Port-aux-Francais'), " + 
					"('Gabon', '1545255', 'Libreville'), " + 
					"('Gambia', '1593256', 'Bathurst'), " + 
					"('Georgia', '4630000', 'Tbilisi'), " + 
					"('Germany', '81802257', 'Berlin'), " + 
					"('Ghana', '24339838', 'Accra'), " + 
					"('Gibraltar', '27884', 'Gibraltar'), " + 
					"('Greece', '11000000', 'Athens'), " + 
					"('Greenland', '56375', 'Nuuk'), " + 
					"('Grenada', '107818', 'St. Georges'), " + 
					"('Guadeloupe', '443000', 'Basse-Terre'), " + 
					"('Guam', '159358', 'Hagatna'), " + 
					"('Guatemala', '13550440', 'Guatemala City'), " + 
					"('Guernsey', '65228', 'St Peter Port'), " + 
					"('Guinea', '10324025', 'Conakry'), " + 
					"('Guinea-Bissau', '1565126', 'Bissau'), " + 
					"('Guyana', '748486', 'Georgetown'), " + 
					"('Haiti', '9648924', 'Port-au-Prince'), " + 
					"('Honduras', '7989415', 'Tegucigalpa'), " + 
					"('Hong Kong', '6898686', 'Hong Kong'), " + 
					"('Hungary', '9982000', 'Budapest'), " + 
					"('Iceland', '308910', 'Reykjavik'), " + 
					"('India', '1173108018', 'New Delhi'), " + 
					"('Indonesia', '242968342', 'Jakarta'), " + 
					"('Iran', '76923300', 'Tehran'), " + 
					"('Iraq', '29671605', 'Baghdad'), " + 
					"('Ireland', '4622917', 'Dublin'), " + 
					"('Isle of Man', '75049', 'Douglas'), " + 
					"('Israel', '7353985', 'Jerusalem'), " + 
					"('Italy', '60340328', 'Rome'), " + 
					"('Ivory Coast', '21058798', 'Yamoussoukro'), " + 
					"('Jamaica', '2847232', 'Kingston'), " + 
					"('Japan', '127288000', 'Tokyo'), " + 
					"('Jersey', '90812', 'Saint Helier'), " + 
					"('Jordan', '6407085', 'Amman'), " + 
					"('Kazakhstan', '15340000', 'Astana'), " + 
					"('Kenya', '40046566', 'Nairobi'), " + 
					"('Kiribati', '92533', 'Tarawa'), " + 
					"('Kosovo', '1800000', 'Pristina'), " + 
					"('Kuwait', '2789132', 'Kuwait City'), " + 
					"('Kyrgyzstan', '5776500', 'Bishkek'), " + 
					"('Laos', '6368162', 'Vientiane'), " + 
					"('Latvia', '2217969', 'Riga'), " + 
					"('Lebanon', '4125247', 'Beirut'), " + 
					"('Lesotho', '1919552', 'Maseru'), " + 
					"('Liberia', '3685076', 'Monrovia'), " + 
					"('Libya', '6461454', 'Tripoli'), " + 
					"('Liechtenstein', '35000', 'Vaduz'), " + 
					"('Lithuania', '2944459', 'Vilnius'), " + 
					"('Luxembourg', '497538', 'Luxembourg'), " + 
					"('Macao', '449198', 'Macao'), " + 
					"('Macedonia', '2062294', 'Skopje'), " + 
					"('Madagascar', '21281844', 'Antananarivo'), " + 
					"('Malawi', '15447500', 'Lilongwe'), " + 
					"('Malaysia', '28274729', 'Kuala Lumpur'), " + 
					"('Maldives', '395650', 'Mali'), " + 
					"('Mali', '13796354', 'Bamako'), " + 
					"('Malta', '403000', 'Valletta'), " + 
					"('Marshall Islands', '65859', 'Majuro'), " + 
					"('Martinique', '432900', 'Fort-de-France'), " + 
					"('Mauritania', '3205060', 'Nouakchott'), " + 
					"('Mauritius', '1294104', 'Port Louis'), " + 
					"('Mayotte', '159042', 'Mamoudzou'), " + 
					"('Mexico', '112468855', 'Mexico City'), " + 
					"('Micronesia', '107708', 'Palikir'), " + 
					"('Moldova', '4324000', 'Chisinau'), " + 
					"('Monaco', '32965', 'Monaco'), " + 
					"('Mongolia', '3086918', 'Ulan Bator'), " + 
					"('Montenegro', '666730', 'Podgorica'), " + 
					"('Montserrat', '9341', 'Plymouth'), " + 
					"('Morocco', '33848242', 'Rabat'), " + 
					"('Mozambique', '22061451', 'Maputo'), " + 
					"('Myanmar [Burma]', '53414374', 'Naypyitaw'), " + 
					"('Namibia', '2128471', 'Windhoek'), " + 
					"('Nauru', '10065', 'Yaren'), " + 
					"('Nepal', '28951852', 'Kathmandu'), " + 
					"('Netherlands', '16645000', 'Amsterdam'), " + 
					"('New Caledonia', '216494', 'Noumea'), " + 
					"('New Zealand', '4252277', 'Wellington'), " + 
					"('Nicaragua', '5995928', 'Managua'), " + 
					"('Niger', '15878271', 'Niamey'), " + 
					"('Nigeria', '154000000', 'Abuja'), " + 
					"('Niue', '2166', 'Alofi'), " + 
					"('Norfolk Island', '1828', 'Kingston'), " + 
					"('North Korea', '22912177', 'Pyongyang'), " + 
					"('Northern Mariana Islands', '53883', 'Saipan'), " + 
					"('Norway', '5009150', 'Oslo'), " + 
					"('Oman', '2967717', 'Muscat'), " + 
					"('Pakistan', '184404791', 'Islamabad'), " + 
					"('Palau', '19907', 'Melekeok'), " + 
					"('Palestine', '3800000', 'Ramallah / East Jerusalem'), " + 
					"('Panama', '3410676', 'Panama City'), " + 
					"('Papua New Guinea', '6064515', 'Port Moresby'), " + 
					"('Paraguay', '6375830', 'Asuncion'), " + 
					"('Peru', '29907003', 'Lima'), " + 
					"('Philippines', '99900177', 'Manila'), " + 
					"('Pitcairn Islands', '46', 'Adamstown'), " + 
					"('Poland', '38500000', 'Warsaw'), " + 
					"('Portugal', '10676000', 'Lisbon'), " + 
					"('Puerto Rico', '3916632', 'San Juan'), " + 
					"('Qatar', '840926', 'Doha'), " + 
					"('Republic of the Congo', '3039126', 'Brazzaville'), " + 
					"('Romania', '21959278', 'Bucharest'), " + 
					"('Russia', '140702000', 'Moscow'), " + 
					"('Rwanda', '11055976', 'Kigali'), " + 
					"('Reunion', '776948', 'Saint-Denis'), " + 
					"('Saint Bartholemy', '8450', 'Gustavia'), " + 
					"('Saint Helena', '7460', 'Jamestown'), " + 
					"('Saint Kitts and Nevis', '51134', 'Basseterre'), " + 
					"('Saint Lucia', '160922', 'Castries'), " + 
					"('Saint Martin', '35925', 'Marigot'), " + 
					"('Saint Pierre and Miquelon', '7012', 'Saint-Pierre'), " + 
					"('Saint Vincent and the Grenadines', '104217', 'Kingstown'), " + 
					"('Samoa', '192001', 'Apia'), " + 
					"('San Marino', '31477', 'San Marino'), " + 
					"('Saudi Arabia', '25731776', 'Riyadh'), " + 
					"('Senegal', '12323252', 'Dakar'), " + 
					"('Serbia', '7344847', 'Belgrade'), " + 
					"('Seychelles', '88340', 'Victoria'), " + 
					"('Sierra Leone', '5245695', 'Freetown'), " + 
					"('Singapore', '4701069', 'Singapore'), " + 
					"('Sint Maarten', '37429', 'Philipsburg'), " + 
					"('Slovakia', '5455000', 'Bratislava'), " + 
					"('Slovenia', '2007000', 'Ljubljana'), " + 
					"('Solomon Islands', '559198', 'Honiara'), " + 
					"('Somalia', '10112453', 'Mogadishu'), " + 
					"('South Africa', '49000000', 'Pretoria'), " + 
					"('South Georgia and the South Sandwich Islands', '30', 'Grytviken'), " + 
					"('South Korea', '48422644', 'Seoul'), " + 
					"('South Sudan', '8260490', 'Juba'), " + 
					"('Spain', '46505963', 'Madrid'), " + 
					"('Sri Lanka', '21513990', 'Colombo'), " + 
					"('Sudan', '35000000', 'Khartoum'), " + 
					"('Suriname', '492829', 'Paramaribo'), " + 
					"('Svalbard and Jan Mayen', '2550', 'Longyearbyen'), " + 
					"('Swaziland', '1354051', 'Mbabane'), " + 
					"('Sweden', '9828655', 'Stockholm'), " + 
					"('Switzerland', '7581000', 'Bern'), " + 
					"('Syria', '22198110', 'Damascus'), " + 
					"('Sao Toma and Principe', '175808', 'Sao Tome'), " + 
					"('Taiwan', '22894384', 'Taipei'), " + 
					"('Tajikistan', '7487489', 'Dushanbe'), " + 
					"('Tanzania', '41892895', 'Dodoma'), " + 
					"('Thailand', '67089500', 'Bangkok'), " + 
					"('Togo', '6587239', 'Lome'), " +
					"('Tonga', '122580', 'Nuku alofa'), " + 
					"('Trinidad and Tobago', '1328019', 'Port of Spain'), " + 
					"('Tunisia', '10589025', 'Tunis'), " + 
					"('Turkey', '77804122', 'Ankara'), " + 
					"('Turkmenistan', '4940916', 'Ashgabat'), " + 
					"('Turks and Caicos Islands', '20556', 'Cockburn Town'), " + 
					"('Tuvalu', '10472', 'Funafuti'), " + 
					"('U.S. Virgin Islands', '108708', 'Charlotte Amalie'), " + 
					"('Uganda', '33398682', 'Kampala'), " + 
					"('Ukraine', '45415596', 'Kiev'), " + 
					"('United Arab Emirates', '4975593', 'Abu Dhabi'), " + 
					"('United Kingdom', '62348447', 'London'), " + 
					"('United States', '310232863', 'Washington'), " + 
					"('Uruguay', '3477000', 'Montevideo'), " + 
					"('Uzbekistan', '27865738', 'Tashkent'), " + 
					"('Vanuatu', '221552', 'Port Vila'), " + 
					"('Vatican City', '921', 'Vatican City'), " + 
					"('Venezuela', '27223228', 'Caracas'), " + 
					"('Vietnam', '89571130', 'Hanoi'), " + 
					"('Wallis and Futuna', '16025', 'Mata-Utu'), " + 
					"('Western Sahara', '273008', 'Layoune / El Aaiun'), " + 
					"('Yemen', '23495361', 'Sanaa'), " + 
					"('Zambia', '13460305', 'Lusaka'), " + 
					"('Zimbabwe', '13061000', 'Harare')");
			statement.executeUpdate();
			con.close();
			System.out.println("Success!");
		}
		catch(Exception e) {
			System.out.println("Database error in insert countries: " + e);
		}
	}
	
	
	// Connects to database
	public static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://82.220.34.6:3306/oad";
			//String url = "jdbc:mysql://127.0.0.1:3306/oad"; // for local testing
			String username = "oad_admin";
			String password = "oad12345";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
		}
		catch(Exception e) {
			System.out.println("Database error in connection: "+ e);
		}
		return null;
	}
	
	// Checks if there are any users in the "users" database; 
	// returns true if there are no users, false otherwise
	public static boolean first_user_check() throws Exception {
		boolean returnvalue = false;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM users");
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				returnvalue = true;
			}
			else {
				returnvalue = false;
			}
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in first user check: "+ e);
		}
		return returnvalue;
	}
	
	// Checks if a username is already found in the "users" database
	// returns true if no same username is found, false if there is one
	public static boolean unique_username_check(String username) throws Exception {
		boolean returnvalue = false;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM " 
					+ "users WHERE (username = '" + username + "')");
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				returnvalue = true;
			}
			else {
				returnvalue = false;
			}
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in unique username check: "+ e);
		}
		return returnvalue;
	}
	
	// Returns the last used/occupied id in a database 
	public static int last_id(String database) throws Exception {
		int returnvalue = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM " + database);
			ResultSet results = statement.executeQuery();	
			while(results.next()) {
				returnvalue++;
			}
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in find next id: "+ e);
		}
		return returnvalue;
	}
	
	// Creates and returns a user by pulling the information from the database
	public static User create_user(int user_id) throws Exception {
		User user = null;
		String username;
		String first_name;
		String last_name;
		String password;
		String type;
		int notifications;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE id = "
					+ user_id);
			ResultSet results = statement.executeQuery();	
			results.next();
			username = results.getString("username");
			first_name = results.getString("first_name");
			last_name = results.getString("last_name");
			password = results.getString("password");
			type = results.getString("status");
			notifications = results.getInt("notification");
			con.close();
			user = new User(username, first_name, last_name, password, type, user_id, notifications);
		}
		catch(Exception e) {
			System.out.println("Database error in create user: "+ e);
		}
		return user;
	}

	
	// Changes the status of a user to admin
	// returns -1 if no user with the entered username was found 
	// returns -2 if the user is already a admin
	// returns -3 if an error occurred,
	// returns 0 if the status was updated successfully 
	public static Integer make_admin(String username) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE username = '"
					+ username + "'");
			ResultSet results = statement.executeQuery();	
			if(results.next() == false) {
				con.close();
				return -1;
			}
			else {
				if("admin".equals(results.getString("status"))) {
					con.close();
					return -2;
				}
				statement = con.prepareStatement("UPDATE users SET status = 'admin' WHERE username = '" 
						+ username + "'");
				statement.executeUpdate();
				con.close();
				return 0;
			}
		}
		catch(Exception e) {
			System.out.println("Database error in make admin: " + e);
		}
		return -3;
	}
	
	// Changes the status of a user to customer
	// returns -1 if no user with the entered username was found 
	// returns -2 if the user is already a customer
	// returns -3 if an error occurred,
	// returns 0 if the status was updated successfully 
	public static Integer make_customer(String username) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE username = '"
					+ username + "'");
			ResultSet results = statement.executeQuery();	
			if(results.next() == false) {
				con.close();
				return -1;
			}
			else {
				if("customer".equals(results.getString("status"))) {
					con.close();
					return -2;
				}
				statement = con.prepareStatement("UPDATE users SET status = 'customer' WHERE username = '" 
						+ username + "'");
				statement.executeUpdate();
				con.close();
				return 0;
			}
		}
		catch(Exception e) {
			System.out.println("Database error in make customer: " + e);
		}
		return -3;
	}
	
	public static int countHotelEvaluations(int hotelID) throws Exception {
		Connection con = getConnection();
		PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM evaluations WHERE hotel_id = '"+ String.valueOf(hotelID) +"';");
		ResultSet results = statement.executeQuery();
		results.next();
		int cnt = results.getInt("COUNT(*)");
		con.close();
		return cnt;
	}

	public static Object[] getFirstHotelEvaluation(int hotelID) throws Exception {
		Connection con = getConnection();
		PreparedStatement statement = con.prepareStatement("SELECT * FROM evaluations " +
				"INNER JOIN users ON evaluations.username = users.username " +
				"WHERE hotel_id = '"+ String.valueOf(hotelID) +"' LIMIT 1;");
		ResultSet results = statement.executeQuery();
		if (!results.next())
			return null;
		Object[] retval = new Object[5];
		retval[0] = results.getString("users.first_name");
		retval[1] = results.getString("users.last_name");
		retval[2] = results.getInt("evaluations.rating");
		retval[3] = results.getInt("evaluations.nights_stayed");
		retval[4] = results.getString("evaluations.description");
		con.close();
		return retval;
	}
	public static void AddHotelEvaluation(int hotel_id, String nights, String description, String rating, User user) throws Exception {
		Connection con = getConnection();
		PreparedStatement statement;
		if(!nights.equals("0")) {
			statement = con.prepareStatement("INSERT INTO evaluations " +
					"(`username`,`hotel_id`,`rating`,`nights_stayed`,`description`) VALUES " +
					"('"+user.getUsername()+"','"+hotel_id+"','"+rating+"','"+nights+"','"+description+"');");
		}
		else {
			statement = con.prepareStatement("INSERT INTO evaluations " +
					"(`username`,`hotel_id`,`rating`,`nights_stayed`,`description`) VALUES " +
					"('"+user.getUsername()+"','"+hotel_id+"','"+rating+"', NULL ,'"+description+"');");
		}
		statement.executeUpdate();
		statement = con.prepareStatement(
				"UPDATE hotels SET rating = (SELECT AVG(rating)FROM evaluations\n" +
						"WHERE `hotel_id` = '"+hotel_id+"') WHERE id = '"+hotel_id+"';");
		statement.executeUpdate();
		con.close();
	}

	public static void deleteHotel(int hotel_id) throws Exception {
		Connection con = getConnection();
		PreparedStatement statement = con.prepareStatement("DELETE FROM hotels WHERE id = '"+hotel_id+"';");
		statement.executeUpdate();
		con.close();
	}
	public static Object[] getHotel(int id)
	{
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels INNER JOIN destinations " +
					"ON hotels.destination_id = destinations.id " +
					"INNER JOIN countries ON destinations.country = countries.name " +
					"WHERE hotels.id = '" + id + "'");
			ResultSet results = statement.executeQuery();
			results.next();
			Object[] retval = new Object[8];
			retval[0] = results.getString("hotels.name");
			retval[1] = results.getString("hotels.description");
			retval[2] = results.getInt("hotels.price");
			retval[3] = results.getDouble("hotels.rating");
			retval[4] = results.getString("destinations.name");
			retval[5] = results.getString("destinations.country");
			retval[6] = results.getString("countries.capital");
			retval[7] = results.getInt("countries.population");
			con.close();
			return retval;
		}
		catch(Exception ex) {
			System.out.println("Database error in getHotelID: " + ex);
		}
		return null;
	}
	public static int getHotelID(String name, String city, String price)
	{
		int id = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT hotels.id FROM hotels " +
					"INNER JOIN destinations ON hotels.destination_id = destinations.id WHERE hotels.name = '"
					+ name + "' AND destinations.name = '" + city + "' AND hotels.price = '" + price + "'");
			ResultSet results = statement.executeQuery();
			results.next();
			id = results.getInt("hotels.id");
			con.close();
			return id;
		}
		catch(Exception ex) {
			System.out.println("Database error in getHotelID: " + ex);
		}
		return id;
	}

	public static void updateHotel(int hotelID,String name, String description, String price, String activities) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement(
					"UPDATE hotels SET name = '"+name+"', description = '"+description+"', price = '"+price+"' " +
							"WHERE id = '"+hotelID+"';");
			statement.executeUpdate();

			statement = con.prepareStatement(
					"DELETE FROM supported_activities WHERE hotel_id = '"+hotelID+"';");
			statement.executeUpdate();

			String[] acts = activities.split(",");
			String query = "INSERT INTO `supported_activities` (`hotel_id`,`activity`) VALUES ";
			for(int i =0; i < acts.length; i++)
			{
				query += "('" + hotelID + "','" + acts[i] + "')";
				if (i == acts.length -1) query += ";";
				else query +=",";
			}
			statement = con.prepareStatement(query);
			statement.executeUpdate();

			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in insert_hotel: " + e);
		}
	}
	
	public static Object[][] getHotels() throws Exception {
		Object[][] hotel_list = new Object[0][5];
		int index = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels");
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index++;
			}
			results.beforeFirst();
			hotel_list = new Object[index][5];
			index = 0;
			while(results.next()) {
				hotel_list[index][0] = results.getString("id");
				hotel_list[index][1] = results.getString("name");
				hotel_list[index][2] = results.getInt("price");
				hotel_list[index][3] = results.getInt("destination_id");
				hotel_list[index][4] = results.getDouble("rating");
				
				index++;
			}
			con.close();
			return hotel_list;
		}
		catch(Exception e) {
			System.out.println("Database error in getHotels: " + e);
		}
		return hotel_list;
	}
	// Displays hotels for the mainPage
	public static Object[][] getHotelsToDisplay() throws Exception {
		Object[][] hotel_list = new Object[0][5];
		int index = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels INNER JOIN destinations ON hotels.destination_id = destinations.id");
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index++;
			}
			results.beforeFirst();
			hotel_list = new Object[index][5];
			index = 0;
			while(results.next()) {
				hotel_list[index][0] = results.getString("name");
				hotel_list[index][1] = results.getString("destinations.name");
				hotel_list[index][2] = results.getDouble("rating");
				hotel_list[index][3] = results.getInt("price");

				/*
				statement = con.prepareStatement("SELECT COUNT(*) FROM evaluations WHERE id = '"+ results.getInt("id") +"';");
				ResultSet results2 = statement.executeQuery();
				results2.next();
				hotel_list[index][4] = results2.getInt("COUNT(*)");
				*/

				statement = con.prepareStatement("SELECT activity FROM supported_activities WHERE hotel_id = '"+ results.getInt("id") +"';");
				ResultSet results2 = statement.executeQuery();
				String activities = "";
				while(results2.next())
					activities += results2.getString("activity")+", ";

				if(activities.length() > 0)
					activities = activities.substring(0,activities.length()-2);

				hotel_list[index][4] = activities;

				index++;
			}
			con.close();
			return hotel_list;
		}
		catch(Exception e) {
			System.out.println("Database error in getHotels: " + e);
		}
		return hotel_list;
	}
	
	
	public static Object[][] getHotelsBySearchAndOrder(String search_value, 
			String sort_method) throws Exception {
		Object[][] hotels = {{}};
		try {
			int hotels_found = 0;
			int index = 0;
			String formatted_search_value = "%" + search_value + "%";
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels "
					+ "INNER JOIN destinations ON hotels.destination_id = destinations.id "
					+ "WHERE (destinations.NAME LIKE ?) OR (hotels.name LIKE ?) " 
					+ sort_method + " LIMIT 50");
			statement.setString(1, formatted_search_value);
			statement.setString(2, formatted_search_value);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				hotels_found++;
			}
			results.beforeFirst();
			hotels = new Object[hotels_found][5];
			while(results.next()) {
				hotels[index][0] = results.getString("name");
				hotels[index][1] = results.getString("destinations.name");
				hotels[index][2] = results.getDouble("rating");
				hotels[index][3] = results.getInt("price");
	
				statement = con.prepareStatement("SELECT activity FROM supported_activities "
						+ "WHERE hotel_id = '" + results.getInt("id") + "';");
				ResultSet results2 = statement.executeQuery();
				String activities = "";
				while (results2.next())
					activities += results2.getString("activity") + ", ";
				if (activities.length() > 0)
					activities = activities.substring(0, activities.length() - 2);
	
				hotels[index][4] = activities;
				index++;
			}
			con.close();
			return hotels;
		}
		catch(Exception ex) {
			System.out.println("Database error in getHotelsBySearchAndOrder: " + ex);
		}
		return hotels;
	}
	
	
	public static Object[][] getHotelsAndOrder(String sort_method) throws Exception {
		Object[][] hotels = {{}};
		try {
			int hotels_found = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels "
					+ "INNER JOIN destinations ON hotels.destination_id = destinations.id "
					+ sort_method + " LIMIT 50");
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				hotels_found++;
			}
			results.beforeFirst();
			hotels = new Object[hotels_found][5];
			while(results.next()) {
				hotels[index][0] = results.getString("name");
				hotels[index][1] = results.getString("destinations.name");
				hotels[index][2] = results.getDouble("rating");
				hotels[index][3] = results.getInt("price");
	
				statement = con.prepareStatement("SELECT activity FROM supported_activities "
						+ "WHERE hotel_id = '" + results.getInt("id") + "';");
				ResultSet results2 = statement.executeQuery();
				String activities = "";
				while (results2.next())
					activities += results2.getString("activity") + ", ";
				if (activities.length() > 0)
					activities = activities.substring(0, activities.length() - 2);
	
				hotels[index][4] = activities;
				index++;
			}
			con.close();
			return hotels;
		}
		catch(Exception ex) {
			System.out.println("Database error in getHotelsAndOrder: " + ex);
		}
		return hotels;
	}
	
	
	// Get cheapest hotel from a certain destination
	public static Object[][] getHotelSearchLowestPrice(String destinationCriteria) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hotels INNER JOIN destinations ON hotels.destination_id = destinations.id WHERE destinations.NAME = '" + destinationCriteria + "' ORDER BY hotels.price ASC LIMIT 1;");
			ResultSet results = statement.executeQuery();
			results.next();
			Object[][] retval = new Object[1][6];

			retval[0][0] = results.getString("name");
			retval[0][1] = results.getString("destinations.name");
			retval[0][2] = results.getDouble("rating");
			retval[0][3] = results.getInt("price");

			statement = con.prepareStatement("SELECT COUNT(*) FROM evaluations WHERE id = '" + results.getInt("id") + "';");
			ResultSet results2 = statement.executeQuery();
			results2.next();
			retval[0][4] = results2.getInt("COUNT(*)");

			statement = con.prepareStatement("SELECT activity FROM supported_activities WHERE hotel_id = '" + results.getInt("id") + "';");
			results2 = statement.executeQuery();
			String activities = "";
			while (results2.next())
				activities += results2.getString("activity") + ", ";

			if (activities.length() > 0)
				activities = activities.substring(0, activities.length() - 2);

			retval[0][5] = activities;


			con.close();
			return retval;
		} catch (Exception ex) {
			System.out.println("Database error in getHotelSearchLowestPrice: " + ex);
		}
		return null;
	}


		// Returns a array of the countries in the database with their name, capital and population
	public static Object[][] getCountries() throws Exception {
		Object[][] country_list = new Object[0][3];
		int index = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM countries");
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index++;
			}
			results.beforeFirst();
			country_list = new Object[index][3];
			index = 0;
			while(results.next()) {
				country_list[index][0] = results.getString("name");
				country_list[index][1] = results.getString("capital");
				country_list[index][2] = results.getInt("population");
				index++;
			}
			con.close();
			return country_list;
		}
		catch(Exception e) {
			System.out.println("Database error in getCountries: " + e);
		}
		return country_list;
	}
	
	
	// Returns a two dimensional object array containing the available themes and  the supported 
	// themes of a destination
	public static Object[][] getThemesForSupportedPage(int destination_id) 
			throws Exception {
		Object[][] themes_list = new Object[1][1];
		Object[][] supported_themes_list;
		int themes = 0;
		int supported_themes = 0;
		int themes_index = 0;
		int supported_index = 0;
		String theme_name;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM themes");
			ResultSet themes_results = statement.executeQuery();
			while(themes_results.next()) {
				themes++;
			}
			themes_list = new Object[themes][3];
			themes_results.beforeFirst();
			statement = con.prepareStatement("SELECT * FROM supported_themes WHERE destination_id = '"
					+ destination_id + "'");
			ResultSet supported_results = statement.executeQuery();
			while(supported_results.next()) {
				supported_themes++;
			}
			if(supported_themes == 0) {
				supported_themes_list = new Object[1][2];
				supported_themes_list[0][0] = "";
				supported_themes_list[0][1] = null;
				supported_themes++;
			}
			else {
				supported_themes_list = new Object[supported_themes][2];
				supported_results.beforeFirst();
				while(supported_results.next()) {
					supported_themes_list[supported_index][0] = supported_results.getString("theme");
					if(supported_results.getObject("rating") == null) {
						supported_themes_list[supported_index][1] = null;
					}
					else {
						supported_themes_list[supported_index][1] = supported_results.getInt("rating");
					}
					supported_index++;
				}
			}
			while(themes_results.next()) {
				theme_name = themes_results.getString("name");
				themes_list[themes_index][0] = theme_name;
				for (int x = 0; x < supported_themes; x++) {
					if(supported_themes_list[x][0].equals(theme_name)) {
						themes_list[themes_index][1] = true;
						if(supported_themes_list[x][1] != null) {
							themes_list[themes_index][2] = supported_themes_list[x][1];
							break;
						}
						else {
							themes_list[themes_index][2] = null;
						}
					}
					else {
						themes_list[themes_index][1] = false;
						themes_list[themes_index][2] = null;
					}
				}
				themes_index++;
			}
			con.close();
			return themes_list;
			
		}
		catch(Exception e) {
			System.out.println("Database error in getThemes: " + e);
		}
		return themes_list;
	}
	
	// Returns the Destination id of a destination by searching for it with the name and country
	public static int getDestinationId(String name, String country) throws Exception {
		int id = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM destinations WHERE name = '"
					+ name + "' AND country = '" + country + "'");
			ResultSet results = statement.executeQuery();
			results.next();
			id = results.getInt("id");
			con.close();
			return id;
		}
		catch(Exception ex) {
			System.out.println("Database error in getDestinationId: " + ex);
		}
		return id;
	}
	
	
	// Returns a list of all destinations in the database
	public static Object[][] getDestinations() throws Exception {
		Object[][] data = {{}};
		int index = 0;
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM destinations");
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index++;
			}
			results.beforeFirst();
			data = new Object[index][2];
			index = 0;
			while(results.next()) {
				data[index][0] = results.getString("name");
				data[index][1] = results.getString("country");
				index++;
			}
			con.close();
			return data;
		}
		catch(Exception ex) {
			System.out.println("Database error in getDestination: " + ex);
		}
		return data;
	}
	
	
	// 
	public static Object[][] getFriendsAndNames(String username) throws Exception {
		Object[][] friends = {{}};
		try {
			int friend_amount = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM friends "
					+ "WHERE friend1 = ? OR friend2 = ?");
			statement.setString(1, username);
			statement.setString(2, username);
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				con.close();
				return friends;
			}
			results.beforeFirst();
			while(results.next()) {
				friend_amount++;
			}
			results.beforeFirst();
			friends = new Object[friend_amount][2];
			while(results.next()) {
				if(results.getString("friend1").equals(username)) {
					friends[index][0] = results.getString("friend2");
					friends[index][1] = getFirstName(results.getString("friend2"), con);
				}
				else {
					friends[index][0] = results.getString("friend1");
					friends[index][1] = getFirstName(results.getString("friend1"), con);
				}
				index++;
			}
			con.close();
			return friends;
		}
		catch(Exception ex) {
			System.out.println("Database error in getFriends: " + ex);
		}
		return friends;
	}
	
	// Returns the first name of a user on an already established connection
	// Returns an empty string if no user is found
	public static String getFirstName(String username, Connection con) throws Exception {
		String first_name = "";
		try {
			PreparedStatement statement = con.prepareStatement("SELECT first_name FROM users "
					+ "WHERE username = ?");
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			if(result.next() == false) {
				return first_name;
			}
			first_name = result.getString("first_name");
			return first_name;
		}
		catch(Exception ex) {
			System.out.println("Database error in getFirstName: " + ex);
		}
		return first_name;
	}
	
	
	// 
	public static Object[][] getMessages(String user1, String user2) throws Exception {
		Object[][] messages = {{}};
		try {
			int message_amount = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM messages "
					+ "WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) "
					+ "ORDER BY sent ASC");
			statement.setString(1, user1);
			statement.setString(2, user2);
			statement.setString(3, user2);
			statement.setString(4, user1);
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				return messages;
			}
			results.beforeFirst();
			while(results.next()) {
				message_amount++;
			}
			messages = new Object[message_amount * 3][1];
			results.beforeFirst();
			while(results.next()) {
				messages[index][0] = "Sent by: " + results.getString("sender") + ", on the date: " 
						+ results.getString("sent");
				index++;
				messages[index][0] = results.getString("content");
				index++;
				messages[index][0] = "";
				index++;
			}
			con.close();
			return messages;
		}
		catch(Exception ex) {
			System.out.println("Database error in getMessages: " + ex);
		}
		return messages;
	}
	
	
	// 
	public static Object[][] getSupportMessages() throws Exception {
		Object[][] messages = {{}};
		try {
			int message_amount = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM admin_messages "
					+ "ORDER BY resolved ASC, sent ASC");
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				return messages;
			}
			results.beforeFirst();
			while(results.next()) {
				message_amount++;
			}
			messages = new Object[message_amount][3];
			results.beforeFirst();
			while(results.next()) {
				messages[index][0] = results.getInt("id");
				messages[index][1] = "Sent by: " + results.getString("sender") 
					+ ", on the date: " + results.getString("sent") + "  Message: " 
					+ results.getString("content");
				if(results.getInt("resolved") == 0) {
					messages[index][2] = false;
				}
				else {
					messages[index][2] = true;
				}
				index++;
			}
			con.close();
			return messages;
		}
		catch(Exception ex) {
			System.out.println("Database error in getSupportMessages: " + ex);
		}
		return messages;
	}
	
	
	// 
	public static Object[][] getActivityPreferences(String user) throws Exception {
		Object[][] activity_preferences = {{}};
		int index = 0;
		try {
			String[] activities = getActivities();
			activity_preferences = new Object[activities.length][2];
			for(String activity : activities) {
				activity_preferences[index][0] = activity;
				activity_preferences[index][1] = 5;
				index++;
			}
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM activity_preference "
					+ "WHERE user = ?");
			statement.setString(1, user);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index = 0;
				while(index < activity_preferences.length) {
					if(results.getString("activity").equals(activity_preferences[index][0])) {
						activity_preferences[index][1] = results.getInt("preference");
					}
					index++;
				}
			}
			con.close();
			return activity_preferences;
		}
		catch(Exception ex) {
			System.out.println("Database error in getActivityInterests: " + ex);
		}
		return activity_preferences;
	}
	
	
	
	public static Object[][] getThemesAndDescriptions() throws Exception {
		Object[][] themes = {{}};
		try {
			int theme_amount = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM themes");
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				return themes;
			}
			results.beforeFirst();
			while(results.next()) {
				theme_amount++;
			}
			themes = new Object[theme_amount][2];
			results.beforeFirst();
			while(results.next()) {
				themes[index][0] = results.getString("name");
				themes[index][1] = results.getString("description");
				index++;
			}
			con.close();
			return themes;
		}
		catch(Exception ex) {
			System.out.println("Database error in getThemesAndDescriptions: " + ex);
		}
		return themes;
	}
	
	
	// 
	public static Object[][] getThemePreferences(String user) throws Exception {
		Object[][] theme_preferences = {{}};
		int index = 0;
		try {
			Object[][] themes = getThemesAndDescriptions();
			theme_preferences = new Object[themes.length][3];
			while(index < themes.length) {
				theme_preferences[index][0] = themes[index][0];
				theme_preferences[index][1] = themes[index][1];
				theme_preferences[index][2] = 5;
				index++;
			}
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM theme_preference "
					+ "WHERE user = ?");
			statement.setString(1, user);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				index = 0;
				while(index < theme_preferences.length) {
					if(results.getString("theme").equals(theme_preferences[index][0])) {
						theme_preferences[index][2] = results.getInt("preference");
					}
					index++;
				}
			}
			con.close();
			return theme_preferences;
		}
		catch(Exception ex) {
			System.out.println("Database error in getThemeInterests: " + ex);
		}
		return theme_preferences;
	}
	
	
	public static Object[][] getHotelEvaluations(int hotel_id) throws Exception {
		Object[][] evaluations = {{}};
		try {
			int evaluation_amount = 0;
			int index = 0;
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM evaluations "
					+ "WHERE hotel_id = ?");
			statement.setInt(1, hotel_id);
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				return evaluations;
			}
			results.beforeFirst();
			while(results.next()) {
				evaluation_amount++;
			}
			results.beforeFirst();
			evaluations = new String[evaluation_amount][4];
			while(results.next()) {
				evaluations[index][0] = results.getString("username");
				evaluations[index][3] = results.getString("description");
				evaluations[index][1] = results.getString("rating");
				evaluations[index][2] = results.getString("nights_stayed");
				index++;
			}
			con.close();
			return evaluations;
		}
		catch(Exception ex) {
			
		}
		return evaluations;
	}
	
	
	// Inserts a destination into the database
	// returns -1 if the destination is already present
	// returns 0 if the destination was successfully added
	// returns -2 if an error occurred
	public static int insert_destination(String name, String country) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM destinations WHERE name = '"
					+ name + "' AND country = '" + country + "'");
			ResultSet results = statement.executeQuery();
			if(results.next() == true) {
				return -1;
			}
			statement = con.prepareStatement("INSERT INTO destinations(name, country) VALUES ('"
					+ name + "','" + country + "')");
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception e) {
			System.out.println("Database error in insert_destination: " + e);
		}
		return -2;
	}
	
	// Inserts an activity into the database
	// returns -1 if the activity is already present
	// returns 0 if the activity was successfully added
	// returns -2 if an error occurred
	public static int insert_activity(String name) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM activities WHERE name = '"
					+ name + "'");
			ResultSet results = statement.executeQuery();
			if(results.next() == true) {
				return -1;
			}
			statement = con.prepareStatement("INSERT INTO activities(name) VALUES ('" 
					+ name + "')");
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in insert_activity: " + ex);
		}
		return -2;
	}
	
	
	// Inserts a friend pair into the database
	// Returns 0 if the friend pair was added successfully
	// Returns -1 if the friend could not be found as a user
	// Returns -2 if an error occurred
	public static int insert_friend(String username, String friend) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM users "
					+ "WHERE username = ?");
			statement.setString(1, friend);
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				con.close();
				return -1;
			}
			statement = con.prepareStatement("INSERT INTO friends(friend1, friend2) VALUES (?, ?)");
			statement.setString(1, username);
			statement.setString(2, friend);
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in insert_friend: " + ex);
		}
		return -2;
	}
	
	
	// Inserts a message into the database
	// Returns 0 if the message was added successfully
	// Returns -1 if an error occurred
	public static int insert_message(String sender, String recipient, String message) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO "
					+ "messages(sender, recipient, sent, content) VALUES (?, ?, CURDATE(), ?)");
			statement.setString(1, sender);
			statement.setString(2, recipient);
			statement.setString(3, message);
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in insert_message: " + ex);
		}
		return -1;
	}
	
	
	// Inserts a admin message into the database
	// Returns 0 if the message was added successfully
	// Returns -1 if an error occurred
	public static int insert_admin_message(String sender, String message) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO admin_messages(sender, "
					+ "content, sent, resolved) VALUES (?, ?, CURDATE(), 0)");
			statement.setString(1, sender);
			statement.setString(2, message);
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in insert admin message: " + ex);
		}
		return -1;
	}
	
	
	// Deletes a friend pair from the database
	// Returns 0 if the friend pair was deleted successfully
	// Returns -1 if the friend pair could not be found
	// Returns -2 if an error occurred
	public static int delete_friend(String username, String friend) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM friends "
					+ "WHERE (friend1 = ? AND friend2 = ?) OR (friend1 = ? AND friend2 = ?)");
			statement.setString(1, username);
			statement.setString(2, friend);
			statement.setString(3, friend);
			statement.setString(4, username);
			ResultSet results = statement.executeQuery();
			if(results.next() == false) {
				con.close();
				return -1;
			}
			String friend1 = results.getString("friend1");
			String friend2 = results.getString("friend2");
			statement = con.prepareStatement("DELETE FROM friends "
					+ "WHERE friend1 = ? AND friend2 = ?");
			statement.setString(1, friend1);
			statement.setString(2, friend2);
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in delete_friend: " + ex);
		}
		return -2;
	}
	
	public static boolean hasUserEvaluatedHotel(int hotel_id, String username) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM evaluations "
					+ "WHERE hotel_id = ? AND username = ?");
			statement.setInt(1, hotel_id);
			statement.setString(2, username);
			ResultSet results = statement.executeQuery();
			if(results.next()) {
				con.close();
				return true;
			}
			else {
				con.close();
				return false;
			}
		}
		catch(Exception ex) {
			System.out.println("Database error in hasUserEvaluatedHotel: " + ex);
		}
		return true;
	}
	
	
	
	public static int deleteEvaluation(int hotel_id, String username) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM evaluations "
					+ "WHERE hotel_id = ? AND username = ?");
			statement.setInt(1, hotel_id);
			statement.setString(2, username);
			statement.executeUpdate();
			con.close();
		}
		catch(Exception ex) {
			System.out.println("Database error in deleteEvaluation: " + ex);
		}
		return -2;
	}
	
	
	// Returns the list of all possible activities.
	public static String[] getActivities() throws Exception {
		ArrayList<String> activity_list = new ArrayList<>();
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM activities");
			ResultSet results = statement.executeQuery();


			results.beforeFirst();
			while(results.next()) {
				activity_list.add(results.getString("name"));
			}
			con.close();
		}
		catch(Exception e) {
			System.out.println("Database error in getActivities: " + e);
		}
		String[] ret = new String[activity_list.size()];
		ret = activity_list.toArray(ret);
		return ret;
	}
	
	public static Object[] getHotelActivities(int hotelID)
	{
		ArrayList<String> activities = new ArrayList<>();
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM supported_activities WHERE `hotel_id` = '"+hotelID+"';");
			ResultSet results = statement.executeQuery();

			while(results.next())
				activities.add(results.getString("activity"));

			con.close();
			return activities.toArray();
		}
		catch(Exception ex) {
			System.out.println("Database error in insert_friend: " + ex);
		}
		return new Object[0];
	}

	// Inserts a theme into the database
	// returns -1 if the theme is already present
	// returns 0 if the theme was successfully added
	// returns -2 if an error occurred
	public static int insert_theme(String name, String description) throws Exception {
		try {
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM themes WHERE name = '"
					+ name + "'");
			ResultSet results = statement.executeQuery();
			if(results.next() == true) {
				return -1;
			}
			statement = con.prepareStatement("INSERT INTO themes(name, description) VALUES ('" 
					+ name + "',  ? )");
			statement.setString(1, description); 
			// Use the above to prevent apostrophes in user input to preemptively end a statement
			statement.executeUpdate();
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in insert_theme: " + ex);
		}
		return -2;
	}
	
	
	// updates the supported themes of a destination 
	// returns true if the changes were successful
	// returns false if no changes were made or an error occurred
	public static boolean update_supported_themes(int id, Object[][] changed_data) 
			throws Exception {
		try {
			int changes = changed_data.length;
			if(changes == 0) {
				return false;
			}
			Connection con = getConnection();
			PreparedStatement statement;
			for(int row_index = 0; row_index < changes; row_index++) {
				if(changed_data[row_index][1].equals(false)) {
					statement = con.prepareStatement("DELETE FROM supported_themes WHERE "
							+ "destination_id = ? AND theme = ?");
					statement.setInt(1, id);
					statement.setString(2, (String) changed_data[row_index][0]);
					statement.executeUpdate();
				}
				else {
					statement = con.prepareStatement("SELECT * FROM supported_themes "
							+ "WHERE destination_id = ? AND theme = ?");
					statement.setInt(1, id);
					statement.setString(2, (String) changed_data[row_index][0]);
					ResultSet results = statement.executeQuery();
					boolean object_exists = results.next();
					statement.close();
					if(object_exists) {
						statement = con.prepareStatement("UPDATE supported_themes "
								+ "SET rating = ? WHERE destination_id = ? AND theme = ?");
						if(changed_data[row_index][2] == null) {
							statement.setNull(1, java.sql.Types.INTEGER);
						}
						else {
							statement.setInt(1, (int) changed_data[row_index][2]);
						}
						statement.setInt(2, id);
						statement.setString(3, (String) changed_data[row_index][0]);
						statement.executeUpdate();
						statement.close();
					}
					else {
						statement.close();
						statement = con.prepareStatement("INSERT INTO "
								+ "supported_themes(destination_id, theme, rating) "
								+ "VALUES ( ?, ?, ? )");
						statement.setInt(1, id);
						statement.setString(2, (String) changed_data[row_index][0]); 
						if(changed_data[row_index][2] == null) {
							statement.setNull(3, java.sql.Types.INTEGER);
						}
						else {
							statement.setInt(3, (int) changed_data[row_index][2]);
						}
						statement.executeUpdate();
						statement.close();
					}
				}
			}
			con.close();
			System.out.println("Successfully updated supported themes");
			return true;
		}
		catch(Exception ex) {
			System.out.println("Database error in update_supported_themes: " + ex);
		}
		return false;
	}
	
	
	// updates the resolved status of support messages
	// returns 0 if the changes were made successfully
	// returns -1 if no changes were made
	// returns -2 if an error occurred
	public static int update_support_messages(Object[][] changed_messages) throws Exception {
		try {
			int changes = changed_messages.length;
			if(changes == 0) {
				return -1;
			}
			if(changed_messages[0][0].equals(null)) {
				return -1;
			}
			Connection con = getConnection();
			PreparedStatement statement;
			for(int row_index = 0; row_index < changes; row_index++) {
				statement = con.prepareStatement("UPDATE admin_messages SET resolved = ? "
						+ "WHERE id = ?");
				if(changed_messages[row_index][2].equals(false)) {
					statement.setInt(1, 0);
				}
				else {
					statement.setInt(1, 1);
				}
				statement.setInt(2, (int) changed_messages[row_index][0]);
				statement.executeUpdate();
			}
			con.close();
			System.out.println("Successfully updated support messages");
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in update_support_messages: " + ex);
		}
		return -2;
	}
	
	
	// Updates the country of a destination
	public static void update_destination(String destination, 
			String old_country, String new_country) throws Exception {
		try {
			if(old_country.equals(new_country)) {
				return;
			}
			Connection con = getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE destinations "
					+ "SET country = ? WHERE name = ? AND country = ?");
			statement.setString(1, new_country);
			statement.setString(2, destination);
			statement.setString(3, old_country);
			statement.executeUpdate();
			con.close();
			return;
		}
		catch(Exception ex) {
			System.out.println("Database error in update_destination: " + ex);
		}
	}
	
	
	// updates the users preference of an activity or inserts it if not existent
	// returns 0 if the changes were made successfully
	// returns -1 if no changes were made
	// returns -2 if an error occurred
	public static int update_activity_preferences(String user, 
			Object[][] changed_activity_preferences) throws Exception {
		try {
			int index = 0;
			int changes = changed_activity_preferences.length;
			if(changes == 0) {
				return -1;
			}
			if(changed_activity_preferences[0][0].equals(null)) {
				return -1;
			}
			Connection con = getConnection();
			PreparedStatement statement;
			while(index < changes) {
				statement = con.prepareStatement("INSERT INTO "
						+ "activity_preference(activity, user, preference) VALUES (?, ?, ?) "
						+ "ON DUPLICATE KEY UPDATE preference = ?");
				statement.setString(1, (String) changed_activity_preferences[index][0]);
				statement.setString(2, user);
				statement.setInt(3, (int) changed_activity_preferences[index][1]);
				statement.setInt(4, (int) changed_activity_preferences[index][1]);
				statement.executeUpdate();
				index++;
			}
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in update_activity_preferences: " + ex);
		}
		return -2;
	}
	
	
	// updates the users preference of a theme or inserts it if not existent
	// returns 0 if the changes were made successfully
	// returns -1 if no changes were made
	// returns -2 if an error occurred
	public static int update_theme_preferences(String user, 
			Object[][] changed_theme_preferences) throws Exception {
		try {
			int index = 0;
			int changes = changed_theme_preferences.length;
			if(changes == 0) {
				return -1;
			}
			if(changed_theme_preferences[0][0].equals(null)) {
				return -1;
			}
			Connection con = getConnection();
			PreparedStatement statement;
			while(index < changes) {
				statement = con.prepareStatement("INSERT INTO "
						+ "theme_preference(theme, user, preference) VALUES (?, ?, ?) "
						+ "ON DUPLICATE KEY UPDATE preference = ?");
				statement.setString(1, (String) changed_theme_preferences[index][0]);
				statement.setString(2, user);
				statement.setInt(3, (int) changed_theme_preferences[index][2]);
				statement.setInt(4, (int) changed_theme_preferences[index][2]);
				statement.executeUpdate();
				index++;
			}
			con.close();
			return 0;
		}
		catch(Exception ex) {
			System.out.println("Database error in update_theme_preferences: " + ex);
		}
		return -2;
	}
	
}
