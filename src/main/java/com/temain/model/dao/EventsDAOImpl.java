package com.temain.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.temain.model.entities.Events;
import com.temain.model.entities.Printers;
import com.temain.model.util.HibernateUtil;

public class EventsDAOImpl implements EventsDAO{

	private static Logger log = Logger.getLogger(EventsDAOImpl.class.getName()); 
	
	@Override
	public Integer addEvent(Events ev) {
		Session session = null;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(ev);
			session.getTransaction().commit();
			return ev.getId();
		}catch(Exception e){
			log.severe("Error on insert 'event': " + e.getMessage());
		}finally{
			if (session != null && session.isOpen()) {
		        session.close();
			}
		}	
		return null;
	}

	@Override
	public List<Events> getAllEventsByPrinter(Printers printer) {
		Session session = null;
	    List<Events> events = new ArrayList<Events>();
	    try {
	        session = HibernateUtil.getSessionFactory().openSession();
	        Criteria c = session.createCriteria(Events.class);
	        c.add(Restrictions.eq("printers", printer));
	        c.addOrder(Order.asc("description"));
	        events = c.list();
	    }catch (Exception e) {
	        log.severe("Error on get all 'printers': " + e.getMessage());
	    }finally {
	        if (session != null && session.isOpen()) {
	          session.close();
	        }
	    }
	    return events;
	}

	@Override
	public Events getEventById(Integer id) {
		Session session = null;
	    Events event = null;
	    try {
	      session = HibernateUtil.getSessionFactory().openSession();
	      event = (Events) session.get(Events.class, id);
	    } catch (Exception e) {
	    	log.severe("Error on eventFindById: " + e.getMessage());	      
	    } finally {
	      if (session != null && session.isOpen()) {
	        session.close();
	      }
	    }
	    return event;
	}

	@Override
	public void updateEvent(Events ev) {
		Session session = null;
		try {
		    session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    session.update(ev);
		    session.getTransaction().commit();
		} catch (Exception e) {
			log.severe("Error on update 'event': " + e.getMessage());
		} finally {
		    if (session != null && session.isOpen()) {
		       session.close();
		    }
		}
	}

	@Override
	public void deleteEvent(Events ev) {
		Session session = null;
	    try {
	       session = HibernateUtil.getSessionFactory().openSession();
	       session.beginTransaction();
	       session.delete(ev);
	       session.getTransaction().commit();
	    } catch (Exception e) {
	    	log.severe("Error on delete 'event': " + e.getMessage());
	    } finally {
	    	if (session != null && session.isOpen()) {
	    		session.close();
	    	}
	    }	
	}

}
