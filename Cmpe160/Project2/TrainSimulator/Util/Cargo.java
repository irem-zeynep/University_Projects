package Util;

public class Cargo{
	private int id, size;
	Station targetStation, loadingStation;
	
	public Cargo(int id, int size, Station target, Station loading) {
		this.id = id;
		this.size = size;
		this.targetStation = target;
		this.loadingStation = loading;
		
	}
	
	public int getId() {
		return id;
	}

	public int getSize() {
		return size;
	}
	
	public Station getTargetStation() {
		return targetStation;
	}

	public Station getLoadingStation() {
		return loadingStation;
	}

	public String toString() {
		return null;
		
	}
	
}