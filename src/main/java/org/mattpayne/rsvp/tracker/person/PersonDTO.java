package org.mattpayne.rsvp.tracker.person;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PersonDTO {

    private Long id;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @NotNull
    @Size(max = 255)
    private String email;

}
