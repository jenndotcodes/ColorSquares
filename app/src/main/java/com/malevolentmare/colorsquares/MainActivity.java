
package com.malevolentmare.colorsquares;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends Activity {
    MediaPlayer mpZep;
    private SecondThread thread;
    private static MySurface ms;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("onresume", "on resume");
        mpZep = MediaPlayer.create(this, R.raw.led_zeppelin_dyer_maker);
        mpZep.start();
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new MySurface(this));
        Log.d("start","on create");

        mpZep = MediaPlayer.create(this, R.raw.led_zeppelin_dyer_maker);
        mpZep.start();





    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("stop","stopping");
        mpZep.stop();
        thread.setRunning(false);


    }





    class MySurface extends SurfaceView implements SurfaceHolder.Callback {


        private int[] colors = new int[5];

        private long startTime;
        private long totalTime = 60;
        private RectF[][] rectangles = new RectF[10][10];
        private int gridSize = 2;
        private int uniqueSquareRow = 0;
        private int uniqueSquareColumn = 0;
        long timeLeft = 60;
        private Random rnd = new Random();
        private double colorVariationPercentage = .95;
        private int row = rnd.nextInt(5);
        private boolean fadingOut = false;
        private boolean gameOver = false;
        private int score = 0;
        Bitmap titleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.title_logo);
        MediaPlayer mpBlop = MediaPlayer.create(getApplicationContext(), R.raw.blop);


        public MySurface(Context context) {
            super(context);
            uniqueSquareColumn = rnd.nextInt(gridSize);
            uniqueSquareRow = rnd.nextInt(gridSize);
            colors[0] = Color.parseColor("#0099CC");
            colors[1] = Color.parseColor("#9933CC");
            colors[2] = Color.parseColor("#669900");
            colors[3] = Color.parseColor("#FF8800");
            colors[4] = Color.parseColor("#CC0000");
            for(int i = 0; i < 10; i++)
                for(int j = 0; j < 10; j++)
                    rectangles[i][j] = new RectF();
            startTime = System.currentTimeMillis();
            getHolder().addCallback(this);
            thread = new SecondThread(getHolder(), this);

        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (rectangles[i][j].contains(event.getX(), event.getY())) {
                            if (i == uniqueSquareRow && j == uniqueSquareColumn) {
                                Log.d("message", "You hit the right square!");
                                mpBlop.start();
                                if (gridSize < 10)
                                    gridSize++;
                                if (colorVariationPercentage > .2)
                                    colorVariationPercentage -= .05;
                                uniqueSquareColumn = rnd.nextInt(gridSize);
                                uniqueSquareRow = rnd.nextInt(gridSize);
                                row = rnd.nextInt(5);
                                score++;
                                return true;
                            }
                        }
                    }
                }
            }

            return true;
        }

        //@Override
        public void Fix_onDraw(Canvas canvas) {
            long nowTime = System.currentTimeMillis();
            long differenceTime = startTime - nowTime;
            long elapsed = differenceTime / 1000;
            timeLeft = totalTime + elapsed;

            if(canvas!=null) {
                if(timeLeft >= 0) {
                    //find out how much time has elapsed

                    final int offset = 5;
                    int xStartPoint = 0;
                    int yStartPoint = 0;
                    double gridWidth;
                    DecimalFormat formatter = new DecimalFormat("00");


                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    canvas.drawColor(Color.BLACK);

                    if (getWidth() > getHeight()) //we're in landscape
                    {
                        gridWidth = .8 * getHeight();
                    } else //we're in portrait
                    {
                        gridWidth = .8 * getWidth();
                    }

                    xStartPoint = (int) (getWidth() - gridWidth) / 2 - offset;
                    yStartPoint = (int) (getHeight() - gridWidth) / 2 - offset;
                    int rectangleWidth = (int) gridWidth / gridSize;

                    for (int i = 0; i < gridSize; i++) {
                        for (int j = 0; j < gridSize; j++) {

                            rectangles[i][j] = new RectF(rectangleWidth * i + offset + xStartPoint, rectangleWidth * j + offset + yStartPoint,
                                    rectangleWidth * i + rectangleWidth + xStartPoint, rectangleWidth * j + rectangleWidth + yStartPoint);
                            if (i == uniqueSquareRow && j == uniqueSquareColumn) {
                                int color = colors[row];
                                int r = Color.red(color);
                                int g = Color.green(color);
                                int b = Color.blue(color);
                                r += (255 - r) * colorVariationPercentage;
                                g += (255 - g) * colorVariationPercentage;
                                b += (255 - b) * colorVariationPercentage;
                                color = Color.rgb(r, g, b);
                                paint.setColor(color);
                            } else {
                                paint.setColor(colors[row]);
                            }
                            canvas.drawRoundRect(rectangles[i][j], 10f, 10f, paint);
                            //offset = 10;
                        }
                    }
                    paint.setTextSize(50);

                    Date dNow = new Date();

                    // DecimalFormat d = new DecimalFormat("00:00");
                    canvas.drawText("Time Left: 00:" + formatter.format(timeLeft), (float) xStartPoint, (float) (gridWidth + yStartPoint + 55), paint);
                    canvas.drawText("Score: " + score, (float) xStartPoint, (float) (gridWidth + yStartPoint + 55 + 55), paint);
                    Drawable d = getResources().getDrawable(R.drawable.title_logo);
                    int width = getWidth() - xStartPoint * 2;

                    d.setBounds(xStartPoint, 0, getWidth()-xStartPoint, width*d.getIntrinsicHeight()/d.getIntrinsicWidth());
                    d.draw(canvas);

                }
                else
                {
                    Intent endGame = new Intent(getContext(), EndGameScreen.class);
                    endGame.putExtra("extScore", score);
                    boolean retry = true;
                    thread.setRunning(false);

                    startActivity(endGame);
                }


            }

    }

    @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {}

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(thread.getState()== Thread.State.TERMINATED)
            {
                thread = new SecondThread(getHolder(),this);
            }
            thread.setRunning(true);
            thread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d("Destoryed","destroyed");
            boolean retry = true;
            thread.setRunning(false);
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {}
            }
        }
    }

    class SecondThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private MySurface mySurface;
        private boolean _run = false;
        static final long FPS = 10;

        public SecondThread(SurfaceHolder surfaceHolder, MySurface mySurface) {
            this.surfaceHolder = surfaceHolder;
            this.mySurface = mySurface;
        }

        public void setRunning(boolean run) {
            _run = run;
        }

        @Override
        public void run() {
            Canvas c;
            long ticksPS = 1000 / FPS;
            long startTime;
            long sleepTime;
            while (_run) {
                c = null;
                startTime = System.currentTimeMillis();
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        mySurface.Fix_onDraw(c);
                        sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
                        try {
                            if (sleepTime > 0)
                                sleep(sleepTime);
                            else
                                sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }

            }
        }
    }
}