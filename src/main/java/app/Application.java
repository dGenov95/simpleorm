package app;

import constants.DbConstants;
import entities.User;
import orm.Connector;
import orm.EntityManager;
import orm.base.DbContext;

import java.sql.SQLException;

public class Application {

    public static void main(String[] args) {
        try {
            Connector.createConnection(DbConstants.DB_USER,DbConstants.DB_PASS,"blog");
            DbContext<User> dbContext = new EntityManager<>(User.class,Connector.getConnection());
            dbContext.find().forEach(System.out::println);
            System.out.println(dbContext.findFirst());
            System.out.println("-------------------------");
            dbContext.persist(dbContext.findFirst());
            dbContext.find().forEach(System.out::println);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
