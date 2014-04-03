package com.temain.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.temain.model.entities.Printers;
import com.temain.model.util.HibernateUtil;

public class PrintersDAOImpl implements PrintersDAO{

	private static Logger log = Logger.getLogger(PrintersDAOImpl.class.getName()); 
	
	@Override
	public Integer addPrinter(Printers printer) {
		Session session = null;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(printer);
			session.getTransaction().commit();
			return printer.getId();
		}catch(Exception e){
			log.severe("Error on insert 'printer': " + e.getMessage());
		}finally{
			if (session != null && session.isOpen()) {
		        session.close();
			}
		}
		return null;		
	}

	@Override
	public List<Printers> getAllPrinters() {
		Session session = null;
	    List<Printers> printers = new ArrayList<Printers>();
	    try {
	        session = HibernateUtil.getSessionFactory().openSession();
	        Criteria c = session.createCriteria(Printers.class);
	        c.addOrder(Order.asc("podrid"));
	        printers = c.list();
	    }catch (Exception e) {
	    	log.severe("Error on get all 'printers': " + e.getMessage());
	    }finally {
	        if (session != null && session.isOpen()) {
	          session.close();
	        }
	    }
	    return printers;
	}

	@Override
	public void updatePrinter(Printers printer) {
		Session session = null;
		try {
		    session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    session.update(printer);
		    session.getTransaction().commit();
		} catch (Exception e) {
			log.severe("Error on update 'printer': " + e.getMessage());
		} finally {
		    if (session != null && session.isOpen()) {
		       session.close();
		    }
		}
	}
	
	public Printers getPrinterById(Integer id){
	    Session session = null;
	    Printers printer = null;
	    try {
	      session = HibernateUtil.getSessionFactory().openSession();
	      printer = (Printers) session.get(Printers.class, id);
	    } catch (Exception e) {
	    	log.severe("Error on printerFindById: " + e.getMessage());
	    } finally {
	      if (session != null && session.isOpen()) {
	        session.close();
	      }
	    }
	    return printer;
	  }

	@Override
	public void deletePrinter(Printers printer) {
		Session session = null;
	    try {
	       session = HibernateUtil.getSessionFactory().openSession();
	       session.beginTransaction();
	       session.delete(printer);
	       session.getTransaction().commit();
	    } catch (Exception e) {
	    	log.severe("Error on delete 'printer': " + e.getMessage());
	    } finally {
	    	if (session != null && session.isOpen()) {
	    		session.close();
	    	}
	    }	
	}
}
