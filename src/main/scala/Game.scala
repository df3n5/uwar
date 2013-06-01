package org.nlines 

// What GL version you plan on using
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{
  Display, 
  DisplayMode
}
import org.lwjgl.input.Keyboard
import Keyboard._

object Game extends App {
  var SCREEN_WIDTH = 640
  var SCREEN_HEIGHT = 480
  var GRID_HALF_SIZE = 16
  val displayMode = new DisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT)
  Display.setTitle("uWar")
  Display.setDisplayMode(displayMode)
  Display.create()
  setup()
  mainLoop()
  Display.destroy()

  def setup() {
    // init OpenGL
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, 1, -1)
    glMatrixMode(GL_MODELVIEW)
  }

  def drawQuad(startX:Int, startY:Int, halfWidth:Int, halfHeight:Int) {
    glBegin(GL_LINE_LOOP)
    glVertex2f(startX-halfWidth,startY-halfHeight)
    glVertex2f(startX+halfWidth,startY-halfHeight)
    glVertex2f(startX+halfWidth,startY+halfHeight)
    glVertex2f(startX-halfWidth,startY+halfHeight)
    glEnd()
  }

  def drawGrid(x:Int=0,y:Int=0) {
    if(x*GRID_HALF_SIZE > SCREEN_WIDTH) drawGrid(0, y+1)
    else if(y*GRID_HALF_SIZE > SCREEN_HEIGHT) return
    else {
      drawQuad(GRID_HALF_SIZE + x*(GRID_HALF_SIZE*2), GRID_HALF_SIZE + y*(GRID_HALF_SIZE*2), GRID_HALF_SIZE, GRID_HALF_SIZE)
      drawGrid(x+1, y)
    }
  }

  def mainLoop() {
    // Clear the screen and depth buffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)  

    // set the color of the quad (R,G,B,A)
    glColor3f(0.5f,0.5f,1.0f)

    drawGrid()

    Display.update()

    Thread.sleep(60)
    if(!Display.isCloseRequested) mainLoop()
  }
}
