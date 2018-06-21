/**
 * 
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author PC
 *
 */
class Connection {
	final private String unibaIp = "172.26.243.58";
	final private String hotSpotIp = "192.168.43.254";
	final private String homeIp = "";
	final private String port = "8080";
	final private String servletUrl = "MAPE%20-%20Servlet/Servlet";

	ObjectInputStream getConnectionStream(String parameters) throws ServerConnectionFailedException {
		ObjectInputStream in;
		URL url;
		HttpURLConnection conn;
		try {
			url = new URL("http://" + unibaIp + ":" + port + "/" + servletUrl + parameters);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(2000);
			conn.connect();
			in = new ObjectInputStream(conn.getInputStream());
		} catch (IOException e) {
			try {
				url = new URL("http://" + hotSpotIp + ":" + port + "/" + servletUrl + parameters);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(2000);
				conn.connect();
				in = new ObjectInputStream(conn.getInputStream());
			} catch (IOException e1) {
				try {
					url = new URL("http://" + homeIp + ":" + port + "/" + servletUrl + parameters);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(2000);
					conn.connect();
					in = new ObjectInputStream(conn.getInputStream());
				} catch (IOException e2) {
					throw new ServerConnectionFailedException();
				}
			}
		}
		return in;
	}
}
