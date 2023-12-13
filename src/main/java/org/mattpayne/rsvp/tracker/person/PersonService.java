package org.mattpayne.rsvp.tracker.person;

import jakarta.transaction.Transactional;
import org.mattpayne.rsvp.tracker.event.Event;
import org.mattpayne.rsvp.tracker.event.EventRepository;
import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.mattpayne.rsvp.tracker.util.NotFoundException;
import org.mattpayne.rsvp.tracker.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final EventRepository eventRepository;

    public PersonService(final PersonRepository personRepository,
            final EventRepository eventRepository) {
        this.personRepository = personRepository;
        this.eventRepository = eventRepository;
    }

    public SimplePage<PersonDTO> findAll(final String filter, final Pageable pageable) {
        Page<Person> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = personRepository.findAllById(longFilter, pageable);
        } else {
            page = personRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(person -> mapToDTO(person, new PersonDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public PersonDTO get(final Long id) {
        return personRepository.findById(id)
                .map(person -> mapToDTO(person, new PersonDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PersonDTO personDTO) {
        final Person person = new Person();
        mapToEntity(personDTO, person);
        return personRepository.save(person).getId();
    }

    public void update(final Long id, final PersonDTO personDTO) {
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(personDTO, person);
        personRepository.save(person);
    }

    public void delete(final Long id) {
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        eventRepository.findAllByPeople(person)
                .forEach(event -> event.getPeople().remove(person));
        personRepository.delete(person);
    }

    private PersonDTO mapToDTO(final Person person, final PersonDTO personDTO) {
        personDTO.setId(person.getId());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setEmail(person.getEmail());
        return personDTO;
    }

    private Person mapToEntity(final PersonDTO personDTO, final Person person) {
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setEmail(personDTO.getEmail());
        return person;
    }

    public boolean emailExists(final String email) {
        return personRepository.existsByEmailIgnoreCase(email);
    }

    public String getReferencedWarning(final Long id) {
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Event peopleEvent = eventRepository.findFirstByPeople(person);
        if (peopleEvent != null) {
            return WebUtils.getMessage("person.event.people.referenced", peopleEvent.getId());
        }
        return null;
    }

}
