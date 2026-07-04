package nus.iss.wellness.backend.repository;

import org.springframework.stereotype.Repository;
import nus.iss.wellness.backend.model.User;
import nus.iss.wellness.backend.model.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

//Author: Cecil

@Repository
public interface UserProfileRepository extends JpaRepository <UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);

}
