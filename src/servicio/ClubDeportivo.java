package servicio;

import configuracion.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import modelo.Pista;
import modelo.Reserva;
import modelo.Socio;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio principal del dominio del club deportivo.
 *
 *  Responsabilidades de esta clase:
 *  Gestionar el acceso a datos con JPA/Hibernate.
 *  Aplicar reglas de negocio antes de persistir cambios.
 *  Ejecutar transacciones por operacion de escritura.
 *  Mantener listas en memoria para alimentar la UI de forma simple.
 *
 * Importante:
 * La capa de vistas JavaFX debe usar solo estos metodos publicos
 * y no implementar validaciones de negocio por su cuenta.
 */
public class ClubDeportivo {

    //////////////////////////////////////////////////// ESTADO Y CONFIGURACION /////////////////////////////////////////

    private final EntityManagerFactory emf;
    private final ArrayList<Socio> socios;
    private final ArrayList<Pista> pistas;
    private final ArrayList<Reserva> reservas;
    private boolean cambiosPendientes;

    //////////////////////////////////////////////////// CONSTRUCTOR E INICIALIZACION ///////////////////////////////////

    /**
     * Construye el servicio y valida que la capa JPA funciona.
     *
     *  Flujo de inicializacion:</p>
     *  Obtiene el {@link EntityManagerFactory} desde {@code HibernateUtil}.
     *  Abre un {@link EntityManager} de prueba para validar conexion.
     *  Ejecuta {@code SELECT 1} para comprobar acceso real a BD.
     *  Carga socios, pistas y reservas en memoria.
     *
     * @throws SQLException si no se puede inicializar la persistencia.
     */
    public ClubDeportivo() throws SQLException {
        try {
            emf = HibernateUtil.getEntityManagerFactory();
            socios = new ArrayList<>();
            pistas = new ArrayList<>();
            reservas = new ArrayList<>();
            try (EntityManager em = HibernateUtil.createEntityManager()) {
                em.createNativeQuery("SELECT 1").getSingleResult();
            }
            cargarDatosDesdeBd();
        } catch (Exception ex) {
            throw new SQLException("No se pudo inicializar la persistencia JPA.", ex);
        }
    }

    //////////////////////////////////////////////////// CIERRE //////////////////////////////////////////////////////////

    /**
     * Cierra la fabrica JPA al cerrar la aplicacion.
     *
     * Se recomienda llamarlo desde  para
     * liberar correctamente conexiones y recursos internos.
     */
    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            HibernateUtil.shutdown();
        }
    }

  

    //////////////////////////////////////////////////// GETTERS PRINCIPALES ///////////////////////////////////////////

    /**
     * Devuelve los socios disponibles para la capa de vistas.
     *
     * Se devuelve una copia de la lista en memoria para no exponer
     * directamente la referencia interna.
     *
     * @return lista de socios para UI y validaciones de negocio.
     */
    public ArrayList<Socio> getSocios() {
        return new ArrayList<>(socios);
    }

    /**
     * Devuelve las pistas disponibles para la capa de vistas.
     *
     * Se devuelve una copia de la lista en memoria para evitar
     * modificaciones externas no controladas.
     *
     * @return lista de pistas para UI y validaciones.
     */
    public ArrayList<Pista> getPistas() {
        return new ArrayList<>(pistas);
    }

    /**
     * Devuelve las reservas actuales para la UI.
     *
     * La lista se carga desde BD con  en el metodo
     * de recarga para evitar problemas de carga diferida al pintar tablas.
     *
     * @return lista de reservas ordenadas por fecha y hora.
     */
    public ArrayList<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    /**
     * Recarga completamente el estado en memoria desde la base de datos.
     *
     * Uso principal:
     *  Carga inicial al arrancar el servicio.
     *  Refresco tras cada operacion con commit.
     *
     * Este metodo limpia las listas internas y vuelve a cargar:
     *  socios ordenados por nombre/apellidos
     *  pistas ordenadas por id
     *  reservas con socio y pista usando
     */
    private void cargarDatosDesdeBd() {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            socios.clear();
            pistas.clear();
            reservas.clear();

            socios.addAll(em.createQuery(
                    "SELECT s FROM Socio s ORDER BY s.nombre, s.apellidos",
                    Socio.class
            ).getResultList());

            pistas.addAll(em.createQuery(
                    "SELECT p FROM Pista p ORDER BY p.idPista",
                    Pista.class
            ).getResultList());

            reservas.addAll(em.createQuery(
                    "SELECT r FROM Reserva r " +
                            "JOIN FETCH r.socio " +
                            "JOIN FETCH r.pista " +
                            "ORDER BY r.fecha, r.horaInicio",
                    Reserva.class
            ).getResultList());

            cambiosPendientes = false;
        } catch (RuntimeException ex) {
            throw new RuntimeException("No se pudieron cargar los datos iniciales.", ex);
        }
    }

    //////////////////////////////////////////////////// SOCIOS //////////////////////////////////////////////////////////

    /**
     * Da de alta un nuevo socio.
     *
     * Validaciones aplicadas:
     *  El objeto socio no puede ser {@code null}.
     *  No puede existir otro socio con el mismo id.
     *  No puede existir otro socio con el mismo DNI.
     *  No puede existir otro socio con el mismo email.
     *
     * Si pasa validaciones:
     *  Abre transaccion.
     *  Persiste el socio.
     *  Hace commit.
     *  Recarga listas en memoria.
     *
     * @param socio socio a registrar.
     * @return true si se guarda correctamente.
     */
    public boolean altaSocio(Socio socio) {
        if (socio == null) {
            throw new RuntimeException("Socio obligatorio.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (em.find(Socio.class, socio.getIdSocio()) != null) {
                throw new RuntimeException("Ya existe un socio con ese id.");
            }

            Long totalDni = em.createQuery(
                    "SELECT COUNT(s) FROM Socio s WHERE LOWER(s.dni) = LOWER(:dni)",
                    Long.class
            ).setParameter("dni", socio.getDni()).getSingleResult();
            if (totalDni != null && totalDni > 0) {
                throw new RuntimeException("Ya existe un socio con ese DNI.");
            }

            Long totalEmail = em.createQuery(
                    "SELECT COUNT(s) FROM Socio s WHERE LOWER(s.email) = LOWER(:email)",
                    Long.class
            ).setParameter("email", socio.getEmail()).getSingleResult();
            if (totalEmail != null && totalEmail > 0) {
                throw new RuntimeException("Ya existe un socio con ese email.");
            }

            em.persist(socio);
            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo registrar el socio.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Da de baja un socio por id.
     *
     * Reglas de negocio:
     *  El id de socio es obligatorio.
     *  El socio debe existir.
     *  No se permite baja si tiene reservas asociadas.
     *
     * Si pasa validaciones, elimina en transaccion y recarga memoria.
     *
     * @param idSocio identificador del socio.
     * @return true si se elimina correctamente.
     */
    public boolean bajaSocio(String idSocio) {
        if (idSocio == null || idSocio.isBlank()) {
            throw new RuntimeException("Id de socio obligatorio.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Socio socioEncontrado = em.find(Socio.class, idSocio);
            if (socioEncontrado == null) {
                throw new RuntimeException("El socio no existe.");
            }

            Long totalReservas = em.createQuery(
                    "SELECT COUNT(r) FROM Reserva r WHERE r.socio.idSocio = :idSocio",
                    Long.class
            ).setParameter("idSocio", idSocio).getSingleResult();
            if (totalReservas != null && totalReservas > 0) {
                throw new RuntimeException("No se puede dar de baja: el socio tiene reservas asociadas.");
            }

            em.remove(socioEncontrado);
            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo dar de baja al socio.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    //////////////////////////////////////////////////// PISTAS //////////////////////////////////////////////////////////

    /**
     * Da de alta una nueva pista.
     *
     * Validaciones aplicadas:
     *  La pista no puede ser {@code null}.
     *  No puede existir otra pista con el mismo id.
     *
     * Si pasa validaciones, persiste en transaccion y recarga memoria.
     *
     * @param pista pista a registrar.
     * @return true si se guarda correctamente.
     */
    public boolean altaPista(Pista pista) {
        if (pista == null) {
            throw new RuntimeException("Pista obligatoria.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (em.find(Pista.class, pista.getIdPista()) != null) {
                throw new RuntimeException("Ya existe una pista con ese id.");
            }

            em.persist(pista);
            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo registrar la pista.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Cambia la disponibilidad de una pista existente.
     *
     * Flujo:
     *  Valida id obligatorio.
     *  Busca pista por id.
     *  Actualiza disponibilidad en transaccion.
     *  Recarga listas en memoria.
     *
     * @param idPista id de la pista.
     * @param disponible nuevo estado de disponibilidad.
     * @return true si se actualiza correctamente.
     */
    public boolean cambiarDisponibilidadPista(String idPista, boolean disponible) {
        if (idPista == null || idPista.isBlank()) {
            throw new RuntimeException("Id de pista obligatorio.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Pista pistaEncontrada = em.find(Pista.class, idPista);
            if (pistaEncontrada == null) {
                throw new RuntimeException("La pista no existe.");
            }

            pistaEncontrada.setDisponible(disponible);
            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo cambiar la disponibilidad de la pista.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    //////////////////////////////////////////////////// RESERVAS ////////////////////////////////////////////////////////

    /**
     * Crea una reserva con validaciones de negocio.
     *
     * Validaciones aplicadas antes de persistir:
     *  La reserva no puede ser {@code null}.
     *  El id de reserva no puede existir previamente.
     *  El socio indicado debe existir.
     *  La pista indicada debe existir.
     *  La pista debe estar disponible.
     *  No puede haber solape en la misma pista y fecha.
     *  El mismo socio no puede solapar hora en el mismo dia,
     *  aunque sea en otra pista.
     *
     * Si pasa validaciones, persiste en transaccion y recarga memoria.
     *
     * @param reserva reserva a crear.
     * @return true si se crea correctamente.
     */
    public boolean crearReserva(Reserva reserva) {
        if (reserva == null) {
            throw new RuntimeException("Reserva obligatoria.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (em.find(Reserva.class, reserva.getIdReserva()) != null) {
                throw new RuntimeException("Ya existe una reserva con ese id.");
            }

            Socio socio = em.find(Socio.class, reserva.getIdSocio());
            if (socio == null) {
                throw new RuntimeException("El socio no existe.");
            }

            Pista pista = em.find(Pista.class, reserva.getIdPista());
            if (pista == null) {
                throw new RuntimeException("La pista no existe.");
            }

            if (!pista.isDisponible()) {
                throw new RuntimeException("La pista seleccionada no esta disponible.");
            }

            List<Reserva> reservasMismaPistaDia = em.createQuery(
                    "SELECT r FROM Reserva r WHERE r.pista.idPista = :idPista AND r.fecha = :fecha",
                    Reserva.class
            ).setParameter("idPista", reserva.getIdPista())
                    .setParameter("fecha", reserva.getFecha())
                    .getResultList();

            List<Reserva> reservasMismoSocioDia = em.createQuery(
                    "SELECT r FROM Reserva r WHERE r.socio.idSocio = :idSocio AND r.fecha = :fecha",
                    Reserva.class
            ).setParameter("idSocio", reserva.getIdSocio())
                    .setParameter("fecha", reserva.getFecha())
                    .getResultList();

            LocalTime nuevaInicio = reserva.getHoraInicio();
            LocalTime nuevaFin = reserva.getHoraFin();
            for (Reserva existente : reservasMismaPistaDia) {
                boolean seSolapan = nuevaInicio.isBefore(existente.getHoraFin())
                        && existente.getHoraInicio().isBefore(nuevaFin);
                if (seSolapan) {
                    throw new RuntimeException("Existe solape con otra reserva en esa pista y fecha.");
                }
            }

            for (Reserva existente : reservasMismoSocioDia) {
                boolean seSolapan = nuevaInicio.isBefore(existente.getHoraFin())
                        && existente.getHoraInicio().isBefore(nuevaFin);
                if (seSolapan) {
                    throw new RuntimeException("El socio ya tiene otra reserva en esa fecha y hora.");
                }
            }

            reserva.setSocio(socio);
            reserva.setPista(pista);
            em.persist(reserva);

            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo crear la reserva.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Cancela una reserva por su identificador.
     *
     * Flujo:
     *  Valida id obligatorio.</p>
     *  Verifica que la reserva exista.
     *  Elimina en transaccion.
     *  Recarga listas en memoria.
     *
     * @param idReserva identificador de la reserva.
     * @return true si se elimina correctamente.
     */
    public boolean cancelarReserva(String idReserva) {
        if (idReserva == null || idReserva.isBlank()) {
            throw new RuntimeException("Id de reserva obligatorio.");
        }

        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Reserva reservaEncontrada = em.find(Reserva.class, idReserva);
            if (reservaEncontrada == null) {
                throw new RuntimeException("La reserva no existe.");
            }

            em.remove(reservaEncontrada);
            tx.commit();
            cargarDatosDesdeBd();
            cambiosPendientes = false;
            return true;
        } catch (jakarta.persistence.PersistenceException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("No se pudo cancelar la reserva.", ex);
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

}
