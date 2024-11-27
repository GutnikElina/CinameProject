package Handlers;

import Services.*;
import java.util.Map;
import java.util.HashMap;

public class CommandFactory {
    private final Map<String, CommandHandler> handlers;

    public CommandFactory(AuthService authService, MovieService movieService, UserService userService,
                          HallService hallService, SessionService sessionService, TicketService ticketService) {
        handlers = Map.of(
                "LOGIN", new LoginHandler(authService),
                "REGISTER", new RegisterHandler(authService),
                "MOVIE", new MovieHandler(movieService),
                "USER", new UserHandler(userService, authService),
                "HALL", new HallHandler(hallService),
                "SESSION", new SessionHandler(sessionService, movieService, hallService),
                "TICKET", new TicketHandler(ticketService, sessionService)
        );
    }

    public CommandHandler getHandler(String command) {
        if (command == null || command.isBlank()) {
            return new ErrorHandler();
        }
        String mainCommand = command.split(";")[0];
        return handlers.getOrDefault(mainCommand, new ErrorHandler());
    }
}