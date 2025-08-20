#include <gtest/gtest.h>
#include <filesystem> 
#include "AddURLToBlacklist.h"
#include "Database.h"
#include "CheckURLInBlacklist.h"
#include "DeleteURL.h"
#include "Blacklist.h"
#include <string>
#include <vector>
using namespace std;


TEST(DeleteTest, SimpleDeleteCheck) {
    filesystem::create_directory("data");
    vector<int> hash = {1,2};
    string input = "www.helloworld.com";
    Database data(8, hash);
    AddURLToBlacklist addCmd(&data);

    // Add URL to blacklist
    string returnMessage;
    returnMessage = addCmd.execute(input);
    set<string> urls =  data.getBlacklist()->getURLs();

    // Expect urls set size = 1, since 1 URL was added
    EXPECT_EQ(urls.count(input), 1);

    DeleteURL deleteCmd(&data);

    // Delete URL from blacklist
    string deleteMessage;
    deleteMessage = deleteCmd.execute(input);
    set<string> urls1 = data.getBlacklist()->getURLs();

    // Expect urls set size = 0, since the only URL was deleted
    EXPECT_EQ(urls1.count(input), 0);

    // Try deleting the same URL again
    string deleteMessage1;
    deleteMessage1 = deleteCmd.execute(input);
    EXPECT_EQ(deleteMessage1, "404 Not Found");
}


TEST(DeleteTest, InvalidDeleteCheck) {
    filesystem::create_directory("data");
    vector<int> hash = {3, 4};
    Database data(8, hash);
    string input = "www.heythere.com";
    DeleteURL deleteCmd(&data);

    // Delete an unblacklisted URL
    string deleteMessage;
    deleteMessage = deleteCmd.execute(input);
    set<string> urls = data.getBlacklist()->getURLs();

    // Expect invalid delete message (logically invalid)
    EXPECT_EQ(deleteMessage, "404 Not Found");

    // Expect urls set size = 0, it doesn't contain URLs
    EXPECT_EQ(urls.count(input), 0);
}