package org.ssa.ironyard.account.dao;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ssa.ironyard.account.model.Account;
import org.ssa.ironyard.account.model.Account.AccountType;
import org.ssa.ironyard.customer.dao.CustomerDAOImpl;
import org.ssa.ironyard.customer.model.Customer;
import org.ssa.ironyard.dao.AbstractDAO;
import org.ssa.ironyard.dao.AbstractDAOTest;

import com.mysql.cj.jdbc.MysqlDataSource;

public class AccountDAOEagerTest extends AbstractDAOTest<Account>
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root&" +
    // "logger=org.ssa.ironyard.database.log.MySQLLog4jLogger&" +
            "useServerPrpStmts=true";
    DataSource dataSource;
    static AbstractDAO<Account> accountDAO;
    static AbstractDAO<Customer> customerDAO;
    Account testAccount;
    Customer testCustomer;
    static List<Account> rawTestAccounts;
    static List<Customer> customersInDB;
    List<Account> accountsInDB;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        DataSource dataSource = mysqlDdataSource;
        customerDAO = new CustomerDAOImpl(dataSource);
        accountDAO = new AccountDAOEager(dataSource);

        customerDAO.clear();
        accountDAO.clear();

        BufferedReader customerReader = null;
        BufferedReader accountReader = null;
        customersInDB = new ArrayList<>();
        rawTestAccounts = new ArrayList<>();

        try
        {
            customerReader = Files.newBufferedReader(
                    Paths.get("C:\\Users\\admin\\workspace\\DatabaseApp\\resources\\MOCK_CUSTOMER_DATA.csv"),
                    Charset.defaultCharset());

            String line;

            while (null != (line = customerReader.readLine()))
            {
                String[] names = line.split(",");
                customersInDB.add(customerDAO.insert(new Customer(names[0], names[1])));
            }

            accountReader = Files.newBufferedReader(
                    Paths.get("C:\\Users\\admin\\workspace\\DatabaseApp\\resources\\MOCK_ACCOUNT_DATA.csv"),
                    Charset.defaultCharset());

            while (null != (line = accountReader.readLine()))
            {
                String[] accounts = line.split(",");
                rawTestAccounts.add(new Account(null, AccountType.getInstance(accounts[0]),
                        BigDecimal.valueOf(Double.parseDouble(accounts[1]))));
            }
        }
        catch (IOException iex)
        {
            System.err.println(iex);
            throw iex;
        }
        finally
        {
            if (null != customerReader)
                customerReader.close();
            if (null != accountReader)
                accountReader.close();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        customerDAO.clear();
    }

    @Before
    public void setUp() throws Exception
    {
        accountDAO.clear();

        accountsInDB = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception
    {
        accountsInDB.clear();
        accountDAO.clear();
    }

    @Test
    public void accountEagerlyLoadsCustomerFromSingleRead()
    {
        testAccount = new Account(customersInDB.get(0), AccountType.CHECKING, BigDecimal.valueOf(1000.0));
        
        Account testAccountInDB = accountDAO.insert(testAccount);
        
        assertNotEquals(testAccountInDB, testAccount);
        assertTrue(testAccountInDB.isLoaded());
        
        Account testAccountFromDB = accountDAO.read(testAccountInDB.getId());
        
        assertEquals(testAccountInDB, testAccountFromDB);
        assertTrue(testAccountInDB.deeplyEquals(testAccountFromDB));
        
        Customer eagerlyLoadedCustomer = testAccountFromDB.getCustomer();
        
        assertEquals(customerDAO.read(eagerlyLoadedCustomer.getId()), eagerlyLoadedCustomer);
        assertTrue(customerDAO.read(eagerlyLoadedCustomer.getId()).deeplyEquals(eagerlyLoadedCustomer));
        assertTrue(eagerlyLoadedCustomer.isLoaded());
        assertNotNull(eagerlyLoadedCustomer.getFirstName());
        assertNotNull(eagerlyLoadedCustomer.getLastName());
    }
    
    @Test
    public void accountEagerlyLoadsCustomerAfterReadingAllUsersAccounts()
    {
        randomlyLinkCustomersAndAccounts();
        
        for (Customer c : customersInDB)
        {
            List<Account> customerAccounts =  ((AccountDAOEager) accountDAO).readUser(c.getId());

            for (Account a : customerAccounts)
            {
                assertEquals(c, a.getCustomer());
                assertNotNull(a.getCustomer().getFirstName());
                assertNotNull(a.getCustomer().getLastName());
                assertTrue(a.getCustomer().isLoaded());
            }
        }
    }
    
    @Test
    public void accountEagerlyLoadsCustomersFromUnderwaterRead()
    {
        randomlyLinkCustomersAndAccounts();
        
        List<Account> underwaterAccounts = ((AccountDAOEager) accountDAO).readUnderwater();

        for (Account a : underwaterAccounts)
        {
            assertEquals(-1, a.getBalance().compareTo(BigDecimal.ZERO));
            assertNotNull(a.getCustomer().getFirstName());
            assertNotNull(a.getCustomer().getLastName());
            assertTrue(a.getCustomer().isLoaded());
        }
    }
    
    private void randomlyLinkCustomersAndAccounts()
    {
        for (Account a : rawTestAccounts)
        {
            Integer randCustomer = (int) (Math.random() * customersInDB.size());
            accountDAO.insert(new Account(customersInDB.get(randCustomer), a.getType(), a.getBalance()));
        }
    }
    
    @Override
    protected Account newInstance()
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        this.dataSource = mysqlDdataSource;

        customerDAO = new CustomerDAOImpl(dataSource);

        Customer customerInDB = customerDAO.insert(new Customer("John", "Doe"));

        return new Account(customerInDB, AccountType.CHECKING, BigDecimal.valueOf(1000.0));
    }

    @Override
    protected AbstractDAO<Account> getDAO()
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        this.dataSource = mysqlDdataSource;

        accountDAO = new AccountDAOEager(dataSource);

        return accountDAO;
    }

}
