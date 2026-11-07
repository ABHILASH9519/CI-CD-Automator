package com.stalin.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryDetailsControllerTest extends AbstractTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getProductsList() throws Exception {
        String uri = "/trends?placeid=1&count=5";
        int status = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        // Without provider credentials, the endpoint must report unavailable
        // instead of returning fabricated trend data.
        assertEquals(503, status);
    }

    @Test
    public void rejectsInvalidTrendCount() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=1&count=0")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    public void rejectsNonNumericPlaceId() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=world&count=5")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }
}
