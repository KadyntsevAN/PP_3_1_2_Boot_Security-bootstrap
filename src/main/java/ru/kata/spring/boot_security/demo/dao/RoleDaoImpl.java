package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Role role) {
        entityManager.persist(role);
    }

    @Override
    public Role find(String role) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.role = :role", Role.class);
        query.setParameter("role", role);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
