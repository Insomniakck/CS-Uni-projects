#include <iostream>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <sstream>
#include <algorithm>
#include <thread>
#include <mutex>
#include <atomic>
#include "Menu.h"
#include "Bloomfilter.h"
#include "App.h"

using namespace std;

// Global mutex
std::mutex app_mutex;

void handle_client(int client_sock, App* app){
    while(true){
        string response = "";
        // Buffer to receive data from client
        char buffer[4096];
        int expected_data_len = sizeof(buffer);

        // Receive data from the client
        int read_bytes = recv(client_sock, buffer, expected_data_len, 0);


        // Error occurred while reading or client closed the connection
        if (read_bytes <= 0) {
            close(client_sock);
            return;
        }


        string input(buffer, read_bytes);

        input.erase(remove(input.begin(), input.end(), '\r'), input.end());
        input.erase(remove(input.begin(), input.end(), '\n'), input.end());

        {
            std::lock_guard<std::mutex> lock(app_mutex);
            if (input.empty()) {
                response = "400 Bad Request";
            } else {
                response = app->run(input);
            }
            
            int sent_bytes = send(client_sock, response.c_str(), response.length(), 0);
            if (sent_bytes < 0) {
                perror("error sending to client");
            }
        }
    }
    // close(client_sock);   
    close(client_sock);
}


int main(int argc, char* argv[]) {
    // Check if the user provided a valid number of arguments
    if (argc < 3) {
        return 1;
    }

    // Create a TCP socket
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("error creating socket");
    }

    // Assign a const port for clients to connect to
    const int port = 5555;
  

    // Set up the server address structure
    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(port);

    // Bind the socket to the specified port and IP
    if (bind(sock, (struct sockaddr *) &sin, sizeof(sin)) < 0) {
        perror("error binding socket");
    }

    //Create an instance of Bloomfilter 
    ostringstream bloomInput;
    for(int i = 1; i < argc; i++) {
        bloomInput << argv[i];
        if (i < argc -1) {
            bloomInput << " ";
        }
    }
    // If arguments for Bloomfilter are invalid, exit the program
    Bloomfilter* bloom = Bloomfilter::createBloom(bloomInput.str());
    if(!bloom){
        return 1;
    }
    App* app = new App(bloom);


        
    // Listen for incoming connections (up to 10 in the queue)
    if (listen(sock, 10) < 0) {
        perror("error listening to a socket");
    }

    while(true) {

        // Accept a client connection
        struct sockaddr_in client_sin;
        unsigned int addr_len = sizeof(client_sin);
        int client_sock = accept(sock, (struct sockaddr*)&client_sin, &addr_len);
        if (client_sock < 0) {
            perror("error accepting client");
            continue;
        }

        // Call handleClient function
        thread(handle_client, client_sock, app).detach();
    }
    
    delete app;
    // Close client and server sockets
    close(sock);
    return 0;
}