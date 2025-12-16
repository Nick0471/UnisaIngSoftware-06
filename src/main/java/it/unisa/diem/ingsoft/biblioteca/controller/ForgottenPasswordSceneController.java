/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */

package it.unisa.diem.ingsoft.biblioteca.controller;


import static it.unisa.diem.ingsoft.biblioteca.Views.LOGIN_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.NEW_PASSWORD_PATH;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetAnswerException;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * @brief Controller per gestire il recupero della password.
 *
 * Permette all'utente di rispondere alle domande di sicurezza per verificare
 * la propria identità e procedere al reset della password.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class ForgottenPasswordSceneController extends GuiController {

    private  AuthService authService;

    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.authService = serviceRepository.getAuthService();
    }

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnVerify;

    @FXML
    private TextField answer1Field;


    @FXML
    private TextField answer2Field;

    @FXML
    private TextField answer3Field;



    /**
     * @brief Gestisce la verifica delle risposte di sicurezza.
     *
     * Se la verifica ha successo, passa alla scena di creazione nuova password.
     *
     * @param event L'evento generato dal click sul pulsante di verifica.
     */
    @FXML
    public void handleVerify(ActionEvent event){

        try {
            String tf1 = this.answer1Field.getText().trim();
            String tf2 = this.answer2Field.getText().trim();
            String tf3 = this.answer3Field.getText().trim();

            if(tf1.isEmpty() && tf2.isEmpty() && tf3.isEmpty()) {
                this.popUp(Alert.AlertType.WARNING, "Valutazione password", "Completa tutti i campi obligatori");
                return;
            }

            boolean a1 = this.authService.checkAnswer(tf1, 1);
            boolean a2 = this.authService.checkAnswer(tf2, 2);
            boolean a3 = this.authService.checkAnswer(tf3, 3);


            if (a1 && a2 && a3)
                this.changeScene(event, NEW_PASSWORD_PATH);
            else {
                this.popUp(Alert.AlertType.WARNING, "Valutazione password", "I campi non sono corretti");
                return;
            }
        }catch (UnsetAnswerException e){
            this.popUp(Alert.AlertType.ERROR, "Errore Dati", e.getMessage());
        }

    }

    /**
     * @brief Annulla l'operazione di recupero.
     *
     */
    @FXML
    public void handleCancel(ActionEvent event){this.changeScene(event,LOGIN_PATH);
    }
}
