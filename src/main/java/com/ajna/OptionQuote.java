/**
 * POJO
 * Basic Quote object
 */
package com.ajna;

public class OptionQuote {
	private double _strikePrice;
	private double _callPrice;
	private double _callNC;
	private double _putPrice;
	private double _putNC;
	
	public double getStrikePrice() {
		return _strikePrice;
	}
	public void setStrikePrice(double _strikePrice) {
		this._strikePrice = _strikePrice;
	}
	public double getCallPrice() {
		return _callPrice;
	}
	public void setCallPrice(double _callPrice) {
		this._callPrice = _callPrice;
	}
	public double getPutPrice() {
		return _putPrice;
	}
	public void setPutPrice(double _putPrice) {
		this._putPrice = _putPrice;
	}
	public double getCallNC() {
		return _callNC;
	}
	public void setCallNC(double _callNC) {
		this._callNC = _callNC;
	}
	public double getPutNC() {
		return _putNC;
	}
	public void setPutNC(double _putNC) {
		this._putNC = _putNC;
	}
	@Override
	public String toString() {
		return "OptionQuote [ _strikePrice=" + _strikePrice + ", _callPrice=" + _callPrice
				+ ", _putPrice=" + _putPrice + "]\n";
	}
	
}