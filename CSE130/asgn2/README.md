Important Note: Used 1 late day

Name: Andy Chen
CruzID: achen81
StudentID: 1537306
Assignment: Assignment 2: Multi-Threaded HTTP Server With Logging

How to use:
1. Clone or pull the code on Ubuntu or on the Unix Timeshare
	Reason for this is because sem_t is used for my code and as I was running my code on
	MacOSX, sem_init() was not supported. Different OS treats sem_t differently, but 
	the Unix timeshare and Ubuntu seemed to work for compiling and running my code
2. Run make then ./httpserver <ip/localhost> <portnumber> <-N %d> <-l %s>
	The -N and -l flags can be put anywhere after ./httpserver
3. Server is now running, so you can now run multiple clients on it

Note: Multiple PUT requests may hang the server due to read() hanging for some odd reason
	  that I cannot figure out still
Note: If running on the default port of 80, sudo must be used!
