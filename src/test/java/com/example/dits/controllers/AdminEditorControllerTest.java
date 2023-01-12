package com.example.dits.controllers;

import com.example.dits.dto.UserInfoDTO;
import com.example.dits.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
class AdminEditorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private final static String GET_ALL_PATH = "/admin/get/user/all";
    private final static String DELETE_USER_PATH = "/admin/delete/user";
    private final static String ADD_USER_PATH = "/admin/add/user";
    private final static String UPDATE_USER_PATH = "/admin/update/user";
    private final static String USER_ROLE = "USER_ROLE";

    @Test
    void getAll() throws Exception {
        List<UserInfoDTO> users = initUserList();
        when(userService.getAllUsers()).thenReturn(users);
        String convertToJson = convertToJson(users);
        mockMvc.perform(get(GET_ALL_PATH)
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson));
    }

    @Test
    void deleteById() throws Exception {
        doNothing().when(userService).deleteById(1);
        mockMvc.perform(delete(DELETE_USER_PATH)
                        .with(csrf())
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addUser() throws Exception {
        UserInfoDTO userInfoDTO = initUserInfo();

        UserInfoDTO userInfoDTOWithId = initUserInfo();
        userInfoDTOWithId.setUserId(3);

        List<UserInfoDTO> users = new ArrayList<>(initUserList());
        users.add(userInfoDTOWithId);

        when(userService.save(userInfoDTO)).thenReturn(userInfoDTOWithId);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.post(ADD_USER_PATH)
                        .with(csrf())
                        .content(convertToJson(userInfoDTO))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(users)));
    }

    @Test
    void updateUser() throws Exception {
        UserInfoDTO userInfoDTO = initUserInfo();
        UserInfoDTO userInfoDTOWithId = initUserInfo();
        userInfoDTOWithId.setUserId(1);

        List<UserInfoDTO> users = new ArrayList<>(initUserList());
        users.add(userInfoDTOWithId);

        when(userService.update(userInfoDTO)).thenReturn(userInfoDTOWithId);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(put(UPDATE_USER_PATH)
                        .with(csrf())
                        .content(convertToJson(userInfoDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(users)));
    }


    private List<UserInfoDTO> initUserList() {
        return List.of(UserInfoDTO.builder()
                        .userId(1)
                        .role(USER_ROLE)
                        .build(),
                UserInfoDTO.builder()
                        .userId(2)
                        .role(USER_ROLE)
                        .build());
    }

    private UserInfoDTO initUserInfo() {
        return UserInfoDTO.builder()
                .role(USER_ROLE)
                .login("login")
                .build();
    }

    private String convertToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}