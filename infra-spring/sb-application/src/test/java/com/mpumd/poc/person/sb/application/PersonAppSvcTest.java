package com.mpumd.poc.person.sb.application;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class PersonAppSvcTest {

    @Mock
    PersonPersistanceRepository persistanceRepository;

    @InjectMocks
    @Spy
    PersonAppSvc personAppSvc;

    @Mock
    PersonRegistrationCommand cmd;

    @Test
    void shouldCallSuperRegister() {
        var captor = ArgumentCaptor.forClass(PersonRegistrationCommand.class);
        doNothing().when((PersonApplicationService)personAppSvc).register(captor.capture());

        personAppSvc.register(cmd);

        assertEquals(captor.getValue(), cmd);
    }
}