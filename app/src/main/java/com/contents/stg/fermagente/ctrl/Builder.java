package com.contents.stg.fermagente.ctrl;

/**
 * Created by Stefano Giancristofaro (stg) on 03/08/16.
 */
public abstract class Builder<T> {
    protected T object;

    public boolean isReady() {
        return true;
    }
    public abstract T retrieveObject() ;
}
