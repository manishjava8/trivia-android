package com.vertechxa.trivia.data;

import com.vertechxa.trivia.model.Question;

import java.util.List;

/**
 * @author robert
 */
public interface AnswerListAsyncResponse {

    void processFinish(List<Question> questionList);
}
