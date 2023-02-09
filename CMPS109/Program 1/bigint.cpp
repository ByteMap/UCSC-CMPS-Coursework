// $Id: bigint.cpp,v 1.76 2016-06-14 16:34:24-07 - - $

#include <cstdlib>
#include <exception>
#include <stack>
#include <stdexcept>
using namespace std;

#include "bigint.h"
#include "debug.h"
#include "relops.h"

bigint::bigint (long that): uvalue (that), is_negative (that < 0) {
   DEBUGF ('~', this << " -> " << uvalue)
}

bigint::bigint (const ubigint& uvalue, bool is_negative):
                uvalue(uvalue), is_negative(is_negative) {
}

bigint::bigint (const string& that) {
   is_negative = that.size() > 0 and that[0] == '_';
   uvalue = ubigint (that.substr (is_negative ? 1 : 0));
}

bigint bigint::operator+ () const {
   return *this;
}

bigint bigint::operator- () const {
   return {uvalue, not is_negative};
}

bigint bigint::operator+ (const bigint& that) const {
  //If both numbers' sign are the same, then simply add them together
  //and flag the result's sign with the two numbers' sign.
  if(is_negative == that.is_negative) {
    return {uvalue + that.uvalue, is_negative};
  }
  //Else if the two numbers' sign are different, then do the following.
  else {
    //If the first number is greater that the second number, then
    //subtract the first number from the second and flag the sign with
    //the sign of the first number.
    if(uvalue > that.uvalue) {
      return {uvalue - that.uvalue, is_negative};
    }
    //Else the second number must be greater, therefore subtract the
    //second number from the first number and flag the result with the 
    //sign of the second number.
    else {
      return {that.uvalue - uvalue, that.is_negative};
    }
  }
}

bigint bigint::operator- (const bigint& that) const {
  //If the signs of both numbers are the same then check the value
  //of each number of both positive and negative.
  if(is_negative == that.is_negative) {
    //By default, if the first value is greater than the second value,
    //then no matter what sign they are, the difference between the 
    //first and the second number will have the sign of the first num.
    if(uvalue > that.uvalue) {
      return {uvalue - that.uvalue, is_negative};
    }
    //If the first number is smaller than the second number and both are
    //positive, then the difference between the two will be negative.
    else if(uvalue < that.uvalue && !is_negative) {
      return {that.uvalue - uvalue, true};
    }
    //If the first number is smaller than the second number (both are
    //unsigned) and both are negative, then the difference between the 
    //two will be positive.
    else if(uvalue < that.uvalue && is_negative) {
      return {that.uvalue - uvalue, false};
    }
    //Else the two numbers are equal to one another, so the result will
    //be zero and the sign will be positive; so is_negative is false.
    else { return {uvalue - that.uvalue, false} ;}
  }
  //Else if both numbers have different signs:
  //If the first number is greater than the second value, then add the
  //first with the second and flag it as the first number's sign.
  else if(uvalue > that.uvalue) {
    return {uvalue + that.uvalue, is_negative};
  }
  //If the first number is smaller than the second number, then add the
  //second with the first and flag it as the first number's sign.
  else if(uvalue < that.uvalue) {
    return {that.uvalue + uvalue, is_negative}; 
  }
  //Else the two numbers are equal to one another, so the result will
  //be zero and the sign will be positive; so is_negative is false.
  else { return {uvalue - that.uvalue, false} ;}
}

bigint bigint::operator* (const bigint& that) const {
  //If the two numbers' signs are the sign, then the result must be a
  //positive number.
  if(is_negative == that.is_negative) {
    return {uvalue * that.uvalue, false};
  }
  //Else if the signs are different, then the result must be a negative
  //number.
  else { return {uvalue * that.uvalue, true}; }
}

bigint bigint::operator/ (const bigint& that) const {
  //If the two numbers' signs are the sign, then the result must be a
  //positive number.
  if(is_negative == that.is_negative) {
    return {uvalue / that.uvalue, false};
  }
  //Else if the signs are different, then the result must be a negative
  //number.
  else { return {uvalue / that.uvalue, true}; }
}

bigint bigint::operator% (const bigint& that) const {
  //If the two numbers' signs are the same, then the result must be the
  //same.
  if(is_negative == that.is_negative) {
    return {uvalue % that.uvalue, is_negative};
  }
  //Else if the signs are different:
  //If the first number is negative, then the result will be positive.
  else if(is_negative) { 
    return {uvalue % that.uvalue, false}; 
  }
  //Else if the second number is negative, the result will be negative.
  else { return {uvalue % that.uvalue, true}; }
}

bool bigint::operator== (const bigint& that) const {
  //Check if the signs are the same AND check if the values are the same
  //by calling the ubigint::operator==.
  return is_negative == that.is_negative and uvalue == that.uvalue;
}

bool bigint::operator< (const bigint& that) const {
  //If the signs are not the same, then return the first number's sign
  //as + < - returns + (false) and - < + returns - (true).
  if (is_negative != that.is_negative) return is_negative;
  //If the signs are the same, then check if the number is a
  //negative. If so, return (second value < first value). Else return
  //(first value < second value). Example: -10 < -5 => 5 < 10 and
  // 10 < 5 => 10 < 5.
  return is_negative ? that.uvalue < uvalue
                     : uvalue < that.uvalue;
}

ostream& operator<< (ostream& out, const bigint& that) {
  //Calls on the operator<< in ubigint to print out the sign of the
  //number followed by the resulting vector itself.
  return out << (that.is_negative ? "-" : "") << that.uvalue;
}

