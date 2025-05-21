package controller;

import org.hibernate.Session;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.AccesoDB;
import model.Usuario;

public class RecuperarPasswController {

	@FXML
    private TextField txtEmail;

    @FXML
    private Button btnEnviar;

    @FXML
    private Label lblResultado;
    
    private static final String colorRojo = "-fx-text-fill: red;";
    private static final String colorVerde = "-fx-text-fill: green;";

    @FXML
    public void initialize() {
        btnEnviar.setOnAction(e -> recuperarPassword());
    }
    
    @FXML
    private void recuperarPassword() {
        String email = txtEmail.getText().trim();
            
        if (email.isEmpty()) {
        	lblResultado.setText("Por favor, introduce tu correo.");
        	lblResultado.setStyle(colorRojo);
            return;
        }
        try (Session session = AccesoDB.getSession()) {
        	Usuario user = session.createQuery(
        			"FROM Usuario WHERE email = :email", Usuario.class)
        			.setParameter("email", email)
        			.uniqueResult();
        	if (user != null) {
        		//NO ENVIAMOS EMAIL REAL, SOLO MENSAJE SIMULADO
        		lblResultado.setText("Se han enviado instrucciones al correo indicado.");
        		lblResultado.setStyle(colorVerde);
        	}else{
        		lblResultado.setText("No existe cuenta con ese email.");
        		lblResultado.setStyle(colorRojo);
        	}	
        } catch (Exception e) {
        	lblResultado.setText("Error al verificar el correo.");
            lblResultado.setStyle(colorRojo);
            e.printStackTrace();
        }
    }

}
