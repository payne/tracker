package org.mattpayne.rsvp.tracker.rsvp;

import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.mattpayne.rsvp.tracker.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class RsvpService {

    private final RsvpRepository rsvpRepository;

    public RsvpService(final RsvpRepository rsvpRepository) {
        this.rsvpRepository = rsvpRepository;
    }

    public SimplePage<RsvpDTO> findAll(final String filter, final Pageable pageable) {
        Page<Rsvp> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = rsvpRepository.findAllById(longFilter, pageable);
        } else {
            page = rsvpRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(rsvp -> mapToDTO(rsvp, new RsvpDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public RsvpDTO get(final Long id) {
        return rsvpRepository.findById(id)
                .map(rsvp -> mapToDTO(rsvp, new RsvpDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RsvpDTO rsvpDTO) {
        final Rsvp rsvp = new Rsvp();
        mapToEntity(rsvpDTO, rsvp);
        return rsvpRepository.save(rsvp).getId();
    }

    public void update(final Long id, final RsvpDTO rsvpDTO) {
        final Rsvp rsvp = rsvpRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(rsvpDTO, rsvp);
        rsvpRepository.save(rsvp);
    }

    public void delete(final Long id) {
        rsvpRepository.deleteById(id);
    }

    private RsvpDTO mapToDTO(final Rsvp rsvp, final RsvpDTO rsvpDTO) {
        rsvpDTO.setId(rsvp.getId());
        rsvpDTO.setResponse(rsvp.getResponse());
        rsvpDTO.setInviteId(rsvp.getInviteId());
        return rsvpDTO;
    }

    private Rsvp mapToEntity(final RsvpDTO rsvpDTO, final Rsvp rsvp) {
        rsvp.setResponse(rsvpDTO.getResponse());
        rsvp.setInviteId(rsvpDTO.getInviteId());
        return rsvp;
    }

}
