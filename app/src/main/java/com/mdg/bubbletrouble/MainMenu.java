package com.mdg.bubbletrouble;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainMenu extends Activity {

	TextView tv;
	int height, width;
	MediaPlayer mediaPlayer;
	RadioButton controlManual, controlSensor;

	static int selectMethod = 1;
    static boolean playMusic = true;
   static MainMenu activity;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
        activity = MainMenu.this;

		mediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
		mediaPlayer.setLooping(true);
		mediaPlayer.setVolume(50, 50);
		mediaPlayer.start();

		// -----------------------------------------------------------------------------------------------------
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

		tv = (TextView)findViewById(R.id.heading);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),
                "fonts/chewy.ttf");
        tv.setTypeface(custom_font);





		/*options.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


			}
		});
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Dialog e = new Dialog(MainMenu.this);
				e.setTitle("Help");
				TextView tv = new TextView(MainMenu.this);
				tv.setText("Aim of Game is to shoot the Bubbles from Gun. Also Avoid the bubbles from Hitting you.ENJOY!!");
				tv.setBackgroundColor(Color.DKGRAY);
				e.setContentView(tv);
				e.show();

			}
		});
*/

	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Window w = activity.getWindow();
            final View decorView = w.getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			if (isFinishing()) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
	}



}

class mainMenuView extends View{

    Bitmap play,settings;
    float x,y,x1,y1;
    Rect mainRect,mainRect2;
    Dialog d;


    public mainMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        play = BitmapFactory.decodeResource(getResources(), R.drawable.btn_play);
        settings = BitmapFactory.decodeResource(getResources(), R.drawable.btn_settings);
        mainRect = new Rect();
        mainRect2 = new Rect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();

        if(event.getAction() == MotionEvent.ACTION_UP){
            x=0;y=0;
            x1 = event.getX();
            y1 = event.getY();
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        if(mainRect.contains((int)x,(int)y)){
            mainRect.set(21*getWidth()/100,62*getHeight()/100,42*getWidth()/100,71*getHeight()/100);
        }else{
           mainRect.set(18*getWidth()/100,60*getHeight()/100,45*getWidth()/100,73*getHeight()/100);
        }
        canvas.drawBitmap(play,null,mainRect,null);


        if(mainRect2.contains((int)x,(int)y)){
            mainRect2.set(58*getWidth()/100,62*getHeight()/100,79*getWidth()/100,71*getHeight()/100);
        }else{
            mainRect2.set(55*getWidth()/100,60*getHeight()/100,82*getWidth()/100,73*getHeight()/100);
        }
        canvas.drawBitmap(settings,null,mainRect2,null);
        if((mainRect.contains((int)x1,(int)y1))){
            Intent i = new Intent("com.mdg.bubbletrouble.MAINACTIVITY");
            getContext().startActivity(i);
            x1=-1;y1 = -1;
        }

        if((mainRect2.contains((int)x1,(int)y1))){
            d = new Dialog(MainMenu.activity);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.options);
            d.setCanceledOnTouchOutside(false);
            d.getWindow().setLayout(65 * getWidth() / 100, 90 * getHeight() / 100);
            Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/chewy.ttf");
           /* ok = (Button) d.findViewById(R.id.ok);
            ok.setTypeface(custom_font);
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    d.cancel();
                }
            });
            */
           RadioButton controlManual = (RadioButton) d.findViewById(R.id.g_sensor);
            controlManual.setTypeface(custom_font);
            RadioButton controlSensor = (RadioButton) d.findViewById(R.id.manual);
            controlSensor.setTypeface(custom_font);


            MainMenu.selectMethod=1;
            RadioGroup control= (RadioGroup)d.findViewById(R.id.radioGroup);
            control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch(i){
                        case R.id.g_sensor:
                            MainMenu.selectMethod=1;
                        case R.id.manual:
                            MainMenu.selectMethod=2;
                    }
                }
            } );


            d.show();
            x1 = -1;
            y1 = -1;
        }


        invalidate();


    }
}

