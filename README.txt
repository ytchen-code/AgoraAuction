README for AgoraAliveAuction project

=====================================================================

(Video of this workflow is inside ./video/ directory)
Example Workflow for 5 Servers and 3 Clients (also tests fault tolerance):
Please use separate terminals for each server and client.

Start 5 Servers (invoke these commands from the same directory as this README.txt):
Server 1: java -jar ./jars/server/Server.jar 30000 localhost:30001,localhost:30002,localhost:30003,localhost:30004
Server 2: java -jar ./jars/server/Server.jar 30001 localhost:30000,localhost:30002,localhost:30003,localhost:30004
Server 3: java -jar ./jars/server/Server.jar 30002 localhost:30000,localhost:30001,localhost:30003,localhost:30004
Server 4: java -jar ./jars/server/Server.jar 30003 localhost:30000,localhost:30001,localhost:30002,localhost:30004
Server 5: java -jar ./jars/server/Server.jar 30004 localhost:30000,localhost:30001,localhost:30002,localhost:30003

Start 3 Clients (invoke these commands from the same directory as this README.txt):
Client 1 connecting to Server 1: java -jar ./jars/client/Client.jar localhost:30000
    then enter name: Sam
Client 2 connecting to Server 2: java -jar ./jars/client/Client.jar localhost:30001
    then enter name: Bob
Client 3 connecting to Server 3: java -jar ./jars/client/Client.jar localhost:30002
    then enter name: John

Client 1 creates an auction for "car" with starting bid price of 1000 dollars:
On Client 1, type: CREATE car 1000

Client 1 creates an auction for "shoes" with starting bid price of 5 dollars:
On Client 1, type: CREATE shoes 5

Client 1 checks to see if his auctions are created:
On Client 1, type: LIST

Client 2 views all the auctions:
On Client 2, type: LIST

Client 2 bids for "car" with a bid of 1100 dollars:
On Client 2, type: BID car 1100

Client 2 bids for "shoes" with a bid of 6 dollars:
On Client 2, type: BID shoes 6

Client 3 views all the auctions:
On Client 3, type: LIST

Client 3 bids for "car" with a bid of 1200 dollars:
On Client 3, type: BID car 1200

Client 1 checks the status of his auctions for "car" and "shoes":
On Client 1, type: LIST

Client 1 is happy with the current bid price for "car" and closes the auction:
On Client 1, type: CLOSE car

Client 1 decides that he doesn't want to sell "shoes" anymore and deletes the auction:
On Client 1, type: DELETE shoes

Client 1 checks to see if previous close and delete were successful:
On Client 1, type: LIST

Client 2 bids for "car" with 1300 but it will fail (car auction was just closed above):
On Client 2, type: BID car 1300

To test for fault tolerance, kill Server 2 (Client 2 is connected to this):
On Server 2, type: control+c

Start Server 2 again, Server 2 will automatically synchronize with the server cluster:
java -jar ./jars/server/Server.jar 30001 localhost:30000,localhost:30002,localhost:30003,localhost:30004

Use Client 2 (which is connected to Server 2) to check that Server 2 has recovered all the data:
On Client 2, type: LIST

=====================================================================

Log generation
The following logs will be generated in the same directory as this README.txt when
servers and clients are run:
./server_log.txt
./client_log.txt

=====================================================================

directories:
./sample_logs/
    contains the sample logs for server and client
./video/
    contains the video of running the above workflow (and the fault tolerance testing)
=====================================================================
