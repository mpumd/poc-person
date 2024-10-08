package com.mpumd.poc.person.sb.application;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonAppSvc extends com.mpumd.poc.person.application.PersonApplicationService {
    public PersonAppSvc(@NonNull PersonPersistanceRepository personPersistanceRepository) {
        super(personPersistanceRepository);
    }

    // TODO maybe put the security check here ?
    @Transactional
    @Override
    public void register(PersonRegistrationCommand cmd) {
        super.register(cmd);
    }
}
