#include "Bloomfilter.h"
#include <iostream>
#include <functional>  
#include <iostream>
# include <fstream>
#include <sstream>

#include <vector>
#include <string>
using namespace std;

// Constructor
Bloomfilter::Bloomfilter(int arraySize, vector<int> hashVector) {


    Bloomfilter::arraySize = arraySize;
    Bloomfilter::hash = hashVector;
    vector<bool> temp(arraySize, 0);
    Bloomfilter::bitArray = temp;

    const string filename = "./data/blacklistFile.txt";
    ifstream infile(filename);

    if (infile.is_open()) {
        string line;
        while (getline(infile, line)) {
            if (!line.empty()) {
                Bloomfilter::add(line);
            }
        }
        infile.close();
    }
}

Bloomfilter* Bloomfilter::createBloom(string input){
    
    vector<int> param;
    bool allNumbers = true;
    string word;
    std::istringstream iss(input);

    // Divide user input by whitespaces into vector<int> 
    while (iss >> word){

        // If input cannot be converted to positive int, invalid input (should get only numbers)
        try {
            int number = stoi(word);
            if (number>0){
                param.push_back(number);
            }else{
                allNumbers = false;
            }
        }catch(...){
            allNumbers = false;
        }
    }

    if (allNumbers && param.size() > 1){
        int size = param[0];
        param.erase(param.begin());

        // Create an instance of Bloomfilter with the relevant arguments
        Bloomfilter* bloom = new Bloomfilter(size, param);
        return bloom;
    }else{
        return nullptr;
    }
}


// BitArray getter
const vector<bool>& Bloomfilter :: getBitArray(){
    return Bloomfilter::bitArray;
}



// Return a vector acorrding to the array size and hash fields
vector<bool> Bloomfilter :: stringToBitArray(string& url){
    vector<bool> temp(arraySize, 0);
    string input = url;
    size_t hashValue;
    std::hash<std::string> hasher;

    
    for (int i = 0; i < Bloomfilter::hash.size(); i++){
        for (int j = 0; j < Bloomfilter::hash[i]; j++){
            hashValue = hasher(input);
            input = to_string(hashValue);
        }
        temp[hashValue % Bloomfilter :: arraySize] = true;
        input = url;
    }

    return temp;
}



// Add a new url to the bloomfilter
void Bloomfilter::add(string& url){
    vector<bool> update = Bloomfilter :: stringToBitArray(url);

    // Equivilant to bitwise OR operation: bitArray = bitArray | update
    for (int i = 0 ; i < Bloomfilter :: arraySize ; i++){
        if (update[i] == true){
            Bloomfilter :: bitArray[i] = true;
        }
    }
}