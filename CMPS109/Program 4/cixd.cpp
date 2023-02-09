// $Id: cixd.cpp,v 1.7 2016-05-09 16:01:56-07 - - $

#include <iostream>
#include <string>
#include <memory>
#include <fstream>
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

void reply_ls (accepted_socket& client_sock, cix_header& header) {
   const char* ls_cmd = "ls -l 2>&1";
   FILE* ls_pipe = popen (ls_cmd, "r");
   if (ls_pipe == NULL) { 
      log << "ls -l: popen failed: " << strerror (errno) << endl;
      header.command = cix_command::NAK;
      header.nbytes = errno;
      send_packet (client_sock, &header, sizeof header);
      return;
   }
   string ls_output;
   char buffer[0x1000];
   for (;;) {
      char* rc = fgets (buffer, sizeof buffer, ls_pipe);
      if (rc == nullptr) break;
      ls_output.append (buffer);
   }
   int status = pclose (ls_pipe);
   if (status < 0) log << ls_cmd << ": " << strerror (errno) << endl;
              else log << ls_cmd << ": exit " << (status >> 8)
                       << " signal " << (status & 0x7F)
                       << " core " << (status >> 7 & 1) << endl;
   header.command = cix_command::LSOUT;
   header.nbytes = ls_output.size();
   memset (header.filename, 0, FILENAME_SIZE);
   log << "sending header " << header << endl;
   send_packet (client_sock, &header, sizeof header);
   send_packet (client_sock, ls_output.c_str(), ls_output.size());
   log << "sent " << ls_output.size() << " bytes" << endl;
}

void reply_get(accepted_socket& client_sock, cix_header& header) {
   //Open up the file of header.filename.
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
      //The null terminating char will be added in the client.
      auto buffer = make_unique<char[]> (header.nbytes);
      //Read the file into the buffer
      file.read(buffer.get(), length);
      //Close the file after reading it into the buffer
      file.close();
      //Change the header command to FILEOUT in order to flag a
      //successfuly GET to the client
      header.command = cix_command::FILEOUT;
      //Send the header to the client, then send in the buffer that
      //contains the contents of the file.
      log << "sending header " << header << endl;
      send_packet(client_sock, &header, sizeof header);
      log << "sending buffer of size: " << length << endl;
      send_packet(client_sock, buffer.get(), length);
   }
   //If the file does not exist
   else {
      //Respond with the NAK command,
      header.command = cix_command::NAK;
      //Set the nbytes to errno,
      header.nbytes = errno;
      //And send the status of the header back to the client.
      cerr << "failed to get file from server, sending NAK to client" <<
      endl;
      send_packet(client_sock, &header, sizeof header);
   }
}

void reply_put(accepted_socket& client_sock, cix_header& header) {
   //Create an outfile that has the same name as header.filename
   ofstream outfile(header.filename, ofstream::binary);
   //If the outfile was successfully made:
   if(outfile) {
      //Set the header command to ACK.
      header.command = cix_command::ACK;
      //Make a buffer of type char[] that has a size of nbytes
      auto buffer = make_unique<char[]> (header.nbytes);
      //And retrieve the file data that the client had sent and store
      //it into buffer, making it a copy of the file
      recv_packet(client_sock, buffer.get(), header.nbytes);
      log << "received " << header.nbytes << " bytes" << endl;
      //And create or overwrite the file of the same name in the current
      //directory
      outfile.write(buffer.get(), sizeof buffer);
      outfile.close();
      //Finally send the updated status of header back to the client.
      log << "sending header " << header << endl;
      send_packet(client_sock, &header, sizeof header);
   }
   //Else
   else {
      //Set the header command to NAK. Meaning the file does not exist
      //from the client.
      header.command = cix_command::NAK;
      //And send the updated status of the header back to the client.
      cerr << "failed to put file into server, sending NAK to client" <<
      endl;
      send_packet(client_sock, &header, sizeof header);
   }
}

void reply_rm(accepted_socket& client_sock, cix_header& header) {
   //Unlink the filename
   int rc = unlink(header.filename);
   //If unlinking failed:
   if(rc == -1) {
      //Set the command to NAK
      header.command = cix_command::NAK;
      //Set the nbytes to errno
      header.nbytes = errno;
      //And send the updated header (flagged failed) to the client
      cerr << "failed to rm file from server, sending NAK to client" <<
      endl;
      send_packet(client_sock, &header, sizeof header);
   }
   //If the unlink is successful
   else {
      //Set the command to ACK
      header.command = cix_command::ACK;
      //And send the updated header (flagged success) to client.
      log << "sending header " << header << endl;
      send_packet(client_sock, &header, sizeof header);
   }
}


void run_server (accepted_socket& client_sock) {
   log.execname (log.execname() + "-server");
   log << "connected to " << to_string (client_sock) << endl;
   try {   
      for (;;) {
         cix_header header; 
         recv_packet (client_sock, &header, sizeof header);
         log << "received header " << header << endl;
         switch (header.command) {
            case cix_command::LS: 
               reply_ls (client_sock, header);
               break;
            case cix_command::GET:
               reply_get(client_sock, header);
               break;
            case cix_command::PUT:
               reply_put(client_sock, header);
               break;
            case cix_command::RM:
               reply_rm(client_sock, header);
               break;
            default:
               log << "invalid header from client:" << header << endl;
               break;
         }
      }
   }catch (socket_error& error) {
      log << error.what() << endl;
   }catch (cix_exit& error) {
      log << "caught cix_exit" << endl;
   }
   log << "finishing" << endl;
   throw cix_exit();
}

void fork_cixserver (server_socket& server, accepted_socket& accept) {
   pid_t pid = fork();
   if (pid == 0) { // child
      server.close();
      run_server (accept);
      throw cix_exit();
   }else {
      accept.close();
      if (pid < 0) {
         log << "fork failed: " << strerror (errno) << endl;
      }else {
         log << "forked cixserver pid " << pid << endl;
      }
   }
}


void reap_zombies() {
   for (;;) {
      int status;
      pid_t child = waitpid (-1, &status, WNOHANG);
      if (child <= 0) break;
      log << "child " << child
          << " exit " << (status >> 8)
          << " signal " << (status & 0x7F)
          << " core " << (status >> 7 & 1) << endl;
   }
}

void signal_handler (int signal) {
   log << "signal_handler: caught " << strsignal (signal) << endl;
   reap_zombies();
}

void signal_action (int signal, void (*handler) (int)) {
   struct sigaction action;
   action.sa_handler = handler;
   sigfillset (&action.sa_mask);
   action.sa_flags = 0;
   int rc = sigaction (signal, &action, nullptr);
   if (rc < 0) log << "sigaction " << strsignal (signal) << " failed: "
                   << strerror (errno) << endl;
}


int main (int argc, char** argv) {
   log.execname (basename (argv[0]));
   log << "starting" << endl;
   vector<string> args (&argv[1], &argv[argc]);
   signal_action (SIGCHLD, signal_handler);
   in_port_t port = get_cix_server_port (args, 0);
   try {
      server_socket listener (port);
      for (;;) {
         log << to_string (hostinfo()) << " accepting port "
             << to_string (port) << endl;
         accepted_socket client_sock;
         for (;;) {
            try {
               listener.accept (client_sock);
               break;
            }catch (socket_sys_error& error) {
               switch (error.sys_errno) {
                  case EINTR:
                     log << "listener.accept caught "
                         << strerror (EINTR) << endl;
                     break;
                  default:
                     throw;
               }
            }
         }
         log << "accepted " << to_string (client_sock) << endl;
         try {
            fork_cixserver (listener, client_sock);
            reap_zombies();
         }catch (socket_error& error) {
            log << error.what() << endl;
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

