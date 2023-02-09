// $Id: interp.cpp,v 1.2 2015-07-16 16:57:30-07 - - $

#include <memory>
#include <string>
#include <vector>
using namespace std;

#include <GL/freeglut.h>

#include "debug.h"
#include "interp.h"
#include "shape.h"
#include "util.h"

unordered_map<string,interpreter::interpreterfn>
interpreter::interp_map {
   {"define" , &interpreter::do_define },
   {"draw"   , &interpreter::do_draw   },
   {"border" , &interpreter::do_border },
   {"moveby" , &interpreter::do_moveby },
};

unordered_map<string,interpreter::factoryfn>
interpreter::factory_map {
   {"text"     , &interpreter::make_text     },
   {"ellipse"  , &interpreter::make_ellipse  },
   {"circle"   , &interpreter::make_circle   },
   {"polygon"  , &interpreter::make_polygon  },
   {"rectangle", &interpreter::make_rectangle},
   {"square"   , &interpreter::make_square   },
   {"diamond"  , &interpreter::make_diamond  },
   {"triangle" , &interpreter::make_triangle },
   {"right_triangle", &interpreter::make_right_triangle },
   {"isosceles", &interpreter::make_isosceles},
   {"equilateral", &interpreter::make_equilateral},
};

interpreter::shape_map interpreter::objmap;

interpreter::~interpreter() {
   for (const auto& itor: objmap) {
      cout << "objmap[" << itor.first << "] = "
           << *itor.second << endl;
   }
}

void interpreter::interpret (const parameters& params) {
   DEBUGF ('i', params);
   param begin = params.cbegin();
   string command = *begin;
   auto itor = interp_map.find (command);
   if (itor == interp_map.end()) throw runtime_error ("syntax error");
   interpreterfn func = itor->second;
   func (++begin, params.cend());
}

void interpreter::do_define (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   string name = *begin;
   //Stores the newly made shape partnered with its name as the key into
   //the unordered hashmap "objmap". The key will be the name of the
   //object and the value will be the pointer to the object of type
   //specified shape that will contain parameters for the characteristic
   //of that shape.
   objmap.emplace (name, make_shape (++begin, end));
}


void interpreter::do_draw (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   if (end - begin != 4) throw runtime_error ("syntax error");
   //Get the name (key)
   string name = begin[1];
   //And try to match it with all the elements in objmap
   shape_map::const_iterator itor = objmap.find (name);
   if (itor == objmap.end()) {
      throw runtime_error (name + ": no such shape");
   }
   rgbcolor color {begin[0]};
   vertex where {from_string<GLfloat> (begin[2]),
                 from_string<GLfloat> (begin[3])};
   //Hold off on the drawing here as the draw() function is also called
   //in graphics.cpp, which is in window::display.
   //So for now, do_draw() will add objects to the objects list for
   //graphics.cpp so that window::display() will be the one drawing
   //the objects.
   //itor->second->draw (where, color);//

   //If the shape/object is found (exists in the objmap), declare a new
   //object,
   object shape_obj;
   //Assign its pshape to the value of the corresponding key of the map
   //(The pointer that points to the object type)
   shape_obj.get_shape() = itor->second;
   //Assign its center coordinates to the vertex "where", indicating
   //where the shape will be drawn.
   shape_obj.get_center() = where;
   //And assign its color to color specified in the command string.
   shape_obj.get_color() = color;
   //Finally push the new object into the object list in the class
   //window so that window can loop through the list and display every
   //object in that list.
   window::push_back(shape_obj);
}

void interpreter::do_border (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   //If there are more than or less than 2 arguments, throw an error.
   if (end - begin != 2) throw runtime_error ("do_border: syntax err");
   rgbcolor color {begin[0]};
   begin++;
   window::border_color = color;
   window::border_thickness = stoi(*begin);
}

void interpreter::do_moveby (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   if (begin == end) throw runtime_error ("do_moveby: Specify pixels");
   window::pixels_to_move = stoi(*begin);
}

shape_ptr interpreter::make_shape (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   string type = *begin++;
   auto itor = factory_map.find(type);
   if (itor == factory_map.end()) {
      throw runtime_error (type + ": no such shape");
   }
   factoryfn func = itor->second;
   //Returns the pointer to the object of the shape that was made that
   //contains arguments that was passed to its constructor.
   return func (begin, end);
}

shape_ptr interpreter::make_text (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   auto find_font = fontcode.find(*begin++);
   if(find_font == fontcode.end()) {
      throw runtime_error("make_text: Font not found");
   }
   //auto font = find_font->second;
   string textdata = *begin++;
   while(begin != end) {
      textdata += " " + *begin++;
   }
   void* font = find_font->second;
   //Creates an object of type text that passes in the arguments font
   //and text (string) to its constructors. Returns the pointer to
   //that object.
   return make_shared<text> (font, textdata);
}

shape_ptr interpreter::make_ellipse (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<ellipse> (GLfloat(stof(*begin++)), 
                              GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_circle (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<circle> (GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_polygon (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   vertex_list vertices;
   while(begin != end) {
      vertices.push_back({GLfloat(stof(*begin++)), 
                        GLfloat(stof(*begin++))});
   }
   return make_shared<polygon> (vertices);
}

shape_ptr interpreter::make_rectangle (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<rectangle> (GLfloat(stof(*begin++)), 
                                 GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_square (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<square> (GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_diamond (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<diamond> 
         (GLfloat(stof(*begin++)), GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_triangle (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   vertex_list vertices;
   while(begin != end) {
   vertices.push_back({GLfloat(stof(*begin++)), 
                     GLfloat(stof(*begin++))});
   }
   return make_shared<triangle> (vertices);                        
}

shape_ptr interpreter::make_right_triangle (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<right_triangle> (GLfloat(stof(*begin++)), 
                                       GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_isosceles (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<isosceles> (GLfloat(stof(*begin++)), 
                                 GLfloat(stof(*begin)));
}

shape_ptr interpreter::make_equilateral (param begin, param end) {
   DEBUGF ('f', range (begin, end));
   return make_shared<equilateral> (GLfloat(stof(*begin)));
}

