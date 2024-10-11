package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.mapper.DomainJPAMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PersonJpaAdapter implements PersonPersistanceRepository {

    private final PersonSpringRepo personSpringRepo;

    @Override
    public boolean isExist(PersonSearchQuery queryPerson) {
        var example = Example.of(
                DomainJPAMapper.toJpa(queryPerson),
                ExampleMatcher.matchingAny()
                        .withIgnoreCase("firstName", "lastName")
        );
        return personSpringRepo.findOne(example).isPresent();
    }

    @Override
    public void push(Person person) {
        personSpringRepo.save(DomainJPAMapper.toJpa(person));
    }
}
