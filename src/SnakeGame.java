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


    Title snakeHead;
    ArrayList<Title> snakeBody;


    Title food;
    Random random;


    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    boolean finishgame = false;
    String winmessage = "";
    String gameovermessage = "";

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        JOptionPane.showMessageDialog(this, "Welcome to Snake Game!\nPress ENTER to start the game.");

        snakeHead = new Title(5, 5);
        snakeBody = new ArrayList<Title>();

        food = new Title(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();

        this.requestFocus();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for(int i = 0; i < boardWidth/TitleSize; i++) {

            g.drawLine(i*TitleSize, 0, i*TitleSize, boardHeight);
            g.drawLine(0, i*TitleSize, boardWidth, i*TitleSize);
        }


        g.setColor(Color.red);
        g.fill3DRect(food.x*TitleSize, food.y*TitleSize, TitleSize, TitleSize, true);


        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*TitleSize, snakeHead.y*TitleSize, TitleSize, TitleSize, true);


        for (int i = 0; i < snakeBody.size(); i++) {
            Title snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*TitleSize, snakePart.y*TitleSize, TitleSize, TitleSize, true);
        }


        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()) + " Prees ENTER to restart the Game", TitleSize - 16, TitleSize);
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

        if (collision(snakeHead, food)) {
            snakeBody.add(new Title(food.x, food.y));
            placeFood();
        }


        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Title snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Title prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;


        for (int i = 0; i < snakeBody.size(); i++) {
            Title snakePart = snakeBody.get(i);

            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x*TitleSize < 0 || snakeHead.x*TitleSize > boardWidth || 
                snakeHead.y*TitleSize < 0 || snakeHead.y*TitleSize > boardHeight ) {
            gameOver = true;
        }// kodisi menang dalam permainan
        if (snakeBody.size() >= targetScore) {
            if (currentLevel < 3) {
                finishgame = true;
                winmessage = "Congratulations! You completed Level " + currentLevel + "! \nPrees SPACE to continue next level";
                resetGame();
            } else {
                finishgame = true;
                winmessage = "Congratulations! You have won the game!\nPress ENTER to restart the game";
                resetGame();
            }
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
        if (currentDelay > 10) {
            int newDelay = currentDelay - 5;
            gameLoop.setDelay(newDelay);
        }
    }

    private void resetGame() {
        targetScore = currentLevel * 10;
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
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver || finishgame) {
            gameLoop.stop();
        }
    }

    //control ular
    @Override
    public void keyPressed(KeyEvent e) {
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

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}