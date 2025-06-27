package com.jesse.routerfunc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 用户实体类。
 */
@Data
@Table(name = "users")
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class UserEntity
{
    @Id
    @Column(value = "user_id")
    private @NonNull Long userId;

    @Column(value = "user_name")
    private @NonNull String userName;

    @Column(value = "password")
    private @NonNull String password;

    @Column(value = "full_name")
    private @NonNull String fullName;

    @Column(value = "telephone_number")
    private @NonNull String telephoneNumber;

    @Column(value = "email")
    private @NonNull String email;

    @Column(value = "register_datetime")
    private @NonNull
    LocalDateTime registerDate;
}
