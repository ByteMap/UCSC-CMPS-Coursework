// $Id: commands.cpp,v 1.17 2018-01-25 14:02:55-08 - - $

#include "commands.h"
#include "debug.h"
#include <string.h>
#include <algorithm>
#include <iomanip>

command_hash cmd_hash {
   {"cat"   , fn_cat   },
   {"cd"    , fn_cd    },
   {"echo"  , fn_echo  },
   {"exit"  , fn_exit  },
   {"ls"    , fn_ls    },
   {"lsr"   , fn_lsr   },
   {"make"  , fn_make  },
   {"mkdir" , fn_mkdir },
   {"prompt", fn_prompt},
   {"pwd"   , fn_pwd   },
   {"rm"    , fn_rm    },
   {"rmr"   , fn_rmr   },
};

command_fn find_command_fn (const string& cmd) {
   // Note: value_type is pair<const key_type, mapped_type>
   // So: iterator->first is key_type (string)
   // So: iterator->second is mapped_type (command_fn)
   DEBUGF ('c', "[" << cmd << "]");
   const auto result = cmd_hash.find (cmd);
   if (result == cmd_hash.end()) {
      throw command_error (cmd + ": no such function");
   }
   return result->second;
}

command_error::command_error (const string& what):
            runtime_error (what) {
}

int exit_status_message() {
   int exit_status = exit_status::get();
   cout << execname() << ": exit(" << exit_status << ")" << endl;
   return exit_status;
}

inode_ptr goto_destination(inode_state& state, const string& path) {
   //Declare the inode_ptr inside of the scope.
   inode_ptr position;
   //This is specifically for help_make() in the case of making a file
   //or directory from the root.
   //Note: all the other methods aside from help_make() will return an
   //error if the path is empty if calling on this method.
   //help_make() trims out the whole path if starting from the root.
   if(path.size() == 0) {
      return state.get_root();
   }
   //Check to see if path starts at the root or not. If it does,
   //set the inital position to the root, else set it to the cwd.
   //Example: /usr/CMPS-109/Program2/commands.cpp starts at the root.
   //         CMPS-109/Program2/commands.cpp starts at the cwd.
   if(path.at(0) == '/') {
      position = state.get_root();
   } else { position = state.get_cwd(); }
   //Tracks if the file is found.
   bool located = false;
   //Now split the string path by "/" and store that into a vector
   //so we can access each directory until we get to the file.
   //Note: split() is not a built-in function. It is in the util.cpp.
   wordvec paths = split(path, "/");
   //If the size of paths is 0, it means that the path argument is "/"
   //or the root. So return the inode pointer to the root.
   if(paths.size() == 0) {
      return position;
   }
   for(size_t i = 0; i < paths.size(); i++) {
      for(auto j = position->get_contents()->get_dirents().begin(); 
               j != position->get_contents()->get_dirents().end(); 
               j++) {
         //cout << paths[i] << j->first << i << paths.size()-1 << endl;

         //If the map confirms the existence of the file/directory in
         //the wordvec, then check if the paths iterator is at the end.
         //If so, it means that the file/directory has been found; so
         //set the new position to the position that of the file/dir.
         if((paths[i] == j->first) && (i == paths.size()-1)) {
            //cout << j->first << endl;
            return j->second;
         }
         //Else if the paths iterator is not at the end, but a sub-
         //directory has been confirmed, reset the position to point
         //at the subdirectory and then break out of the inner loop
         //and traverse through the dirents of the new position.
         else if((paths[i] == j->first) && (i != paths.size()-1)) {
            position = j->second;
            break;
         }
      }
   }
   //If the file or directory has not been found at the end of travers.
   //through the wordvec and map, then throw an error.
   if(located == false) {
      throw command_error("No Such File or Unreachable Directory");
   }
   return position; 
}

void fn_cat (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   if(words.size() == 1) {
      throw command_error("cat: No Files Are Specified");
   }
   for(size_t i = 1; i < words.size(); i++) {
      inode_ptr destination = goto_destination(state, words[i]);
      if(destination->get_contents()->is_file()) {
         cout << destination->get_contents()->readfile() << endl;
      }
      else {
         throw command_error("cat: Cannot cat A Directory");
      }
   }
}

void fn_cd (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   if(words.size() > 2) {
      throw command_error("cd: More Than One Operand Is Given");
   }
   else if(words.size() == 1) {
      state.set_nwd(state.get_root());
   }
   else {
      inode_ptr destination = goto_destination(state, words[1]);
      if(destination->get_contents()->is_file()) {
         throw command_error("cd: cannot cd to a file");
      }
      else {
         state.set_nwd(destination);
      }
   }
}

void fn_echo (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   cout << word_range (words.cbegin() + 1, words.cend()) << endl;
}


void fn_exit (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   throw ysh_exit();
}

void help_path(inode_state& state, inode_ptr position) {
   //Create a wordvec to store the path.
   wordvec path;
   //First retrieve the current working directory.
   path.insert(path.begin(), position->get_contents()->get_name());
   if(position == state.get_root()) {
      path.insert(path.begin(), "/");
   }
   //When the working_dir is not the root
   while(position != state.get_root()) {
      //Traverse through its dirents
      for(auto i = position->get_contents()->get_dirents().begin();
          i != position->get_contents()->get_dirents().end();
          i++) {
         //If the dirent name is = "..", its a parent.
         if(i->first == "..") {
            //So insert the name of that parent into the first pos of
            //the wordvec path.
            path.insert(path.begin(), "/");
            path.insert(path.begin(), 
                        i->second->get_contents()->get_name());
            //Change the new working_dir to the parent directory.
            position = i->second;
            //Break from the for loop in order to traverse through the
            //dirents of the new working directory.
            break;
         }
      }
   }
   //Now traverse through the path vector and print out the path.
   for(auto j = path.begin(); j != path.end(); j++) {
      cout << *j;
   }
}

void fn_ls (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   //If no pathname is specified, the current directory is used.
   if(words.size() == 1) {
      inode_ptr position = state.get_cwd();
      //Print out the path of the position.
      help_path(state, position);
      cout << ":" << endl;
      for(auto i = position->get_contents()->get_dirents().begin();
               i != position->get_contents()->get_dirents().end();
               i++) {
         //setw() is from iomanip. It sets the width to 6, so that
         //aligning columns for inode_nr and size is a lot simpler
         cout << setw(6) << i->second->get_inode_nr() << setw(6)
              << i->second->get_contents()->size() << "  ";
         //If the iterator is pointing to a parent directory (.), 
         //itself (..), or a file: add in the name only to cout.
         if((i->first == ".") || (i->first == "..") || 
            (i->second->get_contents()->is_file())) {
            cout << i->first << endl;
         }
         //Else: The iterator is pointing to a directory, so add in the
         //name of the directory followed by a "/" to cout.
         else {
            cout << i->first << "/" << endl;
         }
      }
   }
   else {
      for(size_t i = 1; i < words.size(); i++) {
         inode_ptr position = goto_destination(state, words[i]);
         //If the position is not pointing to a file, then traverse
         //through its dirents and print the dirents out.
         if(!(position->get_contents()->is_file())) {
            //Print out the path of the position.
            help_path(state, position);
            cout << ":" << endl;
            //Traverse through all of the dirents in position.
            for(auto j = position->get_contents()->
                get_dirents().begin();
                j != position->get_contents()->get_dirents().end();
                j++) {
               //setw() is from iomanip. It sets the width to 6, so that
               //aligning columns for inode_nr and size is a lot simpler
               cout << setw(6) << j->second->get_inode_nr() << setw(6)
               << j->second->get_contents()->size() << "  ";
               //If the iterator is pointing to a parent directory (.), 
               //itself (..), or a file: add in the name only to cout.
               if((j->first == ".") || (j->first == "..") || 
                  (j->second->get_contents()->is_file())) {
                  cout << j->first << endl;
               }
               //Else: The iterator is pointing to a directory, so add
               //in the name of the directory followed by a "/" to cout.
               else {
                  cout << j->first << "/" << endl;
               }
            }
         }
         //Else: If the position is pointing to a file, simply print
         //      out the file elements (inode_r, size, and name).
         else {
            cout << position->get_contents()->get_name() << ":" << endl;
            cout << setw(6) << position->get_inode_nr() << setw(6)
                 << position->get_contents()->size() << "  "
                 << position->get_contents()->get_name() << endl;
         }
      }
   }
}

void help_lsr (inode_state& state, inode_ptr position) {
   help_path(state, position);
   cout << ":" << endl;
   //First traverse through the dirents of the directory and ls them
   for(auto i = position->get_contents()->get_dirents().begin();
       i != position->get_contents()->get_dirents().end();
       i++) {
      //setw() is from iomanip. It sets the width to 6, so that
      //aligning columns for inode_nr and size is a lot simpler
      cout << setw(6) << i->second->get_inode_nr() << setw(6)
           << i->second->get_contents()->size() << "  ";
      //If the iterator is pointing to a parent directory (.), 
      //itself (..), or a file: add in only the name to cout.
      if((i->first == ".") || (i->first == "..") || 
         (i->second->get_contents()->is_file())) {
         cout << i->first << endl;
      }
      //Else: The iterator is pointing to a directory, so add in the
      //name of the directory followed by a "/" to cout.
      else {
         cout << i->first << "/" << endl;
      }
   }
   //Traverse through the directory one more time, this time checking
   //for any child directories. If found, recursively call the function
   //with the new child directory pointer.
   for(auto j = position->get_contents()->get_dirents().begin();
       j != position->get_contents()->get_dirents().end();
       j++) {
      //If a child directory is found:
      if((!(j->second->get_contents()->is_file())) && 
         (j->first != ".") && (j->first != "..")) {
         //Recursively call the function so that the child directory
         //is ls'ed
         help_lsr(state, j->second);
      }
   }
}

void fn_lsr (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   //If no pathname is specified, the current directory is used.
   if(words.size() == 1) {
      inode_ptr position = state.get_cwd();
      help_lsr(state, position);
   }
   else {
      for(size_t i = 1; i < words.size(); i++) {
         inode_ptr position = goto_destination(state, words[i]);
         help_lsr(state, position);
      }
   }
}

inode_ptr help_make(inode_state& state, const string& path) {
   string path_copy = path;
   wordvec paths = split(path, "/");
   //If the cwd is the root and the path did not specify a "/" in the
   //front, add the "/" to the front.
   if((state.get_cwd() == state.get_root()) && path_copy.at(0) != '/') {
      path_copy.insert(0, "/");
   }
   //After splitting up the string by "/", if the wordvec size is equal
   //to 1, it means that the file is being made on the current working
   //directory, so return the cwd.
   if(paths.size() == 1) {
      return state.get_cwd();
   }
   //First reverse the path.
   //Example: usr/Desktop/CMPS109/util.h => 
   //         h.litu/901SPMC/potkseD/rsu/
   reverse(path_copy.begin(), path_copy.end());
   //Find the position of the first "/"
   size_t found = path_copy.find("/");
   //Delete the chars up to the first "/" plus the "/" itself.
   //Example: h.litu/901SPMC/potkseD/rsu/ => 901SPMC/potkseD/rsu/
   path_copy = path_copy.substr(found+1);
   //Re-reverse the path.
   //Example: 901SPMC/potkseD/rsu/ => /usr/Desktop/CMPS109/
   reverse(path_copy.begin(), path_copy.end());
   //Run goto_destination() on the new path to get the pointer
   inode_ptr destination = goto_destination(state, path_copy);
   //Return that pointer to the new path
   return destination;
}

void fn_make (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   if(words.size() == 1) {
      throw command_error("make: Need At Least 2 Operands");
   }
   else {
      //Pointer to the parent directory of the new file.
      inode_ptr position = help_make(state, words[1]);
      //Define the file_position inside of the scope as the data will be
      //stored in if else statements.
      inode_ptr file_position;
      //Turn the string path into a wordvec to enable access to the file
      //that is being requested to be made.
      wordvec paths = split(words[1], "/");
      for(auto i = position->get_contents()->get_dirents().begin();
          i != position->get_contents()->get_dirents().end();
          i++) {
         //If the file already exists, assign the file ptr to file_pos
         if(i->first == paths.back()) {
            file_position = i->second;
            //If the newly assigned pointer is pointing to a directory:
            //throw an error.
            if(!(file_position->get_contents()->is_file())) {
               throw command_error("make: calling make on a directory");
            }
         }
         //Else create the new file and assign it to file_position.
         else {
            file_position = 
            position->get_contents()->mkfile(paths.back());
         }
      }
      //Create a copy of words since words is a const
      wordvec to_write = words;
      //Erase the first two elements of the copy of word; leaving only
      //the contents that are needed to be written to the file.
      to_write.erase(to_write.begin());
      to_write.erase(to_write.begin());
      //Write the contents to the file.
      file_position->get_contents()->writefile(to_write);
   }
}

void fn_mkdir (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   bool dir_exists = false;
   if(words.size() == 1) {
      throw command_error("makedir: path is not specified");
   }
   if(words.size() > 2) {
      throw command_error("makedir: contains more than one operand");
   }
   //Pointer to the parent directory of the new directory.
   inode_ptr position = help_make(state, words[1]);
   //Turn the string path into a wordvec to enable access to the dir
   //that is being requested to be made.
   wordvec paths = split(words[1], "/");
   for(auto i = position->get_contents()->get_dirents().begin();
       i != position->get_contents()->get_dirents().end();
       i++) {
      //If the directory already exists, throw an error.
      if(i->first == paths.back()) {
         dir_exists = true;
         throw command_error("makedir: file/dir name already exists");
      }
      //Else create the new directory.
   }
   if(dir_exists == false) {
      //Assign the new directory to the variable dir.
      inode_ptr dir = position->get_contents()->mkdir(paths.back());
      //Inserting a new map element to the new directory for itself.
      dir->get_contents()->get_dirents().insert(pair
          <string, inode_ptr>(".", dir));
      //Inserting a new map element to the new directory for parent.
      dir->get_contents()->get_dirents().insert(pair
          <string, inode_ptr> ("..", position));
   }

}

void fn_prompt (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   string prompt;
   for(size_t i = 1; i < words.size(); i++) {
      prompt = prompt + words[i] + " ";
   }
   state.set_prompt(prompt);
}

void fn_pwd (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   //Create a wordvec to store the path.
   wordvec path;
   //First retrieve the current working directory.
   inode_ptr working_dir = state.get_cwd();
   path.insert(path.begin(), working_dir->get_contents()->get_name());
   //When the working_dir is not the root
   while(working_dir != state.get_root()) {
      //Traverse through its dirents
      for(auto i = working_dir->get_contents()->get_dirents().begin();
          i != working_dir->get_contents()->get_dirents().end();
          i++) {
         //If the dirent name is = "..", its a parent.
         if(i->first == "..") {
            //So insert the name of that parent into the first pos of
            //the wordvec path.
            path.insert(path.begin(), "/");
            path.insert(path.begin(), 
                        i->second->get_contents()->get_name());
            //Change the new working_dir to the parent directory.
            working_dir = i->second;
            //Break from the for loop in order to traverse through the
            //dirents of the new working directory.
            break;
         }
      }
   }
   //Now traverse through the path vector and print out the path.
   for(auto j = path.begin(); j != path.end(); j++) {
      cout << *j;
   }
   cout << endl;
}

void fn_rm (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   if(words.size() == 1) {
      throw command_error("rm: need to specify path");
   }
   else {
      bool found = false;
      //Get the parent directory of the file/directory that is being del
      inode_ptr parent_dir = help_make(state, words[1]);
      //Turn the string path into a wordvec to enable access to the file
      //or directory that is being requested to be deleted.
      wordvec paths = split(words[1], "/");
      //Traverse through the dirents of the parent directory and try to
      //find the file/directory that is being deleted.
      for(auto i = parent_dir->get_contents()->get_dirents().begin();
          i != parent_dir->get_contents()->get_dirents().end();
          i++) {
         //If the name matches (i.e. the file/directory has been found).
         if(i->first == paths.back()) {
            //If the base_file that is being deleted is a directory.
            if(!(i->second->get_contents()->is_file())) {
               //And if it is not empty (. and .. does not count).
               if(i->second->get_contents()->size() > 2) {
                  //Then throw an error
                  throw command_error("rm: directory must be empty");
               }
               //Else remove the directory.
               else {
                  parent_dir->get_contents()->remove(i->first);
                  found = true;
               }
            }
            //Else remove the file.
            else {
               parent_dir->get_contents()->remove(i->first);
               found = true;
            }
         }
      }
      if(found == false) {
         throw command_error("rm: file or directory does not exist");
      }
   }
}

void help_rmr(inode_ptr position) {
   for(auto i = position->get_contents()->get_dirents().begin();
            i != position->get_contents()->get_dirents().end();
            i++) {
      //If pointing to parent or iteself, continue
      if((i->first == ".") || (i->first == "..")) {
         //continue;
      }
      //Else if pointing to a directory that is not "." or ".."
      //And the size of the directory is greater than 2:
      //recursive call the function with the child directory.
      else if((!(i->second->get_contents()->is_file())) && 
              (i->second->get_contents()->size() > 2)) {
         help_rmr(i->second);
      }
   }
   for(auto j = position->get_contents()->get_dirents().begin();
            j != position->get_contents()->get_dirents().end();
            j++) {
      if((j->first == ".") || (j->first == "..")) {
         continue;
      }
      position->get_contents()->remove(j->first);
   }
}

void fn_rmr (inode_state& state, const wordvec& words){
   DEBUGF ('c', state);
   DEBUGF ('c', words);
   if(words.size() == 1) {
      throw command_error("rmr: path needs to be specified");
   }
   wordvec paths = split(words[1], "/");
   for(size_t i = 1; i != words.size(); i++) {
      //Uses help_make() instead of goto_destination() because
      //help_make() returns the parent while goto_destination() only
      //returns the furthest input as the parents.
      //Example: help_make(): rmr x => also deletes x in the end.
      //         goto_destination(): rmr x => does not delete x
      inode_ptr position = help_make(state, words[i]);
      help_rmr(position);
   }
}

