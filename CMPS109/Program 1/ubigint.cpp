// $Id: ubigint.cpp,v 1.14 2016-06-23 17:21:26-07 - - $

#include <cctype>
#include <cstdlib>
#include <exception>
#include <stack>
#include <stdexcept>
#include <string>
using namespace std;

#include "ubigint.h"
#include "debug.h"

//Takes every digit passed from bigint and passes it to a vector
//with the highest order digit first and onwards. Vector stores the
//digits from end to start.
ubigint::ubigint (unsigned long that) {
   while(that > 0) {
      ubig_value.push_back(that % 10);
      that = that / 10;
   }
   DEBUGF ('~', this << " -> " << that)
}

//Turns every char from the string into an integer by subtracting
//the ASCII value of '0' (i.e 48).
//Insert the integer to the back of the vector with the highest order
//first and onwards.
ubigint::ubigint (const string& that) {
   DEBUGF ('~', "that = \"" << that << "\"");
   for (char digit: that) {
      if (not isdigit (digit)) {
         throw invalid_argument ("ubigint::ubigint(" + that + ")");
      }
      ubig_value.insert(ubig_value.begin(), (digit - '0'));
   }
}

ubigint ubigint::operator+ (const ubigint& that) const {
   ubigint result;
   int sum = 0, carry = 0;

   //Traverse the smaller number first so that no out of bounds
   //exception will be reached.
   for(size_t i = 0; i < that.ubig_value.size(); i++) {
      sum = ubig_value[i] + that.ubig_value[i] + carry;
      if(sum >= 10) { carry = 1; }
      else { carry = 0;}
      sum = sum % 10;
      result.ubig_value.push_back(sum);
   }
   //When the smaller number has run out of digits, traverse through
   //and push the sum of the larger number and the carry where the
   //starting index is the size of the smaller number.
   if(ubig_value.size() != that.ubig_value.size()) {
      for(size_t j = that.ubig_value.size(); 
                      j < ubig_value.size(); j++) {
         sum = ubig_value[j] + carry;
         if(sum >= 10) { carry = 1; }
         else { carry = 0;}
         sum = sum % 10;
         result.ubig_value.push_back(sum);
      }
   }
   //If no more numbers are remaining, but the carry is still greater
   //than one, simply push_back the carry to the result. (i.e 1+9).
   if(carry > 0) {
      result.ubig_value.push_back(carry);
   }
   //Trim the leading zeroes before returning the result.
   while(result.ubig_value.size() > 0 && result.ubig_value.back() == 0){
      result.ubig_value.pop_back();
   }
   //Return the new resulting vector.
   return result;
}

ubigint ubigint::operator- (const ubigint& that) const {
   if (*this < that) throw domain_error ("ubigint::operator-(a<b)");
   ubigint result;
   int sum = 0, borrow = 0;
   //Traverse the smaller number first so that no out of bounds
   //exception will be reached.
   for(size_t i = 0; i < that.ubig_value.size(); i++) {
      sum = ubig_value[i] - that.ubig_value[i] - borrow;
      if(sum < 0) { 
         sum = sum + 10;
         borrow = 1; 
      }
      else { borrow = 0; }
      result.ubig_value.push_back(sum);
   }
   //When the smaller number has run out of digits, traverse through
   //and push the difference of the larger number and the borrow 
   //where the starting index is the size of the smaller number.
   if(ubig_value.size() != that.ubig_value.size()) {
      for(size_t j = that.ubig_value.size();
                      j < ubig_value.size(); j++) {
         sum = ubig_value[j] - borrow;
         if(sum < 0) {
            sum = sum + 10;
            borrow = 1;
         }
         else { borrow = 0; }
         result.ubig_value.push_back(sum);
      }
   }
   //Trim the leading zeroes before returning the result.
   while(result.ubig_value.size() > 0 && result.ubig_value.back() == 0){
      result.ubig_value.pop_back();
   }
   //Return the new resulting vector.
   return result;
}

ubigint ubigint::operator* (const ubigint& that) const {
   //Creates a new ubigint called result, then by using vector:assign,
   //The new ubigint will have the size of ubig_value and
   //that.ubig_value so that accessing result.ubig_value[i+j] is
   //possible. By setting every element to 0 beforehand, the leading
   //zeroes can be cleaned up before returning the result.
   ubigint result;
   result.ubig_value.assign(ubig_value.size() + that.ubig_value.size(),
                            0);
   int product = 0, carry = 0;
   //Start at the beginning of the first vector and increment down to 
   //the end.
   for(size_t i = 0; i < ubig_value.size(); i++) {
      carry = 0;
      //Start at the beginning of the second vector and increment down
      //to the end.
      for(size_t j = 0; j < that.ubig_value.size(); j++) {
         //Multiply the digits of both vectors at ith place and add that
         //to the carry and the digit at the i+jth place in the result.
         //Mod the product of the number and store that into the i+jth
         //place of the result. (Note: Adding the product with the i+jth
         //place of the resulting vector is the same as doing the long
         //addition in multiplication).
         product = result.ubig_value[i + j] + 
               (ubig_value[i] * that.ubig_value[j]) + carry;
         result.ubig_value[i+j] = product % 10;
         //Divide the product by 10 and set that to the carry.
         carry = product / 10;
      }
      //In case if there is a carry, input the carry (whether it is a
      //0 or a 1) into the new end of the resulting vector (i.e. size +
      //1).
      result.ubig_value[i + that.ubig_value.size()] = carry;
   }
   //Trim the leading zeroes before returning the result.
   while(result.ubig_value.size() > 0 && result.ubig_value.back() == 0){
      result.ubig_value.pop_back();
   }
   //Return the new resulting vector.
   return result;
}

void ubigint::multiply_by_2() {
   int product = 0, carry = 0;
   //Start at the lowest order digit in the vector and traverse up to
   //the end of the digit.
   for(size_t i = 0; i < ubig_value.size(); i++) {
      //Override the value at the ith place of the vector with the
      //product of (the value at the ith position of the vector * 2) +
      //carry.
      product = (ubig_value[i] * 2) + carry;
      //Mod the product by 10 and override the current value at position
      //i.
      ubig_value[i] = product % 10;
      //Divide the product by 10 and store it into the carry. (The
      //carry can only be a 1 or 0).
      carry = product / 10;
   }
   //If a carry exists, but the loop has reached the end of the vector,
   //then push the carry to the new end of the vector.
   if(carry > 0) {
      ubig_value.push_back(carry);
   }
   //Trim the leading zeroes before returning the result.
   while(ubig_value.size() > 0 && ubig_value.back() == 0) {
      ubig_value.pop_back();
   }
}

void ubigint::divide_by_2() {
   int quotient = 0, borrow = 0;

   //Start at the highest order digit in the vector and traverse down
   //to the end of the digit.
   for(int i = ubig_value.size() - 1; i >= 0; i--) {
      //Override the value at the ith place of the vector with the
      //quotient of the (original value + borrow)/2.
      quotient = (borrow + ubig_value[i]) / 2;
      //Mod the original value at the ith position of the vector by 2,
      //then multiply it by 10 in the case that a borrow is needed.
      borrow = (ubig_value[i] % 2) * 10;
      ubig_value[i] = quotient;
   }
   //Trim the leading zeroes before returning the result.
   while(ubig_value.size() > 0 && ubig_value.back() == 0) {
      ubig_value.pop_back();
   }
}


//Function that does egyptian division.
struct quo_rem { ubigint quotient; ubigint remainder; };
quo_rem udivide (const ubigint& dividend, ubigint divisor) {
   // Note: divisor is modified so pass by value (copy).
   //Create 4 new vectors: zero which is a vector of 0, power_of_2 which
   //is a vector that begins with 1, quotient that starts off as a 0,
   //and remainder that is a copy of the dividend.
   ubigint zero {0};
   if (divisor == zero) throw domain_error ("udivide by zero");
   ubigint power_of_2 {1};
   ubigint quotient {0};
   ubigint remainder {dividend}; // left operand, dividend
   //Keep multiplying by 2 the divisor and the power_of_2 until the
   //divisor is greater than or equal to the remainder.
   while (divisor < remainder) {
      divisor.multiply_by_2();
      power_of_2.multiply_by_2();
   }
   //When the divisor is greater or equal to the remainder:
   //Loops until power_of_2 is equal to 0.
   while (zero < power_of_2) {
      if (divisor <= remainder) {
         //Subtract the remainder with the divisor.
         remainder = remainder - divisor;
         //Since my ubigint::operator+ does not check for the larger
         //number, The following code checks for the larger number
         //before sending them to operator+.
         if(quotient < power_of_2) { quotient = power_of_2 + quotient; }
         else { quotient = quotient + power_of_2; }
      }
      //Divides both the divisor and power_of_2 by 2.
      divisor.divide_by_2();
      power_of_2.divide_by_2();
   }
   //Returns the quotient and the remainder from the egyptian division.
   return {.quotient = quotient, .remainder = remainder};
}

//Returns the quotient from egyptian divion. Calls on the non-member
//function udivide that makes use of member objects.
ubigint ubigint::operator/ (const ubigint& that) const {
   return udivide (*this, that).quotient;
}

//Returns the remainder from egyptian divion. Calls on the non-member
//function udivide that makes use of member objects.
ubigint ubigint::operator% (const ubigint& that) const {
   return udivide (*this, that).remainder;
}

bool ubigint::operator== (const ubigint& that) const {
   //If the sizes of two vectors are not equal, return false.
   if(ubig_value.size() != that.ubig_value.size()) { return false; }
   //If it is equal, check every digit in the vector. If any of them
   //are not equal, return false.
   for (size_t i = 0; i < ubig_value.size(); i++) {
      if(ubig_value[i] != that.ubig_value[i]) { return false; }
   }
   //Return true if the vectors are the same.
   return true;
}

bool ubigint::operator< (const ubigint& that) const {
   //If the sizes of two vectors are less than or equal to one
   //another:
   if(ubig_value.size() <= that.ubig_value.size()) { 
      //If the first vector's size is less than the second vector's
      //Return false.
      if(ubig_value.size() < that.ubig_value.size()) {
         return true;
      } else {
         //Else go through every integer in each vector:
         for(int i = ubig_value.size() - 1; i >= 0; i--) {
            //If two integers are equal, skip the rest of the statements
            //and continue the loop.
            if(ubig_value[i] == that.ubig_value[i]) { continue; }
            //If the first integer is greater than the second integer,
            //Return false.
            else if(ubig_value[i] > that.ubig_value[i]){ return false; }
            //Else the second integer must be less than the first, so
            //Return true.
            else { return true; }
         }
      }
   }
   //Return false if the first vector size is greater than the second's
   return false;
}

ostream& operator<< (ostream& out, const ubigint& that) {
   int counter = 0;
   //Start from the end of the vector and decrement down to the start
   //of the vector.
   for(int i = that.ubig_value.size() - 1; i >= 0; i--) {
      //Turn each integer back into a char by adding the ASCII value
      //of 48 or '0'.
      char asciiChar = that.ubig_value[i] + '0';
      //Increment the counter.
      counter++;
      //store the new char into the ostream out.
      out << asciiChar;
      //If the counter is equal to 69 (i.e. length of chars is 69):
      //store "\\" and new line into out and reset the counter to 0.
      if(counter == 69) {
         out << "\\" << endl;
         counter = 0;
      }
   }
   //Return the ostream, out so that it will print out every new char.
   return out;
}

