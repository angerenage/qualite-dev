package org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);
}
