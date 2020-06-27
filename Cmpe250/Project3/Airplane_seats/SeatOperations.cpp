/*
Student Name: İrem Zeynep Alagöz
Student Number: 2018400063
Project Number: 3
Compile Status: [SUCCESS/FAIL]
Running Status: [SUCCESS/FAIL]
Notes: Anything you want to say about your code that will be helpful in the grading process.
*/
#include "SeatOperations.h"

using namespace std;

SeatOperations::SeatOperations(int N, int M){
    //Vectors created with needed size. 0th index in both vectors are empty to make seat number calculations simpler.
    this->N = N;
    this->M = M;
    lineA.resize(N+1);
    lineB.resize(M+1);
}

void SeatOperations::addNewPerson(int personType, const string& ticketInfo){
    //Person object created to pass as parameter.
    Person person;
    person.type = personType;
    person.ticketInfo = ticketInfo;
    if(person.type == 3) person.type3NextOperation = 1;
    //Person is added using Person object and ticketinfo.
    addPerson(person,ticketInfo);
}

void SeatOperations::addPerson(Person person, string currentSeat){
    //Appropriate seat is found for this person in A and B lines.
    int indexA = stoi(currentSeat.substr(1)) % N;
    if(indexA == 0) indexA = N;
    int indexB = stoi(currentSeat.substr(1)) % M;
    if(indexB == 0) indexB = M;
    bool isLineA = currentSeat[0]=='A';


    //If the seat is empty, place person and end the function.
    if(isLineA){
        if(lineA[indexA].type == 0 ){
            lineA[indexA] = person;
            return;
        }
    } else {
        if(lineB[indexB].type == 0 ){
            lineB[indexB] = person;
            return;
        }
    }

    //If the seat is not empty old person is copied into temp and new one is seated.
    Person temp;

    if(isLineA){
        temp = lineA[indexA];
        lineA[indexA] = person;
    } else {
        temp = lineB[indexB];
        lineB[indexB] = person;
    }

    //If old person is type one, this function is called again with the new seat info changing the line.
    if(temp.type == 1) {
        addPerson(temp, (isLineA? "B" : "A") + temp.ticketInfo.substr(1));
    }else if(temp.type == 2){
        //If person is type 2, he/she goes one seat back or at the start of other seat.
        if(isLineA){
            if(indexA == N){
                addPerson(temp, "B1" );
            } else {
                addPerson(temp,  "A" + to_string(indexA + 1));
            }
        } else {
            if(indexB == M){
                addPerson(temp, "A1" );
            } else {
                addPerson(temp,  "B" + to_string(indexB + 1));
            }
        }
    }else if(temp.type == 3){
        //If type 3, next seat is calculated using that person's next operation field.
        int nextSeat;
        if(isLineA){
            nextSeat = indexA + temp.type3NextOperation;
        } else {
            nextSeat = indexB + temp.type3NextOperation;
        }

        temp.type3NextOperation = temp.type3NextOperation  + 2;

        //If person needs to change line a while loop searches the appropriate seat until seat number is smaller than N or M.
        //Again, fucntion is called recursively with re-seated person and newly calculated seat number.
        if(isLineA){
            if(nextSeat > N){
                //GO TO B LINE
                string currentLine = "A";
                bool notFound= true;
                int currentSeatNum = nextSeat;
                while(notFound){
                    if(currentLine == "A" && currentSeatNum > N){
                        currentSeatNum = currentSeatNum-N;
                        currentLine = "B";
                    } else if(currentLine == "B" && currentSeatNum > M){
                        currentSeatNum = currentSeatNum-M;
                        currentLine = "A";
                    } else {
                        notFound = false;
                    }
                }
                addPerson(temp, currentLine + to_string(currentSeatNum));
            } else {
                addPerson(temp, "A" + to_string(nextSeat));
            }
        } else {
            if(nextSeat > M){
                string currentLine = "B";
                bool notFound= true;
                int currentSeatNum = nextSeat;
                while(notFound){
                    if(currentLine == "B" && currentSeatNum > M){
                        currentSeatNum = currentSeatNum-M;
                        currentLine = "A";
                    } else if(currentLine == "A" && currentSeatNum > N){
                        currentSeatNum = currentSeatNum-N;
                        currentLine = "B";
                    } else {
                        notFound = false;
                    }
                }
                addPerson(temp, currentLine + to_string(currentSeatNum));
            } else {
                addPerson(temp, "B" + to_string(nextSeat));
            }
        }
    }
}


void SeatOperations::printAllSeats(ofstream& outFile){
    //Both lines are printed one by one.
    for(int i = 1; i < N+1; i++){
        if(lineA[i].type == 0)
            outFile<<0<<endl;
        else
            outFile<<lineA[i].type<<" "<<lineA[i].ticketInfo<<endl;
    }
    for(int i = 1; i < M+1; i++){
        if(lineB[i].type == 0)
            outFile<<0<<endl;
        else
            outFile<<lineB[i].type<<" "<<lineB[i].ticketInfo<<endl;
    }
}

// YOU CAN ADD YOUR HELPER FUNCTIONS