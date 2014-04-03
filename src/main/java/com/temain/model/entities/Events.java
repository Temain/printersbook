package com.temain.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="events")
public class Events  implements java.io.Serializable {

    @Id  
    @SequenceGenerator(name="events_id_seq", sequenceName="events_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_id_seq")
    @Column(name="id", unique=true, nullable=false)
    private int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="printerid", nullable=false)
    private Printers printers;
    
    @Column(name="description", nullable=false, length=256)
    private String description;
   
    @Column(name="date", nullable=false)
    private String date;

	public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public Printers getPrinters() {
        return this.printers;
    }
    
    public void setPrinters(Printers printers) {
        this.printers = printers;
    }

    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
    
}


