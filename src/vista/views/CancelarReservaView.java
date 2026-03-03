package vista.views;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modelo.Reserva;
import servicio.ClubDeportivo;

public class CancelarReservaView extends GridPane {
    public CancelarReservaView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Reserva> id = new ComboBox<>();
        Button cancelar = new Button("Cancelar reserva");

        id.getItems().setAll(club.getReservas());

        addRow(0, new Label("Reserva"), id);
        add(cancelar, 1, 1);

        cancelar.setOnAction(e -> {
            try {
                if (id.getValue() == null) {
                    showError("Selecciona una reserva.");
                    return;
                }
                boolean ok = club.cancelarReserva(id.getValue().getIdReserva());
                if (ok) {
                    showInfo("Reserva cancelada correctamente.");
                    id.getItems().setAll(club.getReservas());
                    id.getSelectionModel().clearSelection();
                } else {
                    showError("No se pudo cancelar la reserva.");
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

