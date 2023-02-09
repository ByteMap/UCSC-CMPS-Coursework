// $Id: shape.cpp,v 1.2 2019-02-28 15:24:20-08 - - $

#include <typeinfo>
#include <unordered_map>
#include <cmath>
using namespace std;

#include "shape.h"
#include "util.h"

static unordered_map<void*,string> fontname {
   {GLUT_BITMAP_8_BY_13       , "Fixed-8x13"    },
   {GLUT_BITMAP_9_BY_15       , "Fixed-9x15"    },
   {GLUT_BITMAP_HELVETICA_10  , "Helvetica-10"  },
   {GLUT_BITMAP_HELVETICA_12  , "Helvetica-12"  },
   {GLUT_BITMAP_HELVETICA_18  , "Helvetica-18"  },
   {GLUT_BITMAP_TIMES_ROMAN_10, "Times-Roman-10"},
   {GLUT_BITMAP_TIMES_ROMAN_24, "Times-Roman-24"},
};

ostream& operator<< (ostream& out, const vertex& where) {
   out << "(" << where.xpos << "," << where.ypos << ")";
   return out;
}

shape::shape() {
   DEBUGF ('c', this);
}

text::text (void* glut_bitmap_font_, const string& textdata_):
      glut_bitmap_font(glut_bitmap_font_), textdata(textdata_) {
   DEBUGF ('c', this);
}

ellipse::ellipse (GLfloat width, GLfloat height):
dimension ({width, height}) {
   DEBUGF ('c', this);
}

circle::circle (GLfloat diameter): ellipse (diameter, diameter) {
   DEBUGF ('c', this);
}


polygon::polygon (const vertex_list& vertices_): vertices(vertices_) {
   DEBUGF ('c', this);
}

rectangle::rectangle (GLfloat width, GLfloat height):
            polygon({{0, height}, {width, height}, {width, 0},{0, 0}}) {
   DEBUGF ('c', this << "(" << width << "," << height << ")");
}

square::square (GLfloat width): rectangle (width, width) {
   DEBUGF ('c', this);
}

diamond::diamond (GLfloat width, GLfloat height): 
            polygon({{width/2, height}, {width, height/2}, 
                     {width/2, 0}, {0, height/2}}) {
   DEBUGF ('c', this << "(" << width << "," << height << ")");
}

triangle::triangle (const vertex_list& vertices_): polygon(vertices_) {
   DEBUGF ('c', this);
}

right_triangle::right_triangle (GLfloat width, GLfloat height):
            triangle({{0, 0}, {0, height}, {width, 0}}) {
   DEBUGF ('c', this << "(" << width << "," << height << ")");
}

isosceles::isosceles (GLfloat width, GLfloat height):
            triangle({{0, 0}, {width/2, height}, {width, 0}}) {
   DEBUGF ('c', this << "(" << width << "," << height << ")");
}

equilateral::equilateral (GLfloat width): 
         triangle({{0, 0}, {width/2, width * GLfloat((sqrt(3)/2))}, 
                  {width, 0}}) {
   DEBUGF ('c', this);
}

/*void draw_number() {
   glColor3ub (0, 0, 0);
   auto ctext = reinterpret_cast<const unsigned char*> (to_string(
                                       this::get_num()));
   auto utext = reinterpret_cast<const GLubyte*> (ctext);
   void* font = GLUT_BITMAP_9_BY_15;
   glRasterPos2f(this->center.xpos, this->center.ypos);
   glutBitmapString(font, utext);
}*/

void text::draw (const vertex& center, const rgbcolor& color, 
                  const bool selected, const GLfloat bord_thickness,
                     const rgbcolor& bord_color, const int num) const {
   DEBUGF ('d', this << "(" << center << "," << color << "," << num
                  << "," << bord_thickness << "," << bord_color << ")");
   auto text_ = reinterpret_cast<const GLubyte*> (textdata.c_str());
   glColor3ubv(color.ubvec);
   glRasterPos2f(center.xpos, center.ypos);
   glutBitmapString(glut_bitmap_font, text_);

   GLfloat xadjust = glutBitmapLength(glut_bitmap_font, text_);
   GLfloat yadjust = glutBitmapHeight(glut_bitmap_font);
   glColor3ub (0, 0, 0);
   auto ctext = to_string(num).c_str();
   auto utext = reinterpret_cast<const GLubyte*> (ctext);
   void* font = GLUT_BITMAP_9_BY_15;
   glRasterPos2f(center.xpos + xadjust/2, center.ypos + yadjust);
   glutBitmapString(font, utext);

   //If selected, draw a border around the text using manipulation of
   //center position.
   if(selected) {
      glLineWidth(bord_thickness);
      glColor3ubv(bord_color.ubvec);
      glBegin(GL_LINE_LOOP);
      //cout << center.xpos << " " << center.ypos << endl;
      //Because the "center" is actually the bottom left vertex of the
      //text, start from that vertex and draw a border around the 
      //text.
      glVertex2f(center.xpos, center.ypos);
      glVertex2f(center.xpos, center.ypos + yadjust);
      glVertex2f(center.xpos + xadjust, center.ypos + yadjust);
      glVertex2f(center.xpos + xadjust, center.ypos);
      glEnd();
   }
}

void ellipse::draw (const vertex& center, const rgbcolor& color, 
                     const bool selected, const GLfloat bord_thickness,
                     const rgbcolor& bord_color, const int num) const {
   DEBUGF ('d', this << "(" << center << "," << color << ")");
   //First smooth the line as it is a circle
   glEnable(GL_LINE_SMOOTH);
   //And set its color
   glColor3ubv(color.ubvec);
   //And begin drawing a polygon
   glBegin(GL_POLYGON);
   //Then draw out every vertex that will make a circle/ellipse.
   const float delta = 2 * M_PI / 32;
   for(float theta = 0; theta < 2 * M_PI; theta += delta) {
      float xpos = (dimension.xpos/2) * cos(theta) + center.xpos;
      float ypos = (dimension.ypos/2) * sin(theta) + center.ypos;
      glVertex2f(xpos, ypos);
   }
   glEnd();

   //Display the object number in the center of the object
   glColor3ub (0, 0, 0);
   auto ctext = to_string(num).c_str();
   auto utext = reinterpret_cast<const GLubyte*> (ctext);
   void* font = GLUT_BITMAP_9_BY_15;
   glRasterPos2f(center.xpos, center.ypos);
   glutBitmapString(font, utext);

   //If selected
   if(selected) {
      //Set the width and color to the border thickness and border
      //color
      glLineWidth(bord_thickness);
      glColor3ubv(bord_color.ubvec);
      //Then smooth the line as it is a border for the circle
      glEnable(GL_LINE_SMOOTH);
      //And begin drawing a line loop
      glBegin(GL_LINE_LOOP);
      //cout << bord_thickness << endl;
      //Loop through each verticies that will make up a circle
      //And draw out the border.
      for(float theta2 = 0; theta2 < 2 * M_PI; theta2 += delta) {
         float border_xpos = (dimension.xpos/2) * cos(theta2) 
                                                + center.xpos;
         float border_ypos = (dimension.ypos/2) * sin(theta2) 
                                                + center.ypos;
         glVertex2f(border_xpos, border_ypos);
      }
      glEnd();
   }
}

void polygon::draw (const vertex& center, const rgbcolor& color, 
                     const bool selected, const GLfloat bord_thickness,
                     const rgbcolor& bord_color, const int num) const {
   DEBUGF ('d', this << "(" << center << "," << color << ")");
   GLfloat xpos_avg;
   GLfloat ypos_avg;
   glEnable(GL_LINE_SMOOTH);
   glColor3ubv(color.ubvec);
   glBegin(GL_POLYGON);
   //Compute the average of the vertices first
   for(auto i = vertices.begin(); i != vertices.end(); i++) {
      xpos_avg = xpos_avg + i->xpos;
      ypos_avg = ypos_avg + i->ypos;
   }
   xpos_avg = xpos_avg/vertices.size();
   ypos_avg = ypos_avg/vertices.size();
   //After computing the average, it is subtracted from each of the
   //vertices to normalize them.
   //Then add the new position to the center positions so that the
   //center of the polygon will be the actual center position.
   //AKA the polygon's center will no longer be at the starting vector.
   for(auto y = vertices.begin(); y != vertices.end(); y++) {
      GLfloat new_xpos = (y->xpos - xpos_avg) + center.xpos;
      GLfloat new_ypos = (y->ypos - ypos_avg) + center.ypos;
      glVertex2f(new_xpos, new_ypos);
   }
   glEnd();

   //This block of code displays the object number in the center of the
   //polygon
   glColor3ub (0, 0, 0);
   auto ctext = to_string(num).c_str();
   auto utext = reinterpret_cast<const GLubyte*> (ctext);
   void* font = GLUT_BITMAP_9_BY_15;
   glRasterPos2f(center.xpos, center.ypos);
   glutBitmapString(font, utext);

   //If the object is selected
   if(selected) {
      //Set the width and color to the border thickness and border color
      glLineWidth(bord_thickness);
      glColor3ubv((bord_color).ubvec);
      glEnable(GL_LINE_SMOOTH);
      //And begin the drawing process of a line loop instead of the
      //usual polygon
      glBegin(GL_LINE_LOOP);
      //Then loop through all of the vertices and draw a border around
      //the polygon.
      for(auto z = vertices.begin(); z != vertices.end(); z++) {
         GLfloat border_xpos = (z->xpos - xpos_avg) + center.xpos;
         GLfloat border_ypos = (z->ypos - ypos_avg) + center.ypos;
         glVertex2f(border_xpos, border_ypos);
      }
      glEnd();
   }
}

void shape::show (ostream& out) const {
   out << this << "->" << demangle (*this) << ": ";
}

void text::show (ostream& out) const {
   shape::show (out);
   out << glut_bitmap_font << "(" << fontname[glut_bitmap_font]
       << ") \"" << textdata << "\"";
}

void ellipse::show (ostream& out) const {
   shape::show (out);
   out << "{" << dimension << "}";
}

void polygon::show (ostream& out) const {
   shape::show (out);
   out << "{" << vertices << "}";
}

ostream& operator<< (ostream& out, const shape& obj) {
   obj.show (out);
   return out;
}

