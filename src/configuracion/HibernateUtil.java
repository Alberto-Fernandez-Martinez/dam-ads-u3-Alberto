package configuracion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class HibernateUtil {

    private static final String PERSISTENCE_UNIT = "clubDamaPU";
    private static final EntityManagerFactory EMF = buildEntityManagerFactory();

    private HibernateUtil() {
        // Utility class
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        } catch (Throwable ex) {
            System.err.println("Error inicializando EntityManagerFactory con " + PERSISTENCE_UNIT);
            ex.printStackTrace(System.err);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF;
    }

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
