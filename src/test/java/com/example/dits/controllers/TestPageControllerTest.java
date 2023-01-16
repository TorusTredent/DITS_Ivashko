package com.example.dits.controllers;

import com.example.dits.dto.AnswerDTO;
import com.example.dits.dto.QuestionDTO;
import com.example.dits.dto.StatisticDTO;
import com.example.dits.dto.UserInfoDTO;
import com.example.dits.service.AnswerService;
import com.example.dits.service.QuestionService;
import com.example.dits.service.TestService;
import com.example.dits.service.impl.StatisticServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"ROLE_USER"})
class TestPageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TestService testService;

    @MockBean
    QuestionService questionService;

    @MockBean
    AnswerService answerService;

    @MockBean
    StatisticServiceImpl statisticServiceImpl;

    private static final String GO_TEST_PATH = "/user/goTest";
    private static final String NEXT_TEST_PAGE_PATH = "/user/nextTestPage";
    private static final String RESULT_PAGE_PATH = "/user/resultPage";

    @Test
    void goTest() throws Exception {
        com.example.dits.entity.Test test = initTest();
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        questionDTOS.add(initQuestionDTO());
        List<AnswerDTO> answerDTOS = initAnswerDTOS();

        when(testService.getTestByTestId(1)).thenReturn(test);
        when(questionService.getQuestionsByTest(test)).thenReturn(questionDTOS);
        when(answerService.getAnswersFromQuestionList(questionDTOS, 0)).thenReturn(answerDTOS);
        when(questionService.getDescriptionFromQuestionList(questionDTOS, 0)).thenReturn(questionDTOS.get(0).getDescription());

        mockMvc.perform(get(GO_TEST_PATH)
                .with(csrf())
                .param("testId", "1")
                .param("theme", "some theme"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void nextTestPage() throws Exception {
        String questionDescription = "some description";
        boolean isCorrect = true;
        int questionNumber = 1;

        List<QuestionDTO> questionList = List.of(initQuestionDTO());
        List<AnswerDTO> answers = new ArrayList<>();
        List<Integer> answeredQuestion = new ArrayList<>();
        UserInfoDTO user = initUserInfoDTO();

        List<StatisticDTO> statisticList = new ArrayList<>();
        statisticList.add(StatisticDTO.builder()
                .questionId(questionList.get(0).getQuestionId())
                .username(user.getLogin())
                .isCorrect(isCorrect)
                .build());

        when(answerService.isRightAnswer(answeredQuestion, questionList, questionNumber)).thenReturn(isCorrect);
        when(answerService.getAnswersFromQuestionList(questionList, questionNumber)).thenReturn(answers);
        when(questionService.getDescriptionFromQuestionList(questionList, questionNumber)).thenReturn(questionDescription);

        mockMvc.perform(MockMvcRequestBuilders.get(NEXT_TEST_PAGE_PATH)
                        .sessionAttr("questions", questionList)
                        .sessionAttr("questionNumber", questionNumber)
                        .sessionAttr("user", user)
                        .sessionAttr("statistics", statisticList))
                .andExpect(status().isOk());
    }

    @Test
    void testStatistic() throws Exception {
        String topicName = "name";
        String testName = "name";
        int percentageOfCorrect = 100;
        int quantityOfRightAnswers = 10;
        int questionSize = 12;
        int questionNumber = 1;
        boolean isCorrect = true;

        List<Integer> answeredQuestion = new ArrayList<>();
        List<QuestionDTO> questionList = List.of(initQuestionDTO());
        UserInfoDTO user = initUserInfoDTO();
        List<StatisticDTO> statisticList = new ArrayList<>();
        statisticList.add(StatisticDTO.builder()
                .questionId(questionList.get(questionNumber - 1).getQuestionId())
                .username(user.getLogin())
                .isCorrect(isCorrect)
                .build());

        when(answerService.isRightAnswer(answeredQuestion, questionList, questionNumber)).thenReturn(isCorrect);

        mockMvc.perform(MockMvcRequestBuilders.get(RESULT_PAGE_PATH)
                        .sessionAttr("topicName", topicName)
                        .sessionAttr("testName", testName)
                        .sessionAttr("percentageOfCorrect", percentageOfCorrect)
                        .sessionAttr("quantityOfRightAnswers", quantityOfRightAnswers)
                        .sessionAttr("questionSize", questionSize)
                        .sessionAttr("questions", questionList)
                        .sessionAttr("user", user)
                        .sessionAttr("statistics", statisticList))
                .andExpect(status().is3xxRedirection());
    }

    private com.example.dits.entity.Test initTest() {
        return com.example.dits.entity.Test.builder()
                .testId(1)
                .description("description")
                .name("testInfo")
                .build();
    }

    private QuestionDTO initQuestionDTO() {
        return new QuestionDTO(1, "description");
    }

    private List<AnswerDTO> initAnswerDTOS() {
        return List.of(initAnswerDTO());
    }

    private AnswerDTO initAnswerDTO() {
        return AnswerDTO.builder()
                .answerId(1)
                .correct(true)
                .description("description")
                .build();
    }

    private UserInfoDTO initUserInfoDTO() {
        return new UserInfoDTO(1, "firstName", "lastName", "user", "USER", "somePassword");
    }
}