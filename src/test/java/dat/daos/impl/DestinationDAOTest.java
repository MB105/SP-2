package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.DestinationDTO;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DestinationDAOTest {

    private static DestinationDAO destinationDAO;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        // Indstil konfigurationen til test og hent entity manager fabrik
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        destinationDAO = DestinationDAO.getInstance(emf);
    }

    @Test
    void create() {
        // Opret en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Tangier", "Morocco");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Bekræft, at destinationen er oprettet korrekt
        assertNotNull(createdDestination);
        assertNotNull(createdDestination.getId());
        assertEquals("Tangier", createdDestination.getCity());
    }

    @Test
    void read() {
        // Opret en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Tangier", "Morocco");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Læs destinationen fra databasen
        DestinationDTO fetchedDestination = destinationDAO.read(createdDestination.getId());

        // Bekræft, at destinationen blev læst korrekt
        assertNotNull(fetchedDestination);
        assertEquals(createdDestination.getId(), fetchedDestination.getId());
    }

    @Test
    void readAll() {
        // Opret tre destinationer
        List<DestinationDTO> destinations = new ArrayList<>();
        destinations.add(destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco")));
        destinations.add(destinationDAO.create(new DestinationDTO(null, "Copenhagen", "Denmark")));
        destinations.add(destinationDAO.create(new DestinationDTO(null, "Barcelona", "Spain")));

        // Læs alle destinationer fra databasen
        List<DestinationDTO> fetchedDestinations = destinationDAO.readAll();

        // Bekræft, at der er tre destinationer
        assertEquals(3, fetchedDestinations.size());
    }

    @Test
    void update() {
        // Opret en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Tangier", "Morocco");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Opdater destinationens navn
        createdDestination.setCity("Updated Tangier");
        DestinationDTO updatedDestination = destinationDAO.update(createdDestination.getId(), createdDestination);

        // Bekræft, at destinationen blev opdateret korrekt
        assertNotNull(updatedDestination);
        assertEquals("Updated Tangier", updatedDestination.getCity());
    }

    @Test
    void delete() {
        // Opret en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Tangier", "Morocco");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);
        assertNotNull(createdDestination);

        // Slet destinationen
        destinationDAO.delete(createdDestination.getId());

        // Bekræft, at destinationen er slettet
        DestinationDTO fetchedDestination = destinationDAO.read(createdDestination.getId());
        assertNull(fetchedDestination);
    }

    @Test
    void validatePrimaryKey() {
        // Opret en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Tangier", "Morocco");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Bekræft, at den oprettede destination har en gyldig primærnøgle
        assertTrue(destinationDAO.validatePrimaryKey(createdDestination.getId()));
        assertFalse(destinationDAO.validatePrimaryKey(-1)); // Tjek for en ikke-eksisterende ID
    }

    @AfterEach
    void cleanUp() {
        // Ryd op i destinationer efter hver test
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Destination").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE destination_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}