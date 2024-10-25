package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.DestinationDAO;
import dat.dtos.DestinationDTO;
import dat.exceptions.Message;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DestinationController implements IController<DestinationDTO, Integer> {

    private final DestinationDAO dao;

    public DestinationController() {
        // Henter EntityManagerFactory og initialiserer DestinationDAO
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = DestinationDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Læser destination id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Finder destination med det givne id
        DestinationDTO destinationDTO = dao.read(id);

        // Returnerer destination hvis fundet ellers får man en 404 status
        if (destinationDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(destinationDTO, DestinationDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Destination not found"));
        }
    }

    @Override
    public void readAll(Context ctx) {
        // Henter alle destinationer
        List<DestinationDTO> destinationDTOs = dao.readAll();

        // Returnerer listen over destinationer
        ctx.res().setStatus(200);
        ctx.json(destinationDTOs, DestinationDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // Validerer og henter destination data fra request body
        DestinationDTO jsonRequest = ctx.bodyAsClass(DestinationDTO.class);

        // Opretter ny destination i databasen
        DestinationDTO destinationDTO = dao.create(jsonRequest);

        // Returnerer den nye destination med status 201
        ctx.res().setStatus(201);
        ctx.json(destinationDTO, DestinationDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Læser destination id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Opdaterer eksisterende destination med nye data
        DestinationDTO destinationDTO = dao.update(id, validateEntity(ctx));

        // Returnerer opdateret destination hvis fundet ellers får man en 404 status
        if (destinationDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(destinationDTO, DestinationDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Destination not found"));
        }
    }

    @Override
    public void delete(Context ctx) {
        // Læser destination id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Sletter destination med det givne id
        dao.delete(id);

        // Returnerer status 204
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        // Validerer om destination id er gyldigt
        return dao.validatePrimaryKey(id);
    }

    @Override
    public DestinationDTO validateEntity(Context ctx) {
        // Validerer destination data i request body
        return ctx.bodyValidator(DestinationDTO.class)
                .check(d -> d.getCity() != null && !d.getCity().isEmpty(), "City must be set")
                .check(d -> d.getCountry() != null && !d.getCountry().isEmpty(), "Country must be set")
                .get();
    }
}
