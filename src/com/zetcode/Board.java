package com.zetcode;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Rectangle; 
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
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
 * TODO add a running timer
 * TODO add a real 'game over' screen
 * TODO main menu page
 */
public class Board extends JPanel implements ActionListener {

	//sizing of board
    private final int B_WIDTH = 900;
    private final int B_HEIGHT = 900;
    
    //size of pictures
    private final int DOT_SIZE = 50;
    private final int ALL_DOTS = 900;
    
    //random position element
    private final int RAND_POS = 18;
    
    //initial delay in timer
    private int delay = 300;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    //setting initial direction to go right
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    //constructor
    public Board() {
        
        initBoard();
    }
    
    //initializing board
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.white);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    //loading images from resources
    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/sydney_head.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/blake_apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/sydney_head.png");
        head = iih.getImage();
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
    }

    //painting images to screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    //helper to paint images on screen
    private void doDrawing(Graphics g) {
        
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

        } else {

            gameOver(g);
        }        
    }

    //game over graphic
    private void gameOver(Graphics g) {
        
        String msg = "Game Over! You suck!";
        Font large = new Font("Helvetica", Font.BOLD, 24);
        FontMetrics metr = getFontMetrics(large);

        g.setColor(Color.blue);
        g.setFont(large);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    //checking if an apple is gained
    private void checkApple() {

        if (x[0] == apple_x && y[0] == apple_y) {

            dots++;
            locateApple();
            
            //making the game harder (faster) as apples are gained
            if(timer.getDelay() > 1) {
            	delay -= 30; 
            	timer.setDelay(delay);
            }
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

    //checking if snake is out of bounds
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

        if (inGame) {

            checkApple();
            checkCollision();
            move();
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
