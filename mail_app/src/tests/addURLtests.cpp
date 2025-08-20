#include <gtest/gtest.h>
#include <filesystem> 
#include "AddURLToBlacklist.h"
#include "Database.h"
#include "Blacklist.h"
#include <string>
#include <vector>
#include <set>


using namespace std;


TEST(addURL,AddURL){
    filesystem::create_directory("data");
    vector<int> hash = {1,2};
    string input = "www.helloworld.com";
    Database data(8, hash);
    set<string> urls1 = data.getBlacklist()->getURLs();
    AddURLToBlacklist cmd(&data);
    EXPECT_EQ(urls1.count(input), 0);


    cmd.execute(input);
    set<string> urls2 =  data.getBlacklist()->getURLs();
    EXPECT_EQ(urls2.count(input), 1);
}

TEST(addURL,AddSameURLTwice){
    filesystem::create_directory("data");
    vector<int> hash = {1};
    string addTwice = "www.addTwice.com";

    Database data(100, hash);
    AddURLToBlacklist cmd(&data);

    cmd.execute(addTwice);
    set<string> urls1 = data.getBlacklist()->getURLs();
    EXPECT_EQ(urls1.count(addTwice),1);

    cmd.execute(addTwice);
    set<string> urls2 = data.getBlacklist()->getURLs();
    EXPECT_EQ(urls1.size(),urls2.size());
}


