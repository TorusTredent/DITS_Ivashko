package com.example.dits.controllers;

import com.example.dits.dto.TestStatistic;
import com.example.dits.service.impl.StatisticServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "ADMIN")
class AdminStatisticControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StatisticServiceImpl statisticServiceImpl;

    private final static String GET_TESTS_STAT_PATH = "/admin/getTestsStatistic";
    private final static String REMOVE_STAT_BY_ID_PATH = "/admin/adminStatistic/removeStatistic/byId";
    private final static String REMOVE_STAT_ALL_PATH = "/admin/adminStatistic/removeStatistic/all";
    private final static String REDIRECT_ADMIN_STAT_PATH = "/admin/adminStatistic";

    @Test
    void getTestsStatistics() throws Exception {
        List<TestStatistic> statistics = initTestStatistic();

        when(statisticServiceImpl.getListOfTestsWithStatisticsByTopic(1)).thenReturn(statistics);

        mockMvc.perform(get(GET_TESTS_STAT_PATH)
                        .with(csrf())
                        .param("id", "1"))
                .andDo(print())
                .andExpect(content().json(convertToJson(statistics)))
                .andExpect(status().isOk());
    }

    @Test
    void removeStatisticByUserId() throws Exception {
        doNothing().when(statisticServiceImpl).removeStatisticByUserId(1);

        mockMvc.perform(get(REMOVE_STAT_BY_ID_PATH)
                        .with(csrf())
                        .param("id", "1"))
                .andDo(print())
                .andExpect(content().string("success"))
                .andExpect(status().isOk());
    }

    @Test
    void removeAllStatistic() throws Exception {
        doNothing().when(statisticServiceImpl).deleteAll();

        mockMvc.perform(get(REMOVE_STAT_ALL_PATH)
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl(REDIRECT_ADMIN_STAT_PATH))
                .andExpect(status().is3xxRedirection());
    }


    private List<TestStatistic> initTestStatistic() {
        return List.of(new TestStatistic("stat1", 1, 1),
                new TestStatistic("stat2", 2, 2));
    }

    private String convertToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}