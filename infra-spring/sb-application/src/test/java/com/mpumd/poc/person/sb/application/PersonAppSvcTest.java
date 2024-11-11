package com.mpumd.poc.person.sb.application;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.command.GenderChangeCommand;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonAppSvcTest {

    @Mock
    PersonPersistanceRepository persistanceRepository;

    @InjectMocks
    @Spy
    PersonAppSvc personAppSvc;

    @Mock
    PersonRegistrationCommand registerCmd;
    @Mock
    GenderChangeCommand genderChangeCommand;

    @Test
    void shouldCallSuperRegister() {
        var captor = ArgumentCaptor.forClass(PersonRegistrationCommand.class);
        UUID id = UUID.randomUUID();
        doReturn(id).when((PersonApplicationService)personAppSvc).register(captor.capture());

        var result = personAppSvc.register(registerCmd);

        assertEquals(captor.getValue(), registerCmd);
        assertEquals(id, result);
    }

    @Test
    void shouldCallSuperChangeSex() {
        var captor = ArgumentCaptor.forClass(GenderChangeCommand.class);
        doNothing().when((PersonApplicationService)personAppSvc).changeSex(captor.capture());

        personAppSvc.changeSex(genderChangeCommand);

        assertEquals(captor.getValue(), genderChangeCommand);
    }

}