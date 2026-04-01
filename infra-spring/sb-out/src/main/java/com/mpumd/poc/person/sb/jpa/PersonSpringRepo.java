package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.sb.jpa.entity.PersonJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonSpringRepo extends JpaRepository<PersonJPAEntity, UUID> {
}