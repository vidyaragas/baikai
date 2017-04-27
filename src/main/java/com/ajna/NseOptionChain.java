/**
 * Maintains the Chain structure of Market Depth
 * HashMap with Key as Symbol and BaiKai object in the value
 * Remember our NseBaikai object intern maintains multiple quites key'd by expiries
 * This class fetches expiries and quotes from NSE website
 * TBD
 */
package com.ajna;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NseOptionChain {

	private HashMap<String, NseBaikai> baikaiMap;
	
	public NseOptionChain(){
		baikaiMap = new HashMap<String,NseBaikai>();
	}
	
	
	public NseBaikai getPrice(final String ticker) {
		
		Document doc;
		NseBaikai baikai   = baikaiMap.get(ticker);
        try {

            if (!baikaiMap.containsKey(ticker)){ //create
            	baikai =  new NseBaikai(ticker); 
            	// get all expiries
            	String url = "https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbol=" + ticker + "&date=-&instrument=-";
            	System.out.println("\nURL " + url);
                // need http protocol
                doc = Jsoup.connect(url).get();
                Elements expiries = doc.select("select").eq(1);
                
                for (Element expiry : expiries) { 	
                	Elements opts = expiry.select("option");
                		for (Element optn : opts){
                			String expry = optn.attr("value");
                			if (!expry.equalsIgnoreCase("select")){
                				baikai.setQuote(expry, new ArrayList<OptionQuote>());
                			}
                		}
                }
                baikaiMap.put(ticker, baikai);	
            }  
            
           
           for (String expry : baikai.getExpiries()) {
            	
            	
        	   System.out.println("\nexpiry : " + expry);
        	   String opturl = "https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbol=" + ticker + "&date=" + expry ;
        	   doc = Jsoup.connect(opturl).get();
        	   Elements prcStr = doc.select("table").eq(0).select("td").eq(1).select("span").eq(0);
        	   String priceStr = prcStr.select("b").tagName("b").html();
        	   String a[] = priceStr.split(" ");
        	   System.out.println("Price of " + a[0] + " is: " + a[1]);
        	   baikai.setLastPrice(Double.parseDouble(a[1]));

        	   ArrayList<OptionQuote> quotes = baikai.getQuote(expry);
        	   if (quotes == null ){
        		   quotes = new ArrayList<OptionQuote>();
        	   }
        	   int i = 0;
        	   Elements rows = doc.select("table").eq(2).select("tbody").select("tr");
        	   System.out.println("total rows: " + rows.size());
        	   for (Element row : rows) {
        		   if((i+1) > rows.size()) continue;
        		   String cltp = row.select("td").eq(5).text();
        		   String strk = row.select("td").eq(11).text();
        		   String pltp = row.select("td").eq(17).text();
        		   //System.out.println(cltp + " , " + strk + " , " + pltp);
        		   //System.out.println(row);
        		   if (i >= quotes.size() ){
        			   OptionQuote oq = new OptionQuote();
        			   quotes.add(oq);
        		   }
        		   OptionQuote oq = quotes.get(i);
        		 
        		   if(!cltp.equals("-") && !cltp.isEmpty())
        		   oq.setCallPrice(Double.parseDouble(cltp.replaceAll(",", "")));
        		   if(!strk.equals("-") && !strk.isEmpty())
        		   oq.setStrikePrice(Double.parseDouble(strk.replaceAll(",", "")));
        		   if(!pltp.equals("-") && !pltp.isEmpty())
        		   oq.setPutPrice(Double.parseDouble(pltp.replaceAll(",", "")));
        		   i++;
        	   }
           }
                	
        } catch (IOException e) {
            e.printStackTrace();
        }
		return baikai;
	}
}
