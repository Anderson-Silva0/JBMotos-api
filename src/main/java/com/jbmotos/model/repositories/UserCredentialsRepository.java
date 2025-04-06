package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    UserDetails findByLogin(String login);

    UserCredentials findCredentialsByLogin(String login);
}
