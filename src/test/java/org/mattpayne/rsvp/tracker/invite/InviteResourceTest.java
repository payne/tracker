package org.mattpayne.rsvp.tracker.invite;

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


public class InviteResourceTest extends BaseIT {

    @Test
    @Sql("/data/inviteData.sql")
    void getAllInvites_success() throws Exception {
        mockMvc.perform(get("/api/invites")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1200)));
    }

    @Test
    @Sql("/data/inviteData.sql")
    void getAllInvites_filtered() throws Exception {
        mockMvc.perform(get("/api/invites?filter=1201")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(((long)1201)));
    }

    @Test
    @Sql("/data/inviteData.sql")
    void getInvite_success() throws Exception {
        mockMvc.perform(get("/api/invites/1200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat."));
    }

    @Test
    void getInvite_notFound() throws Exception {
        mockMvc.perform(get("/api/invites/1866")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createInvite_success() throws Exception {
        mockMvc.perform(post("/api/invites")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/inviteDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, inviteRepository.count());
    }

    @Test
    void createInvite_missingField() throws Exception {
        mockMvc.perform(post("/api/invites")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/inviteDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("sent"));
    }

    @Test
    @Sql("/data/inviteData.sql")
    void updateInvite_success() throws Exception {
        mockMvc.perform(put("/api/invites/1200")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/inviteDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.", inviteRepository.findById(((long)1200)).get().getNotes());
        assertEquals(2, inviteRepository.count());
    }

    @Test
    @Sql("/data/inviteData.sql")
    void deleteInvite_success() throws Exception {
        mockMvc.perform(delete("/api/invites/1200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, inviteRepository.count());
    }

}
