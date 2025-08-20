# include "ICommand.h"
# include "Bloomfilter.h"
# include "Database.h"
# include "Blacklist.h"
# include "CheckURLInBlacklist.h"
# include <vector>
# include <iostream>
using namespace std;


// Constructor
CheckURLInBlacklist::CheckURLInBlacklist(Database* data) {
    this->data = data;
}


string CheckURLInBlacklist::execute(string& url) {
    
    if (CheckURLInBlacklist :: isInBitArray(url)) {
        if (CheckURLInBlacklist ::  isInList(url)){
            return "200 Ok\n\ntrue true";
        }else {
            return "200 Ok\n\ntrue false";
        }
    }else {
        return "200 Ok\n\nfalse";
    }

}


// Check if URL is in bloomfilter
bool CheckURLInBlacklist :: isInBitArray(string& url){
    
    vector<bool> check = CheckURLInBlacklist :: data -> getBloomfilter() -> stringToBitArray(url);
    vector<bool> bloom = CheckURLInBlacklist :: data -> getBloomfilter() -> getBitArray();

    bool flag = true;

    for (int i = 0; i < bloom.size(); i++){
        if (bloom[i] == false && check[i] == true){
            flag = false;
        }
    }

    return flag;
}

// Check if URL is in blacklist 
bool CheckURLInBlacklist ::  isInList(string& url){
    return CheckURLInBlacklist :: data -> getBlacklist() -> isInSet(url);
}

