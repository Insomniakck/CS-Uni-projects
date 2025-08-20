# Running the project
In order to run the project, you will need docker desktop and android studio.

1) Run the following command according to your OS:  
**Linux:**
SERVER_ARGS="100 10 1" docker-compose up --build app server mongodb react-app

**Windows:**
$env:SERVER_ARGS = "100 10 1" 
docker-compose up --build app server mongodb react-app

Note: If you want to run the server with different bloomfilter parameters, you can change them dynamically by changing the content inside the quotation marks.


2) Run the project on your favorite platform:

## Running the Android application
Open android studio.
The default server ip is set to 10.0.2.2 (localhost), configured to work on emulator.

**Running on a physical device:**
If you want to run the app on your personal device:
1. unlock developer options on your phone.
2. Retrieve your local computer's IPv4 (which you can find using 'ipconfig' command in powershell)
3. Inside the 'config.properties' file, which is located in src\android\AndroidGmail\config.properties
   change: SERVER_IP=<your IP>
4. Save and run the program in android studio


## Running the Desktop application
Open your web browser and enter the following URL:
localhost:3000