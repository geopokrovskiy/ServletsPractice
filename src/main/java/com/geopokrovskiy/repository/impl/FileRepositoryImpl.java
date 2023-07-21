package com.geopokrovskiy.repository.impl;

import com.geopokrovskiy.model.File;
import com.geopokrovskiy.repository.FileRepository;
import com.geopokrovskiy.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class FileRepositoryImpl implements FileRepository {
    @Override
    public File addNew(File value) {
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
    public File getById(Long aLong) {
        try (Session session = HibernateUtil.getSession()) {
            Query query = session.createQuery(
                            "FROM File F LEFT JOIN FETCH F.events WHERE F.id =: id")
                    .setParameter("id", aLong);
            return (File) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<File> getAll() {
        try (Session session = HibernateUtil.getSession()) {
            Query query = session.createQuery(
                    "FROM File F LEFT JOIN fetch F.events"
            );
            return (List<File>) query.list();
        }
    }

    @Override
    public File update(File value) {
        return null;
    }

    @Override
    public boolean delete(Long aLong) {
        Session session = HibernateUtil.getSession();
        try {
            File file = this.getById(aLong);
            if (file != null) {
                session.getTransaction().begin();
                session.delete(file);
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
