package com.mpumd.poc.person.sb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.application.exception.PersonNotFoundException;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.mapper.PersonDomainRestMapper;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.RegisterPersonResource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(properties = {
        "spring.jackson.deserialization.adjust-dates-to-context-time-zone=false",
        // usefull for ISO8601 date like 1965-02-15T02:37:00-07:00
})
class PersonRestControllerITest {

    @MockitoBean
    PersonAppSvc appService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    PersonRegistrationCommand registerCmd;

    @Mock
    GenderChangeCommand genderChangeCmd;

    String registerPayload = """
            {
              "firstName": "John",
              "lastName": "Rambo",
              "gender": "Male",
              "birthDate": "1947-07-06T05:00:00+01:00",
              "birthPlace": "Bowie, Arizona, USA",
              "nationality": "US"
            }
            """;

    String changeSexPayload = """
            {
              "gender": "male",
              "changeDate": "1987-02-21T02:42:00"
            }
            """;

    UUID id = UUID.randomUUID();

    @Test
    void register201() throws Exception {
        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            var resourceCaptor = ArgumentCaptor.forClass(RegisterPersonResource.class);
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(resourceCaptor.capture())).thenReturn(registerCmd);
            given(appService.register(registerCmd)).willReturn(id);

            mockMvc.perform(post("/person")
                            .contentType("application/json")
                            .content(registerPayload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(id.toString()));

            JSONAssert.assertEquals(
                    registerPayload,
                    objectMapper.writeValueAsString(resourceCaptor.getValue()),
                    JSONCompareMode.STRICT
            );
        }
    }

    @Test
    void register409() throws Exception {
        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(any(RegisterPersonResource.class))).thenReturn(registerCmd);

            given(appService.register(registerCmd)).willThrow(new PersonAlreadyExistException("person already exist"));

            mockMvc.perform(post("/person")
                            .contentType("application/json")
                            .content(registerPayload)
                    )
                    .andExpect(status().is(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.title").value(PersonAlreadyExistException.class.getSimpleName()))
                    .andExpect(jsonPath("$.detail").value("person already exist"))
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Test
    void register400() throws Exception {
        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(any(RegisterPersonResource.class))).thenReturn(registerCmd);

            given(appService.register(registerCmd)).willThrow(new IllegalArgumentException("field value is wrong"));

            mockMvc.perform(post("/person")
                            .contentType("application/json")
                            .content(registerPayload)
                    )
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.title").value(IllegalArgumentException.class.getSimpleName()))
                    .andExpect(jsonPath("$.detail").value("field value is wrong"))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Test
    void changeSex201() throws Exception {
        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            var resourceCaptor = ArgumentCaptor.forClass(GenderChangeResource.class);
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(eq(id), resourceCaptor.capture())).thenReturn(genderChangeCmd);
            doNothing().when(appService).changeSex(genderChangeCmd);

            mockMvc.perform(post("/person/" + id + "/gender")
                            .contentType("application/json")
                            .content(changeSexPayload))
                    .andExpect(status().isCreated());

            JSONAssert.assertEquals(
                    changeSexPayload,
                    objectMapper.writeValueAsString(resourceCaptor.getValue()),
                    JSONCompareMode.STRICT
            );
        }
    }

    @Test
    void changeSex422() throws Exception {
        try (var mapperMock = mockStatic(PersonDomainRestMapper.class)) {
            mapperMock.when(() -> PersonDomainRestMapper.toDomain(eq(id), any(GenderChangeResource.class))).thenReturn(genderChangeCmd);
            doThrow(new PersonNotFoundException("person not found with id " + id)).when(appService).changeSex(genderChangeCmd);

            mockMvc.perform(post("/person/" + id + "/gender")
                            .contentType("application/json")
                            .content(changeSexPayload))
                    .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                    .andExpect(jsonPath("$.title").value(PersonNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("$.detail").value("person not found with id " + id))
                    .andExpect(jsonPath("$.status").value(422));

        }
    }
}