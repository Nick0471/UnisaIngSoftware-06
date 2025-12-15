package it.unisa.diem.ingsoft.biblioteca;

import java.io.IOException;
import java.net.URL;

import it.unisa.diem.ingsoft.biblioteca.controller.GuiController;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;

/**
 * @brief Classe di utility per creare FXMLLoader e per prendere la root dato un loader
 */
public class Scenes {
    /**
     * @brief Restituisce un FXMLLoader per una view al cui GuiController viene passata la reference
     *  ad un {@link ServiceRepository}.
     *  Necessario per inizializzare un controller che usa i servizi del backend.
     * @return L'FXMLLoader creato.
     * @throws RuntimeException Se il controller della view non e' un GuiController.
     */
    public static FXMLLoader setupLoader(String viewPath, ServiceRepository serviceRepository) {
        URL loginUrl = Scenes.class.getResource(viewPath);
        FXMLLoader loader = new FXMLLoader(loginUrl);

		try {
			loader.load();
		} catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura della view: "
                    + loader.getLocation(), e);
		}

        Object controller = loader.getController();
        if (!(controller instanceof GuiController)) {
            throw new RuntimeException("Il controller non estende la classe GuiController");
        }

        GuiController guiController = (GuiController) controller;
        guiController.setServices(serviceRepository);

        return loader;
    }
}
