FROM gcc:latest

# Install dependencies
RUN apt-get update && apt-get install -y cmake

# Copy the entire project directory into the container
COPY . /usr/src/server

# Make sure the blacklistFile.txt is inside data/ at build time
RUN mkdir -p /usr/src/server/build/data

# Verify the file structure inside the container (for debugging)
RUN ls -R /usr/src/server

# Create a build directory and move there
WORKDIR /usr/src/server/build

# Run cmake on the CMakeLists.txt 
RUN cmake ..
RUN make

EXPOSE 5555

COPY entrypoint.sh /usr/src/server/entrypoint.sh
RUN chmod +x /usr/src/server/entrypoint.sh
ENTRYPOINT ["/usr/src/server/entrypoint.sh"]

