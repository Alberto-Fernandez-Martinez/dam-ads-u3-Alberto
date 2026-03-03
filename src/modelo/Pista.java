package modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;



@Entity
@Table(name = "pistas")
public class Pista {
    @Id
    @Column(name = "id_pista", nullable = false, length = 36)
    private String idPista;

    @Column(name = "deporte", nullable = false, columnDefinition = "enum('tenis','pádel','fútbol sala')")
    private String deporte;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "disponible", nullable = false)
    private boolean disponible;



    protected Pista() {

    }

    public Pista(String idPista, String deporte, String descripcion, boolean disponible) throws IdObligatorioException {
        if (idPista == null || idPista.isBlank()) {
            throw new IdObligatorioException("El id de la pista no puede ser vacio");
        }
        this.idPista = idPista;
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.disponible = disponible;
    }

    public String getIdPista() {
        return idPista;
    }

    public String getDeporte() {
        return deporte;
    }



    public String getDescripcion() {
        return descripcion;
    }



    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }


    @Override
    public String toString() {
        String estado = disponible ? "Disponible" : "No disponible";
        return idPista + " - " + deporte + " (" + estado + ")";
    }
}
