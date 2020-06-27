package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import CargoTrain.Carriage;
import CargoTrain.Train;
import Util.Cargo;
import Util.Station;

public class Main{
	static Train train;
	static ArrayList<Station> stList = null;
	
	public static void main(String[] args) {
		readAndInitialize(args);
		execute();
	}
	
	public static void readAndInitialize(String[] args){
		try {
			Scanner sc = new Scanner(new File(args[0]));
			String[] line = sc.nextLine().trim().split("\\s+");
			
			int carriageCount = Integer.parseInt(line[0]);
			int capacity = Integer.parseInt(line[1]);
			int stationCount = Integer.parseInt(line[2]);
			 
			//Train init
			train = new Train(carriageCount, capacity);
			
			//Carriages init
			Carriage cr = new Carriage(capacity);
			train.setHead(cr);
			train.setTail(cr);
			for(int i = 1; i < carriageCount; i++) {
				cr = new Carriage(capacity);
				cr.setPrev(train.getTail());
				train.getTail().setNext(cr);
				train.setTail(cr);
			}
			
			//Stations init
			stList = new ArrayList<Station>();
			for(int i = 0; i < stationCount; i++) {
				Station st = new Station(i);
				stList.add(st);
			}
			
			//Cargos init
			while(sc.hasNext()) {
				line = sc.nextLine().trim().split("\\s+");
				Cargo c = new Cargo(Integer.parseInt(line[0]), Integer.parseInt(line[3]), stList.get(Integer.parseInt(line[2])),
						stList.get(Integer.parseInt(line[1])));
				stList.get(Integer.parseInt(line[1])).enqueue(c);
			}
			sc.close();
			
			//Setup print stream
			Station.setPrintStream(args[1]);
			
		} catch (FileNotFoundException e) {
			System.out.println("Input file not found.");
		}
	}
	
	public static void execute() {	
		for(Station st : stList) {
			st.process(train);
		}
	}
}

