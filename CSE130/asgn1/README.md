Name: Andy Chen
CruzID: achen81
StudentID: 1537306
Assignment: Assignment 1: HTTP Server

How to use:
1. Run make then ./httpserver <ip/localhost> <portnumber>
2. On another console, run curl -v -T <filename> <ip/localhost>:<portnumber> for PUT
3. Run curl -v <ip/localhost>:<portnumber>/<filename> for GET
4. For running put without Content Length:
   curl -v -T <filename> <ip/localhost>:<portnumber> -H "Content-Length:"
   Note: This will result in the server reading the entire file, but also
         result in having to manually close the connection from the client

Sources used:
1. https://stackoverflow.com/questions/6537436/how-do-you-get-file-size-by-fd