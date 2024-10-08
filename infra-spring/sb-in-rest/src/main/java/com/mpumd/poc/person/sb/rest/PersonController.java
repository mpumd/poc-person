package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class PersonController {

    private final PersonApplicationService PersonApplicationService;

    @Operation(
            summary = "enregister une personne",
            description = "Une personne peut-etre enregister une et une seule fois",
            tags = {"Person"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Succès"),
                    @ApiResponse(responseCode = "409", description = "la personne existe déjà")
            })
    @PostMapping("/person")
    public void register(@RequestBody PersonRegistrationCommand cmd) {
        PersonApplicationService.register(cmd);
    }
}
