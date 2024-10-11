package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonSpringRepo extends JpaRepository<PersonEntity, UUID> {
}