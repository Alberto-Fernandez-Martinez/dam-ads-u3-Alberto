package vista.views;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;
import servicio.ClubDeportivo;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaFormView extends GridPane {
    public ReservaFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        ComboBox<Socio> idSocio = new ComboBox<>();
        ComboBox<Pista> idPista = new ComboBox<>();
        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField hora = new TextField("10:00");
        Spinner<Integer> duracion = new Spinner<>(30, 300, 60, 30);
        TextField precio = new TextField("10.0");
        Button crear = new Button("Reservar");

        idSocio.getItems().setAll(club.getSocios());
        idPista.getItems().setAll(club.getPistas());

        addRow(0, new Label("idReserva*"), id);
        addRow(1, new Label("Socio*"), idSocio);
        addRow(2, new Label("Pista*"), idPista);
        addRow(3, new Label("Fecha*"), fecha);
        addRow(4, new Label("Hora inicio* (HH:mm)"), hora);
        addRow(5, new Label("Duracion (min)"), duracion);
        addRow(6, new Label("Precio (EUR)"), precio);
        add(crear, 1, 7);

        crear.setOnAction(e -> {
            try {
                if (idSocio.getValue() == null || idPista.getValue() == null) {
                    showError("Debes seleccionar socio y pista.");
                    return;
                }

                LocalTime t = LocalTime.parse(hora.getText());
                Reserva r = new Reserva(
                        id.getText(),
                        idSocio.getValue().getIdSocio(),
                        idPista.getValue().getIdPista(),
                        fecha.getValue(),
                        t,
                        duracion.getValue(),
                        Double.parseDouble(precio.getText())
                );

                boolean ok = club.crearReserva(r);
                if (ok) {
                    showInfo("Reserva creada correctamente.");
                    id.clear();
                } else {
                    showError("Reserva no creada.");
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

