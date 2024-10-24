package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.ReviewDTO;
import dat.entities.Destination;
import dat.entities.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewDAO implements IDAO<ReviewDTO, Integer> {

    private static ReviewDAO instance;
    private static EntityManagerFactory emf;

    public static ReviewDAO getInstance(EntityManagerFactory _emf) {
        // opretter en ny instans af reviewdao
        if (instance == null) {
            emf = _emf;
            instance = new ReviewDAO();
        }
        return instance;
    }

    @Override
    public ReviewDTO read(Integer reviewId) {
        // tjekker om review id er null
        if (reviewId == null) {
            throw new IllegalArgumentException("review id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            // finder review med det givne review id
            Review review = em.find(Review.class, reviewId);
            return review != null ? new ReviewDTO(review) : null;
        }
    }

    @Override
    public List<ReviewDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            // henter alle anmeldelser som review dtos
            TypedQuery<ReviewDTO> query = em.createQuery("SELECT new dat.dtos.ReviewDTO(r) FROM Review r", ReviewDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public ReviewDTO create(ReviewDTO reviewDTO) {
        // tjekker om review dto er null
        if (reviewDTO == null) {
            throw new IllegalArgumentException("review dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // finder den tilknyttede destination enhed
            Destination destination = em.find(Destination.class, reviewDTO.getDestinationId());
            if (destination == null) {
                em.getTransaction().rollback();
                return null; // destination skal eksistere for at oprette en anmeldelse
            }

            // opretter anmeldelsen
            Review review = new Review(destination, reviewDTO.getRating(), reviewDTO.getComment());
            em.persist(review);
            em.getTransaction().commit();
            return new ReviewDTO(review); // returnerer den nyligt oprettede ReviewDTO
        }
    }

    @Override
    public ReviewDTO update(Integer reviewId, ReviewDTO reviewDTO) {
        // tjekker om review id eller review dto er null
        if (reviewId == null || reviewDTO == null) {
            throw new IllegalArgumentException("review id and review dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // finder review med det givne review id
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                review.setRating(reviewDTO.getRating());
                review.setComment(reviewDTO.getComment());
                em.getTransaction().commit();
                return new ReviewDTO(review); // returnerer den opdaterede ReviewDTO
            }
            em.getTransaction().rollback();
            return null; // håndterer tilfælde hvor anmeldelsen ikke findes
        }
    }

    @Override
    public void delete(Integer reviewId) {
        // tjekker om review id er null
        if (reviewId == null) {
            throw new IllegalArgumentException("review id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // finder review med det givne review id
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                em.remove(review);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer reviewId) {
        // tjekker om review id er null
        if (reviewId == null) {
            throw new IllegalArgumentException("review id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            // finder review med det givne review id
            Review review = em.find(Review.class, reviewId);
            return review != null;
        }
    }
}
