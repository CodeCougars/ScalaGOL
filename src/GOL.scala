import java.awt.{Graphics, Color}
import java.awt.image.BufferedImage
import java.util.Random
import javax.swing.{JPanel, JFrame}

class GOL extends JFrame {
  val W = 120
  val H = 120

  val SIZE = 4

  var cells = Array.ofDim[Byte](W, H)
  var frame = new GolPanel()
  var bg = new BufferedImage(W * SIZE, H * SIZE, BufferedImage.TYPE_INT_ARGB)

  def init() {
    setTitle("Game of Life")
    setSize(W * SIZE, H * SIZE)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setLocationRelativeTo(null)
    setVisible(true)

    var window = getContentPane

    frame.setSize(W * SIZE, H * SIZE)
    frame.setBackground(new Color(234, 234, 234))

    window.add(frame)

    // randomize cells
    for (x <- 0 until W) {
      println()
      for (y <- 0 until H) {
        cells(x)(y) = new Random().nextInt(2).asInstanceOf[Byte]
        //cells(x)(y) = if (new Random().nextDouble() > 0.70) 1 else 0
      }
    }

    new Thread(new Runnable {
      override def run(){
        while (true) {
          tick()

          Thread.sleep(100)
        }
      }
    }).start()
  }

  class GolPanel extends JPanel {
    override def paintComponent(graphics: Graphics) {
      super.paintComponent(graphics)
      graphics.drawImage(bg, 0, 0, this)
    }
  }

  def draw() {
    var g = bg.getGraphics

    for (x <- 0 until W) {
      for (y <- 0 until H) {
        if (cells(x)(y) == 0) {
          g.setColor(Color.WHITE)
        }
        else {
          g.setColor(new Color(56, 57, 89))
        }

        g.fillRect(x * SIZE, y * SIZE, SIZE, SIZE)
      }
    }

    frame.repaint()
  }

  def tick() {
    var newCells = Array.ofDim[Byte](W, H)

    var neighbours = Array.ofDim[Int](8, 2)
    neighbours(0) = Array(1, 1)
    neighbours(1) = Array(1, 0)
    neighbours(2) = Array(1, -1)
    neighbours(3) = Array(0, -1)
    neighbours(4) = Array(-1, -1)
    neighbours(5) = Array(-1, 0)
    neighbours(6) = Array(-1, 1)
    neighbours(7) = Array(0, 1)

    /*

    |6 |7 |0 |
    |5 |x |1 |
    |4 |3 |2 |

     */


    var dead = 0
    var alive = 0

    for (x <- 0 until W) {
      for (y <- 0 until H) {
        var aliveNeighbours = 0

        for (i <- 0 until neighbours.length) {
          var neighbour = neighbours(i)
          var xPos = x + neighbour(0)
          var yPos = y + neighbour(1)

          if (xPos >= 0 && xPos < W && yPos >= 0 && yPos < H) {
            if (cells(xPos)(yPos) == 1) {
              aliveNeighbours += 1
            }
          }
        }

        if (cells(x)(y) == 0) {
          // cell is dead
          dead += 1

          if (aliveNeighbours == 3) {
            newCells(x)(y) = 1
          }
        }
        else {
          // cell is alive
          alive += 1

          // underpopulation
          if (aliveNeighbours < 2) {
            newCells(x)(y) = 0
          }
          else if (aliveNeighbours > 3) {
            // overpopulation
            newCells(x)(y) = 0
          }
          else {
            // 2 or 3 - survives
            newCells(x)(y) = 1
          }
        }
      }
    }

    println("alive / dead: " + alive / dead.toFloat)

    cells = newCells

    draw()
  }
}