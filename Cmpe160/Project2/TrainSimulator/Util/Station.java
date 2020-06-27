package Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

import CargoTrain.Train;

public class Station{
	int id;
	Queue<Cargo> cargoQueue;
	static PrintStream printStream;
	
	public Station(int id) {
		this.id = id;
		cargoQueue = new LinkedList<Cargo>();
	}
	
	public void process(Train train){	
		train.unload(cargoQueue);
		train.load(cargoQueue);
		
		for(Cargo c: train.getDeliveredList()) {
			printStream.println(c.getId() + " " + c.loadingStation.getID() + " " + c.targetStation.getID() + " " + c.getSize());
		}
		printStream.println(train.getCurrentStation() + " " + train.getLength());
	}
	
	public static void setPrintStream(String filePath) {
		try {
			File f = new File(filePath);
			f.delete();
			printStream = new PrintStream(
				     new FileOutputStream(filePath, true));
		} catch (FileNotFoundException e) {
			System.out.println("Output file not found.");
		} 
	}

	public void enqueue(Cargo c) {
		cargoQueue.offer(c);
	}
	
	public int getID() {
		return id;
	}
}