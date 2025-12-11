/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.LoanAlreadyRegisteredException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownLoanException;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;

/**
 * @brief Interfaccia per la gestione dei prestiti
 */
public interface LoanService {
    /**
     * @brief Recupera una lista di tutti i prestiti registrati.
     * @return Una lista contenente tutti i prestiti.
     */
    List<Loan> getAll();

    /**
     * @brief Recupera una lista di tutti i prestiti registrati attualmente attivi
     * @return Una lista contenente i prestiti attivi
     */
    List<Loan> getAllActive();

    /**
     * @brief Cerca un prestito chiesto da un utente per un libro.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro prestato.
     * @return Un opzionale contenente il prestito se esistente, Optional.empty() altrimenti.
     */
    Optional<Loan> getByUserIDAndBookIsbn(String userId, String bookIsbn);

    /**
     * @brief Recupera una lista di prestiti per il l'utente con matricola specificata.
     * @param userId La matricola dell'utente che ha chiesto i prestiti.
     * @return La lista dei prestiti chiesti dall'utente.
     */
    List<Loan> getByUserId(String userId);

    /**
     * @brief Recupera una lista di prestiti per il libro con l'ISBN specificato.
     * @param bookIsbn L'ISBN del libro per cui sono stati chiesti i prestiti.
     * @return La lista dei prestiti per il libro.
     */
    List<Loan> getByBookIsbn(String bookIsbn);

    /**
     * @brief Registra un prestito chiesto da un utente per un libro.
     *  specificando la data di inizio del prestito e di restituzione
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro dato in prestito.
     * @param start La data di inizio del prestito.
     * @param deadline La data di restituzione massima.
     * @throws LoanAlreadyRegisteredException Il prestito per l'utente ed il libro specificati.
     *  è già esistente
     */
	void register(String userId, String bookIsbn, LocalDate start, LocalDate deadline) throws LoanAlreadyRegisteredException;

    /**
     * @brief Registra la restituzione di un libro da parte di un utente.
     * @param userId La matricola dell'utente che ha restituito il libro.
     * @param bookIsbn L'ISBN del libro restituito.
     * @param end Data di restituzione del libro.
     * @throws UnknownLoanException Il prestito tra utente e libro specificati è inesistente.
     */
    void complete(String userId, String bookIsbn, LocalDate end) throws UnknownLoanException;

    /**
     * @brief Verifica se un utente ha preso in prestito un libro.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro preso in prestito.
     * @return true se l'utente ha preso in prestito il libro, false altrimenti.
     */
    boolean has(String userId, String bookIsbn);


    /**
     * @brief Conta il numero di prestiti fatti da un utente.
     * @param userId La matricola dell'utente da controllare.
     * @return Il numero di prestiti attualmente attivi.
     */
    int countById(String userId);
};
