package org.mattpayne.rsvp.tracker.invite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InviteRepository extends JpaRepository<Invite, Long> {

    Page<Invite> findAllById(Long id, Pageable pageable);

}
