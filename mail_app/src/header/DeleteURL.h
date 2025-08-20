#ifndef DELETEURL_H
#define DELETEURL_H
#include "ICommand.h"
#include <string>
using namespace std;
class ICommnad;

class DeleteURL : public ICommand{
public:
    DeleteURL(Database* data);
    string execute(string& url);
    bool isInList(string& url);
};

#endif 