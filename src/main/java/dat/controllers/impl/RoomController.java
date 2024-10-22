package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.RoomDAO;
import dat.dtos.HotelDTO;
import dat.dtos.RoomDTO;
import dat.exceptions.Message;
import dat.entities.Hotel;
import dat.entities.Room;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.function.BiFunction;

public class RoomController implements IController<RoomDTO, Integer> {

