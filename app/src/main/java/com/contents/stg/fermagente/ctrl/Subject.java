package com.contents.stg.fermagente.ctrl;

public interface Subject<T> {
    void subscribe(Observer observer) ;
    void unsubscribe(Observer observer) ;

    void notifyObservers();
}
