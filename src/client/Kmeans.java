package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Kmeans extends Application {
	private OutputTab dbTab, fileTab;

	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		TextField tabName = new TextField();
		TextField nCluster = new TextField();
		TextField fileName = new TextField();
		FlowPane dbTabUp = new FlowPane();
		dbTabUp.getChildren().add(tabName);
		dbTabUp.getChildren().add(nCluster);
		dbTabUp.getChildren().add(fileName);
		dbTab = new OutputTab(dbTabUp, "MINE", (e) -> {
			try {
				String file = fileName.getText();
				String regularExpression = "[a-zA-Z[0-9]]+";
				if(file.matches(regularExpression)) {
					URL url = new URL("http://172.26.243.58:8080/MAP-Servlet/Servlet?command=DB&tabName="
							+ tabName.getText() + "&nCluster=" + nCluster.getText() + "&fileName=" + file);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
					String result = (String) in.readObject();
					in.close();
					if (result.startsWith("Errore")) {
						new TextInputDialog(result).showAndWait();
					} else {
						dbTab.clusterOutput.setText(result);
					}
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("MUORI");
					alert.showAndWait();
				}
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}

		});
		dbTab.setText("DB");
		// FlowPane fileTabUp = new FlowPane();
		// fileTabUp.getChildren().add();
		tabPane.getTabs().add(dbTab);
		// tabPane.getTabs().add(fileTab);

		Scene s = new Scene(tabPane, 300, 300);
		primaryStage.setScene(s);
		primaryStage.setTitle("CLIENT");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	private class OutputTab extends Tab {
		private GridPane root = new GridPane();
		private Button executeButton = new Button();
		private TextArea clusterOutput = new TextArea();

		OutputTab(Node n, String button, EventHandler<ActionEvent> event) {
			clusterOutput.setEditable(false);
			ScrollPane outputPane = new ScrollPane(clusterOutput);
			executeButton.setText(button);
			executeButton.setOnAction(event);
			root.add(n, 0, 0);
			root.add(outputPane, 0, 1);
			root.add(executeButton, 0, 2);
			this.setContent(root);
		}

	}

}
