// $Id: file_sys.cpp,v 1.6 2018-06-27 14:44:57-07 - - $

#include <iostream>
#include <stdexcept>
#include <unordered_map>

using namespace std;

#include "debug.h"
#include "file_sys.h"

int inode::next_inode_nr {1};

struct file_type_hash {
   size_t operator() (file_type type) const {
      return static_cast<size_t> (type);
   }
};

ostream& operator<< (ostream& out, file_type type) {
   static unordered_map<file_type,string,file_type_hash> hash {
      {file_type::PLAIN_TYPE, "PLAIN_TYPE"},
      {file_type::DIRECTORY_TYPE, "DIRECTORY_TYPE"},
   };
   return out << hash[type];
}

inode_state::inode_state() {
   DEBUGF ('i', "root = " << root << ", cwd = " << cwd
          << ", prompt = \"" << prompt() << "\"");
   root = make_shared<inode>(file_type::DIRECTORY_TYPE);
   cwd = root;
   root->get_contents()->get_dirents().insert(pair<string, inode_ptr>
                                                  (".", root));
   root->get_contents()->get_dirents().insert(pair<string, inode_ptr>
                                                  ("..", root));
}

const string& inode_state::prompt() const { return prompt_; }

inode_ptr inode_state::get_root() {
  return root;
}

inode_ptr inode_state::get_cwd() {
  return cwd;
}

void inode_state::set_nwd(inode_ptr new_directory) {
  cwd = new_directory;
}

void inode_state::set_prompt(string prompt) {
  prompt_ = prompt;
}

ostream& operator<< (ostream& out, const inode_state& state) {
   out << "inode_state: root = " << state.root
       << ", cwd = " << state.cwd;
   return out;
}

inode::inode(file_type type): inode_nr (next_inode_nr++) {
   switch (type) {
      case file_type::PLAIN_TYPE:
           contents = make_shared<plain_file>();
           break;
      case file_type::DIRECTORY_TYPE:
           contents = make_shared<directory>();
           break;
   }
   DEBUGF ('i', "inode " << inode_nr << ", type = " << type);
}

int inode::get_inode_nr() const {
   DEBUGF ('i', "inode = " << inode_nr);
   return inode_nr;
}

base_file_ptr inode::get_contents() {
  return contents;
}


file_error::file_error (const string& what):
            runtime_error (what) {
}

size_t plain_file::size() const {
  size_t size {0};
  DEBUGF ('i', "size = " << size);
  for(size_t i = 0; i < data.size(); i++) {
    size++;
  }
  return size;
}

const wordvec& plain_file::readfile() const {
   DEBUGF ('i', data);
   return data;
}

void plain_file::writefile (const wordvec& words) {
   DEBUGF ('i', words);
   data = words;
}

void plain_file::remove (const string&) {
   throw file_error ("is a plain file");
}

inode_ptr plain_file::mkdir (const string&) {
   throw file_error ("is a plain file");
}

inode_ptr plain_file::mkfile (const string&) {
   throw file_error ("is a plain file");
}

map<string, inode_ptr>& plain_file::get_dirents() {
  throw file_error("is a plain file");
}

void plain_file::set_name(string new_name) {
  name = new_name;
}

string plain_file::get_name() {
  return name;
}


size_t directory::size() const {
   size_t size = dirents.size();
   DEBUGF ('i', "size = " << size);
   return size;
}

const wordvec& directory::readfile() const {
   throw file_error ("is a directory");
}

void directory::writefile (const wordvec&) {
   throw file_error ("is a directory");
}

void directory::remove (const string& filename) {
   DEBUGF ('i', filename);
   dirents.erase(filename);
}

inode_ptr directory::mkdir (const string& dirname) {
   DEBUGF ('i', dirname);
   inode_ptr new_dir = make_shared<inode>(file_type::DIRECTORY_TYPE);
   new_dir->get_contents()->set_name(dirname);
   dirents.insert(pair<string, inode_ptr>(dirname, new_dir));
   return new_dir;
}

inode_ptr directory::mkfile (const string& filename) {
   DEBUGF ('i', filename);
   inode_ptr new_file = make_shared<inode>(file_type::PLAIN_TYPE);
   new_file->get_contents()->set_name(filename);
   dirents.insert(pair<string, inode_ptr>(filename, new_file));
   return new_file;
}

void directory::set_name(string new_name) {
  name = new_name;
}

string directory::get_name() {
  return name;
}

map<string, inode_ptr>& directory::get_dirents() {
  return dirents;
}

