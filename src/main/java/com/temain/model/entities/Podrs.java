package com.temain.model.entities;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="podrs")
public class Podrs  implements java.io.Serializable {

    @Id 
    @SequenceGenerator(name="podrs_id_seq", sequenceName="podrs_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "podrs_id_seq")
    @Column(name="id", unique=true, nullable=false)
    private int id;
    
    @Column(name="title", nullable=false, length=256)
    private String title;
    
    @Column(name="matotv", nullable=false, length=128)
    private String matOtv;
    
    @Column(name="telefon", nullable=false, length=32)
    private String telefon;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="podrs")
    private Set<Printers> printers;
   
    public Podrs(){  	
    }
    
    public Podrs(int id, String title){
    	this.id = id;
    	this.title = title;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

	public String getMatOtv() {
		return matOtv;
	}

	public void setMatOtv(String matOtv) {
		this.matOtv = matOtv;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
    
    public Set<Printers> getPrinters() {
        return this.printers;
    }
    
    public void setPrinters(Set<Printers> printers) {
        this.printers = printers;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Podrs other = (Podrs) obj;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}


