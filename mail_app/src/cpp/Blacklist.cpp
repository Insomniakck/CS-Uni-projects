#include "Blacklist.h"
#include <string>
#include <set>
#include <fstream>  
#include <filesystem> 
#include <iostream> 
using namespace std;

// Constructor. Load urls from file to set (if file exists)
Blacklist::Blacklist() {
    const string filename = "./data/blacklistFile.txt";
    ifstream infile(filename);

    if (infile.is_open()) {
        string line;
        while (getline(infile, line)) {
            if (!line.empty()) {
                urls.insert(line);
            }
        }
        infile.close();
    }
}


// Getter for urls set 
set<string> Blacklist::getURLs() {
    return urls;
}


// Append URL to file
void Blacklist::appendToFile(string& url) {
    const string filename = "./data/blacklistFile.txt";

    // Check if file exists
    if (!filesystem::exists(filename)) {
        // File doesn't exist, create it
        ofstream newFile(filename);
        newFile.close();
    }

    // Open file in append mode
    ofstream outfile(filename, ios::app);
    if (outfile.is_open()) {
        outfile << url << '\n'; // Append URL to the file
        outfile.close();
    } else {
        // If file couldn't be opened, show error
        cout << "Error: Unable to open file for appending.\n";
    }  
}


// Add URL to urls set
void Blacklist::addToSet(string& url) {
    urls.insert(url);
}


// Add URL to urls set and to file
void Blacklist::add(string& url) {
    int setSize = urls.size();
    addToSet(url);
    int newSetSize = urls.size();

    // if set size remained the same, URL was already in set and file. No need to add it again.
    if (setSize != newSetSize) {
        appendToFile(url);
    }
}


// Check if URL is in urls set
bool Blacklist::isInSet(string& url) {
    return urls.find(url) != urls.end();
}


// Check if URL is in file "blacklistFile.txt"
bool Blacklist::isInFile(string& url) {
    const string filename = "./data/blacklistFile.txt";

    // Check if the file exists
    if (!filesystem::exists(filename)) {
        return false;
    }

    // Open the file and check line by line
    ifstream infile(filename);
    string line;
    while (getline(infile, line)) {
        if (line == url) {
            return true; // URL found in file
        }
    }
    
    return false; // URL not found
}


// Remove URL from urls set
void Blacklist::removeFromSet(string& url) {
    urls.erase(url);
}


// Remove URL from file
void Blacklist::removeFromFile(string& url) {
    string filename = "./data/blacklistFile.txt";
    string tempFilename = "./data/temp_blacklistFile.txt";

    // If the file doesn't exist, nothing to remove
    if (!filesystem::exists(filename)) {
        return;
    }

    ifstream infile(filename);
    ofstream tempFile(tempFilename);
    string line;
    while (getline(infile, line)) {
        // As long as ling != url, add it to the new file
        if (line != url) {
            tempFile << line << '\n';
        }
    }

    infile.close();
    tempFile.close();

    // Replace original file with updated temp file
    filesystem::remove(filename);
    filesystem::rename(tempFilename, filename);
}