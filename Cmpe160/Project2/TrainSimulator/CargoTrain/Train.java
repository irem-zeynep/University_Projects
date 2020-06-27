package CargoTrain;

import java.util.ArrayList;
import java.util.Queue;
import Util.Cargo;

public class Train{
	int carCapacity;
	int length;
	ArrayList<Cargo> deliveredList;
	Carriage head, tail;
	int currentStation = -1;
	
	public Train(int length, int carCapacity) {
		this.length = length;
		this.carCapacity = carCapacity;
		head = tail;
		deliveredList = new ArrayList<Cargo>();
	}
	
	public void load(Queue<Cargo> cargos) {
		deliveredList.clear();
		while(!cargos.isEmpty()) {
			Cargo c = cargos.poll();	
			if(c.getTargetStation().getID() != currentStation) {
				//Place cargo
				if(length == 0) {
					Carriage cr = new Carriage(carCapacity);
					head = cr;
					tail = cr;
					cr.push(c);
					cr.emptySlot -= c.getSize();
					length++;
				} else {
					Carriage temp = head;
					boolean carriageNeeded = false;
					for(int i = 1; i <= length && temp.emptySlot < c.getSize(); i++) {
						temp = temp.next;
						if(i == length) {
							carriageNeeded = true;
						}
					}
					
					if(carriageNeeded) {
						Carriage cr = new Carriage(carCapacity);
						cr.setPrev(tail);
						tail.setNext(cr);
						tail = cr;
						length++;
						tail.push(c);
						tail.emptySlot -= c.getSize();
					} else {
						temp.push(c);
						temp.emptySlot -= c.getSize();
					}	
				}
			} else {
				deliveredList.add(c);
			}
		}

		//Delete empty carriages
		while(length > 0 && tail.emptySlot == carCapacity) {
			if(length == 1) {
				head = null;
				tail = null;
			} else {
				tail = tail.prev;
				tail.next.prev = null;
				tail.next = null;
			}
			length--;
		}
		
	}
	
	public void unload(Queue<Cargo> cargos) {
		Carriage temp = head;
		while(temp != null) {
			while(!temp.cargos.isEmpty()) {
				Cargo c = temp.pop();
				temp.emptySlot += c.getSize();
				cargos.offer(c);
			}
			temp = temp.next;
		}
		currentStation++;
	}
	
	public void setHead(Carriage cr) {
		head = cr;
	}
	
	public void setTail(Carriage cr) {
		tail = cr;
	}
	
	public Carriage getHead() {
		return head;
	}
	
	public Carriage getTail() {
		return tail;
	}

	public int getLength() {
		return length;
	}

	public int getCurrentStation() {
		return currentStation;
	}

	public ArrayList<Cargo> getDeliveredList() {
		return deliveredList;
	}
	
}
