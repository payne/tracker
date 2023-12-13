package org.mattpayne.rsvp.tracker.rsvp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RsvpRepository extends JpaRepository<Rsvp, Long> {

    Page<Rsvp> findAllById(Long id, Pageable pageable);

}
