package com.flex.url_shortener.repository;

import com.flex.url_shortener.entity.ShortLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByShortCode(String shortCode);

    @Modifying
    @Query("UPDATE ShortLink sl SET sl.clickCount = sl.clickCount + 1 WHERE sl.shortCode = :shortCode")
    void incrementClickCount(String shortCode);

    @Query(value = "SELECT nextval('short_link_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    Page<ShortLink> findAllByUserEmail(String email, Pageable pageable);
}