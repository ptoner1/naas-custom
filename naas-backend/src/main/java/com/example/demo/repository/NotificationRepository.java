package com.example.demo.repository;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.models.Notification;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIsDraftAndSendDateBefore(String isDraft, Instant now);

    Optional<Notification> findByPublicId(String publicId);

    @Query(value = """
            SELECT DISTINCT p.email_address
            FROM providers p
            JOIN group_memberships gm ON p.id = gm.provider_id
            JOIN provider_groups pg ON gm.group_id = pg.id
            WHERE pg.public_id IN :groupPublicIds
            """, nativeQuery = true)
    List<String> findUniqueEmailsByGroupPublicIds(@Param("groupPublicIds") String[] groupPublicIds);
    
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Required for modifying queries if not handled by the calling service
    @Query(value = """
            UPDATE notifications 
            SET is_draft = :status 
            WHERE public_id = :publicId
            """, nativeQuery = true)
    void updateStatusByPublicId(@Param("publicId") String publicId, @Param("status") String status);

}