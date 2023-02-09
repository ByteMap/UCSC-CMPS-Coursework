// $Id: graphics.h,v 1.1 2015-07-16 16:47:51-07 - - $

#ifndef __GRAPHICS_H__
#define __GRAPHICS_H__

#include <memory>
#include <vector>
using namespace std;

#include <GL/freeglut.h>

#include "rgbcolor.h"
#include "shape.h"

class object {
   private:
      shared_ptr<shape> pshape;
      vertex center;
      rgbcolor color;
      bool selected = false;
      int num = 0;
      GLfloat bord_thickness;
      rgbcolor bord_color;
   public:
      // Default copiers, movers, dtor all OK.
      void draw() { pshape->draw (center, color, selected, 
                                 bord_thickness, bord_color, num); }
      void move (GLfloat delta_x, GLfloat delta_y) {
         center.xpos += delta_x;
         center.ypos += delta_y;
      }
      void select() { selected = true; }
      void deselect() { selected = false; }
      bool select_status() { return selected; }
      void display_num(int new_number) { num = new_number; }
      int& get_num() { return num; }
      vertex& get_center() { return center; }
      shared_ptr<shape>& get_shape() { return pshape; }
      rgbcolor& get_color() { return color; }
      GLfloat& get_bord_thickness() { return bord_thickness; }
      rgbcolor& get_bord_color() { return bord_color; }

};

class mouse {
      friend class window;
   private:
      int xpos {0};
      int ypos {0};
      int entered {GLUT_LEFT};
      int left_state {GLUT_UP};
      int middle_state {GLUT_UP};
      int right_state {GLUT_UP};
   private:
      void set (int x, int y) { xpos = x; ypos = y; }
      void state (int button, int state);
      void draw();
};


class window {
      friend class mouse;
   private:
      static int width;         // in pixels
      static int height;        // in pixels
      static vector<object> objects;
      static size_t selected_obj;
      static mouse mus;
   private:
      static void close();
      static void entry (int mouse_entered);
      static void display();
      static void reshape (int width, int height);
      static void keyboard (GLubyte key, int, int);
      static void special (int key, int, int);
      static void motion (int x, int y);
      static void passivemotion (int x, int y);
      static void mousefn (int button, int state, int x, int y);
   public:
      static int pixels_to_move;   // in pixels
      static int border_thickness;
      static rgbcolor border_color;
      static void select_object(size_t object);
      static void move_selected_object(GLfloat xpos, GLfloat ypos);
      static void push_back (const object& obj) {
                  objects.push_back (obj); }
      static void setwidth (int width_) { width = width_; }
      static void setheight (int height_) { height = height_; }
      static int get_width() { return width; }
      static int get_height() { return height; }
      static GLfloat get_border_thickness() { return border_thickness; }
      static rgbcolor get_border_color() { return border_color; }
      static void main();
};

#endif

