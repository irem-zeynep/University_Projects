import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;


public class Main {

	static int puzzleN;

	public static void main(String[] args) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("Input file not found.");
		}

		String[] tempData = sc.nextLine().split("-");
		puzzleN = (int) Math.sqrt(tempData.length);
		int[] data = new int[puzzleN*puzzleN];

		for(int i = 0; i < tempData.length; i++) {
			data[i] = Integer.parseInt(tempData[i]);
		}

		Queue<Node> queue = new PriorityQueue<Node>();

		int rootZeroIndex = 0;
		for(int i = 0; i < data.length; i++) {
			if(data[i] == 0) {
				rootZeroIndex = i;
				break;
			}
		}

		Node root = new Node(data, "", rootZeroIndex, farFromCompletion(data), null);
		queue.add(root);

		HashSet<String> history = new HashSet<String>();
		history.add(dataToString(root.data));

		String result = "";
		boolean completed = false;
		
		while(!queue.isEmpty() && !completed) {
			Node current = queue.poll();

			int index  = current.zeroIndex;
			int temp[];
			
			//Left swap
			if(index % puzzleN != 0) {
				temp = Arrays.copyOf(current.data, current.data.length);
				temp[index] = temp[index - 1];
				temp[index - 1] = 0;
				if(!history.contains(dataToString(temp))) {
					Node leftChild = new Node(temp, current.path.concat("L"), index - 1, farFromCompletion(temp), current);
					queue.add(leftChild);
					history.add(dataToString(leftChild.data));
					current.children.add(leftChild);
				}
			}

			//Down swap
			if(index <= (puzzleN * (puzzleN-1))-1) {
				temp = Arrays.copyOf(current.data, current.data.length);
				temp[index] = temp[index + puzzleN];
				temp[index + puzzleN] = 0;
				if(!history.contains(dataToString(temp))) {
					Node downChild = new Node(temp, current.path.concat("D"), index + puzzleN, farFromCompletion(temp), current);
					queue.add(downChild);			
					history.add(dataToString(downChild.data));
					current.children.add(downChild);
				}
			}

			//Right swap
			if((index + 1) % puzzleN != 0) {
				temp = Arrays.copyOf(current.data, current.data.length);
				temp[index] = temp[index + 1];
				temp[index + 1] = 0;
				if(!history.contains(dataToString(temp))) {
					Node rightChild = new Node(temp, current.path.concat("R"), index + 1, farFromCompletion(temp), current);
					queue.add(rightChild);
					history.add(dataToString(rightChild.data));
					current.children.add(rightChild);
				}
			}

			//Up swap
			if(index >= puzzleN) {
				temp = Arrays.copyOf(current.data, current.data.length);
				temp[index] = temp[index - puzzleN];
				temp[index - puzzleN] = 0;
				if(!history.contains(dataToString(temp))) {
					Node upChild = new Node(temp, current.path.concat("U"), index - puzzleN, farFromCompletion(temp), current);
					queue.add(upChild);
					history.add(dataToString(upChild.data));
					current.children.add(upChild);
				}
			}

			for(Node child : current.children) {
				if(child.checkForCompletion()) {
					result = child.path;
					completed = true;
				}
			}
		}
		
		PrintStream printer = null;
		try {
			printer = new PrintStream(new File(args[1]));
		} catch (FileNotFoundException e) {
			System.out.println("Could not write to file");
		}

		if(!result.equals("")) {
			printer.print(result);
		} else {
			printer.print("N");
		}
	}


	public static String dataToString(int[] data) {
		String result = "";
		for(int i = 0; i < data.length; i++) {
			result = result.concat(String.valueOf(data[i]));		
		}
		return result;
	}

	public static int farFromCompletion(int[] data) {
		int sum = 0, sum2 = 0;
		
		for(int i = 0; i < data.length; i++) {
			if(data[i] != 0) {
				//Manhattan Distance
				sum += Math.abs(((data[i]-1) / puzzleN) - (i / puzzleN)) + Math.abs(((data[i]-1) % puzzleN) - (i % puzzleN));
				//Misplaced numbers
				if(data[i] != i+1) {
					sum2++;
				}
			}
		}
		
		//Linear Conflicts - best results with * 4
		sum += 4 * (getVerticalConflict(data) + getHorizontalConflict(data));

		//Best results with /2
		sum += sum2/2;
		
		return sum;
	}
	
	public static int getHorizontalConflict(int[] data) {
		int conflict = 0;
		int rowNum = -1;
		int max = -1;
		for(int i = 0; i < data.length; i++) {
			int value = data[i];
			if(i % puzzleN == 0) {
				rowNum++;
				max = -1;
			}
			int trueRowNum = (value - 1) / puzzleN;
			if(value != 0 && trueRowNum == rowNum) {
				if(value > max) {
					max = value;
				} else conflict++;
			}
			
		}
		return conflict;
	}
	
	public static int getVerticalConflict(int[] data) {
		int conflict = 0;
		int colNum = -1;
		int max = -1;
		for(int i = 0; i < data.length; i++) {
			int value = data[(i / puzzleN) + (i % puzzleN)*puzzleN];
			if(i % puzzleN == 0){
				colNum++;;
				max = -1;
			}
			int trueColNum = (value - 1) % puzzleN;
			if(value != 0 && trueColNum == colNum) {
				if(value > max) {
					max = value;
				} else conflict++;
			}
		}
		return conflict;
	}
}