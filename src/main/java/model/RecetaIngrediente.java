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
	
		// METODO QUE INDICA LA PUNTUACION DE CADA RECETA PARA EL RANKING SALUDABLE
		public static int calcularPuntuacion(Receta recet) {
			double caloriasTotal = 0;
			double grTotal = 0;
			double grAzucar = 0;
			double grProcesados = 0;
				
			for (RecetaIngrediente ri : recet.getIngredientes()) {						
				Ingrediente ing = ri.getId_ingrediente();
				int cantidad = ri.getCantidad();
				
				//CALORIAS TOTALES DE ESE INGREDIENTE
				caloriasTotal += (ing.getCalorias100gr() * cantidad) / 100.0;
				grTotal += cantidad;				
				if (ing.isAzucar100gr()) {
					grAzucar += cantidad;			
				}				
				if (ing.isIngredienteProcesado()) {
					grProcesados += cantidad;	
				}
			}			
			
			if (grTotal ==0) return 0; //EVITAMOS DIVISION POR CERO
			
			double calorias100gr = (caloriasTotal / grTotal) * 100;		
			
			double puntosCalorias = Math.max(0, (10 - (calorias100gr /100.0)));
			double puntosAzucar = Math.max(0, (1 - (grAzucar / grTotal))*10.0);
			double puntosProc = Math.max(0, (1 - (grProcesados / grTotal))*10.0);

			//ESCALA DE PUNTUACION DE 0 A 30
			return (int) Math.round(puntosCalorias + puntosAzucar + puntosProc);			
		}
 
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
