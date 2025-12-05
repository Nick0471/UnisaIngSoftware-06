package it.unisa.diem.ingsoft.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class AccountUserController extends GuiController  {


    @FXML
    private Button btnClose;


    private void handleClose(ActionEvent event) {
       changeScene(event, "view/UserScene.fxml");
   }




}
