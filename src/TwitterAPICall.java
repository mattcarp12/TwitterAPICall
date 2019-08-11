import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

public class TwitterAPICall {
	
	final static String TWITTER_BASE_URL = "https://api.twitter.com";
	final static String TWITTER_AUTH_URL = "/oauth2/token";
	final static String QUERY_BASE_URL = "/1.1/search/tweets.json";
	String bearerToken;
	
	public TwitterAPICall() {
		//authenticate();
	}
	
	private void authenticate() {
		bearerToken = getBearerToken();
	}
	
	private String getBearerToken() {
		
		Map<String, String> credentials = getCredentials();
		String bearerTokenCredential = credentials.get("Key") + ":" + credentials.get("Secret");
		String encodedBearerTokenCredential = Base64.getEncoder().encodeToString(bearerTokenCredential.getBytes());
		
		try {
			HttpsURLConnection authCon = (HttpsURLConnection) new URL(TWITTER_BASE_URL + TWITTER_AUTH_URL).openConnection();
			authCon.setDoOutput(true);
			authCon.setRequestMethod("POST");
			authCon.setRequestProperty("Authorization", "Basic " + encodedBearerTokenCredential);
			authCon.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			OutputStream os = authCon.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    
			osw.write("grant_type=client_credentials");
			osw.flush();
			osw.close();
			os.close();  //don't forget to close the OutputStream
			authCon.connect();
			DataInputStream is = new DataInputStream(authCon.getInputStream());
			for( int c = is.read(); c != -1; c = is.read() ) 
				System.out.print( (char)c ); 
			is.close(); 
		} catch (Exception e) { e.printStackTrace(); }
		return null;
		
	}
	
	private Map<String, String> getCredentials() {
		Map<String, String> credentials = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader("res/twitter_api_credentials"))) {
			credentials.put("Key", br.readLine());
			credentials.put("Secret", br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return credentials;
	}
	
	
	public static void main(String... args) {
		TwitterAPICall call = new TwitterAPICall();
		call.authenticate();
	}
}
