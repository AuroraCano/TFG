package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Usuario;
import org.hibernate.Session;

public class LoginController {
	
	@FXML
	private TextField txtemail;
	@FXML
	private PasswordField txtpass;
	@FXML
	private Button loginButton;
	@FXML	
	private Button registerButton;
	@FXML
	private Hyperlink linkOlvidarPass;

	@FXML
	public void initialize() {
		loginButton.setOnAction(e -> iniciarSesion());
		registerButton.setOnAction(e -> irARegistro(new ActionEvent()));
		linkOlvidarPass.setOnAction(e -> mostrarRecordatorio());
	}
	
	@FXML
	public void iniciarSesion() {
		
		String email = txtemail.getText();
		String password = txtpass.getText();
		
		if(email.isBlank() || password.isBlank()) {
			mostrarError("Introduce tu email y contraseña");
			return; //DETIENE EL METODO
		}
		
		// VERIFICA DATOS USUARIO
        if (autenticarUsuario(email, password)) {
        	irARecetario();
        } else {                   
            mostrarError("Email o contraseña incorrectos");
        }
		
	}

	 public boolean autenticarUsuario(String email, String password) {
	        Session session = null;	   	        
	        try {
	        	session = AccesoDB.getSession();
	        	// CONSULTA HQL PARA BURCAR EL USUARIO CON CREDENCIALES INDICADAS
	            Usuario user = session.createQuery(
	                "FROM Usuario WHERE email = :email AND password = :password", Usuario.class)
	                .setParameter("email", email)
	                .setParameter("password", password)
	                .uniqueResult();
	            if(user != null) {
	            	AccesoDB.setUsuarioActual(user);
	            	return true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (session != null) session.close();
	        }
	        return false;
	}

	public void mostrarRecordatorio() {
		try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recuperarPassword.fxml"));
	        Stage stage = new Stage();
	        stage.setTitle("Recuperar contraseña");
	        stage.setScene(new Scene(loader.load()));
	        stage.show();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void irARegistro(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registro.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@FXML
	public void irARecetario () {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
		 
	//METODO PARA MOSTRAR MENSAJE DE ERROR EN LA INTERFAZ DE USUARIO
		public void mostrarError(String mensaje) {
			Alert alerta = new Alert (AlertType.ERROR);
			alerta.setContentText(mensaje);
			alerta.show();
		}
		
	}
