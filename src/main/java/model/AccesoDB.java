package model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AccesoDB {

	private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
	
	 public static Session getSession() {
		 return sessionFactory.openSession();
	 }	 
	 
	 public static void cerrar() {
		 sessionFactory.close();
	 }
}