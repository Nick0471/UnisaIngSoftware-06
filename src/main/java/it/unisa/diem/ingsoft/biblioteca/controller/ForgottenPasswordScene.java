package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_PASSWORD_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.LOGIN_PATH;


public class ForgottenPasswordScene extends GuiController {

    private PasswordService passwordService;

    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.passwordService = serviceRepository.getPasswordService();
    }

    @FXML
    private Button btnCancel;


    @FXML
    private Button btnVerify;




    public void handleVerify(ActionEvent event){

        changeScene(event,EDIT_PASSWORD_PATH);
    }

    public void handleCancel(ActionEvent event){
        changeScene(event,LOGIN_PATH);
    }
}
