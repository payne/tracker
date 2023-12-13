package org.mattpayne.rsvp.tracker.rsvp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/rsvps", produces = MediaType.APPLICATION_JSON_VALUE)
public class RsvpResource {

    private final RsvpService rsvpService;

    public RsvpResource(final RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<SimplePage<RsvpDTO>> getAllRsvps(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(rsvpService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsvpDTO> getRsvp(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(rsvpService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createRsvp(@RequestBody @Valid final RsvpDTO rsvpDTO) {
        final Long createdId = rsvpService.create(rsvpDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateRsvp(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final RsvpDTO rsvpDTO) {
        rsvpService.update(id, rsvpDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteRsvp(@PathVariable(name = "id") final Long id) {
        rsvpService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
