package com.example.dits.controllers;

import com.example.dits.dto.TestInfoDTO;
import com.example.dits.service.TestService;
import com.example.dits.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", authorities = {"ROLE_USER"})
class ChooseTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TopicService topicService;

    @MockBean
    TestService testService;

    private static final String CHOOSE_TEST_PATH = "/user/chooseTest";
    private static final String CHOOSE_THEME_PATH = "/user/chooseTheme";

    @Test
    void userPage() throws Exception {
        when(topicService.getTopicsWithQuestions()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(CHOOSE_TEST_PATH)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getTestNameAndDescriptionFromTopic() throws Exception {
        when(testService.getTestsByTopicName("someTopic")).thenReturn(new ArrayList<TestInfoDTO>());

        mockMvc.perform(get(CHOOSE_THEME_PATH))
                .andExpect(status().isOk());
    }
}