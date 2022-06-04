import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*; 

public class Shoot extends JFrame implements Runnable, KeyListener {
       
        private BufferedImage bi = null;
        private ArrayList msList = null;
        private ArrayList enList = null;
        private static BufferedImage background = null, plane = null, png = null, bullet = null;
        private boolean left = false, right = false, up = false, down = false, fire = false, shift = false;
        private boolean start = false, end = false;
        private static int w = 600, h = 600, x = 250, y = 500, xw = 20, xh = 20, life = 3, sum = 0;



        public Shoot() {
         bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
         msList = new ArrayList();
         enList = new ArrayList();
         this.addKeyListener(this);
         this.setSize(w, h);
         this.setTitle("Shooting Game");
         this.setResizable(false);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setVisible(true); 
         try {
          plane = ImageIO.read(new File("image\\my_plane.png"));
          bullet = ImageIO.read(new File("image\\bullet.png"));
          png = ImageIO.read(new File("image\\en_plane.png"));      
          background = ImageIO.read(new File("image\\back.png"));
       } catch (IOException e) { }
    }   
        
        
        public void run() {
         try {
          int msCnt = 0;
          int enCnt = 0;
          while(true) {
           Thread.sleep(10);
           
           if(start) {
            if(enCnt > 2000) {
             enCreate();
             enCnt = 0;
            }
            if(msCnt >= 100) {
             fireMs();
             msCnt = 0;
            }
            msCnt += 10;
            if(sum >= 0 && sum < 500) 
            enCnt += 10;
            else if(sum >= 500 && sum < 1000)
               enCnt += 20;
               else if(sum >= 1000 && sum < 2000)
                  enCnt += 30;      // 난이도 구현 완료
         else
            enCnt += 100;
            
            keyControl();
            crashChk();
           }
           draw();
          }
         } catch(Exception e) {
          e.printStackTrace();
         }
        }
        public void fireMs() {
         if(fire) {
          if(msList.size() < 100) {
           Ms m = new Ms(this.x, this.y);
           msList.add(m);
          }
         }
        }
        public void enCreate() {
         for(int i = 0; i < 9; i++) {
          double rx = Math.random() * (w - xw);
          double ry = Math.random() * 50;
          Enemy2 en = new Enemy2((int)rx, (int)ry);
          enList.add(en);
         }
        }
        public void crashChk() {
         Graphics g = this.getGraphics();
         Polygon p = null;
         for(int i = 0; i < msList.size(); i++) {
          Ms m = (Ms)msList.get(i);
          for(int j = 0; j < enList.size(); j++) {
           Enemy2 e = (Enemy2)enList.get(j);
           int[] xpoints = {m.x, (m.x + m.w), (m.x + m.w), m.x};
           int[] ypoints = {m.y, m.y, (m.y + m.h), (m.y + m.h)};
           p = new Polygon(xpoints, ypoints, 4);
           if(p.intersects((double)e.x, (double)e.y, (double)e.w, (double)e.h)) {
            msList.remove(i);
            enList.remove(j);
            sum += 50;
           }
          }
         }
         for(int i = 0; i < enList.size(); i++) {
          Enemy2 e = (Enemy2)enList.get(i);
          int[] xpoints = {x, (x + xw), (x + xw), x};
          int[] ypoints = {y, y, (y + xh), (y + xh)};
          p = new Polygon(xpoints, ypoints, 4);
          if(p.intersects((double)e.x-30, (double)e.y, (double)e.w, (double)e.h)) {
           enList.remove(i);
         if(life != 0) {
            --life;
         }
         if(life == 0) {
            start = false; 
            end = true;
            sum = 0;
          }
         }
        }
       }  
        public void draw() {
         Graphics gs = bi.getGraphics();
         gs.setColor(Color.white);
         gs.fillRect(0, 0, w, h);
         gs.setColor(Color.black);
         gs.drawImage(background, 0, 0, this);
         gs.setColor(Color.white);
        gs.setFont(new Font("Pixeboy", Font.PLAIN, 40));
        gs.drawString(sum+"", 15, 110);
        gs.drawString("LIFE : " + life, 450, 70);
         
         if(end) 
          gs.drawString("G A M E     O V E R", 120, 250);
          gs.drawString("Single", 15, 70);
          gs.drawImage(plane, x, y, null);
         
         
          
         for(int i = 0; i < msList.size(); i++) { // 기본 총알의 피격 판정 범위 설정
           Ms m = (Ms)msList.get(i);
           gs.setColor(Color.blue);
           gs.drawImage(bullet, m.x+33, m.y-15, null);
           if(m.y < 0) msList.remove(i);
           m.moveMs();
         }
         gs.setColor(Color.black);
         for(int i = 0; i < enList.size(); i++) {
          Enemy2 e = (Enemy2)enList.get(i);
          gs.drawImage(png, e.x, e.y, null);
          if(e.y > h) enList.remove(i);
          e.moveEn();
         }
         
         Graphics ge = this.getGraphics();
         ge.drawImage(bi, 0, 0, w, h, this);
        }
        
        public void keyControl() { 
          if(0 < x) {
             if(shift&&left) {
                x -= 2;
             }
             else if(left) {
                x -= 6;
             }
          }
          if(w > x + 100) {
             if(right&&shift) {
                x += 2;
             }
             else if(right) {
                x += 6;
             }
          }
          if(25 < y) {
             if(up&&shift) { 
                y -= 2;
             }
             else if(up) { 
                y -= 6;
             }
          }
          if(h > y + 100) {
             if(down&&shift) {
                y += 2;
             }
             else if(down) { 
                y += 6;
             }
          }
       }
        
        public void keyPressed(KeyEvent ke) {
         switch(ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
          left = true;
          break;
         case KeyEvent.VK_RIGHT:
          right = true;
          break;
         case KeyEvent.VK_UP:
          up = true;
          break;
         case KeyEvent.VK_DOWN:
          down = true;
          break;
         case KeyEvent.VK_A:
          fire = true;
          break;
         case KeyEvent.VK_ENTER:
          start = true;
          end = false;
          break;
         case KeyEvent.VK_SHIFT :
         shift = true;
         break;
         }
        }
        
        public void keyReleased(KeyEvent ke) {
         switch(ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
          left = false;
          break;
         case KeyEvent.VK_RIGHT:
          right = false;
          break;
         case KeyEvent.VK_UP:
          up = false;
          break;
         case KeyEvent.VK_DOWN:
          down = false;
          break;
         case KeyEvent.VK_A:
          fire = false;
          break;
         case KeyEvent.VK_SHIFT :
         shift = false;
         break;
         }
        }
        
        public void keyTyped(KeyEvent ke) {} // 키 입력 시 좌표 반환
       
       public static int getCondX() {
          return x;
       }
       public static int getCondY() {
          return y;
       }
       
       public static void setX(int x) {
          Shoot.x = x;
       }
       public static void setY(int y) {
          Shoot.y = y;
       }
        public static void main(String[] args) {
         Thread t = new Thread(new Shoot());
         t.start();
        }
       }
       class Ms {
        int x = 20;
        int y = 20;
        int w = 20;
        int h = 20;
        public Ms(int x, int y) {
         this.x = x;
         this.y = y;
        }
        public void moveMs() { y-=5; }     
        
       }
       class Enemy2 {
        int x = 40;
        int y = 30;
        int w = 30;
        int h = 30;
        public Enemy2(int x, int y) {
         this.x = x;
         this.y = y;
        }
        public void moveEn() {
         y++;
        } 
       }