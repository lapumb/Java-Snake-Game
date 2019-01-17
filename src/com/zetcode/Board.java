package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/*
 * @author janbodnar (GitHub user)
 * @editor lapumb (forked)
 * @date January 2019
 * 
 * This is a modified version of the 1970's 'snake' game. I am using Sydney's head to be the snake parts,
 * and my own face to be the 'apples' she is seeking to eat. 
 * 
 * TODO add high scores
 * TODO deal with running timer issue
 */
public class Board extends JPanel implements ActionListener {

	//sizing of board
    private final int B_WIDTH = 900;
    private final int B_HEIGHT = 900;
    
    //size of pictures
    private final int DOT_SIZE = 50;
    private final int ALL_DOTS = 900;
    
    //size for buttons
    private final int BTN_WIDTH = 150;
    private final int BTN_HEIGHT = 50; 
    
    //random position element
    private final int RAND_POS = 18;
    
    //one second in milliseconds
    private final int SECOND = 1000; 
    
  //string for playAgain button text
    private final String REPLAY = "Play Again"; 

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    //initial number of dots, apple coordinates
    private int dots;
    private int apple_x;
    private int apple_y;
    
    //initial delay in timer
    private int delay;
    private int currentTime; 

    //setting initial direction to go right
    private boolean leftDirection;
    private boolean rightDirection;
    private boolean upDirection;
    private boolean downDirection;
    
    //testing what is happening
    private boolean inGame;
    
    //number of apples gained
    private int numApplesAcquired; 

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    
    //button for game-over page
    private JButton playAgain; 
    
    //current highest score
    private int currentHighApples; 
    private int currentHighTime; 
    
    //saved preferences for scores
	Preferences blakes; 
	Preferences time;

    //constructor
    public Board() {
        initBoard();
    }
    
    //initializing all global variables
    private void initVar() {
    	delay = 300;
        currentTime = 0; 
        
        //starting direction variables
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        inGame = true;
        
        //initial number of dots, apple coordinate
        dots = 0;
        apple_x = 0;
        apple_y = 0;
        
        //starting score of apples acquired
        numApplesAcquired = 0; 
    }
    
    //initializing board
    private void initBoard() {
    	    	
        addKeyListener(new TAdapter());
        setBackground(Color.white);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initVar(); 
        initGame();
    }

    //loading images from resources
    private void loadImages() {

    	//receiving images
    	URL urlh = Board.class.getResource("/sydney_head.png");
        ImageIcon iid = new ImageIcon(urlh);
        ball = iid.getImage();
        
        ImageIcon iih = new ImageIcon(urlh);
        head = iih.getImage();

        URL urla = Board.class.getResource("/blake_apple.png");
        ImageIcon iia = new ImageIcon(urla);
        apple = iia.getImage();
    }

    //initializing game
    private void initGame() {

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple();

        timer = new Timer(delay, this);
        timer.start();
        
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                currentTime += 1; 
            }
        };
        new Timer(SECOND, taskPerformer).start();
        
    }

    //painting images to screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

       
        doDrawing(g);
    }
    
    //helper to paint images on screen
    private void doDrawing(Graphics g) {
    	
        //drawing time elapsed
    	runningTimer(g);
    	appleNumber(g); 
    	
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } 
        
        else {
            gameOver(g);
        }        
    }

    //game over graphic
    private void gameOver(Graphics g) {
    	
    	//testing if new highscore
    	//highScore(numApplesAcquired, currentTime); 
    	//currentHighApples = blakes.getInt("highBlakes", 0); 
    	//currentHighTime = time.getInt("highTime", 0); 
    	
    	//button to retry
    	playAgain = new JButton(REPLAY);
    	
    	//strings to print
        String msg = "Game Over! You suck!";
        String score = "Your Score: " + numApplesAcquired + " Blakes in "+ currentTime + " seconds";
        String highestScore = "Your Highest Score: " + currentHighApples + " Blakes in " + currentHighTime + 
        		" seconds";
    	
    	//font stuff
    	Font btn = new Font("Helvetica", Font.PLAIN, 20);
        Font large = new Font("Helvetica", Font.BOLD, 24);
        Font highscore = new Font("Helvetica", Font.BOLD, 26);
        FontMetrics metr = getFontMetrics(large);
        FontMetrics measure = getFontMetrics(btn); 
        FontMetrics measureHigh = getFontMetrics(highscore);

        //printing game over
        g.setColor(Color.BLUE);
        g.setFont(large);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        
      //instantiating play again button
    	playAgain.addActionListener(this);
    	playAgain.setBounds((B_WIDTH - BTN_WIDTH) / 2, 
    			B_HEIGHT/2 + BTN_HEIGHT, BTN_WIDTH, BTN_HEIGHT);
    	playAgain.setFont(btn);
    	this.add(playAgain); 

    	//printing score obtained
    	g.setColor(Color.BLACK);
    	g.setFont(btn);
    	g.drawString(score, (B_WIDTH - measure.stringWidth(score)) / 2, 
    			B_HEIGHT/2 + 3*(BTN_HEIGHT));
    	
    	//printing high score
    	g.setColor(Color.MAGENTA);
    	g.setFont(highscore);
    	g.drawString(highestScore, (B_WIDTH - measureHigh.stringWidth(highestScore)) / 2, 
    			B_HEIGHT/2 + 5*(BTN_HEIGHT));
    }
    
    private void highScore(int numBlakes, int numTime) {
    	blakes = Preferences.userNodeForPackage(this.getClass());
    	blakes.putInt("highBlakes", 0);
    	
    	time = Preferences.userNodeForPackage(this.getClass());  
    	time.putInt("highTime", 0);
    	
    	if(numBlakes > blakes.getInt("highBlakes", 0)) { 
    		blakes.putInt("highBlakes", numBlakes);
    		time.putInt("highTime", numTime);
    	}
    	
    	else if(numBlakes == blakes.getInt("highBlakes", 0)) {
    		if(numTime > time.getInt("highTime", 0)) {
    			time.putInt("highTime", numTime);
    		}
    	}
    }
    
    //timer for how long you have been playing
    private void runningTimer(Graphics g) {
    	String time = "Time Elapsed: " + currentTime + " seconds";
    	Font small = new Font("Helvetica", Font.ITALIC, 20);
    	FontMetrics metr = getFontMetrics(small);
    	
    	g.setColor(Color.BLACK);
    	g.setFont(small);
    	g.drawString(time, 10, metr.getHeight());
    }
    
    //drawing the string for how many heads have been gained
    private void appleNumber(Graphics g) {
    	String num = "Blakes Aquired: " + numApplesAcquired; 
    	Font number = new Font("Helvetica", Font.ITALIC, 20);
    	FontMetrics metr = getFontMetrics(number); 
    	
    	g.setColor(Color.BLACK);
    	g.setFont(number);
    	g.drawString(num, B_WIDTH - metr.stringWidth(num) - 10, metr.getHeight());
    }

    //checking if an apple is gained
    private void checkApple() {

        if (x[0] == apple_x && y[0] == apple_y) {

            dots++;
            locateApple();
            
            //making the game harder (faster) as apples are gained
            if(timer.getDelay() > 60) {
            	delay -= 30; 
            	timer.setDelay(delay);
            }
            //incrementing number of apples acquired
            numApplesAcquired++; 
        }
    }

    //logic to move
    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    //checking if snake hits bounds
    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    //replacing apple, keeping them in play
    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    //checking on action performed
    @Override
    public void actionPerformed(ActionEvent e) {

    	Object cause = e.getSource(); 
    	
        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        
        //restarting game if the play again button is pressed
        if(cause == playAgain) {
        	//Snake.main(null);
        	removeAll(); 
        	this.revalidate(); 
        	initBoard(); 
        }
        
        repaint();
    }
    
    /*
     * class to handle the key movements
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
