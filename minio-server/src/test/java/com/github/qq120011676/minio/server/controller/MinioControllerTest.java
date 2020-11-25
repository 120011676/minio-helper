package com.github.qq120011676.minio.server.controller;

import com.github.qq120011676.minio.server.MockMvcTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

@SpringBootTest
public class MinioControllerTest extends MockMvcTest {
    @Test
    public void upload() throws Exception {
        var file = new File("/Users/say/Downloads/05G@C7$[3XQ7XVCCYYP7]W6.jpg");
        String result;
        try (var fin = new FileInputStream(file)) {
            result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/upload")
                    .file(new MockMultipartFile("file", file.getName(), Files.probeContentType(file.toPath()), fin)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print()).andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();
        }
        JSONObject jsonObject = new JSONObject(result);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/view/{0}", jsonObject.getString("filename"))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void uploadChinese() throws Exception {
        var file = new File("/Users/say/Downloads/中文.jpg");
        String result;
        try (var fin = new FileInputStream(file)) {
            result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/upload")
                    .file(new MockMultipartFile("file", file.getName(), Files.probeContentType(file.toPath()), fin)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print()).andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();
        }
        JSONObject jsonObject = new JSONObject(result);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/download/{0}", jsonObject.getString("filename"))).andExpect(MockMvcResultMatchers.status().isOk()).andDo(result1 -> {
            System.out.println("===header==");
            System.out.println(result1.getResponse().getHeaderNames());
        });
    }
}
