#include <gtest/gtest.h>

#include "App.h"
#include "Menu.h"
#include <string>
#include <vector>

using namespace std;


TEST(MenuTest,sanityTest){
    Menu menu;
    vector<string> command = {"POST", "www.menuTest.com"};
    EXPECT_TRUE(menu.isValidInput(command));
    
    vector<string> command1 = {"GET", "www.menuTest.com"};
    EXPECT_TRUE(menu.isValidInput(command1));

    vector<string> command2 = {"DELETE", "www.menuTest.com"};
    EXPECT_TRUE(menu.isValidInput(command2));
}


TEST(MenuTest, negetiveTest){
    Menu menu;

    vector<string> command1 = {"HELLO", "www.menuTest.com"};
    EXPECT_FALSE(menu.isValidInput(command1));

    vector<string> command2 = {"GET"};
    EXPECT_FALSE(menu.isValidInput(command2));

    vector<string> command3 = {"www.negetiveTest.com"};
    EXPECT_FALSE(menu.isValidInput(command3));

    vector<string> command4 = {""};
    EXPECT_FALSE(menu.isValidInput(command4));

    vector<string> command5 = {"POST", "www.threeVariables.com", "2"};
    EXPECT_FALSE(menu.isValidInput(command5));
    
}

TEST(MenuTest, urlValidity){
    Menu menu;
    string url1 = "1234";
    EXPECT_FALSE(menu.isValidURL(url1));

    string url2 = "I am not a url";
    EXPECT_FALSE(menu.isValidURL(url2));

    string url3 = "";
    EXPECT_FALSE(menu.isValidURL(url3));

    string url4 = " ";
    EXPECT_FALSE(menu.isValidURL(url4));

    string url5 = "www.example.com0";
    EXPECT_TRUE(menu.isValidURL(url5));

    //random real url
    string url6 = "https://www.youtube.com";
    EXPECT_TRUE(menu.isValidURL(url6));
}

//check if the method returns the seperated string correctly
TEST(MenuTest, nextICommand){
    Menu menu;

    // Check on valid input
    string command = "POST www.nextICommand.com";
    vector<string> result = menu.nextICommand(command);
    EXPECT_EQ(result[0], "POST");
    EXPECT_EQ(result[1], "www.nextICommand.com");

    string command1 = "GET www.nextICommand.com";
    vector<string> result1 = menu.nextICommand(command1);
    EXPECT_EQ(result1[0], "GET");
    EXPECT_EQ(result1[1], "www.nextICommand.com");

    string command2 = "DELETE www.nextICommand.com";
    vector<string> result2 = menu.nextICommand(command2);
    EXPECT_EQ(result2[0], "DELETE");
    EXPECT_EQ(result2[1], "www.nextICommand.com");

    // Check on invalid input
    string command3 = "DELETE";
    vector<string> result3 = menu.nextICommand(command3);
    EXPECT_TRUE(result3.empty());

    string command4 = "www.nextICommand.com";
    vector<string> result4 = menu.nextICommand(command4);
    EXPECT_TRUE(result4.empty());

    string command5 = "";
    vector<string> result5 = menu.nextICommand(command5);
    EXPECT_TRUE(result5.empty());

}

TEST(MenuTest, createApp){
    Menu menu;

    //Check on valid input
    string input = "8 1 2";
    App* app = menu.createApp(input);
    ASSERT_NE(app, nullptr);

    string input1 = "100 2 3 4 6 17";
    App* app1 = menu.createApp(input1);
    ASSERT_NE(app1, nullptr);

    //Check on invalid input
    string input2 = "a";
    App* app2 = menu.createApp(input2);
    ASSERT_EQ(app2, nullptr);

    string input3 = "8 8 a 5";
    App* app3 = menu.createApp(input3);
    ASSERT_EQ(app3, nullptr);

    string input4 = "";
    App* app4 = menu.createApp(input4);
    ASSERT_EQ(app4, nullptr);

}