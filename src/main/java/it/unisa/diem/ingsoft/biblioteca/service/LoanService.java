/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
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
    List<Loan> getActive();

    /**
     * @brief Cerca un prestito chiesto da un utente per un libro.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro prestato.
     * @return Un opzionale contenente il prestito se esistente, Optional.empty() altrimenti.
     */
    Optional<Loan> getByUserIdAndBookIsbn(String userId, String bookIsbn);

    /**
     * @brief Recupera una lista di prestiti per l'utente la cui matricola contiene la stringa
     *  specificata in qualsiasi posizione.
     * @param userId La matricola dell'utente che ha chiesto i prestiti.
     * @return La lista dei prestiti chiesti dall'utente.
     */
    List<Loan> getByUserIdContaining(String userId);

    /**
     * @brief Recupera una lista di prestiti per il libro il cui ISBN contiene la stringa 
     *  specificata in qualsiasi posizione.
     * @param bookIsbn L'ISBN del libro per cui sono stati chiesti i prestiti.
     * @return La lista dei prestiti per il libro.
     */
    List<Loan> getByBookIsbnContaining(String bookIsbn);

    /**
     * @brief Registra un prestito chiesto da un utente per un libro.
     *  specificando la data di inizio del prestito e di restituzione
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro dato in prestito.
     * @param start La data di inizio del prestito.
     * @param deadline La data di restituzione massima.
     * @pre userId deve rispettare il formato matricola valido.
     * @pre bookIsbn deve rispettare il formato ISBN valido.
     * @pre Non deve esistere un prestito attivo per la coppia (userId, bookIsbn).
     * @post Viene registrato un nuovo prestito.
     * @throws LoanAlreadyRegisteredException Il prestito per l'utente ed il libro specificati
     *  è già esistente.
     * @throws InvalidIdException Se la matricola non è valida.
     * @throws InvalidIsbnException Se l'ISBN non è valido.
     */
	void register(String userId, String bookIsbn, LocalDate start, LocalDate deadline)
            throws LoanAlreadyRegisteredException, InvalidIdException, InvalidIsbnException;

    /**
     * @brief Registra la restituzione di un libro da parte di un utente.
     * @param userId La matricola dell'utente che ha restituito il libro.
     * @param bookIsbn L'ISBN del libro restituito.
     * @param end Data di restituzione del libro.
     * @throws UnknownLoanException Il prestito tra utente e libro specificati è inesistente.
     */
    void complete(String userId, String bookIsbn, LocalDate end) throws UnknownLoanException;

    /**
     * @brief Verifica se un utente ha preso in prestito un libro che non
     *  ha ancora restituito.
     * @param userId La matricola dell'utente che ha chiesto il prestito.
     * @param bookIsbn L'ISBN del libro preso in prestito.
     * @return true se l'utente ha preso in prestito il libro, false altrimenti.
     */
    boolean isActive(String userId, String bookIsbn);

    /**
     * @brief Conta il numero di prestiti fatti da un utente.
     * @param userId La matricola dell'utente da controllare.
     * @return Il numero di prestiti attualmente attivi.
     */
    int countById(String userId) throws InvalidIdException;

    /**
     * @brief Recupera una lista di tutti i prestiti registrati attualmente attivi per
     * l'utente specificato.
     * @param userId La matricola dell'utente di cui si vogliono conoscere i prestiti
     *               attivi.
     * @return Una lista contenente i prestiti attivi per l'utente.
     */
    List<Loan> getActiveByUserId(String userId);

};
