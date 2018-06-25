/**
 * 
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * La classe Connection gestisce la connessione con il server.
 */
class Connection {
	/**
	 * L'URL del server remoto.
	 */
	final private String remoteUrl = "http://mape-env.35smckpbkd.us-east-2.elasticbeanstalk.com/";
	/**
	 * l'URL della servlet.
	 */
	final private String servletUrl = "Servlet";
	/**
	 * Il timeout per la connessione con il server, settato a 2000 ms.
	 */
	final private int connectTimeout = 2000;

	/**
	 * Restituisce lo stream di dati di input della connessione.
	 * 
	 * @param parameters
	 *            I parametri da inviare al server remoto tramite protocollo HTTP.
	 * @return lo stream di dati di input della connessione
	 * @throws ServerConnectionFailedException
	 */
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
