package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RegistroController {		

		@FXML
		private TextField txtnombre;
		@FXML
		private TextField txtapellidos;
		@FXML
		private TextField txtemail;
		@FXML
		private PasswordField txtpass;
		@FXML
		private PasswordField txtrepetirpass;
		@FXML
		private TextField txthotel;
		@FXML	
		private Button guardarbutton;

		@FXML
		public void initialize () {
			guardarbutton.setOnAction(e -> guardarRegistro());
	    }
		
		public void guardarRegistro() {

		Usuario user = new Usuario (
			txtnombre.getText(),
			txtapellidos.getText(),
			txtemail.getText(),
			txtpass.getText(),
			txthotel.getText()	
				);
		
		//SI LOS CAMPOS DE CONTRASEÑA NO COINCIDEN MOSTRAR MENSAJE DE ERROR Y DETENER EL METODO guardarRegistro
		if (!txtpass.getText().equals(txtrepetirpass.getText())) {
			mostrarError();
		     return; //DETIENE EL METODO
		    }

		// CONEXION CON LA BBDD E INSETAR REGISTRO
		
		Session session = null;
        Transaction tr = null;
				 
			try {
				session = AccesoDB.getSession();
				tr = session.beginTransaction();
				session.persist(user); //INSERTA EL USUARIO EN LA BBDD
				tr.commit();				
				System.out.println("Usuario registrado correctamente.");
				mostrarMensaje();
				irAInicio();
				
			} catch (Exception e) {
				if (tr != null) tr.rollback();
	            e.printStackTrace();
	            System.out.println("Error al registrar usuario.");	           
	        } finally {
	            if (session != null) session.close();
			}              						
		}
	
		//METODO PARA VOLVER A LA PANTALLA INICIO
		public void irAInicio() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
	            Stage stage = (Stage) guardarbutton.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje() {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setContentText("Registro realizado correctamente");
			alerta.show();
		}
		
		//METODO PARA MOSTRAR MENSAJE ERROR EN LA INTERFAZ DE USUARIO
		public void mostrarError() {
			Alert error = new Alert (AlertType.ERROR);
			error.setContentText("Las contraseñas no coinciden");
			error.show();
		}
}

