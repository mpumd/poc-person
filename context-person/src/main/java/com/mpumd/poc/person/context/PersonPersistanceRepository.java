package com.mpumd.poc.person.context;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;

public interface PersonPersistanceRepository {

//    Person pull(UUID uuid);

    boolean isExist(PersonSearchQuery person);

    void push(Person person);

}
