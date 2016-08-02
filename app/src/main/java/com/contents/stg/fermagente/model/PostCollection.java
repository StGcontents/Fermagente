package com.contents.stg.fermagente.model;

import com.contents.stg.fermagente.ctrl.Observer;
import com.contents.stg.fermagente.ctrl.Subject;

import java.util.ArrayList;
import java.util.List;

public class PostCollection extends ArrayList<Post> implements Subject {

    private static PostCollection me;
    private PostCollection() {
        observers = new ArrayList<>();
    }
    public static synchronized PostCollection instance() {
        if (me == null) me = new PostCollection();
        return me;
    }

    private List<Observer> observers;

    @Override
    public boolean add(Post object) {
        add(0, object);
        notifyObservers();
        return true;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        notifyObservers();
        return result;
    }

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers)
            observer.alert();
    }
}
