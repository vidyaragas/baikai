/**
 * rx style 
 * TBD
 */
package com.ajna;

import rx.Observable;

import java.util.List;

public class StockServer {
	NseOptionChain nso;
	public StockServer(){
		nso = new NseOptionChain();
	}
  public  Observable<NseBaikai> getFeed(List<String> symbols) {
    return Observable.create(
        subscriber -> {
          subscriber.setProducer(request -> {
            //the request generally mean the number of values to return.
            //That often leads to keeping some mutable state here. Instead of doing that
            //I am using request as the index in symbols to return value for.

            int index = (int) request;
            //subscriber.onNext(si.fetch(symbols.get(index)));
            subscriber.onNext(nso.getPrice(symbols.get(index)));
          });
        });
  }
}
