/*
Student Name: İrem Zeynep Alagöz
Student Number: 2018400063
Project Number: 5
Compile Status: [SUCCESS/FAIL]
Running Status: [SUCCESS/FAIL]
Notes: Anything you want to say about your code that will be helpful in the grading process.
*/
#include <iostream>
#include <sstream>
#include <fstream>
#include <algorithm>

using namespace std;

void calculate(int &  number, int &  limit, int prices[] , long long int difference [], ofstream &outFile);

int main(int argc, char* argv[]){

    //Create file streams
    ifstream inFile(argv[1]);
    ofstream outFile(argv[2]);

    //number of chocolates available
    int number;
    //maximum number of chocolates can be bought in one day.
    int limit;

    string line, tmp;
    getline(inFile, line);

    istringstream linestream(line);
    getline(linestream, tmp, ' ');
    number = stoi(tmp);

    getline(linestream, tmp, ' ');
    limit = stoi(tmp);

    int price;
    getline(inFile, line);
    istringstream linestreams(line);

    //Read prices from file and put into prices array.
    int prices[number];
    for (int i = 0; i < number; i++)
    {
        getline(linestreams, tmp, ' ');
        price = stoi(tmp);
        prices[i] = price;
    }

    inFile.close();

    long long int difference[number+1];
    //Sort chocolates prices
    std::sort(prices, prices + number);

    //Calculate minimum amounts to be paid and print them to file.
    calculate(number, limit, prices, difference, outFile);

    outFile.close();
    return 0;
}

void calculate(int &  number, int &  limit, int prices[], long long int difference [], ofstream &outFile){
    //minimum amount to pay
    long long int sum = 0;
    for(int i = 0; i < number; i++){
        //Add current chocolates price to sum and difference array
        if (i < limit) {
            sum = sum + prices[i];
            difference[i] = prices[i];
        }
        //If limit is reached take current chocolate in normal price and pay more for cheaper chocolate in past days.
        else{
            int mod = i%limit;
            sum = sum + prices[i] + difference[mod];
            difference[mod] +=  prices[i];
        }
        outFile << sum << " ";
    }
}