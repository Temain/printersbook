package com.temain.model.dao;

import java.util.List;

import com.temain.model.entities.Events;
import com.temain.model.entities.Printers;

public interface EventsDAO {
	Integer addEvent(Events ev);
	void updateEvent(Events ev);
	void deleteEvent(Events ev);
	List<Events> getAllEventsByPrinter(Printers printer);
	Events getEventById(Integer id);
}
