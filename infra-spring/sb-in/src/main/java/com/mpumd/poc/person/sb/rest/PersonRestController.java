package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.mapper.PersonDomainRestMapper;
import com.mpumd.poc.person.sb.rest.resource.PersonCreatedResponse;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(
        // TODO good or bad way to manage version using vendor style
//        produces = "application/vnd.mpu.person.v1+json",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class PersonRestController {

    private final PersonAppSvc personAppSvc;

    @Operation(
            summary = "Register a new person",
            description = "A person can be register only one time",
            tags = {"Person"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Succès"),
            @ApiResponse(responseCode = "409", description = "la personne existe déjà")
    })
    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonCreatedResponse register(@RequestBody PersonRegisterResource cmd) {
        var uuid = personAppSvc.register(PersonDomainRestMapper.toDomain(cmd));
        return new PersonCreatedResponse(uuid);
    }
}
