package com.mpumd.poc.person.sb.application;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PersonAppSvc extends PersonApplicationService {

    public PersonAppSvc(PersonPersistanceRepository personPersistanceRepository) {
        super(personPersistanceRepository);
    }

    // TODO maybe put the security check here ?
    @Transactional
    @Override
    public UUID register(PersonRegistrationCommand cmd) {
        return super.register(cmd);
    }
}
