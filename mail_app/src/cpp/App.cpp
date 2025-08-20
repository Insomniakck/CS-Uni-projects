
#include "Menu.h"
#include "App.h"
#include "ICommand.h"
#include "AddURLToBlacklist.h"
#include "CheckURLInBlacklist.h"
#include "DeleteURL.h"
#include "Bloomfilter.h"
#include "Database.h"
#include <string>
#include <vector>
using namespace std;

// Constructor
App::App(Menu* menu,  int arraySize, vector<int> param) {
    this->menu = menu;
    
    // Create Database object, initialized with array size and hash vector
    data = new Database(arraySize, param);  

    // Create commands map {"POST" : AddURLToBlacklist, "GET" : CheckURLInBlacklist
    //                       DELETE" : DeleteURL }
       ICommand* addURL = new AddURLToBlacklist(data);
    this->addCommand("POST", addURL);
    ICommand* checkURL = new CheckURLInBlacklist(data);
    this->addCommand("GET", checkURL);
    ICommand* deleteURL = new DeleteURL(data);
    this->addCommand("DELETE", deleteURL);

}   

App::App(Bloomfilter* bloom) {
    Menu* temp = new Menu();
    this -> menu = temp;
    data = new Database(bloom);
    ICommand* addURL = new AddURLToBlacklist(data);
    this->addCommand("POST", addURL);
    ICommand* checkURL = new CheckURLInBlacklist(data);
    this->addCommand("GET", checkURL);
    ICommand* deleteURL = new DeleteURL(data);
    this->addCommand("DELETE", deleteURL);

}

// Destructor
App::~App(){

    delete data;

    // Iterate over the commands map, and delete all the commands
    for (auto& [key, command] : commands) {
        delete command;
    }
}

// Run application infinite loop
string App::run(string& input) {
    
    // Get next command from user
    vector<string> task = menu->nextICommand(input); 

    if (task.size() != 2){
        return "400 Bad Request";
    }

    // If the command is invalid, an out of bounds exception will be thrown
    try{
        string userChoice = task[0];
        string URLaddress = task[1];
        if (commands.find(userChoice) == commands.end()) {
            return "400 Bad Request";
        }
        return commands[userChoice]->execute(URLaddress);
    }catch(...){
        return "400 Bad Request";
    }
}

// Add new command to ICommand map
void App::addCommand(string name, ICommand* cmd) {
    this->commands[name] = cmd;
}

