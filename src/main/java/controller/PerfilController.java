package controller;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Usuario;

public class PerfilController {

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
	private Button actualizarbutton;
	@FXML
	private ImageView flechaRecetario;

	@FXML
	public void initialize () {
		actualizarbutton.setOnAction(e -> actualizarRegistro());
		
		Usuario user = AccesoDB.getUsuarioActual();
		if (user != null) {
			txtnombre.setText(user.getNombre());
			txtapellidos.setText(user.getApellidos());
			txtemail.setText(user.getEmail());
			txthotel.setText(user.getHotel());
		}
	}
	public void actualizarRegistro() {
	
		Usuario user = AccesoDB.getUsuarioActual();
			//ACTUALIZAR CAMPOS CON LOS DATOS INTRODUCIDOS
			user.setNombre(txtnombre.getText());
			user.setApellidos(txtapellidos.getText());
			user.setEmail(txtemail.getText());
			user.setHotel(txthotel.getText());	
						
	//SI LOS DOS CAMPOS DE CONTRASEÑA NO COINCIDEN MOSTRAR MENSAJE DE ERROR
	if (!txtpass.getText().equals(txtrepetirpass.getText())) {
		mostrarError();
	     return; //Y DETIENE EL METODO
	    }

	// CONEXION CON LA BBDD E INSETAR DATOS ACTUALIZADOS	
	Session session = null;
    Transaction tr = null;
			 
		try {
			session = AccesoDB.getSession();
			tr = session.beginTransaction();
			session.merge(user); //ACTUALIZA EL USUARIO EN LA BBDD
			tr.commit();				
			System.out.println("Datos actualizados correctamente.");
			mostrarMensaje();
			irARecetario();
			
		} catch (Exception e) {
			if (tr != null) tr.rollback();
            e.printStackTrace();
            System.out.println("Error al registrar usuario.");	           
        } finally {
            if (session != null) session.close();
		}              						
	}

	//METODO PARA VOLVER A LA PANTALLA DEL MENU PRINCIPAL
	public void irARecetario() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
            Stage stage = (Stage) actualizarbutton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
	public void mostrarMensaje() {
		Alert alerta = new Alert (AlertType.INFORMATION);
		alerta.setContentText("Datos actualizados correctamente");
		alerta.show();
	}
	
	//METODO PARA MOSTRAR MENSAJE ERROR EN LA INTERFAZ DE USUARIO
	public void mostrarError() {
		Alert error = new Alert (AlertType.ERROR);
		error.setContentText("Las contraseñas no coinciden");
		error.show();
	}
}


