package com.temain.model;

import com.temain.model.dao.EventsDAO;
import com.temain.model.dao.EventsDAOImpl;
import com.temain.model.dao.PodrsDAO;
import com.temain.model.dao.PodrsDAOImpl;
import com.temain.model.dao.PrintersDAO;
import com.temain.model.dao.PrintersDAOImpl;
import com.temain.model.dao.UserDAO;
import com.temain.model.dao.UserDAOImpl;

public class Factory {
	
	private static Factory instance = null;	
	private static EventsDAO eventsDAO = null;
	private static PodrsDAO podrsDAO = null;
	private static PrintersDAO printersDAO = null;
	private static UserDAO userDAO = null;
	
	public static synchronized Factory getInstance(){
		if(instance == null){
			instance = new Factory();
		}
		return instance;
	}
	
	public static synchronized EventsDAO getEventsDAO(){
		if(eventsDAO == null){
			eventsDAO = new EventsDAOImpl();
		}
		return eventsDAO;
	}
	
	public static synchronized PodrsDAO getPodrsDAO(){
		if(podrsDAO == null){
			podrsDAO = new PodrsDAOImpl();
		}
		return podrsDAO;
	}
	
	public static synchronized PrintersDAO getPrintersDAO(){
		if(printersDAO == null){
			printersDAO = new PrintersDAOImpl();
		}
		return printersDAO;
	}
	
	public static synchronized UserDAO getUserDAO(){
		if(userDAO == null){
			userDAO = new UserDAOImpl();
		}
		return userDAO;
	}
}
