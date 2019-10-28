import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WeatherAPI {

	// Returns weather information at a given location
	// Returns "Weather data currently unavailable" if the location can not be found or if other errors occur
	// Returns "Weather: description, Temperature: degrees, Humidity: percent" if location is found
	//
	// Taken and adapted from https://mashtips.com/call-soap-with-request-xml-and-get-response-xml-back/
	public static String getWeather(String location) {
		String weather = "Weather data currently unavailable.";
		String api_call = "http://api.openweathermap.org/data/2.5/weather?q=" 
				+ location + "&type=like&units=metric&APPID=dc9466b19318e4376ff47823c1ecaea3";

		String responseString = "";
		String outputString = "";
		try {
			URL url = new URL(api_call);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection)connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", "");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			OutputStream out = httpConn.getOutputStream();
			//Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			//Ready with sending the request.
			 
			//Read the response.
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			 
			//Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
			}
			if(outputString.contains("\"cod\":\"404\"")) {
				return weather;
			}
			else {
				weather = parseStringForWeather(outputString);
				return weather;
			}
		}
		catch(Exception e) {
			System.out.println("Error in get weather hotel interface: " + e);
		}
		return weather;
	}
	
	
	// Parses the outputString returned by the Weather API for the relevant information
	// Returns "Weather: description, Temperature: degrees, Humidity: percent"
	private static String parseStringForWeather(String string_to_parse) {
		String parsed_string = "";
		
		// Finds and saves the weather description
		int start_weather_description_index = string_to_parse.indexOf("description");
		start_weather_description_index += 14;
		int end_weather_description_index = string_to_parse.indexOf("\"", 
				start_weather_description_index);
		String weather_description = string_to_parse.subSequence(start_weather_description_index, 
				end_weather_description_index).toString();
		
		// Finds and saves the temperature
		int start_temp_index = string_to_parse.indexOf("temp", end_weather_description_index);
		start_temp_index += 6;
		int end_temp_index = string_to_parse.indexOf(",", start_temp_index);
		String temperature = string_to_parse.subSequence(start_temp_index, 
				end_temp_index).toString();
		
		// Finds and saves the humidity
		int start_humidity_index = string_to_parse.indexOf("humidity", end_temp_index);
		start_humidity_index += 10;
		int end_humidity_index = string_to_parse.indexOf(",", start_humidity_index);
		String humidity = string_to_parse.subSequence(start_humidity_index, 
				end_humidity_index).toString();
		
		// Combines and formats the pieces to a single string
		parsed_string = weather_description + ", Temperature: " 
				+ temperature + "°C, Humidity: " + humidity + "%";
		
		return parsed_string;
	}
}
