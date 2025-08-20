#include <string>
#include <map>
#include "Menu.h"
#include "Database.h"
#ifndef APP_H
#define APP_H
class ICommand;
using namespace std;

class App {
private:
    map<string, ICommand*> commands;
    Menu* menu;
    Database* data;

public:
    App(Menu* menu, int arraySize, vector<int> param);
    App(Bloomfilter* bloom);
    string run(string& input);
    ~App();
    void addCommand(string name, ICommand* cmd);
};

#endif