package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Ingrediente;
import model.RecetaIngrediente;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class IngredienteController {


	@FXML
	private TextField nombreTxt;
	@FXML
	private ComboBox<String> tipoCombo;
	@FXML
	private TextField caloriasTxt;
	@FXML
	private CheckBox procesadoCheck;
	@FXML	
	private Button añadirButton;
	@FXML	
	private Button editarButton;
	@FXML
	private ImageView flechaRecetario;
	@FXML
	private TableView<Ingrediente> tablaIngredientes;
	@FXML
	private TableColumn<Ingrediente, String> columNombre;
	@FXML
	private TableColumn<Ingrediente, String> columTipo;
	@FXML
	private TableColumn<Ingrediente, Integer> columCalorias;
	@FXML
	private TableColumn<Ingrediente, Boolean> columProcesado;
	@FXML
	private TableColumn<Ingrediente, Void> colEliminar; 
	@FXML
	private ObservableList<Ingrediente> listaIngredientes = FXCollections.observableArrayList();
	
	@FXML
	public void initialize () {
		  
		
		cargarIngredDesdeDB();
		
		columNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
		columTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
		columCalorias.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCalorias100gr()).asObject());
		columProcesado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isIngredienteProcesado()).asObject());
		botonEliminar();
		
		tablaIngredientes.setItems(listaIngredientes);
     
        tipoCombo.getItems().addAll(
		        "Frutas/Verduras",
		        "Bebida",
		        "Cereales",
		        "Frutos secos",
		        "Lácteos",
		        "Especies",
		        "Otros"
		    );
	
        añadirButton.setOnAction(e -> añadirIngrediente());
        editarButton.setOnAction(e -> editarIngrediente());
    }
	
	private Usuario usuarioActual;

	public void setUsuarioActual(Usuario user) {
	    this.usuarioActual = user;
	}
	
	@FXML
	public void añadirIngrediente() {
	
	// VINCULAMOS LA LISTA DE INGREDIENTES A LA TABLA
	tablaIngredientes.setItems(listaIngredientes);

	Ingrediente ingred = new Ingrediente(
		usuarioActual,
		nombreTxt.getText(),
		tipoCombo.getSelectionModel().getSelectedItem(),
		Integer.parseInt(caloriasTxt.getText()),
		procesadoCheck.isSelected()
			);
	listaIngredientes.add(ingred);
//	limpiarFormulario(false);
			
	// PARA ASIGNAR USUARIO ACTUAL
	Usuario user = AccesoDB.getUsuarioActual();
	ingred.setUser(user); 
	
	// CONEXION CON LA BBDD E INSETAR NUEVO INGREDIENTE			
	Session session = null;
    Transaction tr = null;
			 
		try {
			session = AccesoDB.getSession();
			tr = session.beginTransaction();
			session.persist(ingred); //INSERTA EL INGREDIENTE EN LA BBDD
			tr.commit();				
			System.out.println("Ingrediente añadido correctamente.");
			mostrarMensaje();
			limpiarFormulario();
			
		} catch (Exception e) {
			if (tr != null) tr.rollback();
            e.printStackTrace();
            System.out.println("Error al añadir ingrediente.");	           
        } finally {
            if (session != null) session.close();
		}              						
	}

	@FXML	
	public void editarIngrediente() {
		
		Ingrediente ingred = AccesoDB.getIngredienteActual();
			//ACTUALIZAR CAMPOS CON LOS DATOS INTRODUCIDOS
			ingred.setNombre(nombreTxt.getText());
			ingred.setTipo(tipoCombo.getValue());
			ingred.setCalorias100gr(Integer.parseInt(caloriasTxt.getText()));
			ingred.setIngredienteProcesado(procesadoCheck.isSelected());
			
			tablaIngredientes.refresh();
			limpiarFormulario();
							
		// CONEXION CON LA BBDD Y ACTUALIZACION DE LOS DATOS	
		Session session = null;
	    Transaction tr = null;
				 
			try {
				session = AccesoDB.getSession();
				tr = session.beginTransaction();
				session.merge(ingred); //ACTUALIZA EL INGREDIENTE EN LA BBDD
				tr.commit();				
				System.out.println("Ingrediente actualizado correctamente.");
				mostrarMensaje();
		//		irARecetario();
				
			} catch (Exception e) {
				if (tr != null) tr.rollback();
	            e.printStackTrace();
	            System.out.println("Error al actualizar ingrediente.");	           
	        } finally {
	            if (session != null) session.close();
			}              								
	}
	
	//CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
	private void botonEliminar() {
		
	    colEliminar.setCellFactory(param -> new TableCell<>() {
	        private final Button btn = new Button("✖");

	        {
	            btn.setOnAction(event -> {
	                Ingrediente ingred = getTableView().getItems().get(getIndex());
	                
	                //ELIMINAR DE LA BBDD
	                Session session = null;
	        	    Transaction tr = null;
	        		try {
	        			session = AccesoDB.getSession();
	        			tr = session.beginTransaction();
	        			session.remove(session.merge(ingred)); //BORRA EL INGREDIENTE
	        			tr.commit();					        			        				
	        			} catch (Exception ex) {
	        				if (tr != null) tr.rollback();
	        	            ex.printStackTrace();
	        	            System.out.println("Error al eliminar ingrediente.");	           
	        	        } finally {
	        	            if (session != null) session.close();
	        			}
	        		//ELIMINAR DE LA TABLA DE LA INTERFAZ USUARIO
	                listaIngredientes.remove(ingred);
	            });

	            btn.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 14px;");	            
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
	
		private void cargarIngredDesdeDB() {
			//LIMPIAMOS LISTA Y CONECTAMOS CON LA BBDD
		    listaIngredientes.clear();
		    Session session = null;

		    try {
		        session = AccesoDB.getSession();
		        //Usuario usuarioActual = AccesoDB.getUsuarioActual(); // NOS ASEGURAMOS QUE NO ES NULO
		        //if (usuarioActual != null) {
		            listaIngredientes.addAll(
		                session.createQuery("FROM Ingrediente", Ingrediente.class)
		                       .list()
		            );
		        //}
		    } catch (Exception e) {
		        e.printStackTrace();
		        System.out.println("Error al cargar ingredientes desde la BBDD.");
		    } finally {
		        if (session != null) session.close();
		    }
		}
		
	
		//LIMPIAR DATOS DEL FORMULARIO
		private void limpiarFormulario() {
		 // SOLO LIMPIAR LOS DATOS INTRODUCIDOS
				nombreTxt.clear();
				tipoCombo.getSelectionModel().clearSelection();
				caloriasTxt.clear();
				procesadoCheck.setSelected(false);
			   }

	//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
	public void mostrarMensaje() {
		Alert alerta = new Alert (AlertType.INFORMATION);
		alerta.setContentText("Ingrediente añadido correctamente");
		alerta.show();
	}
	
}

