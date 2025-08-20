#include <gtest/gtest.h>
#include "AddURLToBlacklist.h"
#include "CheckURLInBlacklist.h"
#include "Database.h"
#include "Blacklist.h"
#include <string>
#include <vector>
#include <filesystem> 

using namespace std;

TEST(presistenceTest, SavedUpdate){
    vector<int> hash = {2};
    string url1 = "www.presistence.com";
    filesystem::create_directory("data");

    {
        
        Database data(8, hash);
        AddURLToBlacklist cmd(&data);
        
        cmd.execute(url1);
    }

    {
        Database data(8, hash);
        CheckURLInBlacklist cmd(&data);
        EXPECT_EQ(data.getBlacklist()->getURLs().count("www.presistence.com"),1);
        EXPECT_TRUE(cmd.isInList(url1));
        EXPECT_TRUE(cmd.isInBitArray(url1));
    }
}
