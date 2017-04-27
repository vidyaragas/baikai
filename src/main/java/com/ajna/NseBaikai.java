/**
 * POJO
 * Essentially HashMap with a Key as the expiry date and value as a ArrayList of OptionQuote object
 */
package com.ajna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class NseBaikai {
	
	public NseBaikai(String expry){
		this.setExpiry(expry);
		_quote = new HashMap<String, ArrayList<OptionQuote>>();
	}

	public String getExpiry() {
		return _expiry;
	}


	public void setExpiry(String _expry) {
		this._expiry = _expry;
	}


	public double getLastPrice() {
		return _lastPrice;
	}


	public void setLastPrice(double _lastPrice) {
		this._lastPrice = _lastPrice;
	}


	public ArrayList<OptionQuote> getQuote(String expiry) {
		return _quote.get(expiry);
	}

	public Iterable<ArrayList<OptionQuote>> getQuotes() {
		return _quote.values();
	}

	public Iterable<String> getExpiries() {
		return _quote.keySet();
	}
	
	public void setQuote(String expiry, ArrayList<OptionQuote> quote) {
		_quote.put(expiry, quote);
	}

	
	public String getLastTraded() {
		return _lastTraded;
	}

	public void setLastTraded(String _lastTraded) {
		this._lastTraded = _lastTraded;
	}


	private String _expiry;
	private double _lastPrice;
	private String _lastTraded;
	private HashMap<String, ArrayList<OptionQuote>> _quote;
	@Override
	public String toString() {
		return "NseBaikai [_symbol=" + _expiry + ", _lastPrice=" + _lastPrice + ", _quote=" + _quote + "]\n\n";
	}
}
