package com.vertechxa.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vertechxa.trivia.controller.AppController;
import com.vertechxa.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author robert
 */
public class QuestionBank {

    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    List<Question> questionList = new ArrayList<Question>();

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Question question = new Question();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

//                                Log.d("JSON", "onResponse: " + response.getJSONArray(i).get(0));
//                                Log.d("JSON", "onResponse: " + response.getJSONArray(i).getBoolean(1));

                                questionList.add(question);

//                                Log.d("Hello", "onResponse: " + question);
                            } catch (JSONException j) {
                                j.printStackTrace();
                            }
                            Log.d("JSON 1", "onResponse: " + callBack);
                        }
                        Log.d("JSON 2", "onResponse: " + callBack);
                        if(callBack != null)
                            callBack.processFinish(questionList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().add2RequestQueue(jsonArrayRequest);

        return questionList;
    }
}
