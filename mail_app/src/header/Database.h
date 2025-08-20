
#ifndef DATABASE_H
#define DATABASE_H
#include <string>
#include <vector>
class Blacklist;
class Bloomfilter;
using namespace std;

class Database {
protected:
    Blacklist* blacklist;
    Bloomfilter* bloomfilter;

public:
    Database(int arraySize, vector<int> hashVector);
    Database(Bloomfilter* bloom);
    void add(string& url);
    Blacklist* getBlacklist();
    Bloomfilter* getBloomfilter();
    ~Database(); 
};

#endif