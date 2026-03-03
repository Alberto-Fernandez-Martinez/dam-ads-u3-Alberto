package vista.views;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modelo.Pista;
import servicio.ClubDeportivo;

public class CambiarDisponibilidadView extends GridPane {
    public CambiarDisponibilidadView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        ComboBox<Pista> id = new ComboBox<>();
        CheckBox disponible = new CheckBox("Disponible");
        Button cambiar = new Button("Aplicar");

        id.getItems().setAll(club.getPistas());

        id.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                disponible.setSelected(newV.isDisponible());
            }
        });

        addRow(0, new Label("idPista"), id);
        addRow(1, new Label("Estado"), disponible);
        add(cambiar, 1, 2);

        cambiar.setOnAction(e -> {
            try {
                if (id.getValue() == null) {
                    showError("Selecciona una pista.");
                    return;
                }
                boolean ok = club.cambiarDisponibilidadPista(id.getValue().getIdPista(), disponible.isSelected());
                if (ok) {
                    showInfo("Disponibilidad actualizada correctamente.");
                    id.getItems().setAll(club.getPistas());
                } else {
                    showError("No se pudo cambiar la disponibilidad.");
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

