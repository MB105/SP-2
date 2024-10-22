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
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = DestinationDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        DestinationDTO destinationDTO = dao.read(id);
        // response
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
        // List of DTOs
        List<DestinationDTO> destinationDTOs = dao.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(destinationDTOs, DestinationDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        DestinationDTO jsonRequest = ctx.bodyAsClass(DestinationDTO.class);
        // DTO
        DestinationDTO destinationDTO = dao.create(jsonRequest);
        // response
        ctx.res().setStatus(201);
        ctx.json(destinationDTO, DestinationDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        DestinationDTO destinationDTO = dao.update(id, validateEntity(ctx));
        // response
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
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);

        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return dao.validatePrimaryKey(id);
    }

    @Override
    public DestinationDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(DestinationDTO.class)
                .check(d -> d.getCity() != null && !d.getCity().isEmpty(), "City must be set")
                .check(d -> d.getCountry() != null && !d.getCountry().isEmpty(), "Country must be set")
                .get();
    }
}
