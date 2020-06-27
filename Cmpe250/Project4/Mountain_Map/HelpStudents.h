#ifndef CMPE250_ASSIGNMENT3_HELPSTUDENTS_H
#define CMPE250_ASSIGNMENT3_HELPSTUDENTS_H

#include <vector>
#include <list>
#include <iostream>
#include <fstream>
#include <queue>
using namespace std;


class HelpStudents{

public:
    HelpStudents(int  N, int  M, int K, vector < pair< pair <int,int> , int > > ways);
    long long int firstStudent();
    long long int secondStudent();
    long long int thirdStudent();
    long long int fourthStudent();
    long long int fifthStudent();
    long long int runDijkstra(bool isWeightOne);
    typedef pair<long long int, long long int> longPair;
    int edgeNumber;
    int nodeNumber;
    int summit;
    vector<pair<pair<int,int>,int >> ways;
    vector<list<longPair>> adjacencyList;

    // YOU CAN ADD YOUR HELPER FUNCTIONS AND MEMBER FIELDS

};

#endif //CMPE250_ASSIGNMENT3_HELPSTUDENTS_H
