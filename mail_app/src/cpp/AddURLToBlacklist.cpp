#include "ICommand.h"
#include "Database.h" 
#include "AddURLToBlacklist.h"
#include <iostream>
using namespace std;

// Constructor
AddURLToBlacklist::AddURLToBlacklist(Database* data) {
    this->data = data;
}

string AddURLToBlacklist::execute(string& url) {
    data->add(url);
    return "201 Created";
}
