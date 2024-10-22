package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.ReviewDAO;
import dat.dtos.ReviewDTO;
import dat.exceptions.Message;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ReviewController implements IController<ReviewDTO, Integer> {

    private final ReviewDAO dao;

    public ReviewController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = ReviewDAO.getInstance(emf);
    }

    //her bliver der læst en anmeldese ved hjælp af id
    @Override
    public void read(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        ReviewDTO reviewDTO = dao.read(id);
        // response
        if (reviewDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(reviewDTO, ReviewDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Review not found"));
        }
    }

    //læser alle anmeldelser
    @Override
    public void readAll(Context ctx) {
        // List of DTOS
        List<ReviewDTO> reviewDTOS = dao.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(reviewDTOS, ReviewDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        ReviewDTO jsonRequest = ctx.bodyAsClass(ReviewDTO.class);
        // DTO
        ReviewDTO reviewDTO = dao.create(jsonRequest);
        // response
        ctx.res().setStatus(201);
        ctx.json(reviewDTO, ReviewDTO.class);
    }

    //Vi bruger ikke update.
    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        ReviewDTO reviewDTO = dao.update(id, validateEntity(ctx));
        // response
        if (reviewDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(reviewDTO, ReviewDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Review not found"));
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
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public ReviewDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(ReviewDTO.class)
                .check(r -> r.getComment() != null && !r.getComment().isEmpty(), "Review comment must be set")
                .check(r -> r.getRating() >= 1 && r.getRating() <= 5, "Rating must be between 1 and 5")
                .check(r -> r.getId() > 0, "Valid user ID must be provided")
                .check(r -> r.getDestinationId() > 0, "Valid destination ID must be provided")
                .get();
    }
}

