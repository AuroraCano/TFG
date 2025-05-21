package view;

import java.io.IOException;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	@Override
	public void start (Stage primaryStage) throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
	    
		primaryStage.setScene(scene);
		primaryStage.setTitle("PostrEquilibrio");
        primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
