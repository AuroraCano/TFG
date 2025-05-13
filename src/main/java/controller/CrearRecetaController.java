package controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Ingrediente;
import model.Receta;
import model.RecetaIngrediente;
import model.Usuario;

public class CrearRecetaController {

	@FXML
	private TextField txtnombre;
	@FXML
	private ComboBox<String> tipoPostreComBox;
	@FXML
	private TextArea  txtdescripcion;
	@FXML	
	private ImageView imgfoto;
	@FXML
	private Button btnSeleccImagen;
	@FXML	
	private Button guardarButton;
	@FXML
	private ImageView flechaRecetario;
	@FXML
	private ComboBox<Ingrediente> comboIngred;
	@FXML 
	private TextField txtCantidad;
	@FXML
	private Button btnAgregarIngred;
	@FXML
	private TableView<RecetaIngrediente> tablaIngredientes;
	@FXML
	private TableColumn<RecetaIngrediente, String> colIngrediente;
	@FXML
	private TableColumn<RecetaIngrediente, Integer> colCantidad;
	@FXML
	private TableColumn<RecetaIngrediente, Void> colEliminar; //COLUMNA QUE NO ALMACENA VALOR

	private ObservableList<RecetaIngrediente> listaIngredReceta = FXCollections.observableArrayList();

	@FXML
	public void initialize () {
		guardarButton.setOnAction(e -> guardarReceta());
		
		tipoPostreComBox.getItems().addAll(
		        "Bizcocho",
		        "Tarta",
		        "Magdalena",
		        "Helado",
		        "Bebida",
		        "Otro"
		    );
		// CARGAR INGREDIENTES DESDE LA BBDD
	    Session session = AccesoDB.getSession();
	    comboIngred.setItems(FXCollections.observableArrayList(AccesoDB.getTodosLosIngredientes()));
	    session.close();

	    // CONFIGURAMOS LA TABLA DE INGREDIENTES
	    colIngrediente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId_ingrediente().getNombre()));
	    colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
	    tablaIngredientes.setItems(listaIngredReceta);
	    añadirBotonEliminar();
	    
	    btnAgregarIngred.setOnAction(e -> agregarIngrediente());
	    	  	    		
	}
	
	
	// DEFINIMOS ACCION DEL BOTON PARA IR AGREGANDO INGREDIENTES A LA TABLA
	@FXML
	private void agregarIngrediente() {
	    Ingrediente seleccionado = comboIngred.getValue();
	    String cantidadTexto = txtCantidad.getText();

	    if (seleccionado != null && !cantidadTexto.isBlank()) {
	        try {
	            int cantidad = Integer.parseInt(cantidadTexto);

	            // CREAMOS LOS CAMPOS PARA AGREGAR UNA FILA DE LA TABLA
	            RecetaIngrediente ri = new RecetaIngrediente();
	            ri.setId_ingrediente(seleccionado);
	            ri.setCantidad(cantidad);

	            listaIngredReceta.add(ri);

	            // LIMPIAR LOS CAMPOS
	            comboIngred.getSelectionModel().clearSelection();
	            txtCantidad.clear();
	        } catch (NumberFormatException e) {
	            mostrarAlerta("La cantidad debe ser un número entero.");
	        }
	    } else {
	        mostrarAlerta("Selecciona un ingrediente y escribe la cantidad.");
	    }
	}

	
	private void mostrarAlerta(String string) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
	    alert.setTitle("Aviso");
	    alert.setContentText(string);
	    alert.showAndWait();
	}

	//CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
	private void añadirBotonEliminar() {
	    colEliminar.setCellFactory(param -> new TableCell<>() {
	        private final Button btn = new Button("🗑 Eliminar");

	        {
	            btn.setOnAction(e -> {
	                RecetaIngrediente ri = getTableView().getItems().get(getIndex());
	                listaIngredReceta.remove(ri);
	            });

	            btn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");
	        }

	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty) {
	                setGraphic(null);
	            } else {
	                setGraphic(btn);
	            }
	        }
	    });
	}


	private Usuario usuarioActual;

	public void setUsuarioActual(Usuario user) {
	    this.usuarioActual = user;
	}
	
	public void guardarReceta() {
		
		Receta recet = new Receta(
			usuarioActual,
			txtnombre.getText(),
			tipoPostreComBox.getSelectionModel().getSelectedItem(),
			txtdescripcion.getText(),
			null
			);
			if (imgfoto.getImage() != null) {
				recet.setImage(imgfoto.getImage()); // CONVERTIMOS LA IMAGEN A BYTE[]
			}
	
//		listaRecetas.add(recet);
		limpiarFormulario();
		
		// PARA ASIGNAR USUARIO ACTUAL
		Usuario user = AccesoDB.getUsuarioActual();
		recet.setUser(user); 
	
		// CONEXION CON LA BBDD E INSETAR NUEVA RECETA (TABLAS: RECETA Y LINEA_RECETA)			
		Session session = null;
	    Transaction tr = null;
				 
			try {
				session = AccesoDB.getSession();
				tr = session.beginTransaction();
				//PASO 1: GUARDAR LA RECETA PARA GENERAR SU ID
				session.persist(recet);
				session.flush(); //FUERZA A GUARDAR PARA OBTENER EL ID
				//PASO 2: GUARDAR LAS LINEAS DE INGREDIENTES
				for(RecetaIngrediente ri : listaIngredReceta) {
					ri.setId_receta(recet); //ASIGNAMOS LA RECETA YA GUARDADA
					session.persist(ri);
				}
				tr.commit();
				
				System.out.println("Receta añadida correctamente.");
				mostrarMensaje();
		//		irARecetario();
				
			} catch (Exception e) {
				if (tr != null) tr.rollback();
	            e.printStackTrace();
	            System.out.println("Error al añadir receta.");	           
	        } finally {
	            if (session != null) session.close();
			}              						
		}
		
		//PARA QUE EL USUARIO PUEDA AGREGAR UNA IMAGEN EN LA FOTO DE LA RECETA
		@FXML
		private void seleccionarImagen() {
		    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
		    fileChooser.setTitle("Seleccionar imagen de receta");
	
		    // FILTRAR LOS TIPOS DE IMAGENES
		    fileChooser.getExtensionFilters().addAll(
		        new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
		    );
	
		    java.io.File file = fileChooser.showOpenDialog(guardarButton.getScene().getWindow());
		    if (file != null) {
		        javafx.scene.image.Image imagen = new javafx.scene.image.Image(file.toURI().toString());
		        imgfoto.setImage(imagen); // Se muestra la imagen en el ImageView
		    }
		}
		
		//LIMPIAR DATOS DEL FORMULARIO
			private void limpiarFormulario() {
					txtnombre.clear();
					tipoPostreComBox.getSelectionModel().clearSelection();
					txtdescripcion.clear();
					imgfoto.setImage(null);
				}
			
		//METODO PARA VOLVER A LA PANTALLA DEL MENU PRINCIPAL/RECETARIO
			public void irARecetas() {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetas.fxml"));
		            Stage stage = (Stage) flechaRecetario.getScene().getWindow();
		            stage.setScene(new Scene(loader.load()));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
			
		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
			public void mostrarMensaje() {
				Alert alerta = new Alert (AlertType.INFORMATION);
				alerta.setContentText("Receta añadida correctamente");
				alerta.show();
			}
	
	}
	
