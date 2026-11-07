package com.stalin.demo;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryDetailsControllerTest extends AbstractTest {

    @Test
    void rejectsInvalidTrendCount() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=1&count=0")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    void rejectsTrendCountAboveProviderLimit() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=1&count=21")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    void rejectsNonNumericTrendCount() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=1&count=many")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    void rejectsNonNumericPlaceId() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=world&count=5")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    void rejectsNonPositivePlaceId() throws Exception {
        int status = mvc.perform(MockMvcRequestBuilders.get("/trends?placeid=0&count=5")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getStatus();

        assertEquals(400, status);
    }

    @Test
    void rootEndpointReturnsProjectDescription() throws Exception {
        String body = mvc.perform(MockMvcRequestBuilders.get("/"))
                .andReturn().getResponse().getContentAsString();

        assertEquals("This is the sample DevOps Project", body);
    }
}
