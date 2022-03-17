package org.nwolfhub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nwolfhub.database.model.Dao;

import java.util.List;

public class GroupDao implements Dao {

    public GroupDao(){}

    @Override
    public Object get(Class class2pick, Integer id) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Object obj = session.get(class2pick, id);
        session.close();
        return obj;
    }

    @Override
    public void save(Object obj) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(obj);
        transaction.commit();
        session.close();
    }

    @Override
    public void update(Object obj) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.update(obj);
        transaction.commit();
        session.close();
    }

    @Override
    public void delete(Object obj) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(obj);
        transaction.commit();
        session.close();
    }

    public List getAll(String table) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List toReturn = session.createQuery(" from " + table).list();
        session.close();
        return toReturn;
    }
}
