package Handlers;

import Models.RequestDTO;

import java.io.PrintWriter;

public interface CommandHandler {
    void handle(RequestDTO request, PrintWriter out);
}
