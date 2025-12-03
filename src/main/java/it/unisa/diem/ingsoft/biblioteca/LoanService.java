package it.unisa.diem.ingsoft.biblioteca;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @brief Interfaccia per la gestione dei prestiti
 */
public interface LoanService {
    /**
     * @brief Ritorna una lista di tutti i prestiti registrati
     * @return Una lista contenente tutti i prestiti
     */
    List<Loan> getAll();

    /**
     * @brief Cerca un prestito chiesto da un utente per un libro
     * @param userId La matricola dell'utente che ha chiesto il prestito
     * @param bookISBN L'ISBN del libro prestato
     * @return Un opzionale contenente il prestito se esistente, empty altrimenti
     */
    Optional<Loan> getByUserIDAndBookISBN(String userId, String bookISBN);

    /**
     * @brief Cerca i prestiti chiesti da un utente
     * @param userId La matricola dell'utente che ha chiesto i prestiti
     * @return La lista dei prestiti chiesti dall'utente
     */
    List<Loan> getByUserID(String userId);

    /**
     * @brief Cerca i prestiti chiesti per un libro
     * @param bookISBN L'ISBN del libro per cui sono stati chiesti i prestiti
     * @return La lista dei prestiti per il libro
     */
    List<Loan> getByBookISBN(String bookISBN);

    /**
     * @brief Registra un prestito chiesto da un utente per un libro
     *  specificando la data di inizio del prestito e di restituzione
     * @param userId La matricola dell'utente che ha chiesto il prestito
     * @param bookISBN L'ISBN del libro dato in prestito
     * @param start La data di inizio del prestito
     * @param deadline La data di restituzione
     */
	void register(String userId, String bookISBN, LocalDate start, LocalDate deadline);

    /**
     * @brief Registra la restituzione di un libro da parte di un utente
     * @param userId La matricola dell'utente che ha restituito il libro
     * @param bookISBN L'ISBN del libro restituito
     */
    void complete(String userId, String bookISBN, LocalDate end);

    /**
     * @brief Verifica se un utente ha preso in prestito un libro
     * @param userId La matricola dell'utente che ha chiesto il prestito
     * @param bookISBN L'ISBN del libro preso in prestito
     * @return true se l'utente ha preso in prestito il libro, false altrimenti
     */
    boolean has(String userId, String bookISBN);
};
