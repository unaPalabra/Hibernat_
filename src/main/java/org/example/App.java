package org.example;

import org.example.entity.Event;
import org.example.entity.Participant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class App {

    public static void main(String[] args) {
        SessionFactory sessionFactory = null;

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(Event.class) //добавляем ссылку для аннотированного класса, при конфигурациипри конфигурации соединения с помощью аннотаций
                    .addAnnotatedClass(Participant.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e){
            StandardServiceRegistryBuilder.destroy(registry);
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.save(new Event("Demo about Hibernate", new Date()));
        session.getTransaction().commit(); //получаем транзакцию, коммитем (записываем) изменения
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        for (Event event : (List<Event>)(result)){
            System.out.println("Event (" + event.getDate() + ") : " + event.getTitle());
        }
        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();
        Event event= session.load(Event.class, 1L);
        List<Participant> participants = getParticipants();
        for (Participant participant: participants){
            session.save(participant);
        }
        event.setParticipantList(participants);
        session.save(event);
        session.getTransaction().commit();
        result = session.createQuery("from Event").list();
        for (Event iterable : (List<Event>)(result)){
            System.out.println("Event (" + iterable.getDate() + ") : " + iterable.getTitle() + " participants = " + iterable.getParticipantList().size());
        }
        session.close();

        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static List<Participant> getParticipants(){
        return Arrays.asList(
                new Participant("Petr", "Kovalev"),
                new Participant("Devid", "Moss"),
                new Participant("Andrey", "Krab"),
                new Participant("Joe", "Goover")
        );
    }
}
