#ifndef CHECKURLINBLACKLIST_H
#define CHECKURLINBLACKLIST_H
#include "ICommand.h"
#include <string>
using namespace std;
class ICommnad;

class CheckURLInBlacklist : public ICommand{
public:
    CheckURLInBlacklist(Database* data);
    string execute(string& url);
    bool isInBitArray(string& url);
    bool isInList(string& url);
 
};

#endif 