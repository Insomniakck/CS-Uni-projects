#include "Database.h"
#include "Blacklist.h"
#include "Bloomfilter.h"
#include <iostream>
using namespace std;

// Constructor 1. Initialize blacklist and bloomfilter from scratch
Database::Database(int arraySize, vector<int> hashVector) {
    blacklist = new Blacklist();
    bloomfilter = new Bloomfilter(arraySize, hashVector);
}

// Constructor 2. given bloomfilter
Database::Database(Bloomfilter* bloom) {
    blacklist = new Blacklist();
    bloomfilter = bloom;
}

// Getter for Blacklist
Blacklist* Database::getBlacklist() {
    return this->blacklist;
}

// Getter for Bloomfilter
Bloomfilter* Database::getBloomfilter() {
    return this->bloomfilter;
}

// Add URL to both data structures - blacklist and bloomfilter
void Database::add(string& url) {
    bloomfilter->add(url);
    blacklist->add(url);
}

// Destructor
Database::~Database() {
    delete blacklist;
    delete bloomfilter;
}
