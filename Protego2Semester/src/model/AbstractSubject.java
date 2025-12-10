package model;

import java.util.List;
import java.util.ArrayList;



public abstract class AbstractSubject {


private List<Observer> observers = new ArrayList<>();



public void attach(Observer o) {
	observers.add(o);
}
public void detach(Observer o) {
	observers.remove(o);
}

public void notifyObservers() {
	observers.forEach(o -> o.updateObserver());
}

}
