#include <iostream>
#include <sstream>
#include <vector>
#include "Menu.h"
#include "App.h"
#include <string>
#include <regex>
using namespace std;

vector<string> Menu::nextICommand(string& input){
    istringstream iss(input);
    vector<string> cmd;
    string word;

    while(iss >> word){
        cmd.push_back(word);
    }

    if (cmd.size() == 2 && Menu :: isValidURL(cmd[1])){
        return cmd;
    }else{
        return {};
    }
}


// Check if the user input is valid
bool Menu :: isValidInput(vector<string> input) {

    // Check if the user input 2 variables: the command, the url
    if(input.size() != 2) { return false; }

    // Check if the chosen option is POST/DELETE/GET
    if(input[0] != "POST" && input[0] != "DELETE" && input[0] != "GET"){ return false;}

    // Check that the url is valid 
    if(!Menu::isValidURL(input[1])){ return false;}

    return true;
}


// Check if URL is valid
bool Menu::isValidURL(string& url) {
    
    // Regex 1: for real URL addresses
    regex urlRegex1(R"(^((https?|ftp):\/\/)?([a-zA-Z0-9.-]+\.[a-zA-Z]{2,})(:\d+)?(\/[a-zA-Z0-9\/._-]*)?$)", regex::icase);
    
    // Regex 2: for URLs in form "www.example.com0"
    regex urlRegex2(R"(www\.[a-zA-Z0-9\-]+\.com[0-9]+)");

    return regex_match(url, urlRegex1) || regex_match(url, urlRegex2);
}

// Get the variables to initialize the bloomfilter from user
App* Menu::createApp(string& input) {

    istringstream iss(input);
    vector<int> param;
    bool allNumbers = true;
    string word;

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
        int arraySize = param[0];
        param.erase(param.begin());

        // Create an instance of App with the relevant arguments
        return new App(this, arraySize, param);
    }else{
        return nullptr;
    }
}