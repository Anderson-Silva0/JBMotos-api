package com.jbmotos.model.repositories;

import com.jbmotos.model.entity.CredenciaisUsuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface CredenciaisUsuariosRepository extends JpaRepository<CredenciaisUsuarios, Long> {

    UserDetails findByLogin(String login);

    CredenciaisUsuarios findCredenciaisByLogin(String login);
}
