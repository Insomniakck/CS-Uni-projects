import socket
import sys

# Check for correct number of arguments
if len(sys.argv) != 3:
    sys.exit(1)

# Create a TCP socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Set destination IP address and port number
dest_ip = sys.argv[1]

dest_port = int(sys.argv[2])

# Connect the socket to the chosen server IP and port
s.connect((dest_ip, dest_port))

while True:

    # Wait for user input
    msg = input()
    # For server to handle if user pressed enter (didn't send anything)
    if msg == "":
        msg = "\n"
    # Send the message as UTF-8 encoded bytes to the server
    s.send(bytes(msg, 'utf-8'))

    # Receive up to 4096 bytes from the server
    data = s.recv(4096)
    decoded = data.decode('utf-8')
    # Decode and print the server's response
    print(decoded)

s.close()