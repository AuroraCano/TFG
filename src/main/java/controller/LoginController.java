package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
	public void initialize() {
		loginButton.setOnAction(e -> iniciarSesion());
		registerButton.setOnAction(e -> irARegistro(new ActionEvent()));
	}
	
	@FXML
	public void iniciarSesion() {
		
		String email = txtemail.getText();
	//	String password = txtpass.getText();
		
		// VERIFICA DATOS USUARIO
        if (autenticarUsuario(email)) {
        	irARecetario();
        } else {
            // MOSTRAR MENSAJE SI LOS DATOS SON ERRONEOS (EN CONSOLA Y UI)
            System.out.println("Credenciales incorrectas");
            mostrarMensaje();
        }
		
	}
	
	//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
	public void mostrarMensaje() {
		Alert alerta = new Alert (AlertType.INFORMATION);
		alerta.setContentText("Datos incorrectos");
		alerta.show();
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
	
	 public boolean autenticarUsuario(String email) {
	        Session session = null;
	        boolean authenticated = false;
	        
	        try {
	        	session = AccesoDB.getSession();
	        	// Consulta HQL para buscar un usuario con email y password especificados
	            Usuario user = session.createQuery(
	                "FROM Usuario WHERE email = :email", Usuario.class)
	                .setParameter("email", email)
	                .uniqueResult();
	         // Si se encontró un usuario, autenticación

	   //  CUANDO TENGA LA CONTRASEÑA CIFRADA
	 //         BCrypt.checkpw()   
	            authenticated = (user != null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (session != null) session.close();
	        }

	        return authenticated;
	    }	
		
	}
