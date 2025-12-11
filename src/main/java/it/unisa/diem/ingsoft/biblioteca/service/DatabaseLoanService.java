/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanAlreadyRegisteredException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownLoanException;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;

/**
 * @brief Implementazione del LoanService usando un Database per la persistenza
 */
public class DatabaseLoanService implements LoanService {
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa il LoanService usando un database
     */
    public DatabaseLoanService(Database database) {
        this.database = database;
    }

    /**
     * @brief Cerca un prestito chiesto da un utente per un libro.
     *  Esegue una select SQL per ottenere il prestito dal database.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro prestato.
     * @return Un opzionale contenente il prestito se esistente, Optional.empty() altrimenti.
     */
	@Override
	public Optional<Loan> getByUserIDAndBookIsbn(String userId, String bookIsbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans "
                        + "WHERE user_id = :user_id AND book_isbn = :book_isbn")
                    .bind("user_id", userId)
                    .bind("book_isbn", bookIsbn)
                    .mapTo(Loan.class)
                    .findFirst());
    }

    /**
     * @brief Recupera una lista di prestiti per il l'utente con matricola specificata.
     *  Esegue una select SQL per ottenere la lista di prestiti.
     * @param userId La matricola dell'utente che ha chiesto i prestiti.
     * @return La lista dei prestiti chiesti dall'utente.
     */
    @Override
    public List<Loan> getByUserId(String userId) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans "
                        + "WHERE (user_id = :user_id)")
                    .bind("user_id", userId)
                    .mapTo(Loan.class)
                    .list());
    }

    /**
     * @brief Recupera una lista di prestiti per il libro con l'ISBN specificato.
     *  Esegue una select SQL per ottenere la lista di prestiti.
     * @param bookIsbn L'ISBN del libro per cui sono stati chiesti i prestiti.
     * @return La lista dei prestiti per il libro.
     */
    @Override
    public List<Loan> getByBookIsbn(String bookIsbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans "
                        + "WHERE book_isbn = :book_isbn")
                    .bind("book_isbn", bookIsbn)
                    .mapTo(Loan.class)
                    .list());
    }

    /**
     * @brief Registra un prestito chiesto da un utente per un libro.
     *  specificando la data di inizio del prestito e di restituzione
     *  Esegue una insert SQL per registrare l'utente nel database.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro dato in prestito.
     * @param start La data di inizio del prestito.
     * @param deadline La data di restituzione massima.
     * @throws LoanAlreadyRegisteredException Il prestito per l'utente ed il libro specificati.
     *  è già esistente
     */
    @Override
    public void register(String userId, String bookIsbn, LocalDate start, LocalDate deadline) throws LoanAlreadyRegisteredException {
        if (this.isActive(userId, bookIsbn))
            throw new LoanAlreadyRegisteredException();

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO loans(book_isbn, user_id,"
                        + "loan_start, loan_deadline) "
                        + "VALUES (:book_isbn, :user_id, :loan_start, :loan_deadline)")
                    .bind("book_isbn", bookIsbn)
                    .bind("user_id", userId)
                    .bind("loan_start", start)
                    .bind("loan_deadline", deadline)
                    .execute());
    }

    /**
     * @brief Registra la restituzione di un libro da parte di un utente.
     *  Esegue un update SQL per modificare le informazioni del prestito.
     * @param userId La matricola dell'utente che ha restituito il libro.
     * @param bookIsbn L'ISBN del libro restituito.
     * @param end Data di restituzione del libro.
     * @throws UnknownLoanException Il prestito tra utente e libro specificati è inesistente.
     */
	@Override
	public void complete(String userId, String bookIsbn, LocalDate end) throws UnknownLoanException {
        if (!this.isActive(userId, bookIsbn)) {
            throw new UnknownLoanException();
        }

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE loans SET loan_end = :loan_end "
                        + "WHERE user_id = :user_id AND book_isbn = :book_isbn")
                    .bind("user_id", userId)
                    .bind("book_isbn", bookIsbn)
                    .bind("loan_end", end)
                    .execute());
	}

    /**
     * @brief Recupera una lista di tutti i prestiti registrati.
     *  Esegue una select SQL per ottenere la lista di prestiti del database.
     * @return Una lista contenente tutti i prestiti.
     */
	@Override
	public List<Loan> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans")
                    .mapTo(Loan.class)
                    .list());
	}

    /**
     * @brief Verifica se un utente ha preso in prestito un libro.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro preso in prestito.
     * @return true se l'utente ha preso in prestito il libro, false altrimenti.
     */
	@Override
	public boolean isActive(String userId, String bookIsbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM loans "
                        + "WHERE user_id = :user_id AND book_isbn = :book_isbn "
                        + "AND loan_end IS NULL")
                    .bind("user_id", userId)
                    .bind("book_isbn", bookIsbn)
                    .mapTo(Integer.class)
                    .one()) > 0;
	}

    /**
     * @brief Conta il numero di prestiti fatti da un utente.
     *  Esegue una select count SQL per contare il numero di prestiti dell'utente nel database.
     * @param userId La matricola dell'utente da controllare.
     * @return Il numero di prestiti attualmente attivi.
     */
	@Override
	public int countById(String userId) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM loans "
                        + "WHERE user_id = :user_id")
                    .bind("user_id", userId)
                    .mapTo(Integer.class)
                    .one());
	}

    /**
     * @brief Recupera una lista di tutti i prestiti registrati attualmente attivi
     *  Esegue una select SQL per ottenere tutti i prestiti il cui loan_end è nullo
     * @return Una lista contenente i prestiti attivi
     */
	@Override
	public List<Loan> getAllActive() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans "
                        + "WHERE loan_end IS NULL")
                    .mapTo(Loan.class)
                    .list());
	}
}
