package com.mdg.ballx2.Levels;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mdg.ballx2.AccelerometerData;
import com.mdg.ballx2.Ball;
import com.mdg.ballx2.ISoundPoolLoaded;
import com.mdg.ballx2.MainMenu;
import com.mdg.ballx2.PowerUp;
import com.mdg.ballx2.R;
import com.mdg.ballx2.SoundPoolManager;
import com.mdg.ballx2.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseLevel extends View {

    public static float manX = -100, manY = -100;
    public static float arrowY = -1000, arrowX = -100;
    float manVelocity, accelerometerSensorX = -1;
    boolean shoot = false, manMove = true, otherShoot = false;
    public static boolean powerUpSpike, powerUpTornado, powerUpShield = false,
            powerUpArrow = true;
    int isShooting = 0;
    Bitmap player, arrow,playerMove;
    private ShapeDrawable ballDrawable;
    Bitmap directionArrowLeft, directionArrowRight;
    private Rect sourceRect; // the rectangle to be drawn from the animation
    private int currentFrame; // the current frame
    private long frameTicker; // the time of the last frame update
    private int framePeriod; // milliseconds between each frame (1000/fps)

    public static float gameAreaHeight, gameAreaWidth;

    int[] touchX = new int[2];
    int[] touchY = new int[2];
    public static boolean pauseGame = false,isHitTimerActive=false;
    boolean isHit = false;
    int maxTime = 100;
    int time = 0;
    int numberOfLife = MainActivity.noOflife;
    String message = "";
    // Initializing list to store balls and their radius
    ArrayList<Ball> balls = new ArrayList<>();
    ArrayList<Integer> radius = new ArrayList<>();
    ArrayList<PowerUp> gifts = new ArrayList<>();
    public static int BASE_RADIUS;
    double risingFactor = 0.1;
    int shieldTimeCounter = 0,freezeArrowCounter=200;
    Paint paint;
    Rect mainRect;
    long Timer,timePassed;
    onStageChangeListener mlistener;


    public BaseLevel(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        // TODO Auto-generated constructor stub
        paint = new Paint();
        player = BitmapFactory.decodeResource(getResources(), R.drawable.player_idle);
        playerMove = BitmapFactory.decodeResource(getResources(), R.drawable.player_move);
        arrow = BitmapFactory.decodeResource(getResources(),
                R.drawable.bullet_simple);
        directionArrowLeft = BitmapFactory.decodeResource(getResources(),
                R.drawable.arrow_left);
        directionArrowRight = BitmapFactory.decodeResource(getResources(),
                R.drawable.arrow_right);
        sourceRect = new Rect(0, 0, playerMove.getWidth() / 4, playerMove.getHeight()/2);
        ballDrawable = new ShapeDrawable(new OvalShape());
        framePeriod = 120;
        frameTicker = 0l;


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        touchX[0] = (int) event.getX(0);
        touchY[0] = (int) event.getY(0);

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                shoot = true;
                manMove = true;
                touchX[1] = (int) event.getX(0);
                touchY[1] = (int) event.getY(0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                otherShoot = true;
                break;
            case MotionEvent.ACTION_UP:
                manMove = false;
                break;

        }

        return true;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw == 0){
            mainRect = new Rect(0, 0, getWidth(), getHeight());
            initializeGame();
            if(MainMenu.playMusic && SoundPoolManager.getInstance()==null) {
                initializeSounds();
            }
            pauseGame = false;

        }

    }
    public interface onStageChangeListener{
        public void onStageChanged(int runningLevel,int life);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mlistener = MainActivity.activity;
    }

    public void initializeGame() {

        initializeTime();
        initializeGamePanelArena();

    }


    void initializeGamePanelArena() {

        MainActivity.noOflevel.setText("LEVEL "+MainActivity.currentLevel);
        gameAreaHeight = 98 * getHeight() / 100;
        gameAreaWidth = getWidth();
        BASE_RADIUS = (int) (gameAreaHeight / 48);
        powerUpShield = false;
        manX = 52 * gameAreaWidth / 100;
        time = maxTime;
    }

    void initializeTime() {
        switch (MainActivity.currentLevel) {
            case 1:
                maxTime = 8000;
                break;
            case 2:
                maxTime = 10000;
                break;
            case 3:
                maxTime = 15000;
                break;
            case 4:
                maxTime = 22000;
                break;
            case 5:
                maxTime = 18000;
                break;
            case 6:
                maxTime = 12000;
                break;
            case 7:
                maxTime = 28000;
                break;
        }


    }

    void initializeSounds(){
        SoundPoolManager.CreateInstance();
        List<Integer> sounds = new ArrayList<>();
        sounds.add(R.raw.pop);
        sounds.add(R.raw.demo);
        sounds.add(R.raw.gift_taken);
        sounds.add(R.raw.death);
        SoundPoolManager.getInstance().setSounds(sounds);
        try {
            SoundPoolManager.getInstance().InitializeSoundPool(MainActivity.activity, new ISoundPoolLoaded() {
                @Override
                public void onSuccess() {
                    SoundPoolManager.getInstance().setPlaySound(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGame() {
        updateManState();
        updateArrowPosition();
        updateBallPosition();
        updateTimeCounter();
        updateGiftPosition();

    }

    void updateArrowPosition() {
        if (isShooting == 0) {
            arrowY = gameAreaHeight;
            arrowX = manX + gameAreaWidth / 40 - arrow.getWidth() / 2;
        }
        if (shoot || otherShoot) {
            if(isShooting==0){
                if(SoundPoolManager.getInstance()!=null)
                    SoundPoolManager.getInstance().playSound(R.raw.demo);
            }
            isShooting = 1;
            shoot = false;
            otherShoot = false;
        }
        if (isShooting == 1) {
            if (powerUpArrow) {
                arrowY = arrowY - 5*(gameAreaHeight/700);
            } else if (powerUpTornado) {
                arrowY = arrowY - 10*(gameAreaHeight/700);
            }
            if (arrowY < 27 * gameAreaHeight / 100) {
                if (!powerUpSpike) {
                    arrowY = gameAreaHeight;
                    isShooting = 0;
                } else {
                    freezeArrowCounter--;
                    if(freezeArrowCounter<0){
                        arrowY = gameAreaHeight;
                        isShooting = 0;
                        freezeArrowCounter=200;
                    }
                    arrowY = 27 * gameAreaHeight / 100;
                }

            }

        }
    }

    void updateManState() {

        int frameNr = 4;
        long gameTime = System.currentTimeMillis();
        if (gameTime > frameTicker + framePeriod) {
            frameTicker = gameTime;
            // incrementing the frame
            currentFrame++;
            if (currentFrame >= frameNr) {
                currentFrame = 0;
            }
        }
        // defining the rectangle to cut out sprite

        int spriteWidth = playerMove.getWidth() / 4;
        sourceRect.left = currentFrame * spriteWidth;
        sourceRect.right = sourceRect.left + spriteWidth;
        manY = (int) (9 * gameAreaHeight / 10);
        final float manSpeed = gameAreaHeight / 160;

        if (MainMenu.selectMethod == 1) { // if G-sensor is selected
            accelerometerSensorX = AccelerometerData.sensorX;
            manVelocity = 2 * accelerometerSensorX;

            if (Math.abs(manVelocity) < 1) {
                manVelocity = 0;
            } else {
                manVelocity = (manVelocity / Math.abs(manVelocity)) * manSpeed;
            }
        } else if (MainMenu.selectMethod == 2) { // if manual control is
            // selected

            Rect destArrowLeft = new Rect(5 * getWidth() / 100,
                    88 * getHeight() / 100, 13 * getWidth() / 100,
                    97 * getHeight() / 100);
            Rect destArrowRight = new Rect(88 * getWidth() / 100,
                    88 * getHeight() / 100, 96 * getWidth() / 100,
                    97 * getHeight() / 100);
            if (manMove) {
                if (destArrowLeft.contains(touchX[0], touchY[0])) {
                    manVelocity = -1 * manSpeed;
                    shoot = false;
                } else if (destArrowRight.contains(touchX[0], touchY[0])) {
                    manVelocity = manSpeed;
                    shoot = false;
                }
            } else
                manVelocity = 0;
        }
        manX = manX + manVelocity;
        if (manX < 0) {
            manX = 0;
        }
        if (manX > gameAreaWidth - gameAreaWidth / 20) {
            manX = gameAreaWidth - gameAreaWidth / 20;
        }


    }

    void updateBallPosition() {

        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).moveBall(radius.get(i));
        }
    }

    void updateTimeCounter() {
        time = time - 4;
    }

    void updateGiftPosition() {
        for (int i = 0; i < gifts.size(); i++) {
            gifts.get(i).dropGift();
            if(gifts.get(i).istimeUp){
                gifts.remove(i);
            }
        }
    }

    void updatePowerUps(int id) {
        switch (id) {
            case 2:
                numberOfLife++;
                break;
            case 3:
                time = time + 1000;
                if(time>maxTime)time = maxTime;
                break;
            case 4:
                powerUpShield = true;
                shieldTimeCounter = 800;
                break;
            case 5:
                break;
            case 6:
                powerUpTornado = true;
                powerUpArrow = false;
                powerUpSpike = false;
                arrow = BitmapFactory.decodeResource(getResources(),
                        R.drawable.bullet_tornado);
                break;
            case 7:
                powerUpSpike = true;
                powerUpTornado = false;
                powerUpArrow = true;
                break;
            case 8:
                powerUpTornado = false;
                powerUpArrow = true;
                powerUpSpike = false;
                arrow = BitmapFactory.decodeResource(getResources(),
                        R.drawable.bullet_simple);
                break;
            case 9:
                MainActivity.score =  MainActivity.score  + 30;
                break;
            case 10:
                MainActivity.score =  MainActivity.score  + 120;
                break;
            case 11:
                MainActivity.score =  MainActivity.score  + 200;
                break;
        }
    }

    public void renderGame(Canvas c) {
        renderArrow(c);
        renderMan(c);
        renderBalls(c);
        renderOtherObjects(c);
        renderGifts(c);
        renderPowerUps(c);
    }

    void renderArrow(Canvas c) {
        if (powerUpSpike) {
            if (arrowY <= 27 * gameAreaHeight / 100) {
                arrow = BitmapFactory.decodeResource(getResources(),
                        R.drawable.bullet_spike);

            }else if(arrowY >= gameAreaHeight){
                arrow = BitmapFactory.decodeResource(getResources(),
                        R.drawable.bullet_simple);
            }
        }
        float bottom = arrowY + arrow.getHeight();
        if (bottom > gameAreaHeight)
            bottom = gameAreaHeight;
        float arrowWidth = 15*gameAreaWidth/1000;
        if(powerUpTornado)arrowWidth = 3*arrowWidth;
        mainRect.set((int) arrowX, (int) arrowY,
                (int) (arrowX + arrowWidth), (int) (bottom));
        c.drawBitmap(arrow, null, mainRect, null);
    }

    void renderMan(Canvas c) {
        mainRect.set((int) manX, (int) manY, (int) (manX + gameAreaWidth / 20),
                (int) (manY + gameAreaHeight / 10));

        if (manVelocity > 0) {
            sourceRect.top = playerMove.getHeight()/2;
            sourceRect.bottom = playerMove.getHeight();
            c.drawBitmap(playerMove, sourceRect, mainRect, null);
        } else if (manVelocity < 0) {
            sourceRect.top = 0;
            sourceRect.bottom = playerMove.getHeight()/2;
            c.drawBitmap(playerMove, sourceRect, mainRect, null);
        } else
            c.drawBitmap(player,null, mainRect, null);

    }

    void renderBalls(Canvas c) {

        for (int i = 0; i < balls.size(); i++) {

            ballDrawable.getPaint().setColor(Color.BLACK);

            ballDrawable.setBounds((int) balls.get(i).ballX,
                    (int) balls.get(i).ballY,
                    (int) balls.get(i).ballX + radius.get(i),
                    (int) balls.get(i).ballY + radius.get(i));
            ballDrawable.draw(c);
        }

    }

    void renderOtherObjects(Canvas c) {

        renderingTimer(c);
        renderingNavigationArrows(c);
        MainActivity.scoreView.setText(""+MainActivity.score);
        MainActivity.lifeView.setText(""+numberOfLife);

    }


    void renderingTimer(Canvas c) {
        if (time < 0) {
            message = "TIME UP";
            isHit=true; isHitTimerActive = true;
            time = 0;
        }
        paint.setColor(Color.rgb(25, 27, 40));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2*gameAreaHeight / 100);
        float length=0;
        if(maxTime!=0){length =(gameAreaWidth*time) / maxTime;}
        mainRect.set(0,(int)(98.8*getHeight()/100),(int)length,getHeight());
        c.drawRect(mainRect,paint);
        paint.reset();
    }

    void renderingNavigationArrows(Canvas c) {
        if (MainMenu.selectMethod == 2) { // when Manual method is selected

            mainRect.set(5 * getWidth() / 100, 88 * getHeight() / 100,
                    13 * getWidth() / 100, 97 * getHeight() / 100);
            c.drawBitmap(directionArrowLeft, null, mainRect, null);
            mainRect.set(88 * getWidth() / 100, 88 * getHeight() / 100,
                    96 * getWidth() / 100, 97 * getHeight() / 100);
            c.drawBitmap(directionArrowRight, null, mainRect, null);
        }
    }

    void renderGifts(Canvas c) {
        float giftWidth = gameAreaWidth / 55;
        for (int i = 0; i < gifts.size(); i++) {
            int id = gifts.get(i).id;
            if(id==2||id==4||id==5||id==6||id==7||id==8)giftWidth = gameAreaWidth / 65;

            Rect r = new Rect((int) (gifts.get(i).giftX - giftWidth),
                    (int) (gifts.get(i).giftY),
                    (int) (gifts.get(i).giftX + giftWidth),
                    (int) (gifts.get(i).giftY + gameAreaHeight / 15));
            paint.reset();
            if(gifts.get(i).timeGoingToOver){
                paint.setAlpha(125);
            }
            c.drawBitmap(gifts.get(i).gift, null, r, paint);

        }
    }

    void renderPowerUps(Canvas c) {
        if (powerUpShield) {
            Bitmap shield = BitmapFactory.decodeResource(getResources(),
                    R.drawable.armor);
            mainRect.set((int) manX, (int) manY,
                    (int) (manX + gameAreaWidth / 20),
                    (int) (manY + gameAreaHeight / 10));
            c.drawBitmap(shield, null, mainRect, null);
            shieldTimeCounter--;
            if (shieldTimeCounter < 0) {
                powerUpShield = false;
            }
        }
    }

    public void detectCollison() {
        collisonBallArrow();
        if (!powerUpShield) {
            collisonBallMan();
        }
        collisonManGift();
    }

    void collisonBallArrow() {

        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).ballHit == 1) {
                MainActivity.score =  MainActivity.score  + 10;// incrementing scores
                if(SoundPoolManager.getInstance()!=null)
                    SoundPoolManager.getInstance().playSound(R.raw.pop);
                if(radius.get(i)>2*BASE_RADIUS){
                    radius.set(i, radius.get(i)-2*BASE_RADIUS);
                }else radius.set(i, radius.get(i)/2);
                arrowY = gameAreaHeight;
                isShooting = 0;

                if (radius.get(i) < BASE_RADIUS) {
                    balls.remove(i);
                    radius.remove(i);
                } else {
                    int ballX = (int) (balls.get(i).ballX + radius.get(i));
                    int ballY = (int) balls.get(i).ballY;
                    if(ballX+radius.get(i)>gameAreaWidth){
                        ballX = ballX-3*radius.get(i)/4;
                    }
                    if (radius.get(i) < 6 * BASE_RADIUS)
                        risingFactor = -4*gameAreaHeight/700;
                    else
                        risingFactor = -(25/10)*(gameAreaHeight/700);

                    Ball q = new Ball(ballX, ballY, risingFactor,24*gameAreaWidth/10000);
                    balls.add(q);
                    radius.add(radius.get(i));
                    generatePowerUps(balls.get(i).ballY);// generating bonous
                    // powerups
                }
            }
        }
    }

    void collisonBallMan() {
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).manballCollison) {
                // life will be lost
                message = "YOU GOT HIT";
                isHit = true;
                isHitTimerActive = true;
            }

        }
    }

    void collisonManGift() {
        for (int i = 0; i < gifts.size(); i++) {
            if (gifts.get(i).giftTaken) {
                if(SoundPoolManager.getInstance()!=null)
                    SoundPoolManager.getInstance().playSound(R.raw.gift_taken);
                updatePowerUps(gifts.get(i).id);
                gifts.remove(i);
            }
        }
    }



    public void lifeLost() {

        numberOfLife--;
        balls.clear();
        radius.clear();
        gifts.clear();
        isShooting = 0;
        if (numberOfLife > 0) {
            initializeGame();
            updatePowerUps(8);
        } else {
            gameOver();
        }
    }

    public void gameOver() {
        pauseGame = true;
        final Dialog popUp = new Dialog(MainActivity.activity);
        popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUp.getWindow().setGravity(Gravity.CENTER);
        popUp.setContentView(R.layout.game_over);
        popUp.getWindow().setLayout(7*getWidth()/10,9*getHeight()/10);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/chewy.ttf");
        Button yes = (Button) popUp.findViewById(R.id.yes);
        yes.setTypeface(tf);
        Button no = (Button) popUp.findViewById(R.id.no);
        no.setTypeface(tf);
        TextView dis = (TextView) popUp.findViewById(R.id.textView2);
        dis.setTypeface(tf);
        dis.setText(""+MainActivity.score);
        TextView ques = (TextView) popUp.findViewById(R.id.textView);
        TextView title = (TextView) popUp.findViewById(R.id.textView1);
        title.setTypeface(tf);
        ques.setTypeface(tf);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try{
                    ((Activity)getContext()).finish();
                }catch(ClassCastException e){
                    e.printStackTrace();
                }
                MainActivity.currentLevel=1;
                MainActivity.score=0;
                MainActivity.noOflife=5;
                getContext().startActivity(i);

                popUp.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SoundPoolManager.getInstance().stop();
                SoundPoolManager.getInstance().release();
                System.exit(0);
            }
        });
        popUp.setCancelable(false);
        popUp.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        popUp.show();
        //Set the dialog to immersive
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popUp.getWindow().getDecorView().setSystemUiVisibility(
                    MainActivity.activity.getWindow().getDecorView().getSystemUiVisibility());
        }
        //Clear the not focusable flag from the window
        popUp.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

    }

    public void generatePowerUps(float giftY) {
        int powerUpId[] = { 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4,
                4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 8, 8, 9,
                9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 11, 11 };

        Random r = new Random();
        int generatedPowerUpId = powerUpId[r.nextInt(50)];
        switch (MainActivity.currentLevel) {
            case 1:
                if (generatedPowerUpId == 5 || generatedPowerUpId == 6)
                    generatedPowerUpId = 8;
                if (generatedPowerUpId == 10 || generatedPowerUpId == 11)
                    generatedPowerUpId = 9;
                break;
            case 2:
                if (generatedPowerUpId == 5)
                    generatedPowerUpId = 8;
                if (generatedPowerUpId == 6)
                    generatedPowerUpId = 7;
                if (generatedPowerUpId == 11)
                    generatedPowerUpId = 10;
                break;
            case 3:
                if (generatedPowerUpId == 5)
                    generatedPowerUpId = 8;
                if (generatedPowerUpId == 6)
                    generatedPowerUpId = 7;
                if (generatedPowerUpId == 11)
                    generatedPowerUpId = 10;
                break;
            case 4:
                if (generatedPowerUpId == 5)
                    generatedPowerUpId = 8;
                if (generatedPowerUpId == 6)
                    generatedPowerUpId = 7;
                break;
        }
        if(generatedPowerUpId==5) generatedPowerUpId = 6;
        Bitmap gift = null;
        switch (generatedPowerUpId) {
            case 2:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_life);
                break;
            case 3:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_time);
                break;
            case 4:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_armor);
                break;

            case 6:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_tornado);
                break;
            case 7:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_spike);
                break;
            case 8:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_arrow);
                break;
            case 9:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_coin);
                break;
            case 10:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_coins);
                break;
            case 11:
                gift = BitmapFactory.decodeResource(getResources(),
                        R.drawable.gift_dollar);
                break;
        }

        if (generatedPowerUpId != 1) {
            PowerUp p = new PowerUp(arrowX, giftY, generatedPowerUpId,gift);
            gifts.add(p);
        }

    }


    public void onDraw(Canvas c) {
        // TODO Auto-generated method stub
        super.onDraw(c);
        paint.reset();
        c.drawColor(Color.TRANSPARENT);

        if(isHitTimerActive) {
            paint.setColor(Color.BLACK);
            paint.setTextSize((float) (1.1 * gameAreaHeight / 9));
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/chewy.ttf");
            paint.setTypeface(tf);
            c.drawText(message, 40 * gameAreaWidth / 100, 60 * gameAreaHeight / 100, paint);
            paint.reset();
        }

      //  Log.e("jsdncskjdnvkls", "" + pauseGame);
        invalidate(0, (int) (27 * gameAreaHeight / 100), (int) gameAreaWidth, getHeight());
    }


}