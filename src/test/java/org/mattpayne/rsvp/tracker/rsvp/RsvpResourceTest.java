package org.mattpayne.rsvp.tracker.rsvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mattpayne.rsvp.tracker.config.BaseIT;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;


public class RsvpResourceTest extends BaseIT {

    @Test
    @Sql("/data/rsvpData.sql")
    void getAllRsvps_success() throws Exception {
        mockMvc.perform(get("/api/rsvps")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1300)));
    }

    @Test
    @Sql("/data/rsvpData.sql")
    void getAllRsvps_filtered() throws Exception {
        mockMvc.perform(get("/api/rsvps?filter=1301")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(((long)1301)));
    }

    @Test
    @Sql("/data/rsvpData.sql")
    void getRsvp_success() throws Exception {
        mockMvc.perform(get("/api/rsvps/1300")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inviteId").value(((long)52)));
    }

    @Test
    void getRsvp_notFound() throws Exception {
        mockMvc.perform(get("/api/rsvps/1966")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createRsvp_success() throws Exception {
        mockMvc.perform(post("/api/rsvps")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/rsvpDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, rsvpRepository.count());
    }

    @Test
    void createRsvp_missingField() throws Exception {
        mockMvc.perform(post("/api/rsvps")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/rsvpDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("response"));
    }

    @Test
    @Sql("/data/rsvpData.sql")
    void updateRsvp_success() throws Exception {
        mockMvc.perform(put("/api/rsvps/1300")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/rsvpDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(((long)54), rsvpRepository.findById(((long)1300)).get().getInviteId());
        assertEquals(2, rsvpRepository.count());
    }

    @Test
    @Sql("/data/rsvpData.sql")
    void deleteRsvp_success() throws Exception {
        mockMvc.perform(delete("/api/rsvps/1300")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, rsvpRepository.count());
    }

}
