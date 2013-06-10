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
  object MoveType extends Enumeration {
    type MoveType = Value
    val X, O = Value
  }
  import MoveType._

  val SCREEN_WIDTH:Int = 480
  val SCREEN_HEIGHT:Int = 480
  val NTILES_X = 4
  val TILE_HALF_LENGTH:Int = SCREEN_WIDTH / (NTILES_X*2)
  val TILE_LENGTH:Int = TILE_HALF_LENGTH*2
  var mouseUp:Boolean = false 
  var mouseDown:Boolean = false 
  //Textures
  var xTexture:Texture = null
  var oTexture:Texture = null
  var xGrid = List[Point]()
  var oGrid = List[Point]()
  //Add some coords
  //xGrid = xGrid :+ new Point(1, 2)
  //oGrid = oGrid :+ new Point(10, 2)
  // Who's go is it?
  var go = X
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
    if(x*TILE_HALF_LENGTH > SCREEN_WIDTH) drawGrid(0, y+1)
    else if(y*TILE_HALF_LENGTH > SCREEN_HEIGHT) return
    else {
      drawQuad(TILE_HALF_LENGTH + x*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH + y*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH, TILE_HALF_LENGTH)
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
    drawX(TILE_HALF_LENGTH + x*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH + y*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH, TILE_HALF_LENGTH)
  }

  def drawOSimple(x:Int, y:Int) {
    drawO(TILE_HALF_LENGTH + x*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH + y*(TILE_HALF_LENGTH*2), TILE_HALF_LENGTH, TILE_HALF_LENGTH)
  }

  def makeGo(goValue: MoveType, x:Int, y:Int) {
    if(goValue == X) {
      xGrid = xGrid :+ new Point(x, y)
      go = O
    } else {
      oGrid = oGrid :+ new Point(x, y)
      go = X
    }
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
      System.out.println("translated x is :" + Mouse.getX() / TILE_LENGTH + " and y is : " + Mouse.getY()/TILE_LENGTH)
      val x = Mouse.getX() / TILE_LENGTH
      val y = Mouse.getY() / TILE_LENGTH
      //TODO: Make player's move here if it is their turn or send to server.
      val pointInList: PartialFunction[Point, Point] = {
        case point if point.x == x && point.y == y => point
      }
      val xMatches = xGrid collect pointInList
      val oMatches = oGrid collect pointInList
      if((xMatches.length > 0) || (oMatches.length > 0)) {
        println("Invalid move")
      } else {
        makeGo(go, x, y)
      }
    }

    Display.update()

    Thread.sleep(60)
    if(!Display.isCloseRequested) mainLoop()
  }
}
