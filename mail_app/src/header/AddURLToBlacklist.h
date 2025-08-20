#ifndef ADDTOBLACKLIST_H
#define ADTOBLACKLIST_H
#include "ICommand.h"
#include <string>
using namespace std;

class AddURLToBlacklist : public ICommand{
public:
    AddURLToBlacklist(Database* data);
    string execute(string& url);
    void addToBitArray(string& url);
    void addToBlacklist(string& url);

private:
    Database* data;
};

#endif 