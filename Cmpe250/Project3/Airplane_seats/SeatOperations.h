#ifndef CMPE250_ASSIGNMENT3_SEATOPERATIONS_H
#define CMPE250_ASSIGNMENT3_SEATOPERATIONS_H
#include <iostream>
#include <vector>
#include <fstream>

using namespace std;

struct Person{
    int type=0;
    int type3NextOperation=1;
    string ticketInfo = "";
};

class SeatOperations{

private:
    vector<Person> lineA;
    vector<Person> lineB;
    int N, M;

public:
    SeatOperations(int N, int M);
    void addNewPerson(int personType, const string& ticketInfo);
    void addPerson(Person person, string currentSeat);
    void printAllSeats(ofstream& outFile);



    // YOU CAN ADD YOUR HELPER FUNCTIONS

};

#endif //CMPE250_ASSIGNMENT3_SEATOPERATIONS_H
