package it.unisa.diem.ingsoft.biblioteca;

import java.io.IOException;
import java.net.URL;

import it.unisa.diem.ingsoft.biblioteca.controller.GuiController;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * @brief Classe di utility per creare FXMLLoader e per prendere la root dato un loader
 */
public class Scenes {

    /**
     * @brief Restituisce un FXMLLoader per una view al cui GuiController viene passata la reference
     *  ad un {@link ServiceRepository}.
     * @return L'FXMLLoader creato.
     * @throws RuntimeException Se il controller della view non e' un GuiController.
     */
    public static FXMLLoader setupLoader(String viewPath, ServiceRepository serviceRepository) {
        URL loginUrl = App.class.getResource(viewPath);
        FXMLLoader loader = new FXMLLoader(loginUrl);

        Object controller = loader.getController();
        if (!(controller instanceof GuiController)) {
            throw new RuntimeException("Il controller non estende la classe GuiController!");
        }

        GuiController guiController = (GuiController) controller;
        guiController.setServices(serviceRepository);

        return loader;
    }

    /**
     * @brief Restituisce la root di una scena dato un FXMLLoader.
     * @return La root della scena.
     * @throws RuntimeException Se c'e' stato un errore nella lettura della view dal disco.
     */
    public static Parent getRoot(FXMLLoader loader) {
        Parent root;

		try {
			root = loader.load();
		} catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura della view: "
                    + loader.getLocation(), e);
		}

        return root;
    }
}
