// $Id: listmap.tcc,v 1.11 2018-01-25 14:19:14-08 - - $

#include "listmap.h"
#include "debug.h"

//
/////////////////////////////////////////////////////////////////
// Operations on listmap::node.
/////////////////////////////////////////////////////////////////
//

//
// listmap::node::node (link*, link*, const value_type&)
//
template <typename Key, typename Value, class Less>
listmap<Key,Value,Less>::node::node (node* next, node* prev,
                                     const value_type& value):
            link (next, prev), value (value) {
}

//
/////////////////////////////////////////////////////////////////
// Operations on listmap.
/////////////////////////////////////////////////////////////////
//

//
// listmap::~listmap()
//
//-template specifies the parameters.

//-angled brackets "<>" specifies that this function's template
// parameter is also the class template parameter
//-~listmap() is the function inside of the class listmap

template <typename Key, typename Value, class Less>
listmap<Key,Value,Less>::~listmap() {
   DEBUGF ('l', reinterpret_cast<const void*> (this));
   //While the linked list is not empty (if the dummy head's next is
   //not pointing to itself):
   //Erase the leading pair element right after the dummy head.
   while(this->begin() != this->end()) {
      this->erase(this->begin());
   }
}


//
// iterator listmap::insert (const value_type&)
//

//-template specifies the parameters.
//-angled brackets "<>" specifies that this function's template
// parameter is also the class template parameter
//-typename specifies that "iterator" is a type of class listmap:
// Thus the static member "insert" of class listmap returns the
// type iterator pointing to the inserted item.

template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::iterator
listmap<Key,Value,Less>::insert (const value_type& pair) {
   DEBUGF ('l', &pair << "->" << pair);
   //If the list only contains an anchor:
   if(this->begin() == this->end()) {
      //Create a new node where its next points to the anchor, its prev
      //points to the anchor, and set its value type as a pair.
      node* new_node = new node(anchor(), anchor(), pair);
      //Set the anchor's next to the new node.
      anchor()->next = new_node;
      //Set the anchor's prev to the new node.
      anchor()->prev = new_node;
      //Return the pointer to the new node.
      return new_node;
   }
   else {
      //Else if the list contains more than just the anchor:
      //Check if the key exists in the list.
      iterator found = this->find(pair.first);
      //If the key does not exist in the list:
      if(found == this->end()) {
         //Create an iterator that starts at the beginning of the list
         iterator it = this->begin();
         //And traverse it down the list.
         for(; it != this->end(); ++it) {
            //If the new pair's key is smaller than the key that the 
            //iterator is pointing at (lexigraphically)
            if(less(pair.first, it->first)) {
               //Then break out of the loop and the iterator will be
               //pointing at the breaking point.
               break;
            }
            //Else the loop will just end at the end of the list whilst
            //returning the iterator that will be pointing to the end.
         }
         //Create a new node where its next will be the iterators' next,
         //the prev will be the iterator's previous position, and its 
         //value type will be a pair.
         node* new_node = new node(it.where, it.where->prev, pair);
         //To finish creating the links for the doubly linked list:
         //The iterator's prev's next will point at the new node
         it.where->prev->next = new_node;
         //And the iterator's previous will point at the new node.
         it.where->prev = new_node;
         //Finally return the pointer to the new node
         return new_node;
      }
      else {
         //Else if the key exists in the list:
         //Update the value corresponding to the key
         found->second = pair.second;
         //Andy return the pointer to the existing, but newly updated
         //node element.
         return found;
      }
   }
   return iterator();
}

//
// listmap::(const key_type&)
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::iterator
listmap<Key,Value,Less>::find (const key_type& that) {
   DEBUGF ('l', that);
   //Create a 'holding' variable that holds the iterator and will by
   //default point at the beginning of the list.
   iterator it = this->begin();
   //Traverse through the list.
   for(; it != this->end(); ++it) {
      //If the node that the iterator is pointing has a key that matches
      //the key of 'that', return the iterator pointing to that node.
      if(it->first == that) {
         return it;
      }
   }
   //Else the returning iterator will be pointing at the anchor()
   //whether or not if the list is empty.
   return it;
}

//
// iterator listmap::erase (iterator position)
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::iterator
listmap<Key,Value,Less>::erase (iterator position) {
   DEBUGF ('l', &*position);
   //Create an iterator to automatically return the pointer to the next
   //node.
   iterator to_return = position.where->next;
   //The previous pointer's next node of position will be pointing to 
   //the position's next node.
   position.where->prev->next = position.where->next;
   //And the next node's previous will be pointing to the pointer's
   //previous node to fully build the doubly linked list.
   position.where->next->prev = position.where->prev;
   //Now to delete everything related to position:
   //The link that connects position to its next node is set to null
   position.where->next = nullptr;
   //And the link that connects position to its prev node is set to null
   position.where->prev = nullptr;
   //position will lastly be destroyed after its links are set to null.
   delete position.where;
   //And the pointer to the next node of the former position will then
   //be returned.
   return to_return;
}


//
/////////////////////////////////////////////////////////////////
// Operations on listmap::iterator.
/////////////////////////////////////////////////////////////////
//

//
// listmap::value_type& listmap::iterator::operator*()
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::value_type&
listmap<Key,Value,Less>::iterator::operator*() {
   DEBUGF ('l', where);
   return where->value;
}

//
// listmap::value_type* listmap::iterator::operator->()
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::value_type* 
listmap<Key,Value,Less>::iterator::operator->() {
   DEBUGF ('l', where);
   return &(where->value);
}

//
// listmap::iterator& listmap::iterator::operator++()
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::iterator&
listmap<Key,Value,Less>::iterator::operator++() {
   DEBUGF ('l', where);
   where = where->next;
   return *this;
}

//
// listmap::iterator& listmap::iterator::operator--()
//
template <typename Key, typename Value, class Less>
typename listmap<Key,Value,Less>::iterator&
listmap<Key,Value,Less>::iterator::operator--() {
   DEBUGF ('l', where);
   where = where->prev;
   return *this;
}


//
// bool listmap::iterator::operator== (const iterator&)
//
template <typename Key, typename Value, class Less>
inline bool listmap<Key,Value,Less>::iterator::operator==
            (const iterator& that) const {
   return this->where == that.where;
}

//
// bool listmap::iterator::operator!= (const iterator&)
//
template <typename Key, typename Value, class Less>
inline bool listmap<Key,Value,Less>::iterator::operator!=
            (const iterator& that) const {
   return this->where != that.where;
}

