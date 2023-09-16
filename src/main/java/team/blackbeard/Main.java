package team.blackbeard;

import java.awt.*;
import javax.swing.*;

public class Main extends JPanel {

    public static JFrame frame = null;
    private static final long serialVersionUID = 1L;
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    public GameState.State[][] board = new GameState.State[7][13];

    private Font font = new Font("Arial", Font.BOLD, 18);
    FontMetrics metrics;

    public static Color green = new Color(0x1f8f03);
    public static Color purple = new Color(0xae23d9);
    public static Color yellow = new Color(0xe8ac07);
    public static Color white = Color.WHITE;


    private static Main instance = null;
    private static GameState gameState = null;

    public static Main getInstance(){
        return instance;
    }

    public Main() {
        instance = this;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 13; y++) {
                if (y % 2 == 0 && x == 6) {
                    board[6][y] = GameState.State.Invalid;
                }else {
                    board[x][y] = GameState.State.Empty;
                }
            }
        }
        gameState = new GameState();
        gameState.init();
        gameState.start();
    }

    public void update(){
        repaint();
    }




    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Point origin = new Point(WIDTH / 2, HEIGHT / 2);

        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2d.setFont(font);
        metrics = g.getFontMetrics();


        drawHexGridLoop(g2d, origin, 13, 20, 8);
    }

    private void drawHexGridLoop(Graphics g, Point origin, int size, int radius, int padding) {
        double ang30 = Math.toRadians(30);
        double xOff = Math.cos(ang30) * radius;
        double yOff = Math.sin(ang30) * radius;
        int half = size / 2;

        for (int row = 0; row < size; row++) {
            int cols = row % 2 == 0 ? 6 : 7;

            for (int col = 0; col < cols; col++) {
                int xLbl = row < half ? col - row : col - half;
                int yLbl = row - half;
                int x = (int) (origin.x + xOff * (col * 2) + (row % 2 == 0 ? radius : 0) -50);
                int y = (int) (origin.y + yOff  *(row - half) * 3+100);

                drawHex(g, xLbl, yLbl, x, y, radius, board[col][12-row] == GameState.State.Empty ? Color.BLACK : new Color(board[col][12-row].state));
            }
        }
    }

    private void drawHex(Graphics g, int posX, int posY, int x, int y, int r, Color color) {
        Graphics2D g2d = (Graphics2D) g;

        Hexagon hex = new Hexagon(x, y, r);
        String text = String.format("%s : %s", coord(posX), coord(posY));
        int w = metrics.stringWidth(text);
        int h = metrics.getHeight();

        hex.draw(g2d, x, y, 0, color, true);
        hex.draw(g2d, x, y, 4, Color.ORANGE, false);

        g.setColor(new Color(0xFFFFFF));
    }

    private String coord(int value) {
        return (value > 0 ? "+" : "") + Integer.toString(value);
    }

    public void drawCircle(Graphics2D g, Point origin, int radius,
                           boolean centered, boolean filled, int colorValue, int lineThickness) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        int diameter = radius * 2;
        int x2 = centered ? origin.x - radius : origin.x;
        int y2 = centered ? origin.y - radius : origin.y;

        if (filled)
            g.fillOval(x2, y2, diameter, diameter);
        else
            g.drawOval(x2, y2, diameter, diameter);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        Main p = new Main();

        frame = f;
        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}