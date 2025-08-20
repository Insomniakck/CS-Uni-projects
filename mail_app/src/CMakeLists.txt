cmake_minimum_required(VERSION 3.14)
project(my_project)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

include_directories(${CMAKE_SOURCE_DIR}/header)

# --------------------------
# Collect server source files
file(GLOB SERVER_SOURCES ${CMAKE_SOURCE_DIR}/cpp/*.cpp)

# Create the server executable
add_executable(server ${SERVER_SOURCES})

# --------------------------
# Prepare test sources
# Remove server.cpp to avoid double main issue
list(REMOVE_ITEM SERVER_SOURCES ${CMAKE_SOURCE_DIR}/cpp/server.cpp)
file(GLOB TEST_SOURCES ${CMAKE_SOURCE_DIR}/tests/*.cpp)

# Create the test executable
add_executable(run_tests ${SERVER_SOURCES} ${TEST_SOURCES})
target_link_libraries(run_tests GTest::gtest_main)

# Enable test discovery
include(GoogleTest)
gtest_discover_tests(run_tests)
