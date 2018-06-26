package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * La classe Kmeans modella l'interfaccia dell'applicazione.
 */
// $
class Kmeans extends Application {
	/**
	 * La tab relativa al calcolo da database.
	 */
	/**
	 * La tab relativa al caricamento.
	 */
	private OutputTab dbTab, loadTab;
	/**
	 * La connessione al server.
	 */
	private Connection connection = new Connection();

	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();
		FlowPane dbTabUp = new FlowPane();
		dbTabUp.setAlignment(Pos.CENTER);
		dbTabUp.setHgap(7);
		TextField tabName = new TextField();
		Label tabNameLabel = new Label("Nome tabella: ");
		TextField nCluster = new TextField();
		Label nClusterLabel = new Label("Num Cluster: ");
		TextField saveName = new TextField();
		Label saveNameLabel = new Label("Nome salvataggio: ");
		dbTabUp.getChildren().add(tabNameLabel);
		dbTabUp.getChildren().add(tabName);
		dbTabUp.getChildren().add(nClusterLabel);
		dbTabUp.getChildren().add(nCluster);
		dbTabUp.getChildren().add(saveNameLabel);
		dbTabUp.getChildren().add(saveName);
		dbTab = new OutputTab(dbTabUp, "MINE", e -> {
			try {
				String save = saveName.getText();
				String regularExpression = "[a-zA-Z[0-9]]+";
				if (save.matches(regularExpression) && save.length() <= 50) {
					ObjectInputStream in = connection.getConnectionStream("?command=DB&tabName=" + tabName.getText()
							+ "&nCluster=" + nCluster.getText() + "&saveName=" + save);
					try {
						String[] result = (String[]) in.readObject();
						if (result[0].startsWith("Errore")) {
							showAlert(result[0], "ERRORE");
						} else if (result[0].startsWith("Attenzione")) {
							showAlert(result[0], "ATTENZIONE");
							dbTab.clusterOutput.setText(result[1]);
						} else {
							dbTab.clusterOutput.setText(result[1]);
						}
					} finally {
						in.close();
					}
				} else {
					showAlert("Il nome del salvataggio deve essere una stringa alfanumerica (max 50 char)!", "ERRORE");
				}
			} catch (IOException | ClassNotFoundException | ServerConnectionFailedException e1) {
				showAlert("Errore di connessione con il server!", "ERRORE");
			}
		});
		dbTab.setText("DB");
		tabPane.getTabs().add(dbTab);

		FlowPane loadTabUp = new FlowPane();
		loadTabUp.setAlignment(Pos.CENTER);
		loadTabUp.setHgap(7);
		Label loadBoxLabel = new Label("Salvataggio da caricare:");
		ComboBox<String> loadBox = new ComboBox<String>();
		loadBox.setPrefSize(125, 20);
		loadBox.setOnMouseClicked(e -> {
			try {
				ObjectInputStream in = connection.getConnectionStream("?command=SAVED");
				try {
					List<String> saved = (List<String>) in.readObject();
					loadBox.getItems().setAll(saved);
				}finally {
					in.close();
				}
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
				showAlert("Errore di connessione con il server!", "ERRORE");
			} catch (ServerConnectionFailedException e1) {
				showAlert("Errore di connessione con il server!", "ERRORE");
			}
		});
		loadTabUp.getChildren().add(loadBoxLabel);
		loadTabUp.getChildren().add(loadBox);
		loadTab = new OutputTab(loadTabUp, "LOAD", e -> {
			try {
				ObjectInputStream in = connection.getConnectionStream("?command=LOAD&loadName=" + loadBox.getValue());
				try {	
					String result = (String) in.readObject();
					if (result.startsWith("Errore")) {
						showAlert(result, "ERRORE");
					} else {
						loadTab.clusterOutput.setText(result);
					}
				}finally {
					in.close();
				}
			} catch (IOException | ClassNotFoundException e1) {
				showAlert("Errore nel caricamento!", "ERRORE");
			} catch (ServerConnectionFailedException e1) {
				showAlert("Errore di connessione con il server!", "ERRORE");
			}
		});
		loadTab.setText("LOAD");
		tabPane.getTabs().add(loadTab);

		Scene s = new Scene(tabPane, 770, 500);
		primaryStage.setMinWidth(770);
		primaryStage.setMinHeight(300);
		primaryStage.setScene(s);
		primaryStage.setTitle("CLIENT");
		primaryStage.show();
	}

	/**
	 * La inner class OutputTab modella la singola tab dell'interfaccia.
	 */
	private class OutputTab extends Tab {
		/**
		 * Il pannello principale della tab.
		 */
		private GridPane root = new GridPane();
		/**
		 * Il bottone per l'esecuzione.
		 */
		private Button executeButton = new Button();
		/**
		 * La text area per visualizzare l'output dell'esecuzione.
		 */
		private TextArea clusterOutput = new TextArea();

		/**
		 * Il costruttore della classe. Setta il layout per il contenuto della tab.
		 * 
		 * @param n
		 *            Il nodo da inserire nel primo terzo del pannello principale.
		 * @param button
		 *            Il testo da visualizzare nel bottone.
		 * @param event
		 *            L'evento associato al bottone.
		 */
		// $
		private OutputTab(Node n, String button, EventHandler<ActionEvent> event) {
			executeButton.setFont(Font.font(Font.getDefault().toString(), FontWeight.BOLD, 20));
			clusterOutput.setEditable(false);
			ScrollPane outputPane = new ScrollPane(clusterOutput);
			outputPane.setFitToHeight(true);
			outputPane.setFitToWidth(true);
			executeButton.setText(button);
			executeButton.setOnAction(event);
			root.add(n, 0, 0);
			GridPane.setHalignment(n, HPos.CENTER);
			GridPane.setValignment(n, VPos.CENTER);
			root.add(outputPane, 0, 1);
			root.add(executeButton, 0, 2);
			GridPane.setHalignment(executeButton, HPos.CENTER);
			GridPane.setValignment(executeButton, VPos.CENTER);
			root.setVgap(20);
			ColumnConstraints c = new ColumnConstraints();
			c.setPercentWidth(100);
			root.getColumnConstraints().add(c);
			RowConstraints r25 = new RowConstraints();
			RowConstraints r50 = new RowConstraints();
			r25.setPercentHeight(25);
			r50.setPercentHeight(50);
			root.getRowConstraints().addAll(r25, r50, r25);
			this.setContent(root);
			this.setClosable(false);
		}
	}

	/**
	 * Mostra una finestra modale di allerta.
	 * 
	 * @param message
	 *            Il messaggio da mostrare nella finestra.
	 * @param title
	 *            Il titolo della finestra.
	 */
	private void showAlert(String message, String title) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(message);
		alert.setTitle(title);
		alert.showAndWait();
	}

	/**
	 * Il main dell'applicazione. Lancia l'interfaccia.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch();
	}

}
