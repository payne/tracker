package org.mattpayne.rsvp.tracker.config;

import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.mattpayne.rsvp.tracker.TrackerApplication;
import org.mattpayne.rsvp.tracker.event.EventRepository;
import org.mattpayne.rsvp.tracker.invite.InviteRepository;
import org.mattpayne.rsvp.tracker.person.PersonRepository;
import org.mattpayne.rsvp.tracker.rsvp.RsvpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context with a
 * Datasource connected to the Testcontainers Docker instance. The instance is reused for all tests,
 * with all data wiped out before each test.
 */
@SpringBootTest(
        classes = TrackerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Sql("/data/clearAll.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class BaseIT {

    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16.1");

    static {
        postgreSQLContainer.withReuse(true)
                .start();
    }

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public PersonRepository personRepository;

    @Autowired
    public InviteRepository inviteRepository;

    @Autowired
    public RsvpRepository rsvpRepository;

    @SneakyThrows
    public String readResource(final String resourceName) {
        return StreamUtils.copyToString(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }

}
