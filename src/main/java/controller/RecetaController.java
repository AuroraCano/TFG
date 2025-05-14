package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Ingrediente;
import model.Receta;
import model.Usuario;

import java.io.IOException;

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
		private TableColumn<Receta, Void> colEliminar; 
		@FXML
		private TableColumn<Receta, Void> colEditar; 
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
			tablaRecetas.refresh();
			btnEliminar();
			btnEditar();

			añadirButton.setOnAction(e -> añadirReceta());
			
			//AL HACER DOBLE CLIC SOBRE UNA FILA DE RECETA -> ABRE LA FICHA DE LA RECETA (DONDE SE PODRA EDITAR SI SE DESEA)
			tablaRecetas.setRowFactory(tv -> {
			    TableRow<Receta> row = new TableRow<>();
			    row.setOnMouseClicked(event -> {
			        if (event.getClickCount() == 2 && !row.isEmpty()) {
			            Receta recet = row.getItem();
			            abrirFichaReceta(recet);
			        }
			    });
			    return row;
			});
	  
	    }
		
		@FXML
		public void añadirReceta() {	
			
		irACrearReceta();
		
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
				
		//CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
		private void btnEliminar() {
			
		    colEliminar.setCellFactory(param -> new TableCell<>() {
		        private final Button btn = new Button("✖"); 
		        {
		            btn.setOnAction(event -> {
		                Receta recet = getTableView().getItems().get(getIndex());
		                
		                //ELIMINAR DE LA BBDD
		                Session session = null;
		        	    Transaction tr = null;
		        		try {
		        			session = AccesoDB.getSession();
		        			tr = session.beginTransaction();
		        			session.remove(session.merge(recet)); //BORRA EL INGREDIENTE
		        			tr.commit();					        			        				
		        			} catch (Exception ex) {
		        				if (tr != null) tr.rollback();
		        	            ex.printStackTrace();
		        	            System.out.println("Error al eliminar receta.");	           
		        	        } finally {
		        	            if (session != null) session.close();
		        			}
		        		//ELIMINAR ESA FILA EN LA TABLA DE LA INTERFAZ USUARIO
		        		listaRecetas.remove(recet);
		            });

		            btn.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");	            
		        }

		        @Override
		        protected void updateItem(Void item, boolean empty) {
		            super.updateItem(item, empty);
		            if (empty) {
		                setGraphic(null);
		            } else {
		                setGraphic(btn);
		                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		                setAlignment(Pos.CENTER);
		            }
		        }
		    });
		}
		
		// CREAMOS ACCION DEL BOTON EDITAR DENTRO DE LA TABLA
		private void btnEditar() {

			colEditar.setCellFactory(param -> new TableCell<>() {
				private final Button btn = new Button();
				{
					try {
						
					
					ImageView iconoLapiz = new ImageView(new Image(getClass().getResourceAsStream("/images/lapiz.png")));
					iconoLapiz.setFitWidth(20);
					iconoLapiz.setFitHeight(20);
					btn.setGraphic(iconoLapiz);
					} catch (Exception e) {
						System.err.println("No se pudo cargar el icono lápiz: " + e.getMessage());
					}
					btn.setStyle("-fx-background-color: #74A9D8; -fx-padding:4; -fx-cursor: hand;");
					btn.setOnAction(event -> {
						Receta recet = getTableView().getItems().get(getIndex());
						abrirFichaReceta(recet);
					});
				}

				@Override
				protected void updateItem(Void item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
					} else {
						setGraphic(btn);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						setAlignment(Pos.CENTER);
					}
				}

			});
		}

		//METODO PARA IR A LA PANTALLA FICHA DE LA RECETA
		private void abrirFichaReceta(Receta recet) {
		    try {
		    	//GUARDAMOS LA RECETA ACTUAL EN ACCESODB
		    	AccesoDB.setRecetaActual(recet);
		    	//CARGAMOS LA VISTA FXML
		    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fichaReceta.fxml"));
	            Stage stage = (Stage) añadirButton.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));	            
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
					
		
		// METODO PARA VOLVER A LA PANTALLA DEL MENU PRINCIPAL/RECETARIO
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
			// LIMPIAMOS LISTA Y CONECTAMOS CON LA BBDD
			listaRecetas.clear();
			Session session = null;
			try {
				session = AccesoDB.getSession();
				listaRecetas.addAll(session.createQuery("FROM Receta", Receta.class).list());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al cargar recetas desde la BBDD.");
			} finally {
				if (session != null)
					session.close();
			}
		}
			
			
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje() {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setContentText("Receta añadida correctamente");
			alerta.show();
		}
		
	}

