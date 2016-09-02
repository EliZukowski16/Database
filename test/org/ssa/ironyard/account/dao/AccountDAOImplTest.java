package org.ssa.ironyard.account.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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

public class AccountDAOImplTest extends AbstractDAOTest<Account>
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
    public static void setupBeforeClass() throws IOException
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        DataSource dataSource = mysqlDdataSource;
        customerDAO = new CustomerDAOImpl(dataSource);
        accountDAO = new AccountDAOImpl(dataSource);

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

    @Before
    public void setupDB()
    {
        testCustomer = new Customer("John", "Doe");

        accountDAO.clear();

        accountsInDB = new ArrayList<>();
    }

    @After
    public void teardown()
    {
        accountsInDB.clear();
        accountDAO.clear();
    }

    @AfterClass
    public static void teardownAfterClass()
    {
        customerDAO.clear();
    }

    @Test
    public void insertAccountIntoDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(1000.0));

        Account testAccountInDB = accountDAO.insert(testAccount);

        assertNotEquals(testAccount, testAccountInDB);
        assertEquals(testAccount.getCustomer(), testAccountInDB.getCustomer());
        assertEquals(testAccount.getType(), testAccountInDB.getType());
        assertEquals(testAccount.getBalance(), testAccountInDB.getBalance());
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void updateAccountInDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(1000.0));

        Account testAccountInDB = accountDAO.insert(testAccount);

        Account updatedTestAccount = new Account(testAccountInDB.getId(), testCustomerInDB, AccountType.CHECKING,
                BigDecimal.valueOf(2000.0));

        Account updatedTestAccountInDB = accountDAO.update(updatedTestAccount);

        assertEquals(updatedTestAccount.getId(), updatedTestAccountInDB.getId());
        assertTrue(updatedTestAccount.equals(updatedTestAccountInDB));
        assertTrue(testAccountInDB.equals(updatedTestAccountInDB));
        assertTrue(testAccountInDB.equals(accountDAO.read(updatedTestAccountInDB.getId())));

        assertFalse(testAccountInDB.deeplyEquals(updatedTestAccountInDB));
        assertFalse(testAccountInDB.deeplyEquals(accountDAO.read(updatedTestAccountInDB.getId())));

        assertEquals(accountDAO.read(updatedTestAccountInDB.getId()), updatedTestAccountInDB);
        assertTrue(updatedTestAccountInDB.deeplyEquals(accountDAO.read(updatedTestAccountInDB.getId())));

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void deleteAccountFromDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(0, testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(10000.0));
        assertEquals(null, accountDAO.read(testAccount.getId()));
        assertFalse(accountDAO.delete(testAccount.getId()));

        Account testAccountInDB = accountDAO.insert(testAccount);
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));
        assertTrue(accountDAO.delete(testAccountInDB.getId()));

        assertEquals(null, accountDAO.read(testAccountInDB.getId()));
        assertFalse(accountDAO.delete(testAccountInDB.getId()));

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void readSingleAccountFromDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(0, testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(10000.0));
        assertEquals(null, accountDAO.read(testAccount.getId()));

        Account testAccountInDB = accountDAO.insert(testAccount);
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));

        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void readAllAccountsFromOneCustomerInDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(10000.0));
        accountDAO.insert(testAccount);
        testAccount = new Account(testCustomerInDB, AccountType.SAVINGS, BigDecimal.valueOf(20000.0));
        accountDAO.insert(testAccount);

        List<Account> userAccounts = new ArrayList<>();

        userAccounts = ((AccountDAO) accountDAO).readUser(testCustomerInDB.getId());

        assertEquals(2, userAccounts.size());

        for (Account a : userAccounts)
        {
            assertEquals(testCustomerInDB, a.getCustomer());
        }

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void readUnderwaterAccountsFromDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(10000.0));
        accountDAO.insert(testAccount);
        testAccount = new Account(testCustomerInDB, AccountType.SAVINGS, BigDecimal.valueOf(20000.0));
        accountDAO.insert(testAccount);
        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(-5000.0));
        accountDAO.insert(testAccount);
        testAccount = new Account(testCustomerInDB, AccountType.SAVINGS, BigDecimal.valueOf(-1000.0));
        accountDAO.insert(testAccount);
        testAccount = new Account(testCustomerInDB, AccountType.SAVINGS, BigDecimal.valueOf(20000.0));
        accountDAO.insert(testAccount);

        List<Account> userAccounts = new ArrayList<>();

        userAccounts = ((AccountDAO) accountDAO).readUnderwater();

        assertEquals(2, userAccounts.size());

        for (Account a : userAccounts)
        {
            assertEquals(testCustomerInDB, a.getCustomer());
            assertEquals(-1, a.getBalance().compareTo(BigDecimal.ZERO));
        }

        customerDAO.delete(testCustomerInDB.getId());
    }

    @Test
    public void multipleAccountsAttachedToMultipleCustomers()
    {
        randomlyLinkCustomersAndAccounts();
        
        for (Customer c : customersInDB)
        {
            List<Account> customerAccounts = ((AccountDAO) accountDAO).readUser(c.getId());

            for (Account a : customerAccounts)
            {
                assertEquals(c, a.getCustomer());
            }
        }

        List<Account> underwaterAccounts = ((AccountDAO) accountDAO).readUnderwater();

        for (Account a : underwaterAccounts)
        {
            assertEquals(-1, a.getBalance().compareTo(BigDecimal.ZERO));
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

        accountDAO = new AccountDAOImpl(dataSource);

        return accountDAO;
    }
}
