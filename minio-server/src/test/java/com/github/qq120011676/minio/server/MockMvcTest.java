package com.github.qq120011676.minio.server;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

public class MockMvcTest {
    @Resource
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
}
