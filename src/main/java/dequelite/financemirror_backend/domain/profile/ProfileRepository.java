package dequelite.financemirror_backend.domain.profile;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
     Optional<ProfileEntity> findByUser_Id(UUID userId);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("""
         SELECT p
         FROM ProfileEntity p
         WHERE p.user.id = :userId
     """)
     Optional<ProfileEntity> findProfileByUserIdForCreateSpending(@Param("userId") UUID userId);

     @Modifying
     @Query("""
        UPDATE ProfileEntity p
        SET p.balance = p.balance + :amount
        WHERE p.id = :id
    """)
     int incrementBalance(
             @Param("id") UUID id,
             @Param("amount") Integer amount
     );
}
