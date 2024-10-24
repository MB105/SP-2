package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.DestinationDTO;
import dat.entities.Destination;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DestinationDAO implements IDAO<DestinationDTO, Integer> {

    private static DestinationDAO instance;
    private static EntityManagerFactory emf;

    public static DestinationDAO getInstance(EntityManagerFactory _emf) {
        // opretter en ny instans af destinationdao
        if (instance == null) {
            emf = _emf;
            instance = new DestinationDAO();
        }
        return instance;
    }

    @Override
    public DestinationDTO read(Integer destinationId) {
        // tjekker om destination id er null
        if (destinationId == null) {
            throw new IllegalArgumentException("destination id cannot be null.");
        }
        try (EntityManager em = emf.createEntityManager()) {
            // finder destination med det givne destination id
            Destination destination = em.find(Destination.class, destinationId);
            return destination != null ? new DestinationDTO(destination) : null;
        }
    }

    @Override
    public List<DestinationDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            // henter alle destinationer som destination dtos
            TypedQuery<DestinationDTO> query = em.createQuery("SELECT new dat.dtos.DestinationDTO(d) FROM Destination d", DestinationDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public DestinationDTO create(DestinationDTO destinationDTO) {
        // tjekker om destination dto er null
        if (destinationDTO == null) {
            throw new IllegalArgumentException("destination dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // opretter ny destination baseret på destination dto
            Destination destination = new Destination(destinationDTO);
            em.persist(destination);
            em.getTransaction().commit();
            return new DestinationDTO(destination);
        }
    }

    @Override
    public DestinationDTO update(Integer destinationId, DestinationDTO destinationDTO) {
        // tjekker om destination id eller destination dto er null
        if (destinationId == null || destinationDTO == null) {
            throw new IllegalArgumentException("destination id and destination dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // finder destination med det givne destination id
            Destination destination = em.find(Destination.class, destinationId);
            if (destination != null) {
                destination.setCity(destinationDTO.getCity());
                destination.setCountry(destinationDTO.getCountry());
                em.getTransaction().commit();
                return new DestinationDTO(destination);
            }
            em.getTransaction().rollback();
            return null; // håndterer tilfælde, hvor destination ikke findes
        }
    }

    @Override
    public void delete(Integer destinationId) {
        // tjekker om destination id er null
        if (destinationId == null) {
            throw new IllegalArgumentException("destination id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // finder destination med det givne destination id
            Destination destination = em.find(Destination.class, destinationId);
            if (destination != null) {
                em.remove(destination);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer destinationId) {
        // tjekker om destination id er null
        if (destinationId == null) {
            throw new IllegalArgumentException("destination id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            // finder destination med det givne destination id
            Destination destination = em.find(Destination.class, destinationId);
            return destination != null;
        }
    }
}
