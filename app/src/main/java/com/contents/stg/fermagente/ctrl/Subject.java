package com.contents.stg.fermagente.ctrl;

public interface Subject<T> {
    void subscribe(Observer<T> observer) ;
    void unsubscribe(Observer<T> observer) ;

    void notifyObservers(T state);
    void notifyFailed() ;
}
