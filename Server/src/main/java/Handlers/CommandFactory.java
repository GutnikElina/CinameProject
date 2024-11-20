package Handlers;

import Services.*;
import java.util.Map;
import java.util.HashMap;

public class CommandFactory {
    private final Map<String, CommandHandler> handlers;

    public CommandFactory(AuthService authService, MovieService movieService, UserService userService,
                          HallService hallService, SessionService sessionService, TicketService ticketService) {
        handlers = new HashMap<>();
        handlers.put("LOGIN", new LoginHandler(authService));
        handlers.put("REGISTER", new RegisterHandler(authService));
        handlers.put("MOVIE", new MovieHandler(movieService));
        handlers.put("USER", new UserHandler(userService, authService));
        handlers.put("HALL", new HallHandler(hallService));
        handlers.put("SESSION", new SessionHandler(sessionService, movieService, hallService));
        handlers.put("TICKET", new TicketHandler(ticketService));
    }

    public CommandHandler getHandler(String command) {
        return handlers.getOrDefault(command, new ErrorHandler());
    }
}
