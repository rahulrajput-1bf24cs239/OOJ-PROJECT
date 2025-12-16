import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;

public class PacManChase extends JPanel implements ActionListener, KeyListener {

    // 1 = wall, 0 = food, 2 = empty
    private final int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,1,1,0,1,0,1},
            {1,0,0,0,1,0,0,0,1,0,0,0,1,0,1},
            {1,1,1,0,1,1,1,0,1,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
            {1,0,1,1,1,1,1,1,1,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private static final int TILE = 40;

    private int pacX = 1, pacY = 1;
    private int dirX = 0, dirY = 0;

    private int ghostX = 13, ghostY = 7;

    // ✅ EXPLICIT Swing Timer (FIX)
    private javax.swing.Timer timer;

    public PacManChase() {
        setPreferredSize(new Dimension(600, 360));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        timer = new javax.swing.Timer(200, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * TILE, y * TILE, TILE, TILE);
                } else if (map[y][x] == 0) {
                    g.setColor(Color.WHITE);
                    g.fillOval(x * TILE + 15, y * TILE + 15, 10, 10);
                }
            }
        }

        g.setColor(Color.YELLOW);
        g.fillOval(pacX * TILE + 5, pacY * TILE + 5, 30, 30);

        g.setColor(Color.RED);
        g.fillOval(ghostX * TILE + 5, ghostY * TILE + 5, 30, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePacMan();
        moveGhostChasing();
        checkCollision();
        repaint();
    }

    private void movePacMan() {
        int newX = pacX + dirX;
        int newY = pacY + dirY;

        if (map[newY][newX] != 1) {
            pacX = newX;
            pacY = newY;

            if (map[pacY][pacX] == 0) {
                map[pacY][pacX] = 2;
            }
        }
    }

    /* =======================
       BFS CHASING AI
       ======================= */
    private void moveGhostChasing() {
        int rows = map.length;
        int cols = map[0].length;

        boolean[][] visited = new boolean[rows][cols];
        int[][] prevX = new int[rows][cols];
        int[][] prevY = new int[rows][cols];

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(ghostX, ghostY));
        visited[ghostY][ghostX] = true;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            if (p.x == pacX && p.y == pacY) {
                break;
            }

            for (int[] d : dirs) {
                int nx = p.x + d[0];
                int ny = p.y + d[1];

                if (!visited[ny][nx] && map[ny][nx] != 1) {
                    visited[ny][nx] = true;
                    prevX[ny][nx] = p.x;
                    prevY[ny][nx] = p.y;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        int tx = pacX;
        int ty = pacY;

        // Backtrack one step toward Pac-Man
        while (!(prevX[ty][tx] == ghostX && prevY[ty][tx] == ghostY)) {
            int px = prevX[ty][tx];
            int py = prevY[ty][tx];
            tx = px;
            ty = py;
        }

        ghostX = tx;
        ghostY = ty;
    }

    private void checkCollision() {
        if (pacX == ghostX && pacY == ghostY) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
            System.exit(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> { dirX = -1; dirY = 0; }
            case KeyEvent.VK_RIGHT -> { dirX = 1; dirY = 0; }
            case KeyEvent.VK_UP -> { dirX = 0; dirY = -1; }
            case KeyEvent.VK_DOWN -> { dirX = 0; dirY = 1; }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man Chase (Fixed)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PacManChase());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


