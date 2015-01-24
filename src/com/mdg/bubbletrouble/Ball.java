package com.mdg.bubbletrouble;

import android.util.Log;


public class Ball {

	float H, W;
	public float ballX = -1;
	public float ballY = -1;
	float velocityX = (float) 2.6;
	float velocityY = 4;
	float gravity = (float) 0.2;
	float arrowX, arrowY, manX, manY;
	int ballHit = 0;
	boolean manballCollison= false;
	int BASE_RADIUS;
	float arrowWidth,arrowHeight;
    
	
	public Ball(int a, int b,double c,float d) {
		// TODO Auto-generated constructor stub
		ballX = a;
		ballY = b;
		velocityY =(float) c;
		velocityX = d;
	}

	void moveBall(float radius) {
		 
			H = Levels.gameAreaHeight;
			W = Levels.gameAreaWidth;
			arrowX = Levels.arrowX;
			arrowY = Levels.arrowY;
			manX = Levels.manX;
			BASE_RADIUS = Levels.BASE_RADIUS;
		    manY = 9*H/10;
		    ballHit = 0;
		    manballCollison= false;
		    
			ballX = ballX + velocityX;
			ballY = ballY + velocityY;
			if ((ballX > W - radius ) || (ballX < 11*W/100)) {
				velocityX = -velocityX;
			}
			if(Levels.currentLevel==5){
			if(((ballX>52*W/100-radius)&&ballX<58*W/100)||((ballX<58*W/100)&&ballX>52*W/100)){
				velocityX = -velocityX;
			}
			}
			
			if ((ballY > H - radius)) {
				if (radius > 4 * BASE_RADIUS) {
					velocityY = (float) ((-1)*(9.6));
				} else {
					velocityY = (float) (-1 *( 5 +(radius/ BASE_RADIUS)));
				}
				
			}
			
				velocityY = velocityY + gravity;
		
				if(Levels.powerUpArrow)	{
					arrowWidth = 15*W/1000;
					arrowHeight = H;
				}else if(Levels.powerUpTornado){
					arrowWidth = 45*W/1000;
					arrowHeight = arrowY+30*W/1000;
				}
		if (arrowX < ballX + radius &&
				   arrowX +arrowWidth  > ballX &&
				   arrowY < ballY + radius &&
				   arrowHeight> ballY) {
			ballHit = 1;
			if(velocityX>0||velocityX==0){
			velocityX = -24*W/10000;
			}
			if(radius<6*BASE_RADIUS)velocityY =-4;
			else velocityY = -2;
				}
		if(radius==6*BASE_RADIUS)
		Log.w("dhcbjdscnksdjncks", ""+velocityY);
		
		if(ballY<manY+H/20){
		if (ballX < manX + W/23 && ballX + radius > manX+W/100
				&& ballY < manY + H/10 && radius+ ballY > manY) {
			
			manballCollison= true;
		}else{
			if (ballX < manX + W/25 && ballX + radius > manX+W/100
					&& ballY < manY + H/10 && radius+ ballY > manY) {				
				manballCollison= true;
		}
		}
        
	}

}
}