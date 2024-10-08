package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

//interface PersonSpringRepo extends CrudRepository<PersonEntity, Long> {
//}

@Repository
public class PersonJpaAdapter implements PersonPersistanceRepository {

    @Override
    public boolean isExist(PersonSearchQuery person) {
        return false;
    }

    @Override
    public void push(Person person) {

    }
}
