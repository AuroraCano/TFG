package model;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class AccesoDB {
      		
	private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();			
	private static Usuario usuarioActual;	
	private static Ingrediente ingredienteActual;
	private static Receta recetaActual;
		 
	public static void cerrar() {
		sessionFactory.close();
	}

	// METODO PARA LA CONEXION CON LA BBDD
	public static void accederDB(java.util.function.Consumer<Session> accion) {
		Session session = null;
		Transaction tr = null;
		try {
			session = AccesoDB.getSession();
			tr = session.beginTransaction();
			accion.accept(session);
			tr.commit();
		} catch (Exception e) {
			if (tr != null)
				tr.rollback();
			e.printStackTrace();
			System.out.println("Error en la conexión con la base de datos.");
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	
	public static void recalcularYGuardarRanking() {
	    Session session = null;
	    Transaction tr = null;
	    try {
	        session = getSession();
	        tr = session.beginTransaction();

	        List<Receta> recetas = session.createQuery("FROM Receta", Receta.class).list();
	        recetas.sort((a, b) -> Integer.compare(b.getPuntuacion(), a.getPuntuacion()));

	        int pos = 1;
	        for (Receta r : recetas) {
	            r.setRanking(pos++);
	            session.merge(r); // ACTUALIZA POSICION RANKING EN ESA RECETA
	        }

	        tr.commit();
	    } catch (Exception e) {
	        if (tr != null) tr.rollback();
	        e.printStackTrace();
	    } finally {
	        if (session != null) session.close();
	    }
	}

	
	//PARA ACCEDER A LA POSICION ACTUAL EN EL RANKING
	private static int posicionActualRanking = -1;

	public static int getPosicionActualRanking() {
	    return posicionActualRanking;
	}

	public static void setPosicionActualRanking(int posicion) {
	    posicionActualRanking = posicion;
	}
	
	// PARA RECUPERAR TODOS LOS INGREDIENTES DE LA BBDD
	public static List<Ingrediente> getTodosLosIngredientes() {
		Session session = null;
		try {
			session = getSession();
			return session.createQuery("FROM Ingrediente", Ingrediente.class).list();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			if (session != null)
				session.close();
		}
	}
	
	// PARA RECUPERAR TODAS LAS RECETAS DE LA BBDD
	public static List<Receta> getTodasLasRecetas() {
		Session session = null;
		try {
			session = getSession();
			return session.createQuery("FROM Receta", Receta.class).list();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			if (session != null)
				session.close();
		}
	}
			 		 		 		 
		
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Session getSession() {
		return sessionFactory.openSession();
	}

	public static void setUsuarioActual(Usuario user) {
		usuarioActual = user;
	}

	public static Usuario getUsuarioActual() {
		return usuarioActual;
	}

	public static void cerrarSesion() {
		usuarioActual = null;
	}

	public static void setIngredienteActual(Ingrediente ingred) {
		ingredienteActual = ingred;
	}

	public static Ingrediente getIngredienteActual() {
		return ingredienteActual;
	}

	public static Receta getRecetaActual() {
		return recetaActual;
	}

	public static void setRecetaActual(Receta recetaActual) {
		AccesoDB.recetaActual = recetaActual;
	}
		 
}
	    