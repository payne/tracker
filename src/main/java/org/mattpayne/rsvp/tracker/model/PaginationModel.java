package org.mattpayne.rsvp.tracker.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaginationModel {

    private List<PaginationStep> steps;
    private String elements;

}
