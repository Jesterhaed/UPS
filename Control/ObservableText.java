package Control;

import java.util.Observable;

public class ObservableText extends Observable {
	private String value = "";
	
	public ObservableText(String initValue) {
		this.value = initValue;
		notifyObservers(initValue);
	}
	
	public String getValue() {
		return value;
	}
	
	// incrementation of observed value
	public void inc(String text) {
		// test if the value is not too small
			setChanged();
			// notification of all observers
			notifyObservers(text);
		
	}
	
	
}