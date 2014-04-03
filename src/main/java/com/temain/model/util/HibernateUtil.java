package com.temain.model.util;

import java.util.logging.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	
	private static Logger log = Logger.getLogger(HibernateUtil.class.getName());
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	static{
		try{
			Configuration configuration = new Configuration();
		    configuration.configure();
		    serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
		    sessionFactory = configuration.buildSessionFactory(serviceRegistry);

		}catch(Exception ex){
			log.severe("Create SessionFactory is failed! " + ex.getMessage());
		}
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
}
