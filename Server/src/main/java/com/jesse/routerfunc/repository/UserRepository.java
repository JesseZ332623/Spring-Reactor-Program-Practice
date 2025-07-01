package com.jesse.routerfunc.repository;

import com.jesse.routerfunc.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository
    extends ReactiveCrudRepository<UserEntity, Long> {}
