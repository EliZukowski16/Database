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
import org.ssa.ironyard.model.Account;
import org.ssa.ironyard.model.Account.AccountType;

import com.mysql.cj.jdbc.MysqlDataSource;

public class AccountDAOImplTest
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root&";
//                        "logger=org.ssa.ironyard.database.log.MySQLLog4jLogger&" +
//                        "profileSQL=true&useServerPrpStmts=true";
    DataSource dataSource;
    AccountDAOImpl accountDAO;
    Account testAccount;
//    static List<Customer> rawTestCustomers;
    List<Account> accountsInDB;
    
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

        this.accountDAO = new AccountDAOImpl(dataSource, new AccountORMImpl());

        testAccount = new Account(1, AccountType.CHECKING, 1000.0);

        ((AccountDAOImpl) this.accountDAO).clear();
        
        accountsInDB = new ArrayList<>();

    }
    
    @After
    public void teardown()
    {
        accountsInDB.clear();
        ((AccountDAOImpl) this.accountDAO).clear();
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
        Account testAccountInDB = accountDAO.insert(testAccount);

        assertNotEquals(testAccount, testAccountInDB);
        assertEquals(testAccount.getCustomerId(), testAccountInDB.getCustomerId());
        assertEquals(testAccount.getType(), testAccountInDB.getType());
        assertEquals(testAccount.getBalance(), testAccountInDB.getBalance());
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));
    }

    @Test
    public void updateCustomerInDB()
    {
        Account testAccountInDB = accountDAO.insert(testAccount);

        Account updatedTestAccount = new Account(testAccountInDB.getId(), 1, AccountType.CHECKING, 2000.0 );

        Account updatedTestAccountInDB = accountDAO.update(updatedTestAccount);

        assertEquals(updatedTestAccount.getId(), updatedTestAccountInDB.getId());
        assertTrue(updatedTestAccount.equals(updatedTestAccountInDB));
        assertNotEquals(testAccountInDB, updatedTestAccountInDB);
        assertNotEquals(accountDAO.read(updatedTestAccountInDB.getId()), testAccountInDB);
        assertEquals(accountDAO.read(updatedTestAccountInDB.getId()), updatedTestAccountInDB);
    }

//    @Test
    public void deleteCustomerFromDB()
    {
        testAccount = new Account(0, "John", "Doe");
        assertEquals(null, accountDAO.read(testAccount.getId()));
        assertFalse(accountDAO.delete(testAccount.getId()));

        Account testAccountInDB = accountDAO.insert(testAccount);
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));
        assertTrue(accountDAO.delete(testAccountInDB.getId()));

        assertEquals(null, accountDAO.read(testAccountInDB.getId()));
        assertFalse(accountDAO.delete(testAccountInDB.getId()));
    }

//    @Test
    public void readSingleCustomerFromDB()
    {
        testAccount = new Account(0, "John", "Doe");
        assertEquals(null, accountDAO.read(testAccount.getId()));

        Account testAccountInDB = accountDAO.insert(testAccount);
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));

        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));
    }
}
