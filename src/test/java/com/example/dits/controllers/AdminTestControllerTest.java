package com.example.dits.controllers;

import com.example.dits.dto.*;
import com.example.dits.entity.Question;
import com.example.dits.entity.Topic;
import com.example.dits.mapper.QuestionMapper;
import com.example.dits.mapper.TestMapper;
import com.example.dits.service.QuestionService;
import com.example.dits.service.RoleService;
import com.example.dits.service.TestService;
import com.example.dits.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "ADMIN")
class AdminTestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TopicService topicService;

    @MockBean
    TestService testService;

    @MockBean
    QuestionService questionService;

    @MockBean
    TestMapper testMapper;

    @MockBean
    QuestionMapper questionMapper;

    @MockBean
    RoleService roleService;

    private static final String ADD_TOPIC_PATH = "/admin/addTopic";
    private static final String EDIT_TOPIC_PATH = "/admin/editTopic";
    private static final String REMOVE_TOPIC_PATH = "/admin/removeTopic";
    private static final String ADD_TEST_PATH = "/admin/addTest";
    private static final String EDIT_TEST_PATH = "/admin/editTest";
    private static final String REMOVE_TEST_PATH = "/admin/removeTest";
    private static final String ADD_QUESTION_PATH = "/admin/addQuestion";
    private static final String EDIT_QUESTION_ANSWER_PATH = "/admin/editQuestionAnswers";
    private static final String REMOVE_QUESTION_PATH = "/admin/removeQuestion";
    private static final String GET_TOPICS_PATH = "/admin/getTopics";
    private static final String GET_TESTS_PATH = "/admin/getTests";
    private static final String GET_ANSWERS_PATH = "/admin/getAnswers";
    private static final String GET_ROLES_PATH = "/admin/getRoles";

    @Test
    void addTopic() throws Exception {
        TopicDTO topicDTO = initTopicDTO();
        Topic topic = new Topic(topicDTO.getTopicName());
        List<TopicDTO> topics = new ArrayList<>(initTopics());
        topics.add(topicDTO);

        doNothing().when(topicService).save(topic);
        when(topicService.findAll()).thenReturn(topics);

        mockMvc.perform(post(ADD_TOPIC_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(topicDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(topics)));
    }

    @Test
    void editTopic() throws Exception {
        TopicDTO topicDTO = initTopicDTO();
        List<TopicDTO> topics = new ArrayList<>(initTopics());
        topics.add(topicDTO);

        doNothing().when(topicService).updateTopicName(topicDTO.getTopicId(), topicDTO.getTopicName());
        when(topicService.findAll()).thenReturn(topics);

        mockMvc.perform(put(EDIT_TOPIC_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(topicDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(topics)));
    }

    @Test
    void removeTopic() throws Exception {
        List<TopicDTO> topics = new ArrayList<>(initTopics());
        topics.remove(1);

        doNothing().when(topicService).removeTopicByTopicId(1);
        when(topicService.findAll()).thenReturn(topics);

        mockMvc.perform(delete(REMOVE_TOPIC_PATH)
                        .with(csrf())
                        .param("topicId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(topics)));
    }

    @Test
    void addTest() throws Exception {
        Topic topic = initTopic();
        TestInfoDTO testInfoDTO = initTestInfoDto();
        com.example.dits.entity.Test test = com.example.dits.entity.Test.builder()
                .name(testInfoDTO.getName())
                .description(testInfoDTO.getDescription())
                .topic(topic)
                .build();

        when(topicService.getTopicByTopicId(1)).thenReturn(topic);
        doNothing().when(testService).save(test);

        mockMvc.perform(post(ADD_TEST_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(testInfoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void editTest() throws Exception {
        TestInfoDTO test = initTestInfoDto();
        Topic topic = initTopic();

        doNothing().when(testService).update(test.getTestId(), test.getName(), test.getDescription());
        when(topicService.getTopicByTopicId(1)).thenReturn(topic);

        mockMvc.perform(put(EDIT_TEST_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(test)))
                .andExpect(status().isOk());
    }

    @Test
    void removeTest() throws Exception {
        Topic topic = initTopic();

        doNothing().when(testService).removeTestByTestId(1);
        when(topicService.getTopicByTopicId(1)).thenReturn(topic);

        mockMvc.perform(delete(REMOVE_TEST_PATH)
                        .with(csrf())
                        .param("testId", "1")
                        .param("topicId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void addQuestion() throws Exception {
        QuestionEditModel questionEditModel = initQuestionEditModel();
        Topic topic = initTopic();

        doNothing().when(questionService).addQuestion(questionEditModel);
        when(topicService.getTopicByTopicId(1)).thenReturn(topic);

        mockMvc.perform(post(ADD_QUESTION_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(questionEditModel)))
                .andExpect(status().isOk());
    }

    @Test
    void editQuestionAnswers() throws Exception {
        QuestionEditModel questionEditModel = initQuestionEditModel();
        Topic topic = initTopic();

        doNothing().when(questionService).editQuestion(questionEditModel);
        when(topicService.getTopicByTopicId(1)).thenReturn(topic);

        mockMvc.perform(put(EDIT_QUESTION_ANSWER_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(convertToJson(questionEditModel)))
                .andExpect(status().isOk());
    }

    @Test
    void removeQuestion() throws Exception {
        Topic topic = initTopic();

        doNothing().when(questionService).removeQuestionById(1);
        when(topicService.getTopicByTopicId(1)).thenReturn(topic);

        mockMvc.perform(delete(REMOVE_QUESTION_PATH)
                        .with(csrf())
                        .param("questionId", "1")
                        .param("topicId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTopicList() throws Exception {
        List<TopicDTO> topicDTOS = initTopics();

        when(topicService.findAll()).thenReturn(topicDTOS);

        mockMvc.perform(get(GET_TOPICS_PATH)
                        .with(csrf()))
                .andExpect(content().json(convertToJson(topicDTOS)));
    }

    @Test
    void getTestsWithQuestions() throws Exception {
        List<com.example.dits.entity.Test> tests = new ArrayList<>(initTests());
        TestWithQuestionsDTO testWithQuestionsDTO = TestWithQuestionsDTO.builder()
                .name("name")
                .testId(1)
                .description("desc")
                .build();
        List<TestWithQuestionsDTO> testWithQuestionsDTOS = List.of(testWithQuestionsDTO);

        when(testService.getTestsByTopic_TopicId(1)).thenReturn(tests);
        when(testMapper.convertToTestDTO(tests.get(0))).thenReturn(testWithQuestionsDTO);

        mockMvc.perform(get(GET_TESTS_PATH)
                        .with(csrf())
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(testWithQuestionsDTOS)));
    }

    @Test
    void getQuestionsWithAnswers() throws Exception {
        Question question = initQuestion();
        QuestionWithAnswersDTO questionWithAnswersDTO = QuestionWithAnswersDTO.builder()
                .questionId(1)
                .description("descrip")
                .build();

        when(questionService.getQuestionById(1)).thenReturn(question);
        when(questionMapper.convertToQuestionWithAnswersDTO(question)).thenReturn(questionWithAnswersDTO);

        mockMvc.perform(get(GET_ANSWERS_PATH)
                        .with(csrf())
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(questionWithAnswersDTO)));
    }

    @Test
    void getRoles() throws Exception {
        List<String> roles = List.of("USER", "ADMIN");
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get(GET_ROLES_PATH)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(roles)));
    }


    private List<TopicDTO> initTopics() {
        return List.of(new TopicDTO("topic1", 1),
                new TopicDTO("topic2", 2));
    }

    private TopicDTO initTopicDTO() {
        return new TopicDTO("topic3", 3);
    }

    private Topic initTopic() {
        return new Topic(1, "description", "topic1", List.of(com.example.dits.entity.Test.builder()
                .testId(1)
                .description("description")
                .name("testInfo")
                .build()));
    }

    private TestInfoDTO initTestInfoDto() {
        return new TestInfoDTO(1, "testInfo", "description", 1);
    }

    private QuestionEditModel initQuestionEditModel() {
        return new QuestionEditModel("qName", 1, 1, 1, List.of(initAnswerEditModel()));
    }

    private AnswerEditModel initAnswerEditModel() {
        return new AnswerEditModel(true, "aAnswer");
    }

    private List<com.example.dits.entity.Test> initTests() {
        return List.of(com.example.dits.entity.Test.builder()
                .testId(1)
                .description("description")
                .name("testInfo")
                .build());
    }

    private Question initQuestion() {
        return new Question(1, "description", new ArrayList<>(), initTests().get(0), new ArrayList<>());
    }

    private String convertToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}