package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.AccesoDB;

public class VistaPrincipalController {

	@FXML
	private Button perfilButton;
	@FXML
	private Button recetasButton;
	@FXML
	private Button ingredientesButton;
	@FXML
	private Button rankingButton;
	@FXML
	private ImageView flechaInicio;

	@FXML
	public void initialize() {
		perfilButton.setOnAction(e -> irAPerfil(new ActionEvent()));
		recetasButton.setOnAction(e -> irARecetas(new ActionEvent()));
		ingredientesButton.setOnAction(e -> irAIngredientes(new ActionEvent()));
		rankingButton.setOnAction(e -> irARanking(new ActionEvent()));

		flechaInicio.setOnMouseClicked(e -> irAInicio());
	}
	
	//METODO PARA IR A LA PANTALLA DEL PERFIL USUARIO
	@FXML
	public void irAPerfil(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/perfil.fxml"));
            Stage stage = (Stage) perfilButton.getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	//METODO PARA IR A LA PANTALLA DE CREACIÓN DE RECETAS
		@FXML
		public void irARecetas(ActionEvent event) {
			try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetas.fxml"));
	            Stage stage = (Stage) recetasButton.getScene().getWindow();
	            Parent root = loader.load();
	            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
	            stage.setScene(scene);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
		//METODO PARA IR A LA PANTALLA DE CREACIÓN DE INGREDIENTES
		@FXML
		public void irAIngredientes(ActionEvent event) {
			try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingrediente.fxml"));
	            Parent root = loader.load();
	            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
	            
	            IngredienteController controller = loader.getController();
	            controller.setUsuarioActual(AccesoDB.getUsuarioActual());
	            
	            Stage stage = (Stage) ingredientesButton.getScene().getWindow();
	            stage.setScene(scene);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		
		//METODO PARA IR A LA PANTALLA DEL RANKING
				@FXML
				public void irARanking(ActionEvent event) {
					try {
			            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ranking.fxml"));
			            Stage stage = (Stage) ingredientesButton.getScene().getWindow();
			            Parent root = loader.load();
			            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
			            stage.setScene(scene);          
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
				
		//METODO PARA VOLVER A LA PANTALLA LOGIN
		@FXML
		public void irAInicio() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
	            Stage stage = (Stage) flechaInicio.getScene().getWindow();
	            Parent root = loader.load();
	            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
	            stage.setScene(scene);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}			  	
		
	//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
	public void mostrarMensaje() {
		Alert alerta = new Alert (AlertType.INFORMATION);
		alerta.setContentText("Datos incorrectos");
		alerta.show();
	}
	
}
