package fi.nakoradio.hwo.util;

import java.util.Stack;


public class SizedStack<E> extends Stack<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int size;
	
	public SizedStack(int size){
		super();
		this.size = size;
	}
	
	public E push(E item){
		super.push(item);

		if(size() > size)
			removeElementAt(0);
		
		return item;
	}
	
	
	
}
