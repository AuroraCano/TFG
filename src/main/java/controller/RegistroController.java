package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
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
		private ImageView flechaInicio;

		@FXML
		public void initialize () {
			guardarbutton.setOnAction(e -> guardarRegistro());
	    }
		
		public void guardarRegistro() {
		
		//VALIDAMOS QUE NO PUEDE TENER CAMPOS VACÍOS
		if (txtnombre.getText().isBlank() ||
			txtapellidos.getText().isBlank() ||
			txtemail.getText().isBlank() ||
			txtpass.getText().isBlank() ||
			txtrepetirpass.getText().isBlank() ||
			txthotel.getText().isBlank()) {
			mostrarError("Todos los campos deben estar rellenos.");
			return; //DETIENE EL METODO
		}
			
		//VALIDAMOS FORMATO DEL EMAIL
		String email = txtemail.getText();
	    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
	        mostrarError("Introduce un correo electrónico válido.");
	        return;
	    }
	    
	    //VALIDAMOS QUE LAS CONTRASEÑAS COINCIDAN
	    if (!txtpass.getText().equals(txtrepetirpass.getText())) {
			mostrarError("Las contraseñas no coinciden");
		     return; 
		    }
		
		//CONEXION CON LA BBDD E INSETAR REGISTRO		
		Session session = null;
		Transaction tr = null;		
		try {
			session = AccesoDB.getSession();
			
		//VALIDAMOS QUE EL EMAIL INTRODUCIDO NO EXISTA YA	
	    Usuario existente = session.createQuery(
	    		"FROM Usuario WHERE email =: email", Usuario.class)
	    		.setParameter("email", txtemail.getText())
	    		.uniqueResult();
	    
	    if (existente != null) {
	    	mostrarError("Ya existe usuario registrado con ese email");
	    	return;
	    }
		
	    //SI EL EMAIL NO EXISTE, CREAMOS USUARIO
 		Usuario user = new Usuario (
			txtnombre.getText(),
			txtapellidos.getText(),
			txtemail.getText(),
			txtpass.getText(),
			txthotel.getText()	
				);		       				 
			
			tr = session.beginTransaction();
			session.persist(user); //INSERTA EL USUARIO EN LA BBDD
			tr.commit();				
			mostrarMensaje("Registro realizado correctamente");
			irAInicio();
				
		} catch (Exception e) {
			if (tr != null) tr.rollback();
	        e.printStackTrace();
	        mostrarError("Error al registrar usuario.");
	    } finally {
	    	if (session != null) session.close();
			}              						
		}
	
		//METODO PARA VOLVER A LA PANTALLA INICIO
		public void irAInicio() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
	            Stage stage = (Stage) guardarbutton.getScene().getWindow();
	            Parent root = loader.load();
	            Scene scene = new Scene(root, 800, 680); // MISMO TAMAÑO INDICADO EN MAIN
	            stage.setScene(scene);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje(String mensaje) {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setContentText(mensaje);
			alerta.show();
		}
		
		//METODO PARA MOSTRAR MENSAJE ERROR EN LA INTERFAZ DE USUARIO
		public void mostrarError(String mensaje) {
			Alert error = new Alert (AlertType.ERROR);
			error.setContentText(mensaje);
			error.show();
		}
}

