package com.mpumd.poc.person.sb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootApplication
class Runner {
}

@WebMvcTest
class PersonRestControllerITest {

    @MockBean
    PersonAppSvc service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void OK_register() throws Exception {
        PersonRegistrationCommand command = mock();
        UUID id = UUID.randomUUID();
        var payload = """
                {
                  "firstName": "John",
                  "lastName": "Rambo",
                  "gender": "Male",
                  "birthDate": "1947-07-06T00:00:00Z",
                  "birthPlace": "Bowie, Arizona, USA",
                  "nationality": "US"
                }
                """;

        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            var resourceCaptor = ArgumentCaptor.forClass(PersonRegisterResource.class);
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(resourceCaptor.capture())).thenReturn(command);
            given(service.register(command)).willReturn(id);

            mockMvc.perform(post("/person")
                            .contentType("application/json")
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").value(id.toString()));

            JSONAssert.assertEquals(
                    payload,
                    objectMapper.writeValueAsString(resourceCaptor.getValue()),
                    JSONCompareMode.STRICT
            );
        }
    }
}