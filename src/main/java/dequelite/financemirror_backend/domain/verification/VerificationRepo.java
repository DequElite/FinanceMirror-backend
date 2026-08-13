package dequelite.financemirror_backend.domain.verification;

import dequelite.financemirror_backend.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationRepo extends JpaRepository<VerificationEntity, UUID> {
    Optional<VerificationEntity> findByToken(String token);
    Optional<VerificationEntity> findByUser(UserEntity user);
}
