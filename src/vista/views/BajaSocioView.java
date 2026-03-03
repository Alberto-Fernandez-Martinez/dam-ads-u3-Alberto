package vista.views;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modelo.Socio;
import servicio.ClubDeportivo;

public class BajaSocioView extends GridPane {
    public BajaSocioView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Socio> id = new ComboBox<>();
        Button baja = new Button("Dar de baja");

        id.getItems().setAll(club.getSocios());

        addRow(0, new Label("Socio"), id);
        add(baja, 1, 1);

        baja.setOnAction(e -> {
            try {
                if (id.getValue() == null) {
                    showError("Selecciona un socio.");
                    return;
                }
                boolean ok = club.bajaSocio(id.getValue().getIdSocio());
                if (ok) {
                    showInfo("Socio dado de baja correctamente.");
                    id.getItems().setAll(club.getSocios());
                    id.getSelectionModel().clearSelection();
                } else {
                    showError("No se pudo dar de baja.");
                }
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

