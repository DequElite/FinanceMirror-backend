package dequelite.financemirror_backend.domain.password_recovery;

import dequelite.financemirror_backend.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordRecoveryRepo extends JpaRepository<PasswordRecoveryEntity, UUID> {
    Optional<PasswordRecoveryEntity> findByToken(String token);
    Optional<PasswordRecoveryEntity> findByUser(UserEntity user);
}
