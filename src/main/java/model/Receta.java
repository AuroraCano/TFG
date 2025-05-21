package model;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;


@Entity
@Table(name="receta")
	
public class Receta {
	
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column (name = "id_receta")
private int id;

@ManyToOne
@JoinColumn(name = "id_usuario", nullable = false)
private Usuario user;

private	String nombre;
private	String tipoPostre;
private	String descripcion;

@Column (name = "puntuacion")
private Integer puntuacion;

private int ranking;
private double valoracionMedia;
private String hotel;

@Column (name="foto")
@Lob
private	byte[] foto;

//AÑADIMOS ASI RELACION ENTRE RECETA Y RECETAINGREDIENTE
@OneToMany(mappedBy = "idReceta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
private List<RecetaIngrediente> ingredientes = new ArrayList<>();


@Temporal(TemporalType.TIMESTAMP)
@Column(name = "fecha_creacion", updatable = false) //EVITA CAMBIAR ESTA FECHA EN ACTUALIZACIONES
private Date fechaCreacion;

//CONSTRUCTOR POR DEFECTO PARA HIBERNATE
public Receta() {		
}

//CONSTRUCTOR CON PARAMETROS PARA CREACIÓN MANUAL DE UNA RECETA
public Receta(Usuario user, String nombre, String tipoPostre, String descripcion, byte[] foto) {
		this.user = user;	
		this.nombre = nombre;
		this.tipoPostre = tipoPostre;
		this.descripcion = descripcion;
		this.foto = foto;
		}

//CUANDO INSERTEMOS UNA NUEVA RECETA EN LA BBDD session.persist(), HIBERNATE ASIGNARA AUTOMATICAMENTE LA FECHA ACTUAL
@PrePersist
protected void onCreate() {
this.fechaCreacion = new Date();
}

//CONVERTIMOS EL CAMPO FOTO(BYTE) EN UN OBJETO IMAGE
public Image getImage() {
    if (foto != null && foto.length > 0) {
        return new Image(new ByteArrayInputStream(foto));
    }
    return null;
}

//CONVERTIMOS UN OBJETO IMAGE A UN BYTE[] Y LO GUARDA EN EL CAMPO FOTO (PARA SU ALMACENAMIENTO EN BBDD)
public void setImage(Image img) {
    try {
        BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        this.foto = bos.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public Usuario getUser() {
	return user;
}

public void setUser(Usuario user) {
	this.user = user;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getNombre() {
	return nombre;
}

public void setNombre(String nombre) {
	this.nombre = nombre;
}

public String getDescripcion() {
	return descripcion;
}

public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
}

public byte[] getFoto() {
	return foto;
}

public void setFoto(byte[] foto) {
	this.foto = foto;
}

public String getTipoPostre() {
	return tipoPostre;
}

public void setTipoPostre(String tipoPostre) {
	this.tipoPostre = tipoPostre;
}

public Integer getPuntuacion() { 
	return puntuacion; 
}

public void setPuntuacion(Integer puntuacion) {
	this.puntuacion = puntuacion; 
}

public int getRanking() {
	return ranking;
}
public void setRanking(int ranking) {
	this.ranking = ranking;
}

public double getValoracionMedia() {
	return this.valoracionMedia; //Nº DE ESTRELLAS
	}

public void setValoracionMedia(double valoracionMedia) {
	this.valoracionMedia = valoracionMedia;
}

public String getHotel() {
	return hotel;
}

public List<RecetaIngrediente> getIngredientes() {
    return ingredientes;
}

public void setIngredientes(List<RecetaIngrediente> ingredientes) {
    this.ingredientes = ingredientes;
}

}