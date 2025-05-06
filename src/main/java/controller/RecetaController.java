package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Receta;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RecetaController {		

		@FXML
		private TextField txtnombre;
		@FXML
		private TextArea  txtdescripcion;
		@FXML
		private ComboBox<String> tipoPostreComBox;
		@FXML	
		private ImageView imgfoto;
		@FXML	
		private Button guardarbutton;
		
//		private Image imagen; // Imagen para convertir a byte[]
		
		@FXML
		public void initialize () {
//			guardarbutton.setOnAction(e -> guardarReceta());
	    }
		
/*		public void guardarReceta() {

		Receta receta = new Receta (
			txtnombre.getText(),
			txtdescripcion.getText(),			
			imgfoto.getImage(),
			tipoPostreComBox.getValue()
				);
		
		// CONEXION CON LA BBDD E INSETAR REGISTRO
		
		Session session = null;
        Transaction tr = null;
				 
			try {
				session = AccesoDB.getSession();
				tr = session.beginTransaction();
				session.persist(receta); //INSERTA LA RECETA EN LA BBDD
				tr.commit();				
				System.out.println("Receta registrada correctamente");
				mostrarMensaje();
				irARecetario();
				
			} catch (Exception e) {
				if (tr != null) tr.rollback();
	            e.printStackTrace();
	            System.out.println("Error al registrar receta");
	            mostrarError();
	        } finally {
	            if (session != null) session.close();
			}              						
		}
*/
		
		//METODO PARA CONVERTIR IMAGEN JAVAFX A BYTE[]
/*		private byte[] imageToByteArray(Image image) throws IOException {
		    // Convertir Image (JavaFX) a BufferedImage (AWT)
		   
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    
		    // Escribir la imagen en formato PNG (u otro como JPG si prefieres)
		    ImageIO.write(bufferedImage, "png", baos);
		    return baos.toByteArray();
		}
		
*/
		
		//METODO PARA VOLVER A LA PANTALLA INICIO
		public void irARecetario() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
	            Stage stage = (Stage) guardarbutton.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje() {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setContentText("Receta guardada correctamente");
			alerta.show();
		}
		
		//METODO PARA MOSTRAR MENSAJE ERROR EN LA INTERFAZ DE USUARIO
		public void mostrarError() {
			Alert error = new Alert (AlertType.ERROR);
			error.setContentText("Error al registrar receta");
			error.show();
		}
}

