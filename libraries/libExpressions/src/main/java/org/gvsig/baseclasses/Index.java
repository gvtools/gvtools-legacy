package org.gvsig.baseclasses;
/**
 * @author Vicente Caballero Navarro
 */
public class Index {
private int index=0;

public void next() {
	index++;
}
public void previous() {
	index--;
}
public int get() {
	return index;
}
public void set(int i) {
	index=i;
}
}
