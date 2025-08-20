#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H
#include <vector>
#include <string>
#include "Idata.h"
using namespace std;

class Bloomfilter : public Idata {
private:
    int arraySize;
    vector<int> hash;
    vector<bool> bitArray;

public:
    Bloomfilter(int arraySize, vector<int> hashVector);
    static Bloomfilter* createBloom(string input);
    vector<bool> stringToBitArray(string& url);
    const vector<bool>& getBitArray();
    void add(string& url) override;
};

#endif