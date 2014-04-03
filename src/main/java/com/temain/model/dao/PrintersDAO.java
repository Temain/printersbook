package com.temain.model.dao;

import java.util.List;
import com.temain.model.entities.Printers;

public interface PrintersDAO {
	Integer addPrinter(Printers printer);
	List<Printers> getAllPrinters();
	void updatePrinter(Printers printer);
	Printers getPrinterById(Integer Id);
	void deletePrinter(Printers printer);
}
