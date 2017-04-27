/**
 * Maintains the subscriptions per Symbol
 * fetches Quotes from NSE website
 * updates clients using callback interface upon fetching the NSE quotes
 * This one acts independent of NseOptionChain
 */
package com.ajna;

import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BaikaiServer {

	private HashMap<String, Vector<BaikaiInterface>> m_subscriptions;
	private HashMap<String, NseBaikai> m_baikaiMap;

	public BaikaiServer() {
		
		m_subscriptions = new HashMap<String, Vector<BaikaiInterface>>();

		m_baikaiMap = new HashMap<String, NseBaikai>();

	}

	public void subscribe(String sym, BaikaiInterface bk) {
		
		Vector<BaikaiInterface> bkvect;

		if (m_subscriptions.containsKey(sym)) {
			bkvect = m_subscriptions.get(sym);
		} else {
			bkvect = new Vector<BaikaiInterface>();
			m_subscriptions.put(sym, bkvect);
		}
		bkvect.add(bk);

		updateBaikaiExpiries(sym);
	}

	public void unSubscribe(String sym, BaikaiInterface bk) {

		Vector<BaikaiInterface> bkvect = m_subscriptions.get(sym);

		if (bkvect != null) {
			bkvect.remove(bk);
		} else {

		}
		if (bkvect.isEmpty()) {
			m_subscriptions.remove(sym);
		}
	}

	/**
	 * 
	 * @param sym
	 */
	public void updateBaikaiExpiries(String ticker) {

		Document doc;
		NseBaikai baikai = m_baikaiMap.get(ticker);
		try {

			if (!m_baikaiMap.containsKey(ticker)) { // create
				baikai = new NseBaikai(ticker);
				// get all expiries
				String url = "https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbol="
						+ ticker + "&date=-&instrument=-";
				//System.out.println("\nURL " + url);
				// need http protocol
				System.out.println("\n Ticker: " + ticker);
				doc = Jsoup.connect(url).get();
				Elements expiries = doc.select("select").eq(1);

				for (Element expiry : expiries) {
					Elements opts = expiry.select("option");
					for (Element optn : opts) {
						String expry = optn.attr("value");
						if (!expry.equalsIgnoreCase("select")) {
							baikai.setQuote(expry, new ArrayList<OptionQuote>());
						}
					}
				}
				m_baikaiMap.put(ticker, baikai);

			}
			updateBaikaiForAllExpiries(ticker);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param ticker
	 */
	public void updateBaikaiForAllExpiries(String ticker) {
		NseBaikai baikai = m_baikaiMap.get(ticker);
		for (String expry : baikai.getExpiries()) {
			updateBaikaiForExpiry(ticker, expry);
		}
	}

	/**
	 * 
	 * @param sym
	 */
	public void updateBaikaiForExpiry(String ticker, String expry) {

		Document doc;
		Vector<BaikaiInterface> bkvect = m_subscriptions.get(ticker);

		NseBaikai baikai = m_baikaiMap.get(ticker);

		try {

			//System.out.println("\nexpiry : " + expry);
			String opturl = "https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbol="
					+ ticker + "&date=" + expry;
			doc = Jsoup.connect(opturl).get();
			Elements prcStr = doc.select("table").eq(0).select("td").eq(1).select("span").eq(0);
			// System.out.println("prcStr here: " + prcStr);
			String priceStr = prcStr.select("b").tagName("b").html();
			String a[] = priceStr.split(" ");

			String tradeTime = doc.select("table").eq(0).select("td").eq(1).select("span").eq(1).text();

			// String a[] = priceStr.split(" ");
			//System.out.println("TradeTime : " + tradeTime);
			//System.out.println("Price of " + a[0] + " is: " + a[1]);
			baikai.setLastPrice(Double.parseDouble(a[1]));
			baikai.setLastTraded(tradeTime);

			ArrayList<OptionQuote> quotes = baikai.getQuote(expry);
			if (quotes == null) {
				quotes = new ArrayList<OptionQuote>();
			}
			int i = 0;
			Elements rows = doc.select("table").eq(2).select("tbody").select("tr");
			//System.out.println("total rows: " + rows.size());
			for (Element row : rows) {
				if ((i + 1) >= rows.size())
					continue;
				String cltp = row.select("td").eq(5).text();
				String cnc  = row.select("td").eq(6).text();
				String strk = row.select("td").eq(11).text();
				String pnc  = row.select("td").eq(16).text();
				String pltp = row.select("td").eq(17).text();
				// System.out.println(cltp + " , " + strk + " , " + pltp);
				// System.out.println(row);
				if (i >= quotes.size()) {
					OptionQuote oq = new OptionQuote();
					quotes.add(oq);
				}
				OptionQuote oq = quotes.get(i);

				if (!cltp.equals("-") && !cltp.isEmpty())
					oq.setCallPrice(Double.parseDouble(cltp.replaceAll(",", "")));
				if (!cnc.equals("-") && !cnc.isEmpty())
					oq.setCallNC(Double.parseDouble(cnc.replaceAll(",", "")));
				if (!strk.equals("-") && !strk.isEmpty())
					oq.setStrikePrice(Double.parseDouble(strk.replaceAll(",", "")));
				if (!pltp.equals("-") && !pltp.isEmpty())
					oq.setPutPrice(Double.parseDouble(pltp.replaceAll(",", "")));
				if (!pnc.equals("-") && !pnc.isEmpty())
					oq.setPutNC(Double.parseDouble(pnc.replaceAll(",", "")));
				i++;
			}

			// update all clients subscribed for this ticker
			for (BaikaiInterface bi : bkvect) {
				bi.onOptionUpdate(expry, baikai);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
