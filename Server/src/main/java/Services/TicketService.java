package Services;

import Models.Ticket;
import org.hibernate.query.Query;
import java.util.List;

public class TicketService extends BaseService implements Repository<Ticket> {

    @Override
    public void add(Ticket ticket) {
        executeTransaction(session -> session.save(ticket));
    }

    @Override
    public void update(Ticket ticket) {
        executeTransaction(session -> session.update(ticket));
    }

    @Override
    public List<Ticket> getAll() {
        return executeTransactionWithResult(session -> {
            Query<Ticket> query = session.createQuery("FROM Ticket", Ticket.class);
            return query.list();
        });
    }

    @Override
    public Ticket getById(int id) {
        return executeTransactionWithResult(session -> session.get(Ticket.class, id));
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            Ticket ticket = session.get(Ticket.class, id);
            if (ticket != null) {
                session.delete(ticket);
            }
        });
    }
}
