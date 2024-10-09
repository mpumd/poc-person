package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PersonJpaAdapter implements PersonPersistanceRepository {

    private final PersonSpringRepo personSpringRepo;

    @Override
    public boolean isExist(PersonSearchQuery person) {
        return false;
    }

    @Override
    public void push(Person person) {
//        personSpringRepo.save(perso);
    }
}
