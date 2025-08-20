#ifndef BLACKLIST_H
#define BLACKLIST_H
#include "Idata.h"
#include <set>
using namespace std;

class Blacklist : public Idata {
private:
    set<string> urls;

public:
    Blacklist();
    set<string> getURLs();
    void appendToFile(string& url);
    void addToSet(string& url);
    bool isInSet(string& url);
    bool isInFile(string& url);
    void add(string& url) override; 
    void removeFromSet(string& url);
    void removeFromFile(string& url);
};

#endif
