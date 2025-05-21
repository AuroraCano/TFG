package model;

import java.util.Date;

import org.hibernate.Session;

import jakarta.persistence.*;

@Entity
@Table(name="valoracion")

public class Valoracion {
			
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "id_valoracion")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario user;
	
	@ManyToOne
	@JoinColumn(name = "id_receta", nullable = false)
	private Receta recet;	
	private	int estrellas;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_valoracion", updatable = false) //EVITA CAMBIAR ESTA FECHA EN ACTUALIZACIONES
	private Date fechaValoracion;

	public Valoracion() {
		
	}
	
	public Valoracion(Usuario user, Receta recet, int estrellas) {
		this.user = user;
		this.recet = recet;
		this.estrellas = estrellas;	
	}
	
	@PrePersist
	protected void onCreate() {
	this.fechaValoracion = new Date();
	}
	
	public static double calcularValoracionMedia(Receta receta) {
	    try (Session session = AccesoDB.getSession()) {
	        Double media = session.createQuery(
	            "SELECT AVG(v.estrellas) FROM Valoracion v WHERE v.recet.id = :idReceta", Double.class)
	            .setParameter("idReceta", receta.getId())
	            .uniqueResult();
	        return media != null ? Math.round(media * 10.0) / 10.0 : 0.0;
	    }
	}	
	
	//VALIDACION PARA NO REPETIR VOTACION SOBRE UNA RECETA
	public static boolean yaValorado(Usuario user, Receta recet) {
		Session session = AccesoDB.getSession();
		Long count = session.createQuery(
			"SELECT COUNT(v) FROM Valoracion v WHERE v.user.id = :uid AND v.recet.id = :rid",
			Long.class)
			.setParameter("uid", user.getId())		
			.setParameter("rid", recet.getId())
			.uniqueResult();
		session.close();
		return count > 0;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public Receta getRecet() {
		return recet;
	}

	public void setRecet(Receta recet) {
		this.recet = recet;
	}

	public int getEstrellas() {
		return estrellas;
	}

	public void setEstrellas(int estrellas) {
		this.estrellas = estrellas;
	}

	public Date getFechaValoracion() {
		return fechaValoracion;
	}
	
	
}
