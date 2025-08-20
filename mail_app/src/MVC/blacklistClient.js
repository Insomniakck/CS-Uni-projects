const net = require('net');

// Define the host and port to connect to
// process.env.BLACKLIST_HOST in case the user tries to connect to the blacklist server through docker
const HOST = process.env.BLACKLIST_HOST || 'localhost';
const PORT = process.env.BLACKLIST_PORT || 5555;

function sendToBlacklistServer(command, url) {
    return new Promise((resolve, reject) => {
        // Create a new TCP socket
        const client = new net.Socket();

        // When connected to the server, send the command and URL
        client.connect(PORT, HOST, () => {
            const message = `${command} ${url}`;  // format: "command URL"
            client.write(message);                  // Send message to server
        });

        // When data is received from the server
        client.on('data', (data) => {
            resolve(data.toString().trim());        // Convert buffer to string
            client.destroy();                       // Close connection after getting response
        });

        // If an error occurs during connection, show error message
        client.on('error', (err) => {
            reject('Connection error: ' + err.message); 
        });

        client.on('close', () => {
            // Connection closed
        });
    });
}

module.exports = {
    sendToBlacklistServer
}
