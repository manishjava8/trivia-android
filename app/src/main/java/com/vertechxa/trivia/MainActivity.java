package com.vertechxa.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vertechxa.trivia.controller.AppController;
import com.vertechxa.trivia.data.AnswerListAsyncResponse;
import com.vertechxa.trivia.data.QuestionBank;
import com.vertechxa.trivia.model.Question;
import com.vertechxa.trivia.model.Score;
import com.vertechxa.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;

    private TextView questionCounterTextView;

    private Button trueButton;

    private Button falseButton;

    private ImageButton prevButton;

    private ImageButton nextButton;

    private int currentQuestionIndex = 0;

    private List<Question> questionList;

    private int scoreCounter = 0;

    private Score score;

    private TextView scoreTextView;

    private TextView highestScoreView;

    private ImageView splashImage;

    private Prefs prefs;

    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareButton = findViewById(R.id.shareButton);

        prefs = new Prefs(MainActivity.this);

        score = new Score();

        splashImage = findViewById(R.id.splashImage);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);

        questionTextView = findViewById(R.id.question_textview);
        questionCounterTextView = findViewById(R.id.counter_text);
        scoreTextView = findViewById(R.id.score_text);
        highestScoreView = findViewById(R.id.highest_score);

        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(scoreCounter)));
        highestScoreView.setText(MessageFormat.format("Highest Score: {0}", prefs.getHighScore()));

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        currentQuestionIndex = prefs.getState();

        Log.d("onPause Create", "currentQuestionIndex : " + currentQuestionIndex);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinish(List<Question> questionArrayList) {
                getSupportActionBar().show();
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(currentQuestionIndex + " / " + questionArrayList.size());
                Log.d("Main", "processFinish: " + questionArrayList);
            }
        });


        Log.d("Main", "onCreate: " + questionList);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.prev_button:

                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:

                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_button:

                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:

                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.shareButton:
                shareScore();
                break;
        }
    }

    private void shareScore() {

        String message = "My current score is " + score.getScore() + " and My highest score is " + prefs.getHighScore();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "I am playing Trivia");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    public void checkAnswer(boolean userChooseCorrect) {

        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            fadeView();
            addPoint();
            toastMessageId = R.string.correct_answer;
        } else {
            shakeAnimation();
            deductPoint();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }

    private void addPoint() {
        scoreCounter = scoreCounter + 100;
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(scoreCounter)));
    }

    private void deductPoint() {
        scoreCounter = scoreCounter - 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(scoreCounter)));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(scoreCounter)));
        }

    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + " / " + questionList.size());
    }

    private void shakeAnimation() {

        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeView() {

        CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    public void showAlertBox(Context mContext, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle(mContext.getResources().getString(R.string.app_name));
        alertDialog.setIcon(R.mipmap.ic_launcher);
        // Setting Dialog Message
        alertDialog.setMessage(msg);
        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onPause() {

        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        Log.d("onPause", "Current Index: " + currentQuestionIndex);
        super.onPause();
    }
}
