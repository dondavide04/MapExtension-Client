package client;

import javafx.application.Application;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Kmeans extends Application{
	private Tab dbTab,fileTab;

	public void start(Stage primaryStage) {
		TabPane tabPane = new TabPane();
		dbTab = new Tab();
		fileTab = new Tab();
		tabPane.getTabs().add(dbTab);
		tabPane.getTabs().add(fileTab);
		GridPane dbRoot = new GridPane();
		FlowPane dbUp = new FlowPane();
		
		
		
		primaryStage.setTitle("CLIENT");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch();
	}
	
	//private class 

}
