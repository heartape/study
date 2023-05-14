package com.heartape.controller;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件下载单元测试
 */
@SpringBootTest
class DownloadControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void before(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @SneakyThrows
    @Test
    void excel() {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/download/excel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(this::downloadResultHandler);
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadResultHandler(MvcResult result){
        MockHttpServletResponse response = result.getResponse();
        String disposition = response.getHeader("Content-Disposition");
        String filenameUrl = StringUtils.hasText(disposition) ? disposition.substring(disposition.indexOf('=') + 1) : "";
        String filename = URLDecoder.decode(filenameUrl, StandardCharsets.UTF_8);

        String dir = System.getProperty("user.dir");
        File directory = new File(dir + "\\file");
        File file = new File(dir + "\\file\\" + filename);

        if (directory.exists()){
            if (file.exists()){
                file.delete();
            }
        } else {
            directory.mkdirs();
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getContentAsByteArray());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            IOUtils.copy(byteArrayInputStream, fileOutputStream);
        }
    }
}