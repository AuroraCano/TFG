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
import javafx.scene.control.ButtonType;
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
import model.Usuario;
import java.text.Normalizer;
import org.hibernate.Session;

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
	private Button masButton;
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
		configurarColumnas();
		añadirColumnaEliminar();
		configurarComboTipoPostre();       
		tablaIngredientes.setItems(listaIngredientes);
        añadirButton.setOnAction(e -> añadirIngrediente());
        editarButton.setOnAction(e -> editarIngrediente());
        
        //PARA ACCEDER A LOS DATOS DE LA FILA INGREDIENTE SELECCIONADA
        tablaIngredientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                rellenarFormulario(newSel);
                AccesoDB.setIngredienteActual(newSel);
            }
        });
    }		
	
	private Usuario usuarioActual;

	public void setUsuarioActual(Usuario user) {
	    this.usuarioActual = user;
	}
	
	private void configurarComboTipoPostre() {
		tipoCombo.getItems().addAll(
		        "Frutas/Verduras",
		        "Bebida",
		        "Cereales",
		        "Frutos secos",
		        "Lácteos",
		        "Especies",
		        "Otros"
		    );	
	}
	
	private void configurarColumnas() {
		columNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
		columTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
		columCalorias.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCalorias100gr()).asObject());
		columProcesado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isIngredienteProcesado()).asObject());	
	}
	
	//METODO PARA IGNORAR MAYUSCULAS, MINUSCULAS Y TILDES
	private String normalizarTexto(String texto) {
	    return Normalizer.normalize(texto, Normalizer.Form.NFD)
	                     .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
	                     .toLowerCase()
	                     .trim();
		}
	
	@FXML
	public void añadirIngrediente() {
		
		//REALIZAMOS VALIDACION ANTES DE AÑADIR UN NUEVO INGREDIENTE
		 String nombreIngresado = normalizarTexto(nombreTxt.getText());

		    // BUSCAMOS SI YA EXIXTE EN LA LISTA INGREDIENTES
		    boolean existe = listaIngredientes.stream()
		        .anyMatch(ingred -> normalizarTexto(ingred.getNombre()).equals(nombreIngresado));
		    if (existe) {
		        mostrarError("Ese ingrediente ya existe.");
		        return; // DETIENE EL METODO añadirIngrediente()
		    }

		//CREACION INGREDIENTE CON LOS DATOS INTRODUCIDOS POR EL USUARIO (ASIGNANDO EL USUARIO ACTUAL)
		Ingrediente ingred = new Ingrediente(usuarioActual, nombreTxt.getText(),
				tipoCombo.getSelectionModel().getSelectedItem(), Integer.parseInt(caloriasTxt.getText()),
				procesadoCheck.isSelected());
		//SE AÑADE A LA TABLA INGREDIENTES DE LA INTERFAZ
		listaIngredientes.add(ingred);		
		
		//ACCESO A LA BBDD E INSERTAR EL NUEVO INGREDIENTE EN BBDD
		AccesoDB.accederDB(session -> session.persist(ingred));
		mostrarMensaje("Ingrediente añadido correctamente");
		limpiarFormulario();

	}	
	
	@FXML	
	public void editarIngrediente() {
		
		Ingrediente ingred = AccesoDB.getIngredienteActual();
			//ACTUALIZAR CAMPOS CON LOS DATOS INTRODUCIDOS
			ingred.setNombre(normalizarTexto(nombreTxt.getText()));
			ingred.setTipo(tipoCombo.getValue());
			ingred.setCalorias100gr(Integer.parseInt(caloriasTxt.getText()));
			ingred.setIngredienteProcesado(procesadoCheck.isSelected());
			
			tablaIngredientes.refresh();
			limpiarFormulario();
			//ACCESO A LA BBDD Y ACTUALIZAR EL INGREDIENTE EN BBDD
			AccesoDB.accederDB(session -> session.merge(ingred));
			mostrarMensaje("Ingrediente actualizado correctamente");
			
	}
			           									
	// CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
	private void añadirColumnaEliminar() {
	
		colEliminar.setCellFactory(param -> new TableCell<>() {
			private final Button btn = new Button("✖");
				{
				btn.setOnAction(event -> {
					Ingrediente ingred = getTableView().getItems().get(getIndex());
					//MOSTRAR AL USUARIO MENSAJE DE AVISO ANTES DE ELIMINAR INGREDIENTE Y ESPERAR CONFIRMACION					
					Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
				    confirmacion.setTitle("Confirmar eliminación");
				    confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar este ingrediente?");
				    confirmacion.setContentText("Ingrediente: " + ingred.getNombre());

				    confirmacion.showAndWait().ifPresent(respuesta -> {
				        if (respuesta == ButtonType.OK) {
					// ACCESO A LA BBDD Y ELIMINAR EL INGREDIENTE
				    AccesoDB.accederDB(session -> session.remove(session.merge(ingred)));				
					// ELIMINAR DE LA TABLA DE LA INTERFAZ USUARIO
					listaIngredientes.remove(ingred);
				        }
				    });
				});    	
				btn.setStyle ("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-size: 14px;");
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
	
	//AUTO RELLENO DEL FORMULARIO AL SELECCIONAR CADA FILA INGREDIENTE
		private void rellenarFormulario (Ingrediente ingred) {
			nombreTxt.setText(ingred.getNombre());
			tipoCombo.setValue(ingred.getTipo());
			caloriasTxt.setText(String.valueOf(ingred.getCalorias100gr()));
			procesadoCheck.setSelected(ingred.isIngredienteProcesado());
			añadirButton.setVisible(false); //OCULTAMOS EL BOTON AÑADIR
			editarButton.setVisible(true); //MOSTRAMOS EL BOTON EDITAR
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
			listaIngredientes.clear();
			Session session = null;

			try {
				session = AccesoDB.getSession();
				listaIngredientes.addAll(session.createQuery(
						"FROM Ingrediente ORDER BY nombre ASC", Ingrediente.class)
						.list());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al cargar ingredientes desde la BBDD.");
			} finally {
				if (session != null)
					session.close();
			}
		}
		
		@FXML
		public void alPulsarMas() {
		    limpiarFormulario(); 
		    editarButton.setVisible(false); //OCULTAMOS EL BOTON EDITAR
		    añadirButton.setVisible(true); // MOSTRAMOS EL BOTON AÑADIR
		}
		
		// LIMPIAR DATOS DEL FORMULARIO
		public void limpiarFormulario() {
			// SOLO LIMPIAR LOS DATOS INTRODUCIDOS
			nombreTxt.clear();
			tipoCombo.getSelectionModel().clearSelection();
			caloriasTxt.clear();
			procesadoCheck.setSelected(false);
		}

		//METODO PARA MOSTRAR MENSAJE INFORMATIVO EN LA INTERFAZ DE USUARIO
		public void mostrarMensaje(String mensaje) {
			Alert alerta = new Alert (AlertType.INFORMATION);
			alerta.setTitle("Información");
			alerta.setContentText(mensaje);
			alerta.show();
		}
		
		//METODO PARA MOSTRAR MENSAJE DE ERROR EN LA INTERFAZ DE USUARIO
		private void mostrarError(String mensaje) {
		    Alert alerta = new Alert(AlertType.ERROR);
		    alerta.setTitle("Error");
		    alerta.setHeaderText(null);
		    alerta.setContentText(mensaje);
		    alerta.showAndWait();
		}

}

