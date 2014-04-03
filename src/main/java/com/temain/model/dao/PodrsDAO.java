package com.temain.model.dao;

import java.util.List;

import com.temain.model.entities.Podrs;
import com.temain.model.entities.Printers;

public interface PodrsDAO {
	Integer addPodr(Podrs podr);
	List<Podrs> getAllPodrs();
	Podrs getPodrById(Integer Id);
	void deletePodr(Podrs podr);
	void updatePodr(Podrs podr);
}
