// $Id: cix.cpp,v 1.7 2019-02-07 15:14:37-08 - - $

#include <iostream>
#include <fstream>
#include <memory>
#include <string>
#include <unordered_map>
#include <vector>
using namespace std;

#include <libgen.h>
#include <sys/types.h>
#include <unistd.h>

#include "protocol.h"
#include "logstream.h"
#include "sockets.h"

logstream log (cout);
struct cix_exit: public exception {};

unordered_map<string,cix_command> command_map {
   {"exit", cix_command::EXIT},
   {"help", cix_command::HELP},
   {"ls"  , cix_command::LS  },
   {"get" , cix_command::GET },
   {"put" , cix_command::PUT },
   {"rm"  , cix_command::RM  },
};

static const string help = R"||(
exit         - Exit the program.  Equivalent to EOF.
get filename - Copy remote file to local host.
help         - Print help summary.
ls           - List names of files on remote server.
put filename - Copy local file to remote host.
rm filename  - Remove file from remote server.
)||";

void cix_help() {
   cout << help;
}

void cix_ls (client_socket& server) {
   cix_header header;
   header.command = cix_command::LS;
   log << "sending header " << header << endl;
   send_packet (server, &header, sizeof header);
   recv_packet (server, &header, sizeof header);
   log << "received header " << header << endl;
   if (header.command != cix_command::LSOUT) {
      cerr << "sent LS, server did not return LSOUT" << endl;
      cerr << "server returned " << header << endl;
   }else {
      auto buffer = make_unique<char[]> (header.nbytes + 1);
      recv_packet (server, buffer.get(), header.nbytes);
      log << "received " << header.nbytes << " bytes" << endl;
      buffer[header.nbytes] = '\0';
      cout << buffer.get();
   }
}

void cix_get (client_socket& server, string filename) {
   //Create the cix header
   cix_header header;
   //And before filling in the fields, use memset() to clear the struct
   memset(&header, 0, sizeof header);
   //Then set the command of header to GET
   header.command = cix_command::GET;
   //And set the filename of header (char array) to the string filename.
   strcpy(header.filename, filename.c_str());
   log << "sending header " << header << endl;
   //Send in a packet to the server indicating a request to get a
   //specific file that is under header.filename 
   send_packet(server, &header, sizeof header);
   //And receive the packet that was sent from the server that MAY
   //contain the file that was requested.
   recv_packet(server, &header, sizeof header);
   log << "received header " << header << endl;
   //If the new header command is not FILEOUT:
   //That means the server did not find the file that was asked from
   //the client.
   //So log an error message.
   if(header.command != cix_command::FILEOUT) {
      cerr << "sent GET, server did not find the file " << 
      filename << endl;
      cerr << "server returned " << header << endl;
   }
   //Else the new header command is FILEOUT and the server did find the
   //file that was requested
   else {
      //So make a buffer of type char[] that has a size of nbytes+1
      //Where the last element will be '\0'.
      auto buffer = make_unique<char[]> (header.nbytes + 1);
      //And ask the server to retrieve the data in the file and store
      //it into buffer, making it a copy of the file
      recv_packet(server, buffer.get(), header.nbytes);
      log << "received " << header.nbytes << " bytes" << endl;
      //Then set the last position of buffer to '\0' or null terminate
      buffer[header.nbytes] = '\0';
      //Create an outfile that will have the name of string filename
      ofstream outfile(filename, ofstream::binary);
      //And create or overwrite the file of the same name in the current
      //directory
      outfile.write(buffer.get(), sizeof buffer);
      outfile.close();
   }
}

void cix_put (client_socket& server, string filename) {
   //Create the header
   cix_header header;
   //And before filling in the fields, use memset() to clear the struct
   memset(&header, 0, sizeof header);
   //Then set the command of header to PUT
   header.command = cix_command::PUT;
   //And set the filename of header (char array) to the string filename.
   strcpy(header.filename, filename.c_str());
   //Read the entire file into a string
   ifstream file(header.filename, ifstream::binary);
   //If the file exists
   if(file) {
      //Get the length of the file
      file.seekg(0, file.end);
      int length = file.tellg();
      file.seekg(0, file.beg);
      //Set the nbytes of header to the size of the length of the file
      header.nbytes = length;
      //Create a new buffer of type char[] that has as size of nbyte.
      auto buffer = make_unique<char[]> (header.nbytes);
      //Read the file into the buffer
      file.read(buffer.get(), length);
      //Close the file after reading it into the buffer
      file.close();
      //Send the header to the server, then send in the buffer that
      //contains the contents of the file.
      log << "sending header " << header << endl; 
      send_packet(server, &header, sizeof header);
      log << "sending buffer of size: " << length << endl;
      send_packet(server, buffer.get(), length);
      //Call to receive the back header to check whether or not the
      //new header.command is ACK or NAK.
      recv_packet(server, &header, sizeof header);
      log << "received header " << header << endl;
   }
   //Else if the file does not exist, log an error and return.
   else if(!file) {
      cerr << "ERROR: File " << header.filename << " does not exist" 
      << endl;
      return;
   }
   //If the new header.command is not ACK, then log an error.
   if(header.command != cix_command::ACK) {
      cerr << "file failed to upload to the server" << endl;
      cerr << "server returned " << header << endl;
   }
   //Else the new header.command must be ACK, so log a success message.
   else {
      log << "file was uploaded to the server successfully" << endl;
   }
}

void cix_rm (client_socket& server, string filename) {
   //Create the header
   cix_header header;
   //Then set the command of header to RM
   header.command = cix_command::RM;
   //And set the filename of header to the string filename.
   strcpy(header.filename, filename.c_str());
   //And zero the payload of header.
   header.nbytes = 0;
   log << "sending header " << header << endl;
   //Send the header to the server for deletion. 
   send_packet(server, &header, sizeof header);
   //Call to receive the header and check if deletion was successful
   recv_packet(server, &header, sizeof header);
   log << "received header " << header << endl;
   //If header.command did not return an ACK, deletion was unsuccessful
   //so log an error message.
   if(header.command != cix_command::ACK) {
      cerr << "file failed to delete from server" << endl;
      cerr << "server returned " << header << endl;
   }
   //Else the header.command did return an ACK, so deletion was
   //successful. So log a success message.
   else {
      log << "sent RM, ACK was returned: file was deleted from server"
      << endl;
   }
}


void usage() {
   cerr << "Usage: " << log.execname() << " [host] [port]" << endl;
   throw cix_exit();
}

int main (int argc, char** argv) {
   log.execname (basename (argv[0]));
   log << "starting" << endl;
   vector<string> args (&argv[1], &argv[argc]);
   if (args.size() > 2) usage();
   string host = get_cix_server_host (args, 0);
   in_port_t port = get_cix_server_port (args, 1);
   log << to_string (hostinfo()) << endl;
   try {
      log << "connecting to " << host << " port " << port << endl;
      client_socket server (host, port);
      log << "connected to " << to_string (server) << endl;
      for (;;) {
         string line;
         getline (cin, line);
         //Returns the position of the first space.
         //Will be used in substr() to get the second element (filename)
         //of the string
         size_t split_pos = line.find(" ");
         if (cin.eof()) throw cix_exit();
         log << "command " << line << endl;
         const auto& itor = command_map.find(line.substr(0, split_pos));
         cix_command cmd = itor == command_map.end()
                         ? cix_command::ERROR : itor->second;
         switch (cmd) {
            case cix_command::EXIT:
               throw cix_exit();
               break;
            case cix_command::HELP:
               cix_help();
               break;
            case cix_command::LS:
               cix_ls (server);
               break;
            case cix_command::GET:
               //split_pos+1 skips over the found space and points
               //to the filename.
               cix_get(server, line.substr(split_pos+1));
               break;
            case cix_command::PUT:
               cix_put(server, line.substr(split_pos+1));
               break;
            case cix_command::RM:
               cix_rm(server, line.substr(split_pos+1));
               break;
            default:
               log << line << ": invalid command" << endl;
               break;
         }
      }
   }catch (socket_error& error) {
      log << error.what() << endl;
   }catch (cix_exit& error) {
      log << "caught cix_exit" << endl;
   }
   log << "finishing" << endl;
   return 0;
}

