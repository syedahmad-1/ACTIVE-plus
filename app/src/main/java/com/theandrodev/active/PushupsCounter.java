package com.theandrodev.active;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class PushupsCounter extends AppCompatActivity {
   private long mStartTimeInMillis;
   private EditText mEditTextInput;
   private Button mButtonSet;
    private TextView TextViewCountdown;
    private ImageButton start;
    private ImageButton pause;
    private ImageButton reset;
    private int i =0;
    private CountDownTimer countDownTimer;
    private boolean istimerrunnning;
    private ProgressBar progressBar;
    private long TimeleftInMillis = mStartTimeInMillis;
    private int PushUps;
    private TextView textViewCountedPushups;
    private ImageButton resetPushupButton;
    private int initialcount = 0;
    private Boolean isSetBtnClicked=false;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushups_counter);
        resetPushupButton = findViewById(R.id.resetPushupsButton);
        mEditTextInput= findViewById(R.id.edit_text_input);
        mButtonSet = findViewById(R.id.Setbtn);
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                isSetBtnClicked=true;
                if(input.length()==0)
                {
                    Toast.makeText(PushupsCounter.this, "Enter Time in min", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input)*60000;
                if(millisInput==0)
                {
                    Toast.makeText(PushupsCounter.this, "Please Enter a Positive Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                seTime(millisInput);
                if(isSetBtnClicked) {
                    TextViewCountdown.setVisibility(View.VISIBLE);
                    mEditTextInput.setCursorVisible(false);
                    mEditTextInput.setVisibility(View.INVISIBLE);
                    mEditTextInput.setHint("");
                }

            }
        });

        TextViewCountdown = findViewById(R.id.time_view);
        start = findViewById(R.id.startButton);
        pause = findViewById(R.id.pauseButton);
        reset = findViewById(R.id.resetButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(i);
        if(istimerrunnning==false)
        {
            progressBar.setProgress(0);
        }

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();

            }
        });
        resetPushupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPushupcount();
            }
        });
        updateCountDownText();
        //Use of Proximity Sensor

        textViewCountedPushups = findViewById(R.id.countedPushups);
        PushUps=0;
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[0]<proximitySensor.getMaximumRange())
                {
                    incrementpushups();
//
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, proximitySensor, 2*1000*1000);
        tts = new TextToSpeech(PushupsCounter.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int results =tts.setLanguage(Locale.ENGLISH);
                    if (results == TextToSpeech.LANG_MISSING_DATA || results==TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Language Not Supported");
                    }

                }
                else {
                    Log.e("TTS", "Initialization Failed");
                }


            }
        });




//

    }
    private void seTime(long millseconds)
    {
        mStartTimeInMillis = millseconds;
        resetTimer();
    }
    private void startTimer()
    {
     countDownTimer = new CountDownTimer (TimeleftInMillis, 1000) {
         @Override
         public void onTick(long millisUntilFinished) {
             TimeleftInMillis = millisUntilFinished;
             istimerrunnning=true;
             updateCountDownText();
             i++;
             progressBar.setProgress((int)i*100/((int)millisUntilFinished/1000));
         }


         @Override
         public void onFinish() {
             istimerrunnning = false;
             TextViewCountdown.setText("00:00");

             progressBar.setProgress(100);

         }
     } .start();
        istimerrunnning=true;
    }
    private void pauseTimer()
    {   if(istimerrunnning) {
        countDownTimer.cancel();
    }
        istimerrunnning = false;
    }
    private void resetTimer()
    {
        TimeleftInMillis = mStartTimeInMillis;
        updateCountDownText();
        pauseTimer();
        resetPushupcount();
        progressBar.setProgress(0);
        mEditTextInput.setCursorVisible(true);
        TextViewCountdown.setVisibility(View.INVISIBLE);
        mEditTextInput.setVisibility(View.VISIBLE);
        mEditTextInput.setText("");
        mEditTextInput.setHint("00:00");

    }
    private void updateCountDownText()
    {
        int hours = (int) (TimeleftInMillis/1000)/3600;
        int minutes = (int) ((TimeleftInMillis/1000)%3600)/60;
        int seconds = (int) (TimeleftInMillis/1000)%60;
        String timeLeftFormatted;
        if(hours>0)
        {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d",hours, minutes, seconds);
        }
        else {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        }

        TextViewCountdown.setText(timeLeftFormatted);

    }
    private void incrementpushups()
    {
        PushUps=PushUps+1;
        textViewCountedPushups.setText(String.valueOf(PushUps));
        speak();


    }
    @SuppressLint("SetTextI18n")
    private void resetPushupcount()
    {
       PushUps= initialcount;
        textViewCountedPushups.setText("00");
    }
    private void speak()
    {
        String text = String.valueOf(PushUps);
        tts.setPitch(1);
        tts.setSpeechRate(1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    protected void onDestroy() {
        if(tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}