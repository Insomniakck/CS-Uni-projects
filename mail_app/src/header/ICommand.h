#ifndef ICOMMAND_H
#define ICOMMAND_H
#include <string>
using namespace std;
class Database;

class ICommand {
public:
    virtual string execute(string& input) = 0; 

protected:
    Database* data; 
};
#endif 