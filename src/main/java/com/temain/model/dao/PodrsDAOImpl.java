package com.temain.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.temain.model.entities.Podrs;
import com.temain.model.util.HibernateUtil;

public class PodrsDAOImpl implements PodrsDAO{

	private static Logger log = Logger.getLogger(PodrsDAOImpl.class.getName()); 
	
	@Override
	public Integer addPodr(Podrs podr) {
		Session session = null;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(podr);
			session.getTransaction().commit();
			return podr.getId();
		}catch(Exception e){
			log.severe("Error on insert 'podr':" + e.getMessage());
		}finally{
			if (session != null && session.isOpen()) {
		        session.close();
			}
		}	
		return null;
	}

	@Override
	public List<Podrs> getAllPodrs() {
		Session session = null;
	    List<Podrs> podrs = new ArrayList<Podrs>();
	    try {
	      session = HibernateUtil.getSessionFactory().openSession();
	      Criteria c = session.createCriteria(Podrs.class);
	      c.addOrder(Order.asc("title"));
	      podrs = c.list();
	      Iterator it = podrs.iterator();
	      while(it.hasNext()){
	    	  Podrs podr = (Podrs)it.next();
	    	  podr.getPrinters();//for lazy initialization
	      }
	    } catch (Exception e) {
	    	log.severe("Error on get all 'podrs': " + e.getMessage());
	    } finally {
	      if (session != null && session.isOpen()) {
	        //session.close();
	      }
	    }
	    return podrs;
	}

	@Override
	public Podrs getPodrById(Integer Id) {
		Session session = null;
	    Podrs podr = null;
	    try {
	      session = HibernateUtil.getSessionFactory().openSession();
	      podr = (Podrs) session.get(Podrs.class, Id);
	    } catch (Exception e) {
	    	log.severe("Error on podrFindById: " + e.getMessage());
	    } finally {
	      if (session != null && session.isOpen()) {
	        session.close();
	      }
	    }
	    return podr;	  
	}

	@Override
	public void deletePodr(Podrs podr) {
		Session session = null;
	    try {
	       session = HibernateUtil.getSessionFactory().openSession();
	       session.beginTransaction();
	       session.delete(podr);
	       session.getTransaction().commit();
	    } catch (Exception e) {
	    	log.severe("Error on delete 'podr': " + e.getMessage());
	    } finally {
	    	if (session != null && session.isOpen()) {
	    		session.close();
	    	}
	    }	
	}

	@Override
	public void updatePodr(Podrs podr) {
		Session session = null;
		try {
		    session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    session.update(podr);
		    session.getTransaction().commit();
		} catch (Exception e) {
			log.severe("Error on update 'podr': " + e.getMessage());
		} finally {
		    if (session != null && session.isOpen()) {
		       session.close();
		    }
		}	
	}
}
