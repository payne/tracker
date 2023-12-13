package org.mattpayne.rsvp.tracker.event;

import java.util.List;
import org.mattpayne.rsvp.tracker.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllById(Long id, Pageable pageable);

    Event findFirstByPeople(Person person);

    List<Event> findAllByPeople(Person person);

    boolean existsByNameIgnoreCase(String name);

}
