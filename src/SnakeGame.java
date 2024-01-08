import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Title {
        int x;
        int y;

        Title(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int currentLevel = 1;
    int targetScore = 10 ;
    int boardWidth;
    int boardHeight;
    int TitleSize = 25;

    //snake
    Title snakeHead;
    ArrayList<Title> snakeBody;

    //food
    Title food;
    Random random;

    //game logic
    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    boolean finishgame = false;
    String winmessage = "";

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Title(5, 5);
        snakeBody = new ArrayList<Title>();

        food = new Title(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(100, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();

        this.requestFocus();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Grid Lines
        for(int i = 0; i < boardWidth/TitleSize; i++) {
            //(x1, y1, x2, y2)
            g.drawLine(i*TitleSize, 0, i*TitleSize, boardHeight);
            g.drawLine(0, i*TitleSize, boardWidth, i*TitleSize);
        }

        //Food
        g.setColor(Color.red);
        g.fill3DRect(food.x*TitleSize, food.y*TitleSize, TitleSize, TitleSize, true);

        //Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*TitleSize, snakeHead.y*TitleSize, TitleSize, TitleSize, true);

        //Snake Body
        for (int i = 0; i < snakeBody.size(); i++) {
            Title snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*TitleSize, snakePart.y*TitleSize, TitleSize, TitleSize, true);
        }

        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), TitleSize - 16, TitleSize);
        } else if (finishgame) {
            g.setColor(Color.blue);
            g.drawString(winmessage, TitleSize - 16, TitleSize);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), TitleSize - 16, TitleSize);
        }
    }

    public void placeFood(){
        food.x = random.nextInt(boardWidth/TitleSize);
        food.y = random.nextInt(boardHeight/TitleSize);
    }

    public void move() {
        //eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Title(food.x, food.y));
            placeFood();
        }

        //move snake body
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Title snakePart = snakeBody.get(i);
            if (i == 0) { //right before the head
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Title prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        //move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over conditions
        for (int i = 0; i < snakeBody.size(); i++) {
            Title snakePart = snakeBody.get(i);

            //collide with snake head
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x*TitleSize < 0 || snakeHead.x*TitleSize > boardWidth || //passed left border or right border
                snakeHead.y*TitleSize < 0 || snakeHead.y*TitleSize > boardHeight ) { //passed top border or bottom border
            gameOver = true;
        }// winning condition
        if (snakeBody.size() >= targetScore) {
            finishgame = true;
            winmessage = "Congratulations! You completed Level " + currentLevel + "!";
            resetGame();
        }
    }


    private void initializeGameState() {
        snakeHead = new Title(5, 5);
        resetGame();
        snakeBody.clear();
        placeFood();
        velocityX = 1;
        velocityY = 0;
    }

    private void increaseSpeed() {
        int currentDelay = gameLoop.getDelay();
        if (currentDelay > 10) { // Prevent negative delay
            int newDelay = currentDelay - 5; // Decrease delay by 3 milliseconds (adjust as needed)
            gameLoop.setDelay(newDelay);
        }
    }

    private void resetGame() {
        targetScore = currentLevel * 10; // Adjust target score based on the current level
        snakeHead = new Title(5, 5);
        snakeBody.clear();
        placeFood();
        velocityX = 1;
        velocityY = 0;
    }

    public boolean collision(Title Title1, Title Title2) {
        return Title1.x == Title2.x && Title1.y == Title2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver || finishgame) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && (gameOver || finishgame)) {
        currentLevel = 1;
        targetScore = 10;
        initializeGameState();
        gameOver = false;
        finishgame = false;
        winmessage = "";
        gameLoop.start();
    } else if (e.getKeyCode() == KeyEvent.VK_SPACE && finishgame) {
        currentLevel++;
        initializeGameState();
        gameOver = false;
        finishgame = false;
        winmessage = "";
        increaseSpeed();
        gameLoop.start();
    }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}