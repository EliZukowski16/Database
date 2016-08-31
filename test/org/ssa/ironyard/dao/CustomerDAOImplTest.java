package org.ssa.ironyard.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ssa.ironyard.model.Customer;

import com.mysql.cj.jdbc.MysqlDataSource;

public class CustomerDAOImplTest
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root&";
//                        "logger=org.ssa.ironyard.database.log.MySQLLog4jLogger&" +
//                        "profileSQL=true&useServerPrpStmts=true";
    DataSource dataSource;
    CustomerDAOImpl customerDAO;
    Customer testCustomer;
//    static List<Customer> rawTestCustomers;
    List<Customer> customersInDB;
    
//    @BeforeClass
//    public static void setupBeforeClass() throws IOException
//    {
//        BufferedReader reader = null;
//        rawTestCustomers = new ArrayList<>();
//
//        try
//        {
//            reader = Files.newBufferedReader(
//                    Paths.get("C:\\Users\\admin\\workspace\\DatabaseApp\\resources\\MOCK_DATA.csv"),
//                    Charset.defaultCharset());
//
//            String line;
//
//            while (null != (line = reader.readLine()))
//            {
//                String[] names = line.split(",");
//                rawTestCustomers.add(new Customer(names[0], names[1]));
//            }
//        }
//        catch (IOException iex)
//        {
//            System.err.println(iex);
//            throw iex;
//        }
//        finally
//        {
//            if (null != reader)
//                reader.close();
//        }
//    }

    @Before
    public void setupDB()
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        this.dataSource = mysqlDdataSource;

        this.customerDAO = new CustomerDAOImpl(dataSource, new CustomerORMImpl());

        testCustomer = new Customer("John", "Doe");

        ((CustomerDAOImpl) this.customerDAO).clear();
        
        customersInDB = new ArrayList<>();

    }
    
    @After
    public void teardown()
    {
        customersInDB.clear();
        ((CustomerDAOImpl) this.customerDAO).clear();
    }

    @Test
    public void insertCustomerIntoDB()
    {
//        for(Customer c : rawTestCustomers)
//        {
//            Customer insertedCustomer = customerDAO.insert(c);
//            assertNotEquals(c, insertedCustomer);
//            assertTrue(insertedCustomer.getId() > 0);
//            assertEquals(c.getFirstName(), insertedCustomer.getFirstName());
//            assertEquals(c.getLastName(), insertedCustomer.getLastName());
//            
//            customersInDB.add(insertedCustomer);
//        }
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        assertNotEquals(testCustomer, testCustomerInDB);
        assertEquals(testCustomer.getFirstName(), testCustomerInDB.getFirstName());
        assertEquals(testCustomer.getLastName(), testCustomerInDB.getLastName());
        assertEquals(testCustomerInDB, customerDAO.read(testCustomerInDB.getId()));
    }

    @Test
    public void updateCustomerInDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        Customer updatedTestCustomer = new Customer(testCustomerInDB.getId(), "Mike", "Smith");

        Customer updatedTestCustomerInDB = customerDAO.update(updatedTestCustomer);

        assertEquals(updatedTestCustomer.getId(), updatedTestCustomerInDB.getId());
        assertTrue(updatedTestCustomer.equals(updatedTestCustomerInDB));
        assertNotEquals(testCustomerInDB, updatedTestCustomerInDB);
        assertNotEquals(customerDAO.read(updatedTestCustomerInDB.getId()), testCustomerInDB);
        assertEquals(customerDAO.read(updatedTestCustomerInDB.getId()), updatedTestCustomerInDB);
    }

    @Test
    public void deleteCustomerFromDB()
    {
        testCustomer = new Customer(0, "John", "Doe");
        assertEquals(null, customerDAO.read(testCustomer.getId()));
        assertFalse(customerDAO.delete(testCustomer.getId()));

        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        assertEquals(testCustomerInDB, customerDAO.read(testCustomerInDB.getId()));
        assertTrue(customerDAO.delete(testCustomerInDB.getId()));

        assertEquals(null, customerDAO.read(testCustomerInDB.getId()));
        assertFalse(customerDAO.delete(testCustomerInDB.getId()));
    }

    @Test
    public void readSingleCustomerFromDB()
    {
        testCustomer = new Customer(0, "John", "Doe");
        assertEquals(null, customerDAO.read(testCustomer.getId()));

        Customer testCustomerInDB = customerDAO.insert(testCustomer);
        assertEquals(testCustomerInDB, customerDAO.read(testCustomerInDB.getId()));

        assertEquals(testCustomerInDB, customerDAO.read(testCustomerInDB.getId()));
    }
}
