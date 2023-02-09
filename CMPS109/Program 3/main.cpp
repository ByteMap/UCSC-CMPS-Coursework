// $Id: main.cpp,v 1.11 2018-01-25 14:19:29-08 - - $

#include <cstdlib>
#include <exception>
#include <iostream>
#include <string>
#include <fstream>
#include <unistd.h>
#include <regex>

using namespace std;

#include "listmap.h"
#include "xpair.h"
#include "util.h"

using str_str_map = listmap<string,string>;
using str_str_pair = str_str_map::value_type;

void scan_options (int argc, char** argv) {
   opterr = 0;
   for (;;) {
      int option = getopt (argc, argv, "@:");
      if (option == EOF) break;
      switch (option) {
         case '@':
            debugflags::setflags (optarg);
            break;
         default:
            complain() << "-" << char (optopt) << ": invalid option"
                       << endl;
            break;
      }
   }
}

void print_listmap(str_str_map& map, const string& val, const bool all){
   //If the flag 'all' is true;
   if(all) {
      //Traverse through the whole list and print out everything
      //in lexicographic order sorted by key.
      for(auto iter = map.begin(); iter != map.end(); ++iter) {
         cout << iter->first << " = " << iter->second << endl;
      }
   }
   //Else:
   else {
      //Traverse through the whole list
      for(auto iter = map.begin(); iter != map.end(); ++iter) {
         //And check if the iterator pointing to the node contains
         //the same value as the value passed in to the method
         if(iter->second == val) {
            //And if so, print out the key = value in lexicographic
            //order.
            cout << iter->first << " = " << val << endl;
         }
      }
   }
}

void execute_line(string& line, str_str_map& map) {
   //Regex copied from the given file 'matchlines.pdf'
   regex comment_regex {R"(^\s*(#.*)?$)"};
   regex key_value_regex {R"(^\s*(.*?)\s*=\s*(.*?)\s*$)"};
   regex trimmed_regex {R"(^\s*([^=]+?)\s*$)"};
   smatch result;

   //Do not do anything if it is a comment.
   if(regex_search(line, result, comment_regex)) { }

   //Else if there exists a '='
   else if(regex_search(line, result, key_value_regex)) {
      //If there are elements before and after '=':
      if(result[1] != "" && result[2] != "") {
         //Create a pair of the two elements
         str_str_pair pair(result[1], result[2]);
         //And insert it into the map
         map.insert(pair);
         //Then print out the key = value.
         cout << result[1] << " = " << result[2] << endl;
      }
      //Else if the key is present but the value is not:
      else if(result[1] != "" && result[2] == "") {
         //Find the node that has the key in the list
         auto found = map.find(result[1]);
         //And delete that node from the list
         map.erase(found);
      }
      //Else if the key is not present but the value is:
      else if(result[1] == "" && result[2] != "") {
         //Print the list, but only with the nodes that has the
         //corresponding value.
         print_listmap(map, result[2], false);
      }
      //Else if the key and value are not present:
      else {
         //Have a dummy string (as a string is not really needed for 
         //this part).
         string s = "";
         //And print out everything in the list in lexicographic order.
         print_listmap(map, s, true);
      }
   }
   //Else if the '=' is not present and only contains a key:
   else if(regex_search(line, result, trimmed_regex)) {
      //Find the key.
      auto found = map.find(result[1]);
      //If the iterator is pointing to the end (anchor), it means the
      //key is not found and an error message is printed.
      if(found == map.end()) {
         cerr << result[1] << ": key not found" << endl;
      }
      //Else if the key is found:
      else {
         //Print out the key = value.
         cout << found->first << " = " << found->second << endl;
      }
   }
}

int main (int argc, char** argv) {
   sys_info::execname (argv[0]);
   scan_options (argc, argv);

   str_str_map test;
   string line;
   //If there are arguments after the program name:
   if(argc > 1) {
      //Traverse through the arguments:
      //argmt ptr points at second elem. of argv to avoid reading in
      //the program name.
      //*argmt iterates over argv array. argv[argc] represents NULL.
      for(char **argmt = argv+1; *argmt != argv[argc]; argmt++) {
         //Convert "-" to a string so that we can match it.
         string dash = "-";
         //If the argument is a "-", then read in cin.
         if(*argmt == dash) {
            //Line number is set to 1 by default.
            int lnr = 1;
            //Get the line from cin and store it into line.
            while(getline(cin, line)) {
               //Print out the file name (-) followed by the lnr
               //followed by the line of the cin
               cout << "-: " << lnr << ": " << line << endl;
               //And execute the line according to the regex
               execute_line(line, test);
               //and increment the line number.
               lnr++;
            }
         }
         //Else if the argument is a file:
         else {
            //Use ifstream to open up the file.
            ifstream input(*argmt);
            //And if file is opened successfully:
            if(input.is_open()) {
               //Line number is set to 1 by default.
               int lnr = 1;
               //Traverse through the lines in the file one by one
               while(getline(input, line)) {
                  //And print out the file name followed by the lnr
                  //followed by the line that holds the commands
                  cout << *argmt << ": " << lnr << ": " << line << endl;
                  //Then execute the line commands according the the
                  //regex
                  execute_line(line, test);
                  //And increment the line number.
                  lnr++;
               }
               //After all the lines has been parsed through and
               //executed, close the file.
               input.close();
            }
            //Else if the file is not opened successfully, print out
            //an error.
            else {
               cerr << "keyvalue: " << *argmt << 
               ": No such file or directory" << endl;
            }
         }
      }
   }
   //Else: if there are no arguments after the program name:
   //Cin is used by default.
   else {
      //Line number is set to 1 by default.
      int lnr = 1;
      //Traverse through every line in cin
      while(getline(cin, line)) {
         //And print out the file name (-) for cin followed by the lnr
         //followed by the command in the line.
         cout << "-: " << lnr << ": " << line << endl;
         //And then execute the command in the line according to the
         //regex
         execute_line(line, test);
         //And increment the line number.
         lnr++;
      }
   }
   //Finally return 0 when the program successfully ended.
   return 0;
}

