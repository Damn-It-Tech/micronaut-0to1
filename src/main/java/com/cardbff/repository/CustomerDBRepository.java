package com.cardbff.repository;

import com.cardbff.exceptions.DatabaseOperationException;
import com.cardbff.interceptor.LogMethods;
import com.cardbff.model.Customer;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
@LogMethods
@Primary
public class CustomerDBRepository implements CustomerDBDao {

        private static final Logger logger = Logger.getLogger(CustomerDBRepository.class.getName());

        @Inject
        DataSource dataSource;

        private final String TABLE_NAME = "customer";

        private void createTable() {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "id SERIAL PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL," +
                        "dob VARCHAR(10) NOT NULL," +
                        "email VARCHAR(255) NOT NULL," +
                        "pan VARCHAR(10) NOT NULL UNIQUE," +
                        "mobile VARCHAR(15) NOT NULL UNIQUE," +
                        "verified BOOLEAN NOT NULL" +
                        ");";

                Jdbi jdbi=Jdbi.create(dataSource);

                try(Handle handle=jdbi.open()) {
                        handle.execute(createTableSQL);
                        logger.info("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY.");
                }
                catch(Exception e){
                        logger.severe("Failed to create table " + TABLE_NAME + " with exception: " + e.getMessage());
                        throw new DatabaseOperationException(e.getMessage());
                }

        }

        @Override
        public void createUser(String name, String dob, String pan, String mobile, String email) throws DatabaseOperationException  {
                createTable();
                Jdbi jdbi=Jdbi.create(dataSource);
                try(Handle handle=jdbi.open()) {
                        ResultBearing result =  handle.createUpdate("INSERT INTO "+ TABLE_NAME +" (name, dob, pan, mobile, email, verified) VALUES (?, ?, ?, ?, ?, ?)")
                                .bind(0, name)
                                .bind(1, dob)
                                .bind(2, pan)
                                .bind(3, mobile)
                                .bind(4, email)
                                .bind(5, false)
                                .executeAndReturnGeneratedKeys();

                        logger.info("Customer persisted successfully with ID: " + result.mapTo(Integer.class).first());

                }
                catch(Exception e){
                        logger.severe("Some error occurred while inserting into table: " + e.getMessage());
                        throw new DatabaseOperationException(e.getMessage());
                }
        }

        @Override
        public void updatePanVerificationStatus(String pan, boolean status) throws DatabaseOperationException {
                Jdbi jdbi=Jdbi.create(dataSource);
                try(Handle handle=jdbi.open()) {
                        handle.createUpdate("UPDATE " + TABLE_NAME + " SET verified = ? where pan = ? ")
                                .bind(0, Boolean.valueOf(status))
                                .bind(1, pan)
                                .execute();

                }
                catch(Exception e){
                        logger.severe("Exception occurred while updating pan verification status");
                        throw new DatabaseOperationException(e.getMessage());
                }
        }
        @Override
        public Optional<Customer> getCustomerByMobile(String mobile) throws DatabaseOperationException{
                Jdbi jdbi=Jdbi.create(dataSource);
                try(Handle handle=jdbi.open()) {

                        Optional<Customer> result = handle.createQuery("select * from "+ TABLE_NAME + " where mobile = ?")
                                .bind(0, mobile)
                                .mapToBean(Customer.class)
                                .findFirst();

                        handle.close();

                        if (result.isPresent()) {
                                logger.info("Got the customer with mentioned mobile number");
                        } else {
                                logger.info("Sorry, no customer matches this mobile number");
                        }

                        return result;
                }
                catch(Exception e){
                        logger.severe("Exception occurred while getting customer by mobile number");
                        throw new DatabaseOperationException(e.getMessage());
                }

        }
        @Override
        public Optional<Customer> getCustomerByPan(String pan) throws DatabaseOperationException {

                Jdbi jdbi=Jdbi.create(dataSource);
                try(Handle handle=jdbi.open()) {

                        Optional<Customer> result = handle.createQuery("select * from " + TABLE_NAME + " where pan = ?")
                                .bind(0, pan)
                                .mapToBean(Customer.class)
                                .findFirst();

                        handle.close();

                        if (result.isPresent()) {
                                logger.info("Got the customer with mentioned pan number");
                        } else {
                                logger.info("Sorry, no customer matches this pan number");
                        }

                        return result;
                }
                catch(Exception e){
                        logger.severe("Exception occurred while getting customer by PAN");
                        throw new DatabaseOperationException(e.getMessage());

                }
        }
}
