
#include "RestaurantOrganizer.h"

using namespace std;

RestaurantOrganizer::RestaurantOrganizer(const vector<int>& tableCapacityInput){
    numberOfTables = tableCapacityInput.size();
    for(int i=0;i<numberOfTables;i++){
        tableCapacity[i] = tableCapacityInput[i];
        heap[i] = i;
        heapUp(i);
    }
}

void RestaurantOrganizer::addNewGroup(int groupSize, ofstream& outFile){
    // If group cant be fit -1 is written. Else, most empty table in heap is used and its capacity is updated.
    if(groupSize > tableCapacity[heap[0]]){
        outFile << -1 << endl;
    } else {
       outFile << heap[0] << endl;
       tableCapacity[heap[0]] -= groupSize;
       heapDown(0);
    }
}

void RestaurantOrganizer::heapUp(int index){
    //Newly added nodes are sent up in the heap until they reach their place.
    int parent;
    for(int i = index; tableCapacity[heap[(index-1)/2]] < tableCapacity[heap[index]]; index = parent) {
        parent = (index-1)/2;
        int temp = heap[parent];
        heap[parent] = heap[index];
        heap[index] = temp;
    }
}

void RestaurantOrganizer::heapDown(int index){
    // Used tables go down in the heap.
    int leftChild = index * 2 + 1;
    int rightChild = index * 2 + 2;
    bool isEnd = false;

    // When pushing nodes down, each node is compared to their child. Below, every possible situation is written using if/else statements.
    while(leftChild < numberOfTables && !isEnd){
        if(rightChild >= numberOfTables){
            if((tableCapacity[heap[leftChild]] > tableCapacity[heap[index]]) || ((tableCapacity[heap[leftChild]] == tableCapacity[heap[index]]) && heap[leftChild] < heap[index])){
                swap(heap[index], heap[leftChild]);
                index = leftChild;
            }
            isEnd = true;
        } else {
            if(tableCapacity[heap[leftChild]] > tableCapacity[heap[index]] && tableCapacity[heap[leftChild]] > tableCapacity[heap[rightChild]]){
                swap(heap[index], heap[leftChild]);
                index = leftChild;
            } else if(tableCapacity[heap[rightChild]] > tableCapacity[heap[index]] && tableCapacity[heap[rightChild]] > tableCapacity[heap[leftChild]]) {
                swap(heap[index], heap[rightChild]);
                index = rightChild;
            } else if(tableCapacity[heap[leftChild]] == tableCapacity[heap[index]] && tableCapacity[heap[leftChild]] > tableCapacity[heap[rightChild]] && heap[leftChild] < heap[index]) {
                swap(heap[index], heap[leftChild]);
                index = leftChild;
            } else if(tableCapacity[heap[rightChild]] == tableCapacity[heap[index]] && tableCapacity[heap[rightChild]] > tableCapacity[heap[leftChild]] && heap[rightChild] < heap[index]) {
                swap(heap[index], heap[rightChild]);
                index = rightChild;
            } else if(tableCapacity[heap[rightChild]] > tableCapacity[heap[index]] && tableCapacity[heap[rightChild]] > tableCapacity[heap[leftChild]]) {
                swap(heap[index], heap[rightChild]);
                index = rightChild;
            } else if(tableCapacity[heap[leftChild]] > tableCapacity[heap[index]] && tableCapacity[heap[leftChild]] == tableCapacity[heap[rightChild]]){
                if(heap[leftChild] < heap[rightChild]){
                    swap(heap[index], heap[leftChild]);
                    index = leftChild;
                } else {
                    swap(heap[index], heap[rightChild]);
                    index = rightChild;
                }
            } else if(tableCapacity[heap[leftChild]] == tableCapacity[heap[index]] && tableCapacity[heap[leftChild]] == tableCapacity[heap[rightChild]]){
                if(heap[leftChild] < heap[index] && heap[leftChild] < heap[rightChild]){
                    swap(heap[index], heap[leftChild]);
                    index = leftChild;
                } else if(heap[rightChild] < heap[index]){
                    swap(heap[index], heap[rightChild]);
                    index = rightChild;
                }
            }
        }

        if(index >= leftChild){
            leftChild = index * 2 + 1;
            rightChild = leftChild + 1;
        } else {
            isEnd = true;
        }
    }
}

void RestaurantOrganizer::printSorted(ofstream& outFile){
    // To print the sorted heap, after outputting the max element, it it set to -1. Therefore it goes down and next max element can be written in the file.
    outFile << tableCapacity[heap[0]];
    tableCapacity[heap[0]] = -1;
    heapDown(0);
    for(int i = 1; i < numberOfTables; i++){
        outFile << " " << tableCapacity[heap[0]] ;
        tableCapacity[heap[0]] = -1;
        heapDown(0);
    }
}

