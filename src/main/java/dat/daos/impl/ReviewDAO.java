package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.ReviewDTO;
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
    public ReviewDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Review review = em.find(Review.class, id);
            return review != null ? new ReviewDTO(review.getId(), review.getUserId(), review.getDestinationId(), review.getRating(), review.getComment()) : null;
        }
    }

    @Override
    public List<ReviewDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<ReviewDTO> query = em.createQuery("SELECT new dat.dtos.ReviewDTO(r.id, r.userId, r.destinationId, r.rating, r.comment) FROM Review r", ReviewDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public ReviewDTO create(ReviewDTO reviewDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Review review = new Review(reviewDTO);
            em.persist(review);
            em.getTransaction().commit();
            return new ReviewDTO(review.getId(), review.getUserId(), review.getDestinationId(), review.getRating(), review.getComment());
        }
    }

    @Override
    public ReviewDTO update(Integer id, ReviewDTO reviewDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Review review = em.find(Review.class, id);

            if (review != null) {
                review.setRating(reviewDTO.getRating());
                review.setUserId(reviewDTO.getUserId());
                review.setDestinationId(reviewDTO.getDestinationId());
                review.setComment(reviewDTO.getComment());
                Review mergedReview = em.merge(review);
                em.getTransaction().commit();
                return new ReviewDTO(mergedReview.getId(), mergedReview.getUserId(), mergedReview.getDestinationId(), mergedReview.getRating(), mergedReview.getComment());
            } else {
                em.getTransaction().rollback();
                return null;
            }
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Review review = em.find(Review.class, id);
            if (review != null) {
                em.remove(review);
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Review review = em.find(Review.class, id);
            return review != null;
        }
    }
}
