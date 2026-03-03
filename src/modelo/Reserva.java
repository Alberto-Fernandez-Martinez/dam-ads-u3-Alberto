package modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @Column(name = "id_reserva", nullable = false, length = 36)
    private String idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pista", nullable = false)
    private Pista pista;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "duracion_min", nullable = false)
    private int duracionMin;

    @Column(name = "precio", nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Transient
    private String socioRefId;

    @Transient
    private String pistaRefId;

    protected Reserva() {
        // Required by JPA
    }

    public Reserva(String idReserva, String idSocio, String idPista, LocalDate fecha, LocalTime horaInicio, int duracionMin, double precio) throws IdObligatorioException {
        if (idReserva == null || idReserva.isBlank()) {
            throw new IdObligatorioException("idReserva obligatorio");
        }
        if (idSocio == null || idSocio.isBlank()) {
            throw new IdObligatorioException("idSocio obligatorio");
        }
        if (idPista == null || idPista.isBlank()) {
            throw new IdObligatorioException("idPista obligatorio");
        }
        if (fecha == null) {
            throw new IdObligatorioException("fecha obligatoria");
        }
        if (horaInicio == null) {
            throw new IdObligatorioException("horaInicio obligatoria");
        }
        if (duracionMin <= 0) {
            throw new IdObligatorioException("duracion debe ser > 0");
        }
        if (precio < 0) {
            throw new IdObligatorioException("precio debe ser >= 0");
        }

        this.idReserva = idReserva;
        this.socioRefId = idSocio;
        this.pistaRefId = idPista;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.duracionMin = duracionMin;
        this.precio = BigDecimal.valueOf(precio);
    }

    public String getIdReserva() {
        return idReserva;
    }

    public String getIdSocio() {
        if (socio != null) {
            return socio.getIdSocio();
        }
        return socioRefId;
    }

    public String getIdPista() {
        if (pista != null) {
            return pista.getIdPista();
        }
        return pistaRefId;
    }



    public void setSocio(Socio socio) {
        this.socio = socio;
        this.socioRefId = socio != null ? socio.getIdSocio() : null;
    }

    public Pista getPista() {
        return pista;
    }

    public void setPista(Pista pista) {
        this.pista = pista;
        this.pistaRefId = pista != null ? pista.getIdPista() : null;
    }

    public LocalDate getFecha() {
        return fecha;
    }



    public LocalTime getHoraInicio() {
        return horaInicio;
    }



    public int getDuracionMin() {
        return duracionMin;
    }



    public double getPrecio() {
        return precio.doubleValue();
    }



    public LocalTime getHoraFin() {
        return horaInicio.plusMinutes(duracionMin);
    }

    @Override
    public String toString() {
        return idReserva + " - " + fecha + " " + horaInicio + " (" + getIdPista() + ")";
    }
}
