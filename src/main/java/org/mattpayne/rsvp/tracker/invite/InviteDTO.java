package org.mattpayne.rsvp.tracker.invite;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InviteDTO {

    private Long id;

    @NotNull
    private LocalDateTime sent;

    private String notes;

    @NotNull
    private Long personId;

    @NotNull
    private Long eventId;

}
