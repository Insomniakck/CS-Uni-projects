# include "ICommand.h"
# include "Bloomfilter.h"
# include "Database.h"
# include "Blacklist.h"
# include "DeleteURL.h"
# include <vector>
# include <iostream>
using namespace std;

// Constructor
DeleteURL::DeleteURL(Database* data) {
    this->data = data;
}

string DeleteURL::execute(string& url) {

    // If URL is not blacklisted, cannot delete
    if (!DeleteURL::isInList(url)) {
        return "404 Not Found";
    }

    // Delete URL from urls set
    data->getBlacklist()->removeFromSet(url);

    // Delete URL from file "blacklist.txt"
    data->getBlacklist()->removeFromFile(url);
    return "204 No Content";

}

// Check if URL is in blacklist 
bool DeleteURL:: isInList(string& url){
    return DeleteURL::data -> getBlacklist() -> isInSet(url);
}