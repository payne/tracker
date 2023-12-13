package org.mattpayne.rsvp.tracker.rsvp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RsvpDTO {

    private Long id;

    @NotNull
    private Response response;

    private Long inviteId;

}
