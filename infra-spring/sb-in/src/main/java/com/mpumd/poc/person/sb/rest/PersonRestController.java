package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.sb.application.PersonAppSvc;
import com.mpumd.poc.person.sb.rest.resource.PersonRegisterResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(
        produces = "application/vnd.mpu.person.v1+json",
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
    public void register(@RequestBody PersonRegisterResource cmd) {
//        personAppSvc.register(cmd);
    }
}
