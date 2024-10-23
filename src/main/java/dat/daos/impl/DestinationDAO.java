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
        if (instance == null) {
            emf = _emf;
            instance = new DestinationDAO();
        }
        return instance;
    }

    @Override
    public DestinationDTO read(Integer destinationId) {
        try (EntityManager em = emf.createEntityManager()) {
            Destination destination = em.find(Destination.class, destinationId);
            return destination != null ? new DestinationDTO(destination) : null;
        }
    }

    @Override
    public List<DestinationDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<DestinationDTO> query = em.createQuery("SELECT new dat.dtos.DestinationDTO(d) FROM Destination d", DestinationDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public DestinationDTO create(DestinationDTO destinationDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Destination destination = new Destination(destinationDTO);
            em.persist(destination);
            em.getTransaction().commit();
            return new DestinationDTO(destination);
        }
    }

    @Override
    public DestinationDTO update(Integer destinationId, DestinationDTO destinationDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Destination destination = em.find(Destination.class, destinationId);
            if (destination != null) {
                destination.setCity(destinationDTO.getCity());
                destination.setCountry(destinationDTO.getCountry());
                em.getTransaction().commit();
                return new DestinationDTO(destination);
            }
            em.getTransaction().rollback();
            return null; // Handle the case where the destination doesn't exist
        }
    }

    @Override
    public void delete(Integer destinationId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Destination destination = em.find(Destination.class, destinationId);
            if (destination != null) {
                em.remove(destination);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer destinationId) {
        try (EntityManager em = emf.createEntityManager()) {
            Destination destination = em.find(Destination.class, destinationId);
            return destination != null;
        }
    }
}

