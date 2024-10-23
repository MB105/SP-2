package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.DestinationDTO;
import dat.dtos.ReviewDTO;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOTest {

    private static ReviewDAO reviewDAO;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        // Indstil konfigurationen til test og hent entity manager fabrik
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        reviewDAO = ReviewDAO.getInstance(emf);
    }

    @Test
    void create() {
        // Opret en destination for at associere med anmeldelsen
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));

        // Opret en anmeldelse direkte med destinationens ID
        ReviewDTO reviewDTO = new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!");
        ReviewDTO createdReview = reviewDAO.create(reviewDTO);

        // Bekræft, at anmeldelsen er oprettet korrekt
        assertNotNull(createdReview);
        assertNotNull(createdReview.getId());
        assertEquals(5, createdReview.getRating());
        assertEquals("Great place!", createdReview.getComment());
    }

    @Test
    void read() {
        // Opret en destination og en anmeldelse for den
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));
        ReviewDTO reviewDTO = new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!");
        ReviewDTO createdReview = reviewDAO.create(reviewDTO);

        // Læs anmeldelsen fra databasen
        ReviewDTO fetchedReview = reviewDAO.read(createdReview.getId());

        // Bekræft, at anmeldelsen blev læst korrekt
        assertNotNull(fetchedReview);
        assertEquals(createdReview.getId(), fetchedReview.getId());
    }

    @Test
    void readAll() {
        // Opret en destination og flere anmeldelser for den
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));
        reviewDAO.create(new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!"));
        reviewDAO.create(new ReviewDTO(null, destinationDTO.getId(), 4, "Good place!"));

        // Læs alle anmeldelser fra databasen
        List<ReviewDTO> reviews = reviewDAO.readAll();

        // Bekræft, at der er mindst to anmeldelser
        assertTrue(reviews.size() >= 2);
    }

    @Test
    void update() {
        // Opret en destination og en anmeldelse
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));
        ReviewDTO reviewDTO = new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!");
        ReviewDTO createdReview = reviewDAO.create(reviewDTO);

        // Opdater anmeldelsen
        createdReview.setRating(4);
        createdReview.setComment("Good place, but could improve.");
        ReviewDTO updatedReview = reviewDAO.update(createdReview.getId(), createdReview);

        // Bekræft, at anmeldelsen blev opdateret korrekt
        assertNotNull(updatedReview);
        assertEquals(4, updatedReview.getRating());
        assertEquals("Good place, but could improve.", updatedReview.getComment());
    }

    @Test
    void delete() {
        // Opret en destination og en anmeldelse
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));
        ReviewDTO reviewDTO = new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!");
        ReviewDTO createdReview = reviewDAO.create(reviewDTO);

        // Bekræft, at anmeldelsen er oprettet
        assertNotNull(createdReview);

        // Slet anmeldelsen
        reviewDAO.delete(createdReview.getId());

        // Bekræft, at anmeldelsen er slettet
        ReviewDTO fetchedReview = reviewDAO.read(createdReview.getId());
        assertNull(fetchedReview);
    }

    @Test
    void validatePrimaryKey() {
        // Opret en destination og en anmeldelse
        DestinationDAO destinationDAO = DestinationDAO.getInstance(emf);
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Tangier", "Morocco"));
        ReviewDTO reviewDTO = new ReviewDTO(null, destinationDTO.getId(), 5, "Great place!");
        ReviewDTO createdReview = reviewDAO.create(reviewDTO);

        // Bekræft, at den oprettede anmeldelse har en gyldig primærnøgle
        assertTrue(reviewDAO.validatePrimaryKey(createdReview.getId()));
        assertFalse(reviewDAO.validatePrimaryKey(-1)); // Bekræft at -1 ikke er en gyldig ID
    }

    @AfterEach
    void cleanUp() {
        // Ryd op i anmeldelser og destinationer efter hver test
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Review").executeUpdate();
            em.createQuery("DELETE FROM Destination").executeUpdate(); // Ryd op i destinationer hvis nødvendigt
            em.createNativeQuery("ALTER SEQUENCE review_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE destination_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
