package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
		private ComboBox<String> tipoPostreComBox;
		@FXML
		private TextArea  txtdescripcion;
		@FXML	
		private ImageView imgfoto;
		@FXML	
		private Button añadirButton;
		@FXML	
		private Button editarButton;
		@FXML	
		private Button eliminarButton;
		@FXML	
		private Button guardarButton;
		@FXML
		private ComboBox<String> buscadorComBox;
		@FXML
		private TextField buscadorTxt;
		@FXML	
		private Button buscadorButton;
		@FXML
		private ImageView flechaRecetario;		
		@FXML
		private TableView<Receta> tablaRecetas;
		@FXML
		private TableColumn<Receta, String> columNombre;
		@FXML
		private TableColumn<Receta, String> columTipo;
		@FXML
		private ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();
	
		
		@FXML
		public void initialize () {
			
			buscadorComBox.getItems().addAll(
			        "Tipo postre",
			        "Contiene ingrediente:",
			        "NO Contiene ingrediente:"
			    );
			buscadorComBox.getSelectionModel().getSelectedItem();
			
			cargarRecetaDesdeDB();
			
			columNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
			columTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoPostre()));

			tablaRecetas.setItems(listaRecetas);

			añadirButton.setOnAction(e -> añadirReceta());
	    //    editarButton.setOnAction(e -> editarReceta());
	  //      eliminarButton.setOnAction(e -> eliminarReceta());

	    }
		
		@FXML
		public void añadirReceta() {
		
		irACrearReceta();

		// VINCULAMOS LA LISTA DE RECETAS A LA TABLA
	//	tablaRecetas.setItems(listaRecetas);

		}
					
		//METODO PARA IR A LA PANTALLA CREACIÓN DE RECETA
		public void irACrearReceta() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crearReceta.fxml"));
	            Stage stage = (Stage) añadirButton.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
				
		@FXML	
		public void editarReceta() {
			irACrearReceta();
			Receta recet = AccesoDB.getRecetaActual();
				//ACTUALIZAR CAMPOS CON LOS DATOS INTRODUCIDOS
				recet.setNombre(txtnombre.getText());
				recet.setTipoPostre(tipoPostreComBox.getValue());
				recet.setDescripcion(txtdescripcion.getText());
			//VER FOTO ACTUAL	
				
		//		tablaRecetas.refresh();
				limpiarFormulario(false);
								
			// CONEXION CON LA BBDD Y ACTUALIZACION DE LOS DATOS	
			Session session = null;
		    Transaction tr = null;
					 
				try {
					session = AccesoDB.getSession();
					tr = session.beginTransaction();
					session.merge(recet); //ACTUALIZA LA RECETA EN LA BBDD
					tr.commit();				
					System.out.println("Receta actualizada correctamente.");
					mostrarMensaje();
			
					
				} catch (Exception e) {
					if (tr != null) tr.rollback();
		            e.printStackTrace();
		            System.out.println("Error al actualizar receta.");	           
		        } finally {
		            if (session != null) session.close();
				}              								
		}
		
		@FXML
		public void eliminarReceta() {
			
			Receta recet = AccesoDB.getRecetaActual();
	//		listaRecetas.remove(recet);
				
		}		
		
		//LIMPIAR DATOS DEL FORMULARIO
			private void limpiarFormulario(boolean limpiarTodo) {
			// SOLO LIMPIAR LOS DATOS INTRODUCIDOS
				if (limpiarTodo) {
					txtnombre.clear();
					tipoPostreComBox.getSelectionModel().clearSelection();
					txtdescripcion.clear();
					imgfoto.setImage(null);
				}
			}				
				
			//METODO PARA VOLVER A LA PANTALLA DEL MENU PRINCIPAL/RECETARIO
			public void irARecetario() {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
		            Stage stage = (Stage) añadirButton.getScene().getWindow();
		            stage.setScene(new Scene(loader.load()));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		
			private void cargarRecetaDesdeDB() {
				//LIMPIAMOS LISTA Y CONECTAMOS CON LA BBDD
			    listaRecetas.clear();
			    Session session = null;

			    try {
			        session = AccesoDB.getSession();
			        //Usuario usuarioActual = AccesoDB.getUsuarioActual(); // NOS ASEGURAMOS QUE NO ES NULO
			        //if (usuarioActual != null) {
			            listaRecetas.addAll(
			                session.createQuery("FROM Receta", Receta.class)
			                       .list()
			            );
			        //}
			    } catch (Exception e) {
			        e.printStackTrace();
			        System.out.println("Error al cargar recetas desde la BBDD.");
			    } finally {
			        if (session != null) session.close();
			    }
			}
			
			
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje() {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setContentText("Receta añadida correctamente");
			alerta.show();
		}
		
	}

