package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.sb.application.PersonAppSvc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
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
    
    private final PersonAppSvc personAppSvc;

    @Operation(
            summary = "enregister une personne",
            description = "Une personne peut-etre enregister une et une seule fois",
            tags = {"Person"}
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload for create a person resource",
            required = true,
            content = @Content(schema = @Schema(example = """
                    {
                      "firstName": "John",
                      "lastName": "Rambo",
                      "gender": "Male",
                      "birthDate": "1947-07-06T00:00:00Z",
                      "birthPlace": "Bowie, Arizona, USA",
                      "nationality": "American"
                    }
                    """)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Succès"),
            @ApiResponse(responseCode = "409", description = "la personne existe déjà")
    })
    @PostMapping("/person")
    public void register(@RequestBody PersonRegistrationCommand cmd) {
        personAppSvc.register(cmd);
    }
}
