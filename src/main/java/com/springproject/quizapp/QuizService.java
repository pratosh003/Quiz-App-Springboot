package com.springproject.quizapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDAO quizDAO;
    @Autowired
    QuestionDAO questionDAO;

    public ResponseEntity<String> createQuestions(String category, int numQ, String title) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);

        List<Question> questions = questionDAO.getRandomQuestionsByCategory(category, numQ);
        quiz.setQuestions(questions);

        quizDAO.save(quiz);

        return new ResponseEntity<>("quiz created!", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestions(Integer id) {
        Optional<Quiz> quiz = quizDAO.findById(id);
        List<Question> questionsFromDB = quiz.get().getQuestions();
        List<QuestionWrapper> questionsToUser = new ArrayList<>();

        for(Question q: questionsFromDB){
            questionsToUser.add(new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.option1, q.option2, q.option3, q.option4));
        }

        return new ResponseEntity<>(questionsToUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer quizId, List<Response> responses) {
        Optional<Quiz> quiz = quizDAO.findById(quizId);
        List<Question> questions = quiz.get().getQuestions();
        int result = 0, i = 0;
        for(Response r: responses){
            if(r.response.equals(questions.get(i).getRightAnswer()))
                result++;
            i++;
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
