package model;

import jakarta.persistence.*;

@Entity
@Table(name="ingrediente")

public class Ingrediente {

		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "id_ingrediente")
	private int id;
	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario user;	
	private	String nombre;
	private	String tipo;
	
    @Column(name = "calorias_100gr")
	private	int calorias100gr;
    
    @Column(name = "ingrediente_procesado")
	private	boolean ingredienteProcesado;
    
    @Column(name="azucar_100gr")
    private Boolean azucar100gr = false;

	//CONSTRUCTOR POR DEFECTO PARA HIBERNATE
	public Ingrediente() {		
	}

	//CONSTRUCTOR CON PARAMETROS PARA CREACIÓN MANUAL DE UN INGREDIENTE
	public Ingrediente(Usuario user, String nombre, String tipo, int calorias100gr, boolean ingredienteProcesado) {
			this.user = user;	
			this.nombre = nombre;
			this.tipo = tipo;
			this.calorias100gr = calorias100gr;
			this.ingredienteProcesado = ingredienteProcesado;
			}

	@Override
	public String toString() {
	    return this.nombre;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	    this.azucar100gr = nombre.contains("azucar");
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getCalorias100gr() {
		return calorias100gr;
	}

	public void setCalorias100gr(int calorias100gr) {
		this.calorias100gr = calorias100gr;
	}

	public boolean isIngredienteProcesado() {
		return ingredienteProcesado;
	}

	public void setIngredienteProcesado(boolean ingredienteProcesado) {
		this.ingredienteProcesado = ingredienteProcesado;
	}

	public boolean isAzucar100gr() {
		return azucar100gr != null && azucar100gr;
	}

	public void setAzucar100gr(boolean azucar100gr) {
		this.azucar100gr = azucar100gr;
	}
		
}
