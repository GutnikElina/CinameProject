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

    public boolean existsBySessionAndSeat(int sessionId, String seatNumber) {
        return executeTransactionWithResult(session -> {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(t) FROM Ticket t WHERE t.sessionId = :sessionId AND t.seatNumber = :seatNumber", Long.class);
            query.setParameter("sessionId", sessionId);
            query.setParameter("seatNumber", seatNumber);
            return query.uniqueResult() > 0;
        });
    }

    public int getHallCapacity(int sessionId) {
        return executeTransactionWithResult(session -> {
            Query<Integer> query = session.createQuery(
                    "SELECT h.capacity FROM FilmSession s JOIN Hall h ON s.hallId = h.id WHERE s.id = :sessionId", Integer.class);
            query.setParameter("sessionId", sessionId);

            Integer capacity = query.uniqueResult();
            if (capacity == null) {
                throw new IllegalArgumentException("Вместимость зала не найдена для данного сеанса: " + sessionId);
            }
            return capacity;
        });
    }

}
