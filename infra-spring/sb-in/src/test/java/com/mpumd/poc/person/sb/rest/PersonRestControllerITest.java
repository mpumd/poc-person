package com.mpumd.poc.person.sb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.mapper.PersonDomainRestMapper;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PersonRestControllerITest {

    @MockBean
    PersonAppSvc appService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    PersonRegistrationCommand command;

    String payload = """
            {
              "firstName": "John",
              "lastName": "Rambo",
              "gender": "Male",
              "birthDate": "1947-07-06T00:00:00Z",
              "birthPlace": "Bowie, Arizona, USA",
              "nationality": "US"
            }
            """;

    @Test
    void register201() throws Exception {
        UUID id = UUID.randomUUID();

        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            var resourceCaptor = ArgumentCaptor.forClass(PersonRegisterResource.class);
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(resourceCaptor.capture())).thenReturn(command);
            given(appService.register(command)).willReturn(id);

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

    @Test
    void register409() throws Exception {

        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(any(PersonRegisterResource.class))).thenReturn(command);

            given(appService.register(command)).willThrow(new PersonAlreadyExistException("person already exist"));

            mockMvc.perform(post("/person")
                            .contentType("application/json")
                            .content(payload)
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").value(PersonAlreadyExistException.class.getSimpleName()))
                    .andExpect(jsonPath("$.title").value("person already exist"))
                    .andExpect(jsonPath("$.status").value(409));
        }
    }
}