/*
Student Name: İrem Zeynep Alagöz
Student Number: 2018400063
Project Number: 4
Compile Status: [SUCCESS/FAIL]
Running Status: [SUCCESS/FAIL]
Notes: Anything you want to say about your code that will be helpful in the grading process.
*/
#include <queue>
#include <limits>
#include "HelpStudents.h"


using namespace std;
const long long int INF = numeric_limits<long long int>::max();
typedef pair<long long int, long long int> longPair;

HelpStudents::HelpStudents(int  N, int  M, int K, vector < pair< pair <int,int> , int > > ways) {

    this->edgeNumber = M;
    this->nodeNumber = N;
    this->summit = K;
    this->ways = ways;

    //Resize adjacency list for node number. Index 0 is empty.
    adjacencyList.resize(nodeNumber+1);

    //Create adjacency list for nodes.
    for(int i = 0; i < ways.size(); i++) {
        adjacencyList.at(ways[i].first.first).push_back(make_pair(ways[i].first.second, ways[i].second));
        adjacencyList.at(ways[i].first.second).push_back(make_pair(ways[i].first.first, ways[i].second));
    }
}

long long int HelpStudents::firstStudent() {
    //Dijksta helper is run here, with original weight values.
    return runDijkstra(false);
}

long long int HelpStudents::secondStudent() {
    //Prim's Algorithm is used for this task so we can find minimum spanning tree.
    priority_queue<longPair, vector<longPair>, greater<longPair>> pq;
    //Vectors for distance, parents and visited list is created and resized.
    vector<long long int> distances(nodeNumber+1, INF);
    vector<int> parents;
    vector<bool> visited;
    parents.resize(nodeNumber+1);
    visited.resize(nodeNumber+1);
    distances[1] = 0;

    //Initialize queue with start node.
    pq.push(make_pair(0,1));
    visited[1] = true;
    parents[1] = -1;

    while(!pq.empty()){
        int node = pq.top().second;
        pq.pop();
        visited[node] = true;
        //Traverse adjacency list for prim's algorithm.
        list<longPair>::iterator i;
        for(i = adjacencyList.at(node).begin(); i!= adjacencyList.at(node).end(); ++i){
            int neighbor = (*i).first;
            int edgeWeight = (*i).second;

            //If a shorter distance is found update arrays.
            if(!visited[neighbor] && distances[neighbor] > edgeWeight){
                distances[neighbor] = edgeWeight;
                pq.push(make_pair(distances[neighbor], neighbor));
                parents[neighbor] = node;
            }
        }
    }
    //Reach start node from summit by looking parents.
    long long int max = -1;
    long long int dist = -1;
    int node = summit;
    while(node != 1){
        dist = distances[node];
        if(dist > max)
            max = dist;
        node = parents[node];

    }
    return max;
}
long long int HelpStudents::thirdStudent() {
    //Dijkstra is used with weights of one so we can find minimum number of nodes to summit.
    return runDijkstra(true);
}

long long int HelpStudents::fourthStudent() {
    bool *routeAvailable = new bool[(nodeNumber+1)*(nodeNumber+1)];

    //To determine which edges is used, an adjacency matrix is created.
    for(int i = 0; i < ways.size(); i++) {
        routeAvailable[ways[i].first.first * (nodeNumber+1) + ways[i].first.second] = true;
        routeAvailable[ways[i].first.second * (nodeNumber+1) + ways[i].first.first] = true;
    }

    int currentNode = 1;
    long long int roadLength = 0;
    bool edgeAvailable = true;
    while(edgeAvailable && currentNode != summit){
        int closestNode = -1;
        long long int minLength = INF;
        //Traverse all neighbors
        list<longPair>::iterator i;
        for(i = adjacencyList.at(currentNode).begin(); i!= adjacencyList.at(currentNode).end(); ++i){
            int neighbor = (*i).first;
            int edgeWeight = (*i).second;
            //If weights are equal choose smaller numbered node or take the shortest path
            if(routeAvailable[currentNode * (nodeNumber+1) + neighbor] && edgeWeight <= minLength){
                if((edgeWeight == minLength && neighbor < closestNode) || edgeWeight < minLength){
                    closestNode = neighbor;
                    minLength = edgeWeight;
                }
            }
        }
        //If we are stuck then end execution the print -1.
        if(closestNode == -1){
            edgeAvailable = false;
        } else {
            //Mark routes as visited an update current location and distance.
            routeAvailable[currentNode * (nodeNumber+1) + closestNode] = false;
            routeAvailable[closestNode * (nodeNumber+1) + currentNode] = false;
            roadLength += minLength;
            currentNode = closestNode;
        }
    }

    delete[] routeAvailable;
    return edgeAvailable? roadLength:-1;
}

long long int HelpStudents::fifthStudent() {
    //Create an array thrice the size of nodeNumber so we can save best distances for each node variation
    //Variation means node reached with 0 moves,1 moves or 2 moves. So each node has 3 versions.
    typedef pair<long long int,pair<int,int>> queuePair;
    auto *pq =  new priority_queue <queuePair, vector<queuePair>, greater<queuePair>>();
    long long int *bestDistance = new long long int [(nodeNumber+1)*3];
    fill(bestDistance, bestDistance+(nodeNumber+1)*3, INF);

    //Run algorihm similar to dijkstra but this time triple the node size to solve the problem.
    pq->push(make_pair(0, make_pair(1,0)));
    while(!pq->empty()){
        long long int nodeDist = pq->top().first;
        int node = pq->top().second.first;
        int nodeMoveCount = pq->top().second.second;
        pq->pop();

        //Traverse neighbors and set edge weight to 0 if this node is version 2(Reached by 2 moves).
        list<longPair>::iterator i;
        for(i = adjacencyList.at(node).begin(); i!= adjacencyList.at(node).end(); ++i){
            int neighbor = (*i).first;
            long long int edgeWeight = (nodeMoveCount == 2)? 0 : (*i).second;

            long long int newWeight = nodeDist + edgeWeight;
            int neighborMoveCount = (nodeMoveCount == 2)? 0:nodeMoveCount+1;

            //Update distance if a shorter way is found for this node.
            if(bestDistance[neighbor*3+neighborMoveCount] > newWeight){
                bestDistance[neighbor*3+neighborMoveCount] = newWeight;
                pq->push(make_pair(newWeight, make_pair(neighbor,neighborMoveCount)));
            }
        }
    }

    delete pq;
    return min(bestDistance[summit*3], min(bestDistance[summit*3+1], bestDistance[summit*3+2]));
}

long long int HelpStudents::runDijkstra(bool isWeightOne){
    //This method runs dijkstra for 1st and 3rd students they are the same, except 3rd one should have weights as 1.
    priority_queue<longPair, vector<longPair>, greater<longPair>> pq;
    vector<long long int> distances(nodeNumber+1, INF);
    distances[1] = 0;
    pq.push(make_pair(0,1));

    while(!pq.empty()){
        int node = pq.top().second;
        pq.pop();
        //Traverse neighbors.
        list<longPair>::iterator i;
        for(i = adjacencyList.at(node).begin(); i!= adjacencyList.at(node).end(); ++i){
            int neighbor = (*i).first;
            int edgeWeight = isWeightOne? 1 : (*i).second;

            //Update distances if shorter is found.
            if(distances[neighbor] > distances[node] + edgeWeight){
                distances[neighbor] = distances[node] + edgeWeight;
                pq.push(make_pair(distances[neighbor], neighbor));
            }
        }
    }
    return distances[summit];
}