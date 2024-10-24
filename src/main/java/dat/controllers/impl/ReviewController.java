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
        // Henter EntityManagerFactory og initialiserer ReviewDAO
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = ReviewDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Læser anmeldelses id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Finder anmeldelse med det givne id
        ReviewDTO reviewDTO = dao.read(id);

        // Returnerer anmeldelse hvis fundet ellers får man en 404 status
        if (reviewDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(reviewDTO, ReviewDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Review not found"));
        }
    }

    @Override
    public void readAll(Context ctx) {
        // Henter alle anmeldelser
        List<ReviewDTO> reviewDTOS = dao.readAll();

        // Returnerer listen over anmeldelser
        ctx.res().setStatus(200);
        ctx.json(reviewDTOS, ReviewDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // Validerer og henter anmeldelsesdata fra request body
        ReviewDTO jsonRequest = ctx.bodyAsClass(ReviewDTO.class);

        // Opretter ny anmeldelse i databasen
        ReviewDTO reviewDTO = dao.create(jsonRequest);

        // Returnerer den nye anmeldelse med status 201
        ctx.res().setStatus(201);
        ctx.json(reviewDTO, ReviewDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Læser anmeldelses id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Opdaterer eksisterende anmeldelse med nye data
        ReviewDTO reviewDTO = dao.update(id, validateEntity(ctx));

        // Returnerer opdateret anmeldelse hvis fundet ellers får man en 404 status
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
        // Læser anmeldelses id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Sletter anmeldelse med det givne id
        dao.delete(id);

        // Returnerer status 204
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        // Validerer, om anmeldelses id er gyldigt
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public ReviewDTO validateEntity(Context ctx) {
        // Validerer anmeldelses data i request body
        return ctx.bodyValidator(ReviewDTO.class)
                .check(r -> r.getComment() != null && !r.getComment().isEmpty(), "Review comment must be set")
                .check(r -> r.getRating() >= 1 && r.getRating() <= 5, "Rating must be between 1 and 5")
                .check(r -> r.getId() > 0, "Valid user ID must be provided")
                .check(r -> r.getDestinationId() > 0, "Valid destination ID must be provided")
                .get();
    }
}
