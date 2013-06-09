package org.nlines 

// What GL version you plan on using
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{
  Display, 
  DisplayMode
}
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.newdawn.slick.opengl._
import org.newdawn.slick.util._
import Keyboard._

class Point(xc: Int, yc: Int) {
  var x: Int = xc
  var y: Int = yc

  def getX() = x
  def getY() = y

  def move(dx: Int, dy: Int) {
    x = x + dx
    y = y + dy
  }
  override def toString(): String = "(" + x + ", " + y + ")"
}

object Game extends App {
  val SCREEN_WIDTH:Int = 640
  val SCREEN_HEIGHT:Int = 480
  val GRID_HALF_SIZE:Int = 16
  val GRID_SIZE:Int = GRID_HALF_SIZE*2
  var mouseUp:Boolean = false 
  var mouseDown:Boolean = false 
  //Textures
  var xTexture:Texture = null
  var oTexture:Texture = null
  var xGrid = List[Point]()
  var oGrid = List[Point]()
  //Add some coords
  xGrid = xGrid :+ new Point(1, 2)
  oGrid = oGrid :+ new Point(10, 2)
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
    glEnable(GL_TEXTURE_2D)               
        
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f)          
        
    // enable alpha blending
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    xTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("media/x.png"))
    oTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("media/o.png"))
  }

  def drawQuad(startX:Int, startY:Int, halfWidth:Int, halfHeight:Int) {
    glBegin(GL_LINE_LOOP)
    glVertex2f(startX-halfWidth,startY-halfHeight)
    glVertex2f(startX+halfWidth,startY-halfHeight)
    glVertex2f(startX+halfWidth,startY+halfHeight)
    glVertex2f(startX-halfWidth,startY+halfHeight)
    glEnd()
  }

  def drawTexturedQuad(texture:Texture, startX:Int, startY:Int, halfWidth:Int, halfHeight:Int) {
    texture.bind()
    
    glBegin(GL_QUADS)
      glTexCoord2f(0,0)
      glVertex2f(startX-halfWidth,startY-halfHeight)
      glTexCoord2f(1,0)
      glVertex2f(startX+halfWidth,startY-halfHeight)
      glTexCoord2f(1,1)
      glVertex2f(startX+halfWidth,startY+halfHeight)
      glTexCoord2f(0,1)
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

  def drawX(startX:Int, startY:Int, halfWidth:Int, halfHeight:Int) {
    drawTexturedQuad(xTexture, startX, startY, halfWidth, halfHeight)
  }

  def drawO(startX:Int, startY:Int, halfWidth:Int, halfHeight:Int) {
    drawTexturedQuad(oTexture, startX, startY, halfWidth, halfHeight)
  }

  def drawXSimple(x:Int, y:Int) {
    drawX(GRID_HALF_SIZE + x*(GRID_HALF_SIZE*2), GRID_HALF_SIZE + y*(GRID_HALF_SIZE*2), GRID_HALF_SIZE, GRID_HALF_SIZE)
  }

  def drawOSimple(x:Int, y:Int) {
    drawO(GRID_HALF_SIZE + x*(GRID_HALF_SIZE*2), GRID_HALF_SIZE + y*(GRID_HALF_SIZE*2), GRID_HALF_SIZE, GRID_HALF_SIZE)
  }

  def mainLoop() {
    // Clear the screen and depth buffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)  

    // set the color of the quad (R,G,B,A)
    glColor3f(0.5f,0.5f,1.0f)

    drawGrid()
    xGrid.foreach((elem:Point) => drawXSimple(elem.getX(), elem.getY()))
    oGrid.foreach((elem:Point) => drawOSimple(elem.getX(), elem.getY()))

    if(!Mouse.isButtonDown(0)) mouseUp = true
    if(Mouse.isButtonDown(0) && mouseUp) {
      mouseDown = true
      mouseUp = false
      System.out.println("x is :" + Mouse.getX() + " and y is : " + Mouse.getY())
      System.out.println("translated x is :" + Mouse.getX() / GRID_SIZE + " and y is : " + Mouse.getY()/GRID_SIZE)
      val x = Mouse.getX() / GRID_SIZE
      val y = Mouse.getY() / GRID_SIZE
      //TODO: Make player's move here if it is their turn or send to server.
    }

    Display.update()

    Thread.sleep(60)
    if(!Display.isCloseRequested) mainLoop()
  }
}
