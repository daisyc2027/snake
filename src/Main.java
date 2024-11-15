import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

class MainGame extends JPanel implements ActionListener, KeyListener{
    GameBoard gameBoard;
    Food food;
    Snake snake;
    boolean gameOver = false;
    boolean gameStarted = false;
    JLabel score = new JLabel("0");

    public MainGame(){
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        score.setForeground(Color.WHITE);
        add(score);
        food = new Food();
        snake = new Snake(5, 5);
        gameBoard = new GameBoard(snake, food);
        Timer timer = new Timer(100, this);
        timer.start();
        food.generateFood(gameBoard.grid.length, snake);
        gameBoard.grid[food.currentFoodPosition.x][food.currentFoodPosition.y] = 2;
        setBackground(Color.BLACK);
    }

    public boolean snakeEatingFood(){
        if(food.currentFoodPosition.equals(snake.head)){
            gameBoard.grid[food.currentFoodPosition.x][food.currentFoodPosition.y] = 1;
            food.generateFood(gameBoard.grid.length, snake);
            score.setText(String.valueOf(Integer.parseInt(score.getText()) + 1));
            return true;
        }
        return false;
    }


    public boolean collisionWithWall(Point p){
        return p.x >= gameBoard.grid.length || p.x < 0 ||
                p.y >= gameBoard.grid.length || p.y < 0;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameOver || !gameStarted){
            return;
        }

        Point nextPosition = new Point(
                snake.head.x+snake.currentDirection.dx,
                snake.head.y+snake.currentDirection.dy
        );

        if(collisionWithWall(nextPosition)) {
            gameOver = true;
            return;
        }
        snake.move(snakeEatingFood());

        for(Point p: snake.getSnakeBody()) {
            gameBoard.grid[p.x][p.y] = 1;
        }
        gameBoard.grid[snake.tail.x][snake.tail.y] = 0;

        if(snake.detectSelfCollision()){
            gameOver = true;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameBoard.draw(g);

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            snake.updateDirection(Direction.RIGHT);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            snake.updateDirection(Direction.LEFT);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            snake.updateDirection(Direction.UP);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            snake.updateDirection(Direction.DOWN);
        }

        if (!gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameStarted = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

class Food {
    Point currentFoodPosition;

    public void generateFood(int gridSize, Snake snake) {
        Random random = new Random();
        do {
            int foodX = random.nextInt(gridSize);
            int foodY = random.nextInt(gridSize);
            currentFoodPosition = new Point(foodX, foodY);
        } while (isOnSnake(snake));
    }

    private boolean isOnSnake(Snake snake) {
        for (Point p : snake.getSnakeBody()) {
            if (currentFoodPosition.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g, int cellSize, Point p){
        int pixelX = p.x * cellSize;
        int pixelY = p.y * cellSize;
        g.setColor(Color.red);
        g.fillRect(pixelX,pixelY,cellSize,cellSize);
    }
}

class GameBoard  {
    int[][] grid = new int[20][20];
    int cellSize = 20;
    Snake snake;
    Food food;

    public GameBoard(Snake snake, Food food){
        this.snake = snake;
        this.food = food;
    }

    protected void draw(Graphics g){
        snake.draw(g, cellSize);
        food.draw(g, cellSize, food.currentFoodPosition);

    }
}

class Snake{
    private final LinkedList<Point> snakeBody;
    Point head;
    Point tail;
    Direction currentDirection;
    int initialSize = 3;

    public Snake(int startX, int startY){
        snakeBody = new LinkedList<>();
    for(int i = 0; i<initialSize; i++){
        snakeBody.add(new Point(startX-i, startY));
    }
    currentDirection = Direction.RIGHT;
    head = snakeBody.getFirst();
    tail = snakeBody.getLast();
    }

    public LinkedList<Point> getSnakeBody(){
        return snakeBody;
    }

    public void draw(Graphics g, int cellSize){
        for(Point p: snakeBody) {
            int pixelX = p.x * cellSize;
            int pixelY = p.y * cellSize;
            g.setColor(Color.GREEN);
            g.fillRect(pixelX, pixelY, cellSize, cellSize);
        }
    }

    public void updateDirection(Direction nextDirection){
        if(nextDirection.dy != currentDirection.dy && nextDirection.dx != currentDirection.dx) {
            currentDirection = nextDirection;
        }
    }

    public void move(boolean eatingFood){
            Point newHead = new Point(head.x + currentDirection.dx, head.y + currentDirection.dy);
            snakeBody.addFirst(newHead);
            head = newHead;
        if(!eatingFood) {
            snakeBody.removeLast();
            tail = snakeBody.getLast();
        }
    }

    public boolean detectSelfCollision(){
        for(int i = 0; i<snakeBody.size(); i++){
            for(int j = i+1; j<snakeBody.size(); j++){
                if(snakeBody.get(i).equals(snakeBody.get(j))){
                    return true;
                }
            }
        }
        return false;
    }

}

enum Direction{
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0,-1),
    DOWN(0,1);

    public int dx;
    public int dy;

    Direction(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        MainGame game = new MainGame();
        frame.add(game);
        frame.setSize(400, 400);  // or whatever size you want
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}