/**
 * 
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author PC
 *
 */
class Connection {
	final private String remoteUrl = "http://mape-env.35smckpbkd.us-east-2.elasticbeanstalk.com/";
	final private String servletUrl = "Servlet";
	final private int connectTimeout = 2000;

	ObjectInputStream getConnectionStream(String parameters) throws ServerConnectionFailedException {
		ObjectInputStream in;
		URL url;
		HttpURLConnection conn;
		try {
			url = new URL(remoteUrl + servletUrl + parameters);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(connectTimeout);
			conn.connect();
			in = new ObjectInputStream(conn.getInputStream());
		} catch (IOException e) {
			throw new ServerConnectionFailedException();
		}
		return in;
	}
}
