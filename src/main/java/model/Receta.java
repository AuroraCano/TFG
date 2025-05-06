package model;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name="receta")
	
public class Receta {
	
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column (name = "id_receta")
private int id;

private	String nombre;
private	String descripcion;
private	byte[] foto;
private	String tipoPostre;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "fecha_creación", updatable = false) //EVITA CAMBIAR ESTA FECHA EN ACTUALIZACIONES
private Date fechaCreacion;

//CONSTRUCTOR POR DEFECTO PARA HIBERNATE
public Receta() {		
}

//CONSTRUCTOR CON PARAMETROS PARA CREACIÓN MANUAL DE UNA RECETA
public Receta(String nombre, String descripcion, byte[] foto, String tipoPostre) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.foto = foto;
		this.tipoPostre = tipoPostre;
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

}