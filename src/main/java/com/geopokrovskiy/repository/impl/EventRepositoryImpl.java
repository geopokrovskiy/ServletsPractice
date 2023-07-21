package com.geopokrovskiy.repository.impl;

import com.geopokrovskiy.model.Event;
import com.geopokrovskiy.repository.EventRepository;
import com.geopokrovskiy.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class EventRepositoryImpl implements EventRepository {
    @Override
    public Event addNew(Event value) {
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
    public Event getById(Long aLong) {
        return null;
    }

    @Override
    public List<Event> getAll() {
        return null;
    }

    @Override
    public Event update(Event value) {
        return null;
    }

    @Override
    public boolean delete(Long aLong) {
        return false;
    }
}
