// 기능들을 불러옴.
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*; 


public class Shoot extends JFrame implements Runnable, KeyListener {
	private static BufferedImage bi = null;
   	private static ArrayList ultimateList = null;  //필살기 변수
	private static ArrayList msList = null;
	private static ArrayList enList = null;
	private static BufferedImage plane = null, bullet = null, png = null, background = null, ultimate = null;;
	private static boolean left = false, right = false, up = false, down = false, fire = false, shift = false, start = true, end = false, ultimate_fire =false;;
	private static int w = 600, h = 600, x = 250, y = 500, xw = 20, xh = 20, life = 3;
	public static int sum = 0;	//점수 처리 변수
	
	// 실질적으로 쓰이는 변수 선언.
	
	
	public Shoot() {
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // 배경화면 밑 바탕
		msList = new ArrayList(); // 총알 기본 선언
		enList = new ArrayList(); // 적기 기본 선언
		ultimateList = new ArrayList();	//필살기 객체 담는 리스트
		this.addKeyListener(this); // 키 기능
		this.setSize(600, 600);  // 기본 화면 크기
		this.setTitle("Shooting Game"); // 화면 이름
		this.setResizable(false); // 창 크기 조절 여부
		this.setLocationRelativeTo(null); // 화면에 가운데로 띄운다
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 창 종료시 프로세스를 완전히 닫는다
		this.setVisible(true); // 창을 나타내는 여부
		try {
			plane = ImageIO.read(new File("image\\my_plane.png"));
			bullet = ImageIO.read(new File("image\\bullet.png"));
			png = ImageIO.read(new File("image\\en_plane.png"));		// 이미지를 출력하는 구문, 오류 발생시 밑에서 처리함
			background = ImageIO.read(new File("image\\background.png"));
			ultimate = ImageIO.read(new File("image\\ultimate.png"));
		} catch (IOException e) { }
	}	
	
	public void run() { // 실행하는 부분
		try {

			int msCnt = 0;
			int enCnt = 0;
			while(start) {
				Thread.sleep(10);
				if(true) {
					if(enCnt > 2000) {
						enCreate();
						enCnt = 0;
					}
					if(msCnt >= 100) {
						fireMs();
						ultimate_fireMs();	//필살기 쏘기
						msCnt = 0;
					}
					msCnt += 10;
					
					if(sum >= 0 && sum < 1000) 
						enCnt += 10;
						else if(sum >= 1000 && sum <3000)
							enCnt += 20;
							else if(sum >= 3000 && sum < 5000)
								enCnt += 30;		// 난이도 구현 완료
					else
						enCnt += 100;
							
					keyControl();
					crashChk();
					ultimatecrashChk();
				}
				draw();	
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void fireMs() { // 기존 총알 생성시키는 메소드
		if(fire) {
			if(msList.size() < 100) {
				Ms m = new Ms(this.x, this.y);
				msList.add(m);
			}
		}
	}

	   
 	  public void ultimate_fireMs() { //필살기 사용했을때 한발씩 나가는것
	    	  if(ultimate_fire) {
	       		  if(ultimateList.size() < 1) {
	           			 Us n = new Us(this.x, this.y);
	          			  ultimateList.add(n);
	        		 }
	 	  }
 	  }
	
	public static void enCreate() { // 적기 생성시키는 메소드
			for(int i = 0; i < 9; i++) {
			double rx = Math.random() * (w - xw) - 20;
			double ry = Math.random() * 50;
			Enemy en = new Enemy((int)rx, (int)ry);
			enList.add(en);
		}
	}
	
	public void crashChk() { // 적기와 총알이 닿았을때 적기와 총알이 사라지고 점수 증가
		Graphics g = this.getGraphics();
		Polygon p = null;
		for(int i = 0; i < msList.size(); i++) {
			Ms m = (Ms)msList.get(i);
			for(int j = 0; j < enList.size(); j++) {
				Enemy e = (Enemy)enList.get(j);
				int[] xpoints = {m.x, (m.x + m.w), (m.x + m.w), m.x};
				int[] ypoints = {m.y, m.y, (m.y + m.h), (m.y + m.h)};
				p = new Polygon(xpoints, ypoints, 4);
				try { 
					if(p.intersects((double)e.x-5, (double)e.y, (double)e.w, (double)e.h+30)) {
						msList.remove(i);
						enList.remove(j);
						sum += 50;  // 삭제 되었을 때 점수 증가
					}
				}catch (IndexOutOfBoundsException e1) { e1.printStackTrace(); }
			}
		}
	}
  		//필살기 맞았을때 적이 없어지고 점수 오르게 하는 것
	public void ultimatecrashChk() {
          		Graphics g = this.getGraphics();
          		Polygon p = null;
          		for(int i = 0; i < ultimateList.size(); i++) {
             		Us u = (Us)ultimateList.get(i);
            			for(int j = 0; j < enList.size(); j++) {
                			Enemy e = (Enemy)enList.get(j);
                			int[] xpoints = {u.x, (u.x + u.w+1000), (u.x + u.w-1000), u.x}; // 필살기 답게 범위가 전 범위로 설정
               			int[] ypoints = {u.y, u.y, (u.y + u.h), (u.y + u.h)};
                			p = new Polygon(xpoints, ypoints, 4);
                			try { 
                   				if(p.intersects((double)e.x-5, (double)e.y, (double)e.w, (double)e.h+30)) {
                      					enList.remove(j); // 필살기의 경우 적기는 사라지지만 총알은 계속해서 나감
                      					sum += 50; 
                   				}
                			}catch (IndexOutOfBoundsException e1) { e1.printStackTrace(); }
             		}
          		}
 
		
		for(int i = 0; i < enList.size(); i++) { // 적기와 아군기가 닿았을 때 life 감소와 life가 0일시 게임 종료 + 점수 초기화
			Enemy e = (Enemy)enList.get(i);
			int[] xpoints = {x, (x + xw), (x + xw), x};
			int[] ypoints = {y, y, (y + xh), (y + xh)};
			p = new Polygon(xpoints, ypoints, 4);
			if(p.intersects((double)e.x-10, (double)e.y, (double)e.w, (double)e.h)) {
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
	
	public void draw() { // 창 안에 폰트 설정, 기본 색 설정, 점수와 목숨 표기 
		Polygon pa = null;
		Graphics gs = bi.getGraphics();
		gs.setColor(new Color(6,88,144));
		gs.drawImage(background, 0, 0, this);
		gs.setColor(Color.white);
		gs.setFont(new Font("Pixeboy", Font.PLAIN, 40));
		gs.drawString(sum+"", 15, 110);
		gs.drawString("LIFE : " + life, 450, 70);
		
		if(end) // end 시에 GAME OVER 출력위치 설정 
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


		
      			for(int i = 0; i < ultimateList.size(); i++) { // 필살기 총알의 피격 판정 범위 설정
          			Us u = (Us)ultimateList.get(i);
          			gs.setColor(Color.blue);
          			gs.drawImage(ultimate, u.x-170, u.y-205, null);
          			if(u.y < 0) ultimateList.remove(i);
          			u.moveUs();
       		}
		
		gs.setColor(Color.black); // 적기 피격 판정 범위 설정
		for(int i = 0; i < enList.size(); i++) {
			Enemy e = (Enemy)enList.get(i);
			gs.drawImage(png, e.x, e.y, null);
			if(e.y > h) enList.remove(i);
			e.moveEn();
		}
		
		Graphics ge = this.getGraphics();
		ge.drawImage(bi, 0, 0, w, h, this); // 배경 위치 설정
	}
	
	public void keyControl() { // 정밀한 동작을 위한 슬로우 모드 구현을 위한 쉬프트 사용시 속도 감소
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
	
	public void keyPressed(KeyEvent ke) { // 기본적인 키 컨트롤 방향키 4개와 공격 키 설정 메소드
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
		case KeyEvent.VK_SPACE:
			fire = true;
			break;
		case KeyEvent.VK_A:	//a키 누르면 필살기
    	  		ultimate_fire = true;
    	  		break;
		case KeyEvent.VK_ENTER:
			if(end) {
				System.exit(0);
				break;
			}
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
    			ultimate_fire = false;
    	  		break;
		case KeyEvent.VK_SPACE:
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

class Ms { // 기본 총알 움직이게 하는 메소드
	int x, y, w =20, h=20, speed = 5;
	
	public Ms(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void moveMs() { y-=5; } // 총알 속도 증가
}

class Us {		// 필살기 총알 움직이게 하는 메소드
	int x, y, w = 20, h=20, speed = 5;
	   
	   public Us(int x, int y) {
		   this.x = x;
		   this.y = y;
	   }
	   public void moveUs() { y-=5; } // 총알 속도 증가
}
	
class Enemy { // 적기 움직이게 하는 메소드
	int x, y, w = 30, h = 30;
	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void moveEn() { y+=2;} // 적기 속도 증가 
}