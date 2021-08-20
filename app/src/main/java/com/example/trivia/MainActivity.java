package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final String MESSAGE_ID = "message" ;
    private ActivityMainBinding binding;
        private int currentQuestionIndex = 0;
        List<Question> questionList;
        private int scoreCounter = 0;
        private Score score;
        private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        score = new Score();
        score.setScore(scoreCounter);
        prefs = new Prefs(MainActivity.this);

        currentQuestionIndex = prefs.getState();


        binding.scoreKeeperTextView.setText(MessageFormat.format("Your Score is : {0} Points", +score.getScore()));
        binding.highestTextView.setText(MessageFormat.format("Highest : {0} Points", + prefs.getHighestScore()));


        //Fetching data from Api to set the Question out of TextView
        questionList =  new Repository().getQuestion(questionArrayList ->{
                         binding.questionTextView.setText
                         (questionArrayList.get(currentQuestionIndex).getAnswer());
       binding.textViewQuestionOutOF.setText(MessageFormat.format("Question : {0}/{1}", currentQuestionIndex, questionArrayList.size()));
                });

        //Setting onClickListener
       binding.trueButton.setOnClickListener(v -> {
           checkAnswer(true);
           updateQuestion();

       });
       binding.falseButton.setOnClickListener(v -> {
           checkAnswer(false);
           updateQuestion();
       });


       binding.shareButton.setOnClickListener(v -> {
           Intent intent = new Intent(Intent.ACTION_SEND);
           intent.setType("text/plain");
           intent.putExtra(Intent.EXTRA_TEXT , "My Score is: " + score.getScore() + " and My Highest Score is: " + prefs.getHighestScore());
           intent.putExtra(Intent.EXTRA_SUBJECT , "I am Playing Trivia Game.");
           startActivity(intent);
       });

       binding.imageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               resetGame();
           }
       });

    }

    private void resetGame() {
        currentQuestionIndex = 1;
             updateQuestion();
             score.setScore(0);
             prefs.saveHighestScore(score.getScore());
        binding.scoreKeeperTextView.setText(MessageFormat.format("Your Score is : {0} Points", +score.getScore()));

    }

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
    }

    private void checkAnswer(boolean userChooseCorrect) {
      boolean answer = questionList.get(currentQuestionIndex).getAnswerTrue();
      int snackBarID = 0;
      if (answer == userChooseCorrect){
         snackBarID = R.string.correct;
         trueAnimation();
         addPoints();
      }else {
          snackBarID = R.string.incorrect;
          falseAnimation();
          subPoints();
      }

      Snackbar.make(binding.cardView , snackBarID , Snackbar.LENGTH_SHORT).show();
    }



    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewQuestionOutOF.setText("Question : " + currentQuestionIndex + '/' + questionArrayList.size());
    }


    public void updateQuestion(){
        binding.questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
        updateCounter((ArrayList<Question>) questionList);
    }
    public void falseAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this , R.anim.shake);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            binding.questionTextView.setTextColor(Color.WHITE);
            getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void trueAnimation() {
        AlphaAnimation shake = new AlphaAnimation(1.0f , 0.0f) ;
        shake.setDuration(300);
        shake.setRepeatCount(1);
        shake.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.scoreKeeperTextView.setText(MessageFormat.format("Your Score is : {0} Points", +score.getScore()));
    }
    private void subPoints(){
        if (scoreCounter > 0){
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            binding.scoreKeeperTextView.setText(MessageFormat.format("Your Score is : {0} Points", score.getScore()));
        }else
            score.setScore(0);
            binding.scoreKeeperTextView.setText(MessageFormat.format("Your Score is : {0} Points", score.getScore()));
    }

    @Override
    protected void onPause() {
        prefs.setState(currentQuestionIndex);
        prefs.saveHighestScore(score.getScore());
        super.onPause();
    }
}