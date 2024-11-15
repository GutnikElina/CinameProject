package Handlers;

import java.io.PrintWriter;

public interface CommandHandler {
    void handle(String[] requestParts, PrintWriter out);
}
