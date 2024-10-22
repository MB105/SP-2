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
        if (instance == null) {
            emf = _emf;
            instance = new ReviewDAO();
        }
        return instance;
    }

    @Override
    public ReviewDTO read(Integer reviewId) {
        try (EntityManager em = emf.createEntityManager()) {
            Review review = em.find(Review.class, reviewId);
            return review != null ? new ReviewDTO(review) : null;
        }
    }

    @Override
    public List<ReviewDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ReviewDTO> query = em.createQuery("SELECT new dat.dtos.ReviewDTO(r) FROM Review r", ReviewDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public ReviewDTO create(ReviewDTO reviewDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find the associated Destination entity
            Destination destination = em.find(Destination.class, reviewDTO.getDestinationId());
            if (destination == null) {
                em.getTransaction().rollback();
                return null; // Destination must exist for creating a Review
            }

            // Create a new Review entity
            Review review = new Review(destination, reviewDTO.getRating(), reviewDTO.getComment());
            em.persist(review);
            em.getTransaction().commit();
            return new ReviewDTO(review); // Return the newly created ReviewDTO
        }
    }

    @Override
    public ReviewDTO update(Integer reviewId, ReviewDTO reviewDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                review.setRating(reviewDTO.getRating());
                review.setComment(reviewDTO.getComment());

                // Optionally, you can handle destination changes here
                // For now, we'll keep the existing destination

                em.getTransaction().commit();
                return new ReviewDTO(review); // Return the updated ReviewDTO
            }
            em.getTransaction().rollback();
            return null; // Handle the case where the review doesn't exist
        }
    }

    @Override
    public void delete(Integer reviewId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                em.remove(review);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer reviewId) {
        try (EntityManager em = emf.createEntityManager()) {
            Review review = em.find(Review.class, reviewId);
            return review != null;
        }
    }
}