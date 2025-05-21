package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.AccesoDB;
import model.Receta;

public class RankingController {

	@FXML
	private TableView<RankingDatos> tablaRanking;
	@FXML
	private TableColumn<RankingDatos, Integer> columPosicion;
	@FXML
	private TableColumn<RankingDatos, Integer> columPuntuacion;
	@FXML
	private TableColumn<RankingDatos, String> columNombreReceta;
	@FXML
	private TableColumn<RankingDatos, String> columNombreUsuario;
	@FXML
	private TableColumn<RankingDatos, String> columHotel;
	@FXML
	private ImageView flechaRecetario;

	private ObservableList<RankingDatos> listaRanking = FXCollections.observableArrayList();

	
	@FXML
	public void initialize() {
		cargarRanking();
		configurarColumnas();
		
	    tablaRanking.setItems(listaRanking);
	    
	  //AL HACER DOBLE CLIC SOBRE UNA FILA DE RECETA -> ABRE LA FICHA DE LA RECETA
		tablaRanking.setRowFactory(tv -> {
		    TableRow<RankingDatos> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && !row.isEmpty()) {
		        	RankingDatos rd = row.getItem();
		        	Receta recet = rd.getReceta();
		        	
		        	AccesoDB.setRecetaActual(recet);
		        	AccesoDB.setPosicionActualRanking(rd.getPosicion());
		            abrirFichaReceta(recet);
		        }
		    });
		    return row;
		});
	}
		
	//METODO PARA IR A LA PANTALLA FICHA DE LA RECETA
			private void abrirFichaReceta(Receta recet) {
			    try {
			    	//GUARDAMOS LA RECETA ACTUAL EN ACCESODB
			    	AccesoDB.setRecetaActual(recet);
			    	//CARGAMOS LA VISTA FXML
			    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fichaReceta.fxml"));
		            Stage stage = (Stage) tablaRanking.getScene().getWindow();
		            stage.setScene(new Scene(loader.load()));	            
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}	

	private void cargarRanking() {
		try (Session session = AccesoDB.getSession()) {						
			//CARGAMOS RECETAS Y SU USUARIO CREADOR
			List<Receta> recet = session.createQuery(
					"SELECT r FROM Receta r JOIN FETCH r.user u", Receta.class)
					.list();
			
			//ORDENAMOS RECETAS POR PUNTUACION DESCENDENTE (MAS SALUDABLE PRIMERO)
			recet.sort((a, b) -> Integer.compare(b.getPuntuacion(), a.getPuntuacion()));
			listaRanking.clear();
						
			//GENERAMOS LISTA DE POSICIONES SEGUN PUNTUACION Y GUARDAMOS EN BBDD			
			Transaction tr = session.beginTransaction();
			List<RankingDatos> ranking = new ArrayList<>();
			int pos = 1;
			for (Receta receta : recet) { 
					receta.setRanking(pos); //ASIGNAMOS POSICION A LA RECETA
					AccesoDB.recalcularYGuardarRanking();
					ranking.add(new RankingDatos(
					pos,
					receta.getPuntuacion(),
					receta.getNombre(),
					receta.getUser().getNombre() + " " + receta.getUser().getApellidos(),
					receta.getUser().getHotel(),
					receta
					));
					pos++;
			}
			
			tr.commit();
			//AÑADIMOS DATOS RANKING A LA LISTA, ACTUALIZANDO LA TABLA EN LA INTERFAZ
			listaRanking.addAll(ranking);
		} catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Error al cargar o guardar ranking.");
	    }
	}
			
	private void configurarColumnas() {
		columPosicion.setCellValueFactory(new PropertyValueFactory<>("posicion"));
		columPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
		columNombreReceta.setCellValueFactory(new PropertyValueFactory<>("nombreReceta"));
		columNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
		columHotel.setCellValueFactory(new PropertyValueFactory<>("nombreHotel"));
	}		
	
	@FXML
	public void irARecetario() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recetario.fxml"));
            Stage stage = (Stage) flechaRecetario.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	// CLASE INTERNA PARA ENCAPSULAR LOS DATOS DE LA TABLA RANKING
	public static class RankingDatos {
       
		private int posicion;
        private int puntuacion;
        private String nombreReceta;
        private String nombreUsuario;
        private String nombreHotel;
        private Receta receta;
       
        public RankingDatos(int posicion, int puntuacion, String nombreReceta, String nombreUsuario, String nombreHotel, Receta receta) {
            this.posicion = posicion;
            this.puntuacion = puntuacion;
            this.nombreReceta = nombreReceta;
            this.nombreUsuario = nombreUsuario;
            this.nombreHotel = nombreHotel;
            this.receta = receta;
        }       

        public int getPosicion() {
        	return posicion; }
        
        public int getPuntuacion() {
        	return puntuacion; }
        
        public String getNombreReceta() {
        	return nombreReceta; }
        
        public String getNombreUsuario() {
        	return nombreUsuario; }
        
        public String getNombreHotel() {
        	return nombreHotel; }
        
        public Receta getReceta() {
        	return receta; }	
	}
	
}
