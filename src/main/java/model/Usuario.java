package model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name="usuario")
	
public class Usuario {
	
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column (name = "id_usuario")
private int id;

private	String nombre;
private	String apellidos;

@Column(unique = true)
private	String email;

private	String password;
private	String hotel;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "fecha_registro", updatable = false) //EVITA CAMBIAR ESTA FECHA EN ACTUALIZACIONES
private Date fechaRegistro;

//CONSTRUCTOR POR DEFECTO PARA HIBERNATE
public Usuario() {		
}

//CONSTRUCTOR CON PARAMETROS PARA CREACIÓN MANUAL
public Usuario(String nombre, String apellidos, String email, String password, String hotel) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
		this.hotel = hotel;	
		}

		//CUANDO INSERTEMOS UN REGISTRO EN LA BBDD (session.persist(user);)
		//HIBERNATE ASIGNARA AUTOMATICAMENTE LA FECHA ACTUAL
		@PrePersist
		protected void onCreate() {
		this.fechaRegistro = new Date();
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

		public String getApellidos() {
			return apellidos;
		}

		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getHotel() {
			return hotel;
		}

		public void setHotel(String hotel) {
			this.hotel = hotel;
		}
}