package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Receta;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;

public class RecetaController {		
		
		@FXML	
		private Button masButton;
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
		private TableColumn<Receta, Integer> columPuntuacion;
		@FXML
		private ObservableList<Receta> listaRecetas = FXCollections.observableArrayList();		
		
		@FXML
		public void initialize () {			
			buscadorComBox.getItems().addAll(
			        "Tipo postre",
			        "Contiene ingrediente:",
			        "NO Contiene ingrediente:"
			    );
			
			cargarRecetaDesdeDB();
			configurarColumnas();			
			tablaRecetas.setItems(listaRecetas);
			añadirColumnaEliminar();
			añadirColumnaEditar();			
						
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
		
		private void configurarColumnas() {
			columNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
			columTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoPostre()));
			columPuntuacion.setCellValueFactory(cellData ->	new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPuntuacion()).asObject());
		}
					
		//METODO PARA IR A LA PANTALLA CREACIÓN DE RECETA
		public void irACrearReceta() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crearReceta.fxml"));
	            Stage stage = (Stage) masButton.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
				
		//CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
		private void añadirColumnaEliminar() {
			
		    colEliminar.setCellFactory(param -> new TableCell<>() {
		        private final Button btn = new Button("✖"); 
		        {
		            btn.setOnAction(event -> {
		                Receta recet = getTableView().getItems().get(getIndex());
		                //MOSTRAR AL USUARIO MENSAJE DE AVISO ANTES DE ELIMINAR RECETA Y ESPERAR CONFIRMACION					
						Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
					    confirmacion.setTitle("Confirmar eliminación");
					    confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta receta?");
					    confirmacion.setContentText("Receta: " + recet.getNombre());

					    confirmacion.showAndWait().ifPresent(respuesta -> {
					        if (respuesta == ButtonType.OK) {		                		                		             
					    // ACCESO A LA BBDD Y ELIMINAR EL INGREDIENTE
						AccesoDB.accederDB(session -> session.remove(session.merge(recet)));				
						// ELIMINAR DE LA TABLA DE LA INTERFAZ USUARIO
						listaRecetas.remove(recet);
					        }
					    });	
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
		private void añadirColumnaEditar() {

			colEditar.setCellFactory(param -> new TableCell<>() {
				private final Button btn = new Button();
				{								
					ImageView iconoLapiz = new ImageView(new Image(getClass().getResourceAsStream("/images/lapiz.png")));
					iconoLapiz.setFitWidth(20);
					iconoLapiz.setFitHeight(20);
					btn.setGraphic(iconoLapiz);				
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
	            Stage stage = (Stage) tablaRecetas.getScene().getWindow();
	            stage.setScene(new Scene(loader.load()));	            
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}					
		
		// METODO PARA VOLVER A LA PANTALLA DEL MENU PRINCIPAL/RECETARIO
		public void irARecetario() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
				Stage stage = (Stage) flechaRecetario.getScene().getWindow();
				stage.setScene(new Scene(loader.load()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void cargarRecetaDesdeDB() {
			// LIMPIAMOS LISTA Y CONECTAMOS CON LA BBDD
			listaRecetas.clear();
			Session session = null;
			try {
				session = AccesoDB.getSession();
				
			//CARGAMOS LA RECETA JUNTO CON SUS INGREDIENTES
			List<Receta> recetas = session.createQuery(
				"SELECT DISTINCT r FROM Receta r LEFT JOIN FETCH r. ingredientes ORDER BY r.nombre ASC", Receta.class)
				.getResultList();
				
 			listaRecetas.addAll(recetas);
 				
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

