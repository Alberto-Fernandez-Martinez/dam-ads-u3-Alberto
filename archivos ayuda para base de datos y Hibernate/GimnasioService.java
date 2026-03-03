package servicio;

import entidades.Actividade;
import entidades.Cliente;
import entidades.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;

public class GimnasioService {
    private EntityManagerFactory emf;
    public GimnasioService() {
        emf = Persistence.createEntityManagerFactory("repasoJPA");
    }
    public boolean insertarReserva(Integer idCliente, Integer idActividad) {
        EntityManager em = emf.createEntityManager();
        Cliente cliente=em.find(Cliente.class, idCliente);
        Actividade actividad=em.find(Actividade.class, idActividad);
        if (cliente==null || actividad==null){
            em.close();
            return false;
        }
        else {
            Reserva reserva = new Reserva(actividad, cliente, LocalDate.now(), null);
            em.getTransaction().begin();
            em.persist(reserva);
            em.getTransaction().commit();
            em.close();
            return true;
        }

    }
    public List<Reserva> resrvasActiva(){
        EntityManager em = emf.createEntityManager();
        Query consulta =em.createNamedQuery("ReservasActivas");
        return consulta.getResultList();
    }
    public List<Reserva> listarReservaPorClinete(int idCliente){
        EntityManager em = emf.createEntityManager();
        Query consulta=em.createQuery("Select r from Reserva r join fetch r.cliente join fetch r.actividad where r.cliente.id=:idCliente");
        consulta.setParameter("idCliente",idCliente);
        List<Reserva> listaReservas= consulta.getResultList();
        em.close();
        return listaReservas;

    }

}
