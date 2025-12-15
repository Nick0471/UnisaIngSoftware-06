/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_PASSWORD_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

/**
 * @brief Controller per la modifica delle domande di sicurezza.
 *
 * Permette all'utente loggato di aggiornare le risposte alle domande di sicurezza
 * necessarie per il recupero della password.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class UpdateSecurityQuestionsController extends GuiController {

    @FXML private TextField answer1Field;
    @FXML private TextField answer2Field;
    @FXML private TextField answer3Field;

    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    private AuthService passwordService;

    /**
     * @brief Costruttore vuoto del controller.
     */
    public UpdateSecurityQuestionsController() {}

    /**
     * @brief Setter per i servizi necessari.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il PasswordService
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.passwordService = serviceRepository.getPasswordService();
    }

    /**
     * @brief Gestisce il salvataggio delle nuove risposte di sicurezza.
     *
     * Verifica che i campi non siano vuoti, invoca il servizio per l'aggiornamento,
     * mostra un popup di conferma e reindirizza alla Homepage.
     *
     * @param event L'evento generato dal click sul pulsante "Salva Risposte".
     */
    @FXML
    private void handleSaveAnswers(ActionEvent event) {
        String ans1 = this.answer1Field.getText();
        String ans2 = this.answer2Field.getText();
        String ans3 = this.answer3Field.getText();

        if (ans1.isEmpty() && ans2.isEmpty() && ans3.isEmpty()) {
            super.popUp(Alert.AlertType.WARNING, "Nessuna modifica", "Compila almeno un campo per aggiornare le risposte.");
            return;
        }

        try {
            if(!ans1.isEmpty()) {
                this.passwordService.changeAnswer(ans1, 1);
            }

            if(!ans2.isEmpty()) {
                this.passwordService.changeAnswer(ans2, 2);
            }

            if(!ans3.isEmpty()) {
                this.passwordService.changeAnswer(ans3, 3);
            }

            super.popUp(Alert.AlertType.INFORMATION, "Operazione completata", "Le domande di sicurezza sono state aggiornate con successo.");

            super.changeScene(event, HOMEPAGE_PATH);

        } catch (Exception e) {
            super.popUp(Alert.AlertType.ERROR, "Errore salvataggio", "Impossibile aggiornare i dati: " + e.getMessage());
        }
    }

    /**
     * @brief Gestisce l'annullamento dell'operazione.
     *
     * Ritorna alla scena di modifica password (Gestione Password).
     *
     * @param event L'evento generato dal click sul pulsante "Annulla".
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        super.changeScene(event, EDIT_PASSWORD_PATH);
    }
}