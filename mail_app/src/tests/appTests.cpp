#include <gtest/gtest.h>
#include <filesystem> 
#include "App.h"
#include "Menu.h"
#include <string>
#include <vector>

using namespace std;


TEST(AppTests, run_function){
    filesystem::create_directory("data");
    Menu menu;
    vector<int> hash = {1, 2};
    App app(&menu, 8, hash);

    //  Test if the correct string is returned for every command in run function
    string userInput = "POST www.appRun.com";
    string result = app.run(userInput);
    EXPECT_TRUE(result == "201 Created");

    string userInput1 = "GET www.appRun.com";
    string result1 = app.run(userInput1);
    EXPECT_TRUE(result1 == "200 Ok\n\ntrue true");

    string userInput2 = "DELETE www.appRun.com";
    string result2 = app.run(userInput2);
    EXPECT_TRUE(result2 == "204 No Content");

    string userInput3 = "GET www.appRun.com";
    string result3 = app.run(userInput3);
    EXPECT_TRUE(result3 == "200 Ok\n\ntrue false");

    string userInput4 = "DELETE www.appRun.com";
    string result4 = app.run(userInput4);
    EXPECT_TRUE(result4 == "404 Not Found");

    string userInput5 = "INVALID COMMAND";
    string result5 = app.run(userInput5);
    EXPECT_TRUE(result5 == "400 Bad Request");

}