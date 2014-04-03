package com.temain.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;

import com.temain.model.entities.Podrs;
import com.temain.model.entities.User;
import com.temain.model.util.HibernateUtil;

public class UserDAOImpl implements UserDAO {

	private static Logger log = Logger.getLogger(UserDAOImpl.class.getName()); 
	
	@Override
	public List<User> getAllUsers() {
		Session session = null;
	    List<User> users = new ArrayList<User>();
	    try {
	      session = HibernateUtil.getSessionFactory().openSession();
	      users = session.createCriteria(User.class).list();
	      Iterator<User> it = users.iterator();
	      while (it.hasNext()) {
			User user = (User) it.next();
			user.getRole();//for lazy
		}
	    } catch (Exception e) {
	    	log.severe("Error on get all 'users': " + e.getMessage());
	    } finally {
	      if (session != null && session.isOpen()) {
	        //session.close();
	      }
	    }
	    return users;
	}

}
