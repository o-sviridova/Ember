package db;
import com.google.common.collect.ImmutableMap;
import org.postgresql.util.PSQLException;
import play.db.Database;
import play.db.Databases;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.*;
import utils.HashHelper;

import static org.junit.Assert.*;

public class DatabaseTest {

    Database database;

    @Before
    public void setupDatabase() throws SQLException {
        database = Databases.createFrom(
                "ember",
                "org.postgresql.Driver",
                "jdbc:postgresql://localhost:5432/ember",
                ImmutableMap.of(
                        "username", "ember",
                        "password", "qwerty"));
    }


    /*@After
    public void cleanDatabase() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("delete from accounts where id > 55").execute();
        connection.prepareStatement("delete from habitants where id > 5").execute();
        connection.prepareStatement("delete from organization where id > 1").execute();
    }*/

    @Test
    public void testInsertAccount() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')) , '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();

        assertTrue(
                connection.prepareStatement("select * from accounts where id = currval('accounts_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testInsertExistAccount() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (1, concat('test_username_', currval('accounts_id_seq')) , '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
    }

    @Test
    public void testSelectAccount() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("select * from accounts where id = 1").execute();

        assertTrue(
                connection.prepareStatement("select * from accounts where id = 1").executeQuery().next());
    }

    @Test
    public void testUpdateAccount() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("update accounts set username=concat('test_username_0', currval('accounts_id_seq')) where id = currval('accounts_id_seq')").execute();
        ResultSet rs = connection.prepareStatement("select * from accounts where id = currval('accounts_id_seq')").executeQuery();
        rs.next();
        Long id = rs.getLong("id");

        assertTrue(rs.getString("username").equals("test_username_0" + id.toString()));

    }

    @Test
    public void testDeleteAccount() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("delete from accounts where id = currval('accounts_id_seq')").execute();

        assertFalse(
                connection.prepareStatement("select * from accounts where id = currval('accounts_id_seq')").executeQuery().next());
    }

    @Test
    public void testInsertHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();

        assertTrue(
                connection.prepareStatement("select * from habitants where id = currval('habitants_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testInsertExistHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (1, 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
    }

    @Test
    public void testSelectHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("select * from habitants where id = 1").execute();

        assertTrue(
                connection.prepareStatement("select * from habitants where id = 1").executeQuery().next());
    }

    @Test
    public void testUpdateHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("update habitants set name='NewName' where id = currval('habitants_id_seq')").executeUpdate();
        ResultSet rs = connection.prepareStatement("select * from habitants where id = currval('habitants_id_seq')").executeQuery();
        rs.next();

        assertTrue(rs.getString("name").equals("NewName"));

    }

    @Test
    public void testDeleteHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("delete from habitants where id = currval('habitants_id_seq')").execute();

        assertFalse(
                connection.prepareStatement("select * from habitants where id = currval('habitants_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testDeleteAccWithoutHabitant() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("delete from accounts where id = currval('accounts_id_seq')").execute();
    }

    @Test
    public void testInsertOrganization() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (nextval('organization_id_seq'), concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();

        assertTrue(
                connection.prepareStatement("select * from organization where id = currval('organization_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testInsertExistOrganization() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (1, concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();

    }

    @Test
    public void testSelectOrganization() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("select * from organization where id = 1").execute();

        assertTrue(
                connection.prepareStatement("select * from organization where id = 1").executeQuery().next());
    }

    @Test
    public void testUpdateOrganization() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (nextval('organization_id_seq'), concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();
        connection.prepareStatement("update organization set name=concat('Org',currval('organization_id_seq')) where id = currval('organization_id_seq')").executeUpdate();
        ResultSet rs = connection.prepareStatement("select * from organization where id = currval('organization_id_seq')").executeQuery();
        rs.next();

        Long id = rs.getLong("id");
        assertTrue(rs.getString("name").equals("Org" + id));

    }

    @Test
    public void testDeleteOrganization() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (nextval('organization_id_seq'), concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();
        connection.prepareStatement("delete from organization where id = currval('organization_id_seq')").execute();
        assertFalse(
                connection.prepareStatement("select * from organization where id = currval('organization_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testDeleteHabitantWithoutOrg() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (nextval('organization_id_seq'), concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();
        connection.prepareStatement("delete from habitants where id = currval('habitants_id_seq')").execute();
    }

    @Test
    public void testInsertVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')))").execute();

        assertTrue(
                connection.prepareStatement("select * from vacancy_types where id = currval('vacancy_types_id_seq')").executeQuery().next());
    }

    @Test
    public void testSelectVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("select * from vacancy_types where id = 1").execute();

        assertTrue(
                connection.prepareStatement("select * from vacancy_types where id = 1").executeQuery().next());
    }


    @Test
    public void testUpdateVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("update vacancy_types set name='normalVacancy' where id = 1").execute();
        ResultSet rs = connection.prepareStatement("select * from vacancy_types where id = 1").executeQuery();
        rs.next();

        assertTrue(rs.getString("name").equals("normalVacancy"));

    }

    @Test
    public void testDeleteVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("delete from vacancy_types where id = currval('vacancy_types_id_seq')").execute();

        assertFalse(
                connection.prepareStatement("select * from vacancy_types where id = currval('vacancy_types_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testDeleteOrgWithoutVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into organization values (nextval('organization_id_seq'), concat('Organization', currval('organization_id_seq')), true, currval('habitants_id_seq'))").execute();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),currval('organization_id_seq'))").execute();
        connection.prepareStatement("delete from organization where id = currval('organization_id_seq')").execute();
    }

    @Test(expected = PSQLException.class)
    public void testInsertExistVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (1, concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();

    }

    @Test
    public void testSelectVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("select * from vacancies where id = 1").execute();

        assertTrue(
                connection.prepareStatement("select * from vacancies where id = 1").executeQuery().next());
    }

    @Test
    public void testInsertVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("insert into vacancies values(nextval('vacancies_id_seq'), current_date, current_date, false, null, currval('vacancy_types_id_seq'))").execute();

        assertTrue(
                connection.prepareStatement("select * from vacancies where id = currval('vacancies_id_seq')").executeQuery().next());
    }

    @Test
    public void testUpdateVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("insert into vacancies values(nextval('vacancies_id_seq'), current_date, current_date, false, null, currval('vacancy_types_id_seq'))").execute();
        connection.prepareStatement("update vacancies set is_archived=false where id = currval('vacancies_id_seq')").execute();
        ResultSet rs = connection.prepareStatement("select * from vacancies where id = currval('vacancies_id_seq')").executeQuery();
        rs.next();

        assertFalse(rs.getBoolean("is_archived"));
    }

    @Test
    public void testDeleteVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("insert into vacancies values(nextval('vacancies_id_seq'), current_date, current_date, false, null, currval('vacancy_types_id_seq'))").execute();
        connection.prepareStatement("delete from vacancies where id = currval('vacancies_id_seq')").execute();

        assertFalse(
                connection.prepareStatement("select * from vacancies where id = currval('vacancies_id_seq')").executeQuery().next());
    }

    @Test(expected = PSQLException.class)
    public void testInsertExistVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancies values(1, current_date, current_date, false, null, currval('vacancy_types_id_seq'))").execute();

    }

    @Test(expected = PSQLException.class)
    public void testDeleteVacTypeWithoutVac() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("insert into vacancies values(nextval('vacancies_id_seq'), current_date, current_date, false, null, currval('vacancy_types_id_seq'))").execute();
        connection.prepareStatement("delete from vacancy_types where id = currval('vacancy_types_id_seq')").execute();
    }

    @Test(expected = PSQLException.class)
    public void testDeleteHabitantWithoutVacType() throws SQLException {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into accounts values (nextval('accounts_id_seq'), concat('test_username_', currval('accounts_id_seq')), '" + HashHelper.createPassword("qwerty") + "', 'HABITANT')").execute();
        connection.prepareStatement("insert into vacancy_types values (nextval('vacancy_types_id_seq'), concat('superVacancy',currval('vacancy_types_id_seq')),1)").execute();
        connection.prepareStatement("insert into habitants values (nextval('habitants_id_seq'), 'Test', 'Test', 'Test', 'MALE', TO_TIMESTAMP('2019-12-20 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), true, currval('accounts_id_seq'), 1)").execute();
        connection.prepareStatement("insert into vacancies values(nextval('vacancies_id_seq'), current_date, current_date, false, currval('habitants_id_seq'), currval('vacancy_types_id_seq'))").execute();

        connection.prepareStatement("delete from habitants where id = currval('habitants_id_seq')").execute();
    }
}
