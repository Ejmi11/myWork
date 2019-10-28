public class User {
	public enum UserType {
	    ADMIN, CUSTOMER 
	}
	private String first_name;
	private String last_name;
	private String username;
	private String password;
	private UserType user_type;
	private int user_id;
	private int checked_notifications;
	
	public User(String username_, String first_name_, 
			String last_name_, String password_, String type, int id_, int notifications_)
	{
		username = username_;
		first_name = first_name_;
		last_name = last_name_;
		password = password_;
		user_id = id_;
		if(type == null) {
			user_type = UserType.CUSTOMER;
		}
		else if(type.equals("customer")) {
			user_type = UserType.CUSTOMER;
		}
		else {
			user_type = UserType.ADMIN;
		}
		checked_notifications = notifications_;
	}
	
	public void update_information(String first_name_, 
			String last_name_, String password_) {
		first_name = first_name_;
		last_name = last_name_;
		password = password_;
		return;
	}
	
	// Getters
	public String getUsername()
	{
		return username;
	}
	
	public String getFirstName()
	{
		return first_name;
	}
	
	public String getLastName()
	{
		return last_name;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public UserType getStatus()
	{
		return user_type;
	}
	
	public int getId()
	{
		return user_id;
	}
	
	public int getCheckedNotifications() 
	{
		return checked_notifications;
	}
	
	
	// Setters
	public void setUsername(String username_)
	{
		username = username_;
	}
	
	public void setFirstName(String first_name_)
	{
		first_name = first_name_;
	}
	
	public void setLastName(String last_name_)
	{
		last_name = last_name_;
	}
	
	public void setSatus(UserType type_)
	{
		user_type = type_;
	}
	
	public void setPassword(String password_)
	{
		password = password_;
	}
	
	public void setCheckedNotifications(int checked) 
	{
		checked_notifications = checked;
	}
	
}
