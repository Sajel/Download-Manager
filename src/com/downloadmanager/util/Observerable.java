package com.downloadmanager.util;

import java.util.List;

public interface Observerable {
	
	List<Observer> getObservers();
	
    default void registerObserver(Observer o) {
    	getObservers().add(o);
    }

    default void removeObserver(Observer o) {
    	getObservers().remove(o);
    }

    default void notifyObservers(int bytesDownload){
    	for (Observer observer : getObservers()) {
			observer.update(bytesDownload);
		}
    }
}