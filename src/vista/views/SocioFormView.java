package vista.views;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import modelo.Socio;
import servicio.ClubDeportivo;

public class SocioFormView extends GridPane {
    public SocioFormView(ClubDeportivo club) {
        setPadding(new Insets(12));
        setHgap(8);
        setVgap(8);

        TextField id = new TextField();
        TextField dni = new TextField();
        TextField nombre = new TextField();
        TextField apellidos = new TextField();
        TextField tel = new TextField();
        TextField email = new TextField();
        Button crear = new Button("Crear");

        tel.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null
        ));

        addRow(0, new Label("idSocio*"), id);
        addRow(1, new Label("DNI*"), dni);
        addRow(2, new Label("Nombre*"), nombre);
        addRow(3, new Label("Apellidos*"), apellidos);
        addRow(4, new Label("Telefono*"), tel);
        addRow(5, new Label("Email*"), email);
        add(crear, 1, 6);

        crear.setOnAction(e -> {
            try {
                boolean ok = club.altaSocio(new Socio(
                        id.getText(),
                        dni.getText(),
                        nombre.getText(),
                        apellidos.getText(),
                        tel.getText(),
                        email.getText()
                ));
                if (ok) {
                    showInfo("Socio insertado correctamente.");
                    id.clear();
                    dni.clear();
                    nombre.clear();
                    apellidos.clear();
                    tel.clear();
                    email.clear();
                } else {
                    showError("Socio no insertado");
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

