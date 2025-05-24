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
		
		//TAMAÑO UNIFICADO PARA TODA LA APP
		Scene scene = new Scene(root, 800, 680);
	    
		primaryStage.setScene(scene);
		primaryStage.setTitle("PostrEquilibrio");
		primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/icono.png")));
        primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
