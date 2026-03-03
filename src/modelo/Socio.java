package modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "socios")
public class Socio {
    @Id
    @Column(name = "id_socio", nullable = false, length = 36)
    private String idSocio;

    @Column(name = "dni", nullable = false, length = 16, unique = true)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 120)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 120, unique = true)
    private String email;

    @OneToMany(mappedBy = "socio")
    private List<Reserva> reservas = new ArrayList<>();

    protected Socio() {

    }

    public Socio(String idSocio, String dni, String nombre, String apellidos, String telefono, String email) throws IdObligatorioException {
        this.idSocio = validarObligatorio(idSocio, "idSocio");
        this.dni = validarObligatorio(dni, "dni");
        this.nombre = validarObligatorio(nombre, "nombre");
        this.apellidos = validarObligatorio(apellidos, "apellidos");
        this.telefono = validarTelefono(telefono);
        this.email = validarObligatorio(email, "email");
    }

    public String getIdSocio() {
        return idSocio;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = validarObligatorio(dni, "dni");
    }

    public String getNombre() {
        return nombre;
    }



    public String getApellidos() {
        return apellidos;
    }



    public String getTelefono() {
        return telefono;
    }



    public String getEmail() {
        return email;
    }



    public List<Reserva> getReservas() {
        return reservas;
    }

    @Override
    public String toString() {
        return idSocio + " - " + nombre + " " + apellidos;
    }

    private static String validarObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IdObligatorioException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    private static String validarTelefono(String telefono) {
        String telefonoLimpio = validarObligatorio(telefono, "telefono");
        if (!telefonoLimpio.matches("\\d+")) {
            throw new IdObligatorioException("El telefono solo puede tener numeros");
        }
        return telefonoLimpio;
    }
}
