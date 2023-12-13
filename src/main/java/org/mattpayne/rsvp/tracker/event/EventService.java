package org.mattpayne.rsvp.tracker.event;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.mattpayne.rsvp.tracker.person.Person;
import org.mattpayne.rsvp.tracker.person.PersonRepository;
import org.mattpayne.rsvp.tracker.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final PersonRepository personRepository;

    public EventService(final EventRepository eventRepository,
            final PersonRepository personRepository) {
        this.eventRepository = eventRepository;
        this.personRepository = personRepository;
    }

    public SimplePage<EventDTO> findAll(final String filter, final Pageable pageable) {
        Page<Event> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = eventRepository.findAllById(longFilter, pageable);
        } else {
            page = eventRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(event -> mapToDTO(event, new EventDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EventDTO get(final Long id) {
        return eventRepository.findById(id)
                .map(event -> mapToDTO(event, new EventDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EventDTO eventDTO) {
        final Event event = new Event();
        mapToEntity(eventDTO, event);
        return eventRepository.save(event).getId();
    }

    public void update(final Long id, final EventDTO eventDTO) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(eventDTO, event);
        eventRepository.save(event);
    }

    public void delete(final Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(final Event event, final EventDTO eventDTO) {
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setWhen(event.getWhen());
        eventDTO.setPeople(event.getPeople().stream()
                .map(person -> person.getId())
                .toList());
        return eventDTO;
    }

    private Event mapToEntity(final EventDTO eventDTO, final Event event) {
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setWhen(eventDTO.getWhen());
        final List<Person> people = personRepository.findAllById(
                eventDTO.getPeople() == null ? Collections.emptyList() : eventDTO.getPeople());
        if (people.size() != (eventDTO.getPeople() == null ? 0 : eventDTO.getPeople().size())) {
            throw new NotFoundException("one of people not found");
        }
        event.setPeople(people.stream().collect(Collectors.toSet()));
        return event;
    }

    public boolean nameExists(final String name) {
        return eventRepository.existsByNameIgnoreCase(name);
    }

}
