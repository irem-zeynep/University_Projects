package CargoTrain;

import java.util.Stack;
import Util.Cargo;

public class Carriage{
	int emptySlot;
	Stack<Cargo> cargos;
	Carriage next, prev;
	
	public Carriage(int capacity) {
		this.emptySlot = capacity;
		cargos = new Stack<Cargo>();
		next = null;
		prev = null;
	}
	
	public boolean isFull() {
		return emptySlot == 0; 
	}
	
	public void push(Cargo cargo) {
		cargos.push(cargo);
	}
	
	public Cargo pop() {
		return cargos.pop();
	}

	public void setNext(Carriage next) {
		this.next = next;
	}

	public void setPrev(Carriage prev) {
		this.prev = prev;
	}
	
}