package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.mapper.PersonDomainRestMapper;
import com.mpumd.poc.person.sb.rest.resource.GenderChangeResource;
import com.mpumd.poc.person.sb.rest.resource.PersonCreatedResponse;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping(
        // TODO good or bad way to manage version using vendor style
//        produces = "application/vnd.mpu.person.v1+json",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Slf4j
public class PersonRestController {

    private final PersonAppSvc personAppSvc;

    @Operation(
            summary = "Register a new person",
            description = "A person can be register only one time",
            tags = {"Person"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "person is created"),
            @ApiResponse(responseCode = "409", description = "person already exist")
    })
    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonCreatedResponse register(@RequestBody PersonRegisterResource cmd) {
        var uuid = personAppSvc.register(PersonDomainRestMapper.toDomain(cmd));
        return new PersonCreatedResponse(uuid);
    }

    @Operation(
            summary = "set a new gender for a person",
            description = "A person can change of sex during their life",
            tags = {"Person"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "succes")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/person/{id}/gender")
    public void changeSex(@PathVariable UUID id, @RequestBody GenderChangeResource genderChangeResource) {
        personAppSvc.changeSex(PersonDomainRestMapper.toDomain(id, genderChangeResource));
    }
}
