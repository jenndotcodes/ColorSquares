package com.malevolentmare.colorsquares;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class EndGameScreen extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_end_game_screen);
        TextView txtScore = (TextView)findViewById(R.id.txtScore);
        TextView txtBestScore = (TextView)findViewById(R.id.txtBestScore);
        final TextView txtBestInit = (TextView)findViewById(R.id.txtBestScoreInits);
        final EditText edtInit = (EditText)findViewById(R.id.edtInitials);
        final TextView txtInitLabel = (TextView)findViewById(R.id.txtInitialLabel);

        Button btnCont = (Button)findViewById(R.id.btnCont);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final long bestScore = sharedPref.getInt(getString(R.string.high_score_preference_key),-1);
        final String bestInits = sharedPref.getString(getString(R.string.initials_preference_key),"XXX");
        final int score = getIntent().getIntExtra("extScore", -1);
        if(score>0)
            txtScore.setText(""+score);
        if(bestScore>0)
        {
            txtBestScore.setText(""+bestScore);
            txtBestInit.setText(bestInits);
        }
        if(bestScore > score)
        {
            edtInit.setVisibility(View.INVISIBLE);
            txtInitLabel.setVisibility(View.INVISIBLE);
        }



        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(score > bestScore)
                {
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.high_score_preference_key), score);
                    String initials = edtInit.getText().toString();
                    if(initials.isEmpty()) { initials = "XXX";}
                    editor.putString(getString(R.string.initials_preference_key), initials);
                    editor.commit();

                }
                Intent playAgain = new Intent(EndGameScreen.this,MainActivity.class);
                startActivity(playAgain);
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.end_game_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
