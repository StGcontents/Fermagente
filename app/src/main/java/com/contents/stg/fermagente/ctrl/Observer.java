package com.contents.stg.fermagente.ctrl;

public interface Observer<T> {
    void alert(T state) ;
    void alertFailed() ;
}
