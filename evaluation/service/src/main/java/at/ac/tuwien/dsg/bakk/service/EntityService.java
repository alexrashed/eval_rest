package at.ac.tuwien.dsg.bakk.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.BaseEntity;

/**
 * Abstract implementation providing some methods for the JPA handling.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 * @param T
 *            Type of the entity, f.e. Articles
 */
public abstract class EntityService<T extends BaseEntity> {

	private Class<T> entityClass;
	private EntityManager entityManager = Persistence.createEntityManagerFactory("restEval").createEntityManager();

	public EntityService(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * @param id
	 *            of the entity which should be found
	 * @return entity with the given identifier or null if not found
	 */
	public T getById(Long id) {
		entityManager.clear();
		return entityManager.find(entityClass, id);
	}

	/**
	 * @return all entries available
	 */
	public List<T> getAll() {
		return get(null, null);
	}

	/**
	 * Returns a specifically requested page.
	 * 
	 * @param offset
	 *            of the search result (start index) - starting with the first
	 *            if null
	 * @param limit
	 *            number of requested entries - no limit if null
	 * @return the entries contained in the database (limited by the parameter
	 *         limit) starting from the offset
	 */
	public List<T> get(Integer offset, Integer limit) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> entities = cq.from(entityClass);
		cq.select(entities);
		TypedQuery<T> q = entityManager.createQuery(cq);
		if (offset != null) {
			q.setFirstResult(offset);
		}
		if (limit != null) {
			q.setMaxResults(limit);
		}
		return q.getResultList();
	}

	/**
	 * Saves the changes of the entity to the storage or creates it if it has
	 * not been created before.
	 * 
	 * @param entity
	 *            to save
	 */
	public T createOrUpdate(T entity) {
		Long primaryKey = entity != null ? entity.getId() : null;

		try {
			entityManager.getTransaction().begin();
			T result = entity;
			if (primaryKey != null && entityManager.find(entityClass, primaryKey) != null) {
				result = entityManager.merge(entity);
			} else {
				entityManager.persist(entity);
			}
			entityManager.getTransaction().commit();
			return result;
		} catch (RuntimeException t) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw t;
		}
	}

	/**
	 * Deletes the given entry from the storage
	 * 
	 * @param entity
	 *            to delete
	 */
	public void delete(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(entity);
			entityManager.getTransaction().commit();
		} catch (RuntimeException t) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw t;
		}
	}

	/**
	 * Finds the current amount of entities in the table.
	 * 
	 * @return number of rows in the table
	 */
	public long getLimit() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(entityClass)));
		return entityManager.createQuery(cq).getSingleResult();
	}

}