package com.geopokrovskiy.repository.impl;

import com.geopokrovskiy.model.User;
import com.geopokrovskiy.repository.UserRepository;
import com.geopokrovskiy.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    @Override
    public User addNew(User value) {
        Session session = HibernateUtil.getSession();
        try {
            session.getTransaction().begin();
            session.save(value);
            session.getTransaction().commit();
            return value;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new IllegalArgumentException("Incorrect input");
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    @Override
    public User getById(Long aLong) {
        try (Session session = HibernateUtil.getSession()) {
            Query query = session.createQuery(
                            "FROM User U LEFT JOIN FETCH U.events WHERE U.id =: id")
                    .setParameter("id", aLong);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getAll() {
        try (Session session = HibernateUtil.getSession()) {
            Query query = session.createQuery(
                    "FROM User U LEFT JOIN fetch U.events"
            );
            return (List<User>) query.list();
        }
    }

    @Override
    public User update(User value) {
        Session session = HibernateUtil.getSession();
        try {
            session.getTransaction().begin();
            session.update(value);
            session.getTransaction().commit();
            return value;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new IllegalArgumentException("Incorrect input!");
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    @Override
    public boolean delete(Long aLong) {
        Session session = HibernateUtil.getSession();
        try {
            User user = this.getById(aLong);
            if (user != null) {
                session.getTransaction().begin();
                session.delete(user);
                session.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            session.getTransaction().rollback();
            return false;
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
