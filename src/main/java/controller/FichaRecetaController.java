package controller;

import java.util.Comparator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import model.Valoracion;

public class FichaRecetaController {
	
	@FXML
	private Label lblRanking;
	@FXML 
	private Button btnValorar;
	@FXML
	private ComboBox<Integer> comboEstrellas;
	@FXML
	private Label lblValoracionMedia;
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
	private Button actualizarButton;
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
	private Receta recet;
	private Usuario user;
	
	@FXML
	public void initialize() {
		
		comboEstrellas.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
		btnValorar.setOnAction(e -> registrarValoracion());
		
		//CARGARMOS LA RECETA A EDITAR
		recet = AccesoDB.getRecetaActual();
		user = AccesoDB.getUsuarioActual();
		
		if (recet == null || user == null)
			return;

		// AUTO RELLENO DE LOS CAMPOS DE DICHA RECETA
		txtnombre.setText(recet.getNombre());
		tipoPostreComBox.getItems().addAll("Bizcocho", "Tarta", "Magdalena", "Helado", "Bebida", "Otro");
		tipoPostreComBox.setValue(recet.getTipoPostre());
		txtdescripcion.setText(recet.getDescripcion());
		if (recet.getFoto() != null) {
			javafx.scene.image.Image imagen = recet.getImage(); // CONVERTIMOS BYTE[] EN IMAGE
			imgfoto.setImage(imagen); // MOSTRAR LA IMAGEN
		}

		// CARGARMOS TODOS LOS INGREDIENTES QUE EXISTEN EN BBDD ORDENADOS ALFABETICAMENTE
		Session session = AccesoDB.getSession();

		List<Ingrediente> ingredientes = AccesoDB.getTodosLosIngredientes();
		ObservableList<Ingrediente> obsList = FXCollections.observableArrayList(ingredientes);
		SortedList<Ingrediente> sortedList = new SortedList<>(obsList);
		sortedList.setComparator(Comparator.comparing(Ingrediente::getNombre));
		comboIngred.setItems(sortedList);
		session.close();

		// CONFIGURAMOS LA TABLA INGREDIENTES PARA AÑADIR O ELIMINAR
		colIngrediente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId_ingrediente().getNombre()));
		colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
		tablaIngredientes.setItems(listaIngredReceta);
		añadirBotonEliminar();
		btnAgregarIngred.setOnAction(e -> agregarIngrediente());

		// CARGAMOS LOS INGREDIENTES ASOCIADOS A LA RECETA ACTUAL
		session = AccesoDB.getSession();
		List<RecetaIngrediente> ingredRecet = session
				.createQuery("FROM RecetaIngrediente WHERE idReceta.id = :id", RecetaIngrediente.class)
				.setParameter("id", recet.getId()).getResultList();
		session.close();
		listaIngredReceta.clear(); // LIMPIA LA LISTA
		listaIngredReceta.addAll(ingredRecet); // Y AÑADE LOS INGREDIENTES RECUPERADOS
		
		double media = Valoracion.calcularValoracionMedia(recet);
		lblValoracionMedia.setText(media + " ★");
		
		if (Valoracion.yaValorado(user, recet)) {
		    btnValorar.setDisable(true);
		    comboEstrellas.setDisable(true);
		}
		
		// CARGAR POSICIÓN DE ESTA RECETA EN EL RANKING
		lblRanking.setText(recet.getRanking() + "º 🏆");

	}

	private void registrarValoracion() {
		Usuario user = AccesoDB.getUsuarioActual();
		int estrellas = comboEstrellas.getValue();
		
		Valoracion v = new Valoracion();
		v.setRecet(recet);
	    v.setUser(user);
	    v.setEstrellas(estrellas);
	    
	    Session session = AccesoDB.getSession();
	    Transaction tr = session.beginTransaction();
	    session.persist(v);
	    tr.commit();
	    session.close();
	    
	    lblValoracionMedia.setText(String.valueOf(Valoracion.calcularValoracionMedia(recet)));
	    mostrarMensaje("¡Gracias por valorar!");
	    btnValorar.setDisable(true);
	    comboEstrellas.setDisable(true);
	    
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
				ri.setId_receta(recet);
				ri.setId_ingrediente(seleccionado);
				ri.setCantidad(cantidad);

				listaIngredReceta.add(ri);

				// LIMPIAR LOS CAMPOS
				comboIngred.getSelectionModel().clearSelection();
				txtCantidad.clear();
			} catch (NumberFormatException e) {
				mostrarMensaje("La cantidad debe ser un número entero.");
			}
		} else {
			mostrarMensaje("Selecciona un ingrediente y escribe la cantidad.");
		}
	}

	//CREAMOS ACCION DEL BOTON ELIMINAR DENTRO DE LA TABLA
	private void añadirBotonEliminar() {
	    colEliminar.setCellFactory(param -> new TableCell<>() {
	        private final Button btn = new Button("✖");

	        {
	            btn.setOnAction(e -> {
	                RecetaIngrediente ri = getTableView().getItems().get(getIndex());
	                listaIngredReceta.remove(ri);
	            });

	            btn.setStyle("-fx-background-color: #FF6666; -fx-text-fill: white; -fx-font-weight: bold;");
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

	public void actualizarReceta() {
	
		// CONEXION CON LA BBDD Y ACTUALIZAR RECETA (TABLAS: RECETA Y LINEA_RECETA)			
		Session session = null;
	    Transaction tr = null;				 
			try {
				session = AccesoDB.getSession();
				tr = session.beginTransaction();
				//ACTUALIZA CAMPOS
				recet.setNombre(txtnombre.getText());
				recet.setTipoPostre(tipoPostreComBox.getValue());
				recet.setDescripcion(txtdescripcion.getText());
				if(imgfoto.getImage()!=null) {
					recet.setImage(imgfoto.getImage());
				}
				//ASIGNAMOS INGREDIENTES ACTUALIZADOS + CALCULAMOS PUNTUACION RECETA + GUARDAMOS RECETA
				recet.setIngredientes(listaIngredReceta);
				recet.setPuntuacion(RecetaIngrediente.calcularPuntuacion(recet));				
				session.merge(recet);
				AccesoDB.recalcularYGuardarRanking(); // ACTUALIZAMOS POSICION RANKING SEGUN PUNTUACION
				tr.commit();				
				mostrarMensaje("Receta actualizada correctamente.");
				irARecetas();
				
			} catch (Exception e) {
				if (tr != null) tr.rollback();
	            e.printStackTrace();
	            System.out.println("Error al editar receta.");
	            mostrarError("Error al editar receta.");
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
	
		    java.io.File file = fileChooser.showOpenDialog(actualizarButton.getScene().getWindow());
		    if (file != null) {
		        javafx.scene.image.Image imagen = new javafx.scene.image.Image(file.toURI().toString());
		        imgfoto.setImage(imagen); // MUESTRA LA IMAGEN EN IMAGEVIEW
		    }
		}
									
		//METODO PARA VOLVER A LA PANTALLA DE LISTA DE RECETAS
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
			public void mostrarMensaje(String mensaje) {
				Alert alerta = new Alert (AlertType.INFORMATION);
				alerta.setTitle("Información");
				alerta.setContentText(mensaje);
				alerta.show();
			}
		//
			public void mostrarError(String mensaje) {
				Alert alerta = new Alert(AlertType.ERROR);
			    alerta.setTitle("Error");
			    alerta.setHeaderText(null);
			    alerta.setContentText(mensaje);
			    alerta.showAndWait();			
			}
	
	}	
