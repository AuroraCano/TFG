package model;

import jakarta.persistence.*;


@Entity
@Table(name="linea_receta")

public class RecetaIngrediente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "id_linea")
	private int idLinea;
	
	@ManyToOne
	@JoinColumn(name = "id_receta", nullable = false)
	private Receta idReceta;
	
	@ManyToOne
	@JoinColumn(name = "id_ingrediente", nullable = false)
	private Ingrediente idIngrediente;
	
	private	int cantidad;
	
	public Receta getId_receta() {
		return idReceta;
	}
	
	public void setId_receta(Receta idReceta) {
		this.idReceta = idReceta;
	}
	
	public Ingrediente getId_ingrediente() {
		return idIngrediente;
	}
	
	public void setId_ingrediente(Ingrediente idIngrediente) {
		this.idIngrediente = idIngrediente;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
}
