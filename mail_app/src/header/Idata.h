#ifndef IDATA_H
#define IDATA_H
#include <string>
using namespace std;

class Idata {
public:
    virtual void add(string& url) = 0;
};

#endif