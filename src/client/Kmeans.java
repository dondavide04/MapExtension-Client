package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Kmeans extends Application {
	private OutputTab dbTab, fileTab;

	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();
		FlowPane dbTabUp = new FlowPane();
		dbTabUp.setAlignment(Pos.CENTER);
		dbTabUp.setHgap(7);
		TextField tabName = new TextField();
		Label tabNameLabel = new Label("Nome tabella: ");
		TextField nCluster = new TextField();
		Label nClusterLabel = new Label("Num Cluster: ");
		TextField fileName = new TextField();
		Label fileNameLabel = new Label("Nome file: ");
		dbTabUp.getChildren().add(tabNameLabel);
		dbTabUp.getChildren().add(tabName);
		dbTabUp.getChildren().add(nClusterLabel);
		dbTabUp.getChildren().add(nCluster);
		dbTabUp.getChildren().add(fileNameLabel);
		dbTabUp.getChildren().add(fileName);
		dbTab = new OutputTab(dbTabUp, "MINE", e -> {
			try {
				String file = fileName.getText();
				String regularExpression = "[a-zA-Z[0-9]]+";
				if (file.matches(regularExpression)) {
					URL url = new URL("http://172.26.243.58:8080/MAPE%20-%20Servlet/Servlet?command=DB&tabName="
							+ tabName.getText() + "&nCluster=" + nCluster.getText() + "&fileName=" + file);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
					String[] result = (String[]) in.readObject();
					in.close();
					if (result[0].startsWith("Errore")) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText(result[0]);
						alert.setTitle("ERRORE");
						alert.showAndWait();
					} else if (result[0].startsWith("Attenzione")) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText(result[0]);
						alert.setTitle("ATTENZIONE");
						alert.showAndWait();
						dbTab.clusterOutput.setText(result[1]);
					} else {
						dbTab.clusterOutput.setText(result[1]);
					}
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("Il nome del file deve essere una stringa alfanumerica!");
					alert.setTitle("ERRORE");
					alert.showAndWait();
				}
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		dbTab.setText("DB");
		tabPane.getTabs().add(dbTab);

		FlowPane fileTabUp = new FlowPane();
		fileTabUp.setAlignment(Pos.CENTER);
		fileTabUp.setHgap(7);
		Label fileBoxLabel = new Label("File da caricare:");
		ComboBox<String> fileBox = new ComboBox<String>();
		fileBox.setPrefSize(125, 20);
		fileBox.setOnMouseClicked(e -> {
			try {
				URL url = new URL("http://172.26.243.58:8080/MAPE%20-%20Servlet/Servlet?command=SAVED");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
				String[] saved = (String[]) in.readObject();
				in.close();
				fileBox.getItems().setAll(saved);
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		fileTabUp.getChildren().add(fileBoxLabel);
		fileTabUp.getChildren().add(fileBox);
		fileTab = new OutputTab(fileTabUp, "STORE FROM FILE", e -> {
			try {
				URL url = new URL("http://172.26.243.58:8080/MAPE%20-%20Servlet/Servlet?command=FILE&fileName="
						+ fileBox.getValue());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
				String result = (String) in.readObject();
				in.close();
				if (result.startsWith("Errore")) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(result);
					alert.setTitle("ERRORE");
					alert.showAndWait();
				} else {
					fileTab.clusterOutput.setText(result);
				}
			} catch (IOException | ClassNotFoundException e1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Errore nel caricamento da file!");
				alert.setTitle("ERRORE");
				alert.showAndWait();
			}
		});
		fileTab.setText("FILE");
		tabPane.getTabs().add(fileTab);

		Scene s = new Scene(tabPane, 720, 500);
		primaryStage.setMinWidth(720);
		primaryStage.setMinHeight(300);
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

}
