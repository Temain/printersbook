package com.temain.model.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name="printers")
public class Printers  implements java.io.Serializable {


    @Id  
    @SequenceGenerator(name="printers_id_seq", sequenceName="printers_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "printers_id_seq")
    @Column(name="id", unique=true, nullable=false)
    private int id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="podrid", nullable = false)
    private Podrs podrs;
    
    @Column(name="invent", length=11)
    private String invent;
    
    @Column(name="model", length=128)
    private String model;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="printers")
    private Set<Events> events;

    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public Podrs getPodrs() {
        return this.podrs;
    }
    
    public void setPodrs(Podrs podrs) {
        this.podrs = podrs;
    }

    public String getInvent() {
        return this.invent;
    }
    
    public void setInvent(String invent) {
        this.invent = invent;
    }

    public String getModel() {
        return this.model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }

    public Set<Events> getEvents() {
        return this.events;
    }
    
    public void setEvents(Set<Events> events) {
        this.events = events;
    }




}


