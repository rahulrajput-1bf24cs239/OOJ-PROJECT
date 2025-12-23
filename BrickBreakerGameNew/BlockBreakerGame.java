import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BlockBreakerGame extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;
    private int score = 0;
    private static int highScore = 0;

    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -1;
    private int ballYDir = -2;

    private BrickGenerator bricks;

    public BlockBreakerGame() {
        bricks = new BrickGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // draw bricks
        bricks.draw((Graphics2D) g);

        // borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // score
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Score: " + score, 520, 30);
        g.drawString("High Score: " + highScore, 20, 30);

        // paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // ball
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // win condition
        if (totalBricks <= 0) {
            play = false;
            ballXDir = 0;
            ballYDir = 0;

            if (score > highScore) {
                highScore = score;
            }

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won!", 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
            g.drawString("Press Space to Exit", 245, 380);
        }

        // lose condition
        if (ballPosY > 570) {
            play = false;
            ballXDir = 0;
            ballYDir = 0;

            if (score > highScore) {
                highScore = score;
            }

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over!", 250, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
            g.drawString("Press Space to Exit", 245, 380);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (play) {
            if (new Rectangle(ballPosX, ballPosY, 20, 20)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir;
            }

            A:
            for (int i = 0; i < bricks.map.length; i++) {
                for (int j = 0; j < bricks.map[0].length; j++) {

                    if (bricks.map[i][j] > 0) {
                        int brickX = j * bricks.brickWidth + 80;
                        int brickY = i * bricks.brickHeight + 50;

                        Rectangle brickRect = new Rectangle(
                                brickX, brickY,
                                bricks.brickWidth, bricks.brickHeight
                        );

                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            bricks.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x ||
                                ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A;
                        }
                    }
                }
            }

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            if (ballPosX < 0 || ballPosX > 670) {
                ballXDir = -ballXDir;
            }

            if (ballPosY < 0) {
                ballYDir = -ballYDir;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX < 600) {
                play = true;
                playerX += 20;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX > 10) {
                play = true;
                playerX -= 20;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            play = true;
            ballPosX = 120;
            ballPosY = 350;
            ballXDir = -1;
            ballYDir = -2;
            playerX = 310;
            score = 0;
            totalBricks = 21;
            bricks = new BrickGenerator(3, 7);
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        BlockBreakerGame game = new BlockBreakerGame();

        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Block Breaker Game");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
    }
}

// ================= Brick Generator =================

class BrickGenerator {

    public int[][] map;
    public int brickWidth;
    public int brickHeight;

    public BrickGenerator(int row, int col) {
        map = new int[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = 1;
            }
        }

        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {

                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(
                            j * brickWidth + 80,
                            i * brickHeight + 50,
                            brickWidth,
                            brickHeight
                    );

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(
                            j * brickWidth + 80,
                            i * brickHeight + 50,
                            brickWidth,
                            brickHeight
                    );
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
