package model;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

	public class AccesoDB {
      
		
		private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();			
			
		public static SessionFactory getSessionFactory() {
	        return sessionFactory;
	    }

	    public static Session getSession() {
	        return sessionFactory.openSession();
	    }
 
		 
		 public static void cerrar() {
			 sessionFactory.close();
		 }

		// PARA RECUPERAR LOS INGREDIENTES DE LA BBDD
		 public static List<Ingrediente> getTodosLosIngredientes() {
			    Session session = null;
			    try {
			        session = getSession();
			        return session.createQuery("FROM Ingrediente", Ingrediente.class).list();
			    } catch (Exception e) {
			        e.printStackTrace();
			        return Collections.emptyList();
			    } finally {
			        if (session != null) session.close();
			    }
			}

		 private static Usuario usuarioActual;
		 
		    public static void setUsuarioActual(Usuario user) {
		        usuarioActual = user;
		    }

		    public static Usuario getUsuarioActual() {
		        return usuarioActual;
		    }
		    

		    public static void cerrarSesion() {
		        usuarioActual = null;
		    }
	    
		 private static Ingrediente ingredienteActual;

		 public static void setIngredienteActual(Ingrediente ingred) {
		 		ingredienteActual = ingred;
		 	}
	   
		 public static Ingrediente getIngredienteActual() {
			 return ingredienteActual;
	 	}

		private static Receta recetaActual;

		public static Receta getRecetaActual() {
			return recetaActual;
		}

		public static void setRecetaActual(Receta recetaActual) {
			AccesoDB.recetaActual = recetaActual;
		}
		 
}
	    