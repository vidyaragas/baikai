/**
 * Model for NseBaikai for UI
 */
package com.ajna;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NseBaikaiModel {

	//StringProperty expiry = new SimpleStringProperty();
	SimpleDoubleProperty strkPrc = new SimpleDoubleProperty();
	SimpleDoubleProperty callPrc = new SimpleDoubleProperty();
	SimpleDoubleProperty putPrc  = new SimpleDoubleProperty();
	SimpleDoubleProperty callNC  = new SimpleDoubleProperty();
	SimpleDoubleProperty putNC   = new SimpleDoubleProperty();
	
	SimpleStringProperty callString  = new SimpleStringProperty();
	SimpleStringProperty putString  = new SimpleStringProperty();
    
     
    public NseBaikaiModel(String e, Double s, Double c, Double p, Double cnc, Double pnc) {
    	//this.expiry.set(e);
        this.strkPrc.set(s);
        this.callPrc.set(c);
        this.putPrc.set(p); 
        this.callNC.set(cnc);
        this.putNC.set(pnc);
        this.callString.set(c + "	(" + cnc + ")");
        this.putString.set(p + "	(" + pnc+")");
         
    }

	public NseBaikaiModel() {
		// TODO Auto-generated constructor stub
	}

	 
	/*public String getExpiry() {
		return expiry.get();
	}


	public void setExpiry(String expiry) {
		this.expiry.set(expiry);
	}*/


	public Double getStrkPrc() {
		return strkPrc.get();
	}


	public void setStrkPrc(Double strkPrc) {
		this.strkPrc.set(strkPrc);
	}


	public Double getCallPrc() {
		return callPrc.get();
	}


	public void setCallPrc(Double callPrc) {
		this.callPrc.set(callPrc);
	}


	public Double getPutPrc() {
		return putPrc.get();
	}


	public void setPutPrc(Double putPrc) {
		this.putPrc.set(putPrc);
	}

	public Double getCallNC() {
		return callNC.get();
	}

	public void setCallNC(Double callNC) {
		this.callNC.set(callNC);
	}

	public Double getPutNC() {
		return putNC.get();
	}

	public void setPutNC(Double putNC) {
		this.putNC.set(putNC);
	}

	public String getCallString() {
		return callString.get();
	}

	public void setCallString(String callString) {
		this.callString.set(callString);
	}

	public String getPutString() {
		return putString.get();
	}

	public void setPutString(String putString) {
		this.putString.set(putString);
	}
    
}