package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PersonSpringRepo extends CrudRepository<PersonEntity, UUID> {
}