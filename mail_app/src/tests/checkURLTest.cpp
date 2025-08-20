#include <gtest/gtest.h>
#include <filesystem> 
#include "AddURLToBlacklist.h"
#include "Database.h"
#include "CheckURLInBlacklist.h"
#include <string>
#include <vector>
using namespace std;

TEST(checkURLTest, BasicAddAndCheck) {
    filesystem::create_directory("data");
    // Define URLs
    string url1 = "www.bad1.com";
    string url2 = "www.bad2.com";
    string url3 = "www.good.com";

    // Define database
    vector<int> hash = {1,2};
    Database data(8,hash);
    AddURLToBlacklist cmdAdd(&data);
    
    // Add url1, url2 to blacklist
    cmdAdd.execute(url1);
    cmdAdd.execute(url2);

    // Real list check
    CheckURLInBlacklist cmdCheck(&data);

    EXPECT_TRUE(cmdCheck.isInList(url1));
    EXPECT_TRUE(cmdCheck.isInList(url2));
    EXPECT_FALSE(cmdCheck.isInList(url3));


    // If url is in the real list, it also must be in captured in the bloom filter 
    // (assume there is no false negative)
    EXPECT_TRUE(cmdCheck.isInBitArray(url1));
    EXPECT_TRUE(cmdCheck.isInBitArray(url2));

    // If url was not captured in bloom filter, it is not in the real list
    if (!cmdCheck.isInBitArray(url3)) {
        EXPECT_FALSE(cmdCheck.isInList(url3));
    }
}