package vista.views;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import servicio.ClubDeportivo;

import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;

public class DashboardView extends BorderPane {
    public DashboardView(ClubDeportivo club) {
        setPadding(new Insets(10));
        setPrefSize(960, 640);

        TableView<Socio> tablaSocios = new TableView<>();
        TableColumn<Socio, String> s1 = new TableColumn<>("idSocio");
        s1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdSocio()));
        TableColumn<Socio, String> s2 = new TableColumn<>("dni");
        s2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getDni()));
        TableColumn<Socio, String> s3 = new TableColumn<>("nombre");
        s3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<Socio, String> s4 = new TableColumn<>("apellidos");
        s4.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getApellidos()));
        TableColumn<Socio, String> s5 = new TableColumn<>("telefono");
        s5.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTelefono()));
        TableColumn<Socio, String> s6 = new TableColumn<>("email");
        s6.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getEmail()));
        tablaSocios.getColumns().addAll(s1, s2, s3, s4, s5, s6);
        tablaSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaSocios.getItems().addAll(club.getSocios());

        TableView<Pista> tablaPistas = new TableView<>();
        TableColumn<Pista, String> p1 = new TableColumn<>("idPista");
        p1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdPista()));
        TableColumn<Pista, String> p2 = new TableColumn<>("deporte");
        p2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getDeporte()));
        TableColumn<Pista, String> p3 = new TableColumn<>("descripcion");
        p3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<Pista, String> p4 = new TableColumn<>("disponible");
        p4.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.valueOf(p.getValue().isDisponible())));
        tablaPistas.getColumns().addAll(p1, p2, p3, p4);
        tablaPistas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaPistas.getItems().addAll(club.getPistas());

        TableView<Reserva> tablaReservas = new TableView<>();
        TableColumn<Reserva, String> r1 = new TableColumn<>("idReserva");
        r1.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdReserva()));
        TableColumn<Reserva, String> r2 = new TableColumn<>("idSocio");
        r2.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdSocio()));
        TableColumn<Reserva, String> r3 = new TableColumn<>("idPista");
        r3.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getIdPista()));
        TableColumn<Reserva, String> r4 = new TableColumn<>("fecha");
        r4.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getFecha().toString()));
        TableColumn<Reserva, String> r5 = new TableColumn<>("horaInicio");
        r5.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getHoraInicio().toString()));
        TableColumn<Reserva, String> r6 = new TableColumn<>("duracionMin");
        r6.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.valueOf(p.getValue().getDuracionMin())));
        TableColumn<Reserva, String> r7 = new TableColumn<>("precio");
        r7.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(String.valueOf(p.getValue().getPrecio())));
        tablaReservas.getColumns().addAll(r1, r2, r3, r4, r5, r6, r7);
        tablaReservas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaReservas.getItems().addAll(club.getReservas());

        VBox panelSocios = new VBox(4, new Label("Socios"), tablaSocios);
        VBox panelPistas = new VBox(4, new Label("Pistas"), tablaPistas);
        HBox filaSuperior = new HBox(8, panelSocios, panelPistas);
        HBox.setHgrow(panelSocios, Priority.ALWAYS);
        HBox.setHgrow(panelPistas, Priority.ALWAYS);
        panelSocios.setPrefWidth(750);
        panelPistas.setPrefWidth(400);
        tablaSocios.setPrefHeight(320);
        tablaPistas.setPrefHeight(320);

        VBox panelReservas = new VBox(4, new Label("Reservas"), tablaReservas);
        tablaReservas.setPrefHeight(320);
        VBox.setVgrow(tablaReservas, Priority.ALWAYS);

        VBox contenido = new VBox(8, filaSuperior, panelReservas);
        VBox.setVgrow(filaSuperior, Priority.ALWAYS);
        VBox.setVgrow(panelReservas, Priority.ALWAYS);

        setCenter(contenido);
    }
}
