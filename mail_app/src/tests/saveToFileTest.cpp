#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include "AddURLToBlacklist.h"
#include "Database.h"
#include <vector>
#include <string>

using namespace std;

TEST(saveToFile, sanityTest) {
    vector<int> hash = {1,2};
    Database data(256, hash);
    AddURLToBlacklist cmd(&data);
    string url = "www.saveToFile.com";
    string fileName = "blacklist.txt";
    bool flag = false;

    //clears the blacklist.txt file and adds one line
    fstream file(fileName, ios::trunc);
    cmd.execute(url);

    //opens the file, and looks for the line "www.saveToFile.com"
    if(file.is_open()){
        while(getline(file,url)){
            if(url=="www.saveToFile.com"){
                flag = true;
                break;
            }
        }
        EXPECT_TRUE(flag);
    }
    file.close();
}

TEST(saveToFile, negetiveTest) {
    vector<int> hash = {1,2};
    Database data(256, hash);
    AddURLToBlacklist cmd(&data);
    string url;
    

    //clears the blacklist.txt file
    fstream file("blacklist.txt", ios::trunc);

    //opens the file, and looks for the line "www.negetiveTest.com"
    if(file.is_open()){
        bool flag = false;
        while(getline(file,url)){
            if(url == "www.negetiveTest.com"){
                flag = true;
                break;
            }
        }
        EXPECT_FALSE(flag);
    }
    file.close();
}
