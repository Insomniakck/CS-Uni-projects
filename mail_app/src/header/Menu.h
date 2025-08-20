#ifndef MENU_H
#define MENU_H
#include <string>
#include <vector>
using namespace std;
class App;

class Menu {
public:
    vector<string> nextICommand(string& input);

    App* createApp(string& input);

    bool isValidURL(string& url);

    bool isValidInput(vector<string> input);
};

#endif 