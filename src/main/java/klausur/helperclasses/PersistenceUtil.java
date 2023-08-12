package klausur.helperclasses;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.hibernate.query.Query;

/**
 * Diese Klasse dient zur Vereinfachung der Nutzung von Hibernate ORM.
 * Sie bietet rudimentäre Funktionen für das Management einer SessionFactory
 * sowie für einfaches Schreiben/Lesen in die/aus der Datenbank.
 *
 * Diese Klasse ist neu und nur ein einem Projekt getestet.
 * Zudem ist sie nicht auf Laufzeit oder Threadsicherheit optimiert.
 *
 * Sie dient zu Lehrzwecken und kann nach Belieben verändert werden.
 * Verbesserungsvorschläge und Erweiterungen sind willkommen!
 *
 * Kompatibel unter anderem mit Hibernate ORM core final 6.1.0, Java 11
 *
 * @author		Martin Schulten
 * @version		0.2
 *
<dependency>
<groupId>jakarta.persistence</groupId>
<artifactId>jakarta.persistence-api</artifactId>
<version>3.1.0</version>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-core</artifactId>
<version>6.1.0.Final</version>
</dependency>
 */
public class PersistenceUtil<BeanType> {

	private static SessionFactory sessionFactory;
	private final Class<BeanType> beanTypeClass;
	private final Lock lock = new ReentrantLock();
	/**
	 * Konstruktor mit type token.
	 */
	public PersistenceUtil(Class<BeanType> beanTypeClass) {
		super();
		this.beanTypeClass = beanTypeClass;
	}

	/**
	 * Liest Konfiguration aus hibernate.cfg.xml und erstellt damit eine SessionFactory.
	 *
	 * @return	SessionFactory
	 */
	private SessionFactory createSessionFactory() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return sessionFactory;
	}

	/**
	 * Getter für SessionFactory.
	 *
	 * @return	SessionFactory
	 */
	private SessionFactory getSessionFactory() {
		return createSessionFactory();
	}

	/**
	 * Schließt SessionFactory. Sollte abschließend aufgerufen werden,
	 * da sonst Memory/Resource Leaks entstehen können.
	 */
	private void closeSessionFactory() {
		if (sessionFactory != null)
			sessionFactory.close();
	}

	/**
	 * Speichert oder aktualisiert ein Obkjekt in der Datenbank.
	 *
	 * @param <T>	Typparameter für Bean
	 * @param bean	Bean-Instanz
	 */
	public void saveOrUpdate(BeanType bean) {

		lock.lock();
		try {

			Session hib_session = getSessionFactory().getCurrentSession();
			hib_session.beginTransaction();
			hib_session.flush();
			hib_session.clear();


			hib_session.merge(bean);

			hib_session.getTransaction().commit();
			hib_session.close();
			closeSessionFactory();
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Lädt alle Beans eines bestimmten Typs aus der Datenbank.
	 * <pre>{@code
	 * Aufrufbeispiel: List<MyBean> dbresults = hibUtil.obtainAll();
	 * }</pre>
	 *
	 * @param <BeanType>		Klasse des Beans
	 * @return          		Liste der Beans
	 */
	public List<BeanType> obtainAll() {
		Session hib_session = getSessionFactory().getCurrentSession();
		hib_session.beginTransaction();

		CriteriaQuery<BeanType> critquery = hib_session.getCriteriaBuilder().createQuery(beanTypeClass);
		Root<BeanType> root = critquery.from(beanTypeClass);
		critquery.select(root);
		List<BeanType> dbresults = hib_session.createQuery(critquery).getResultList();

		hib_session.getTransaction().commit();
		hib_session.close();
		closeSessionFactory();
		return dbresults;
	}

	/**
	 * Lädt alle Beans eines bestimmten Typs aus der Datenbank, die die "where"-Bedingung erfüllen.
	 * <pre>{@code
	 * Aufrufbeispiel:  List<MyBean> dbresults =
	 * 					hibUtil.obtainWhere("vorname", String.class, "Max");
	 * }</pre>
	 * @param <BeanType>		Klasse des Beans
	 * @param propertyName		Bezeichnung eines Propertys des Beans
	 * @param propertyClass		Klassenbeschreibungsobjekt (.class) der Klasse eines Propertys des Beans
	 * @param propertyValue		Wert des Propertys des Beans, mit dem selektiert werden soll
	 * @return          		Liste der Beans, die die Bedingung erfüllen
	 */
	public List<BeanType> obtainWhere(String propertyName, Class<?> propertyClass, Object propertyValue) {

		Session hib_session = getSessionFactory().getCurrentSession();
		hib_session.beginTransaction();

		CriteriaBuilder builder = hib_session.getCriteriaBuilder();
		CriteriaQuery<BeanType> critquery = builder.createQuery(beanTypeClass);
		Root<BeanType> root = critquery.from(beanTypeClass);
		critquery.select(root).where(builder.equal(root.get(propertyName), propertyClass.cast(propertyValue)));

		//critquery.select(root).where(builder.equal(root.get("vorname"), String.class.cast("Max")));
		// Es können weitere Filter angehängt werden .where(...);

		List<BeanType> dbresults = hib_session.createQuery(critquery).getResultList();

		hib_session.getTransaction().commit();
		hib_session.close();
		closeSessionFactory();
		return dbresults;
	}

	/**
	 * Deletes all beans of a specific type from the database.
	 *
	 * @param <BeanType> the class of the beans
	 */
	public void resetDatabase() {
		Session hib_session = getSessionFactory().getCurrentSession();
		hib_session.beginTransaction();

		CriteriaBuilder builder = hib_session.getCriteriaBuilder();
		CriteriaDelete<BeanType> deleteQuery = builder.createCriteriaDelete(beanTypeClass);
		Root<BeanType> root = deleteQuery.from(beanTypeClass);
		deleteQuery.where(builder.isNotNull(root));

		int deletedCount = hib_session.createQuery(deleteQuery).executeUpdate();

		hib_session.getTransaction().commit();
		hib_session.close();
		closeSessionFactory();

		System.err.println("Deleted " + deletedCount + " beans from the database.");
	}

}