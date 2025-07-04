package com.jesse.routerfunc.repository;

import com.jesse.routerfunc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserRepository
    extends ReactiveCrudRepository<UserEntity, Long>
{
    /** 查询用户表中的所有用户名。 */
    @Query(value = "SELECT user_name FROM users")
    Flux<String> findAllUserName();
}
