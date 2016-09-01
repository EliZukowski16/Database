package org.ssa.ironyard.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ssa.ironyard.model.Account;
import org.ssa.ironyard.model.Account.AccountType;
import org.ssa.ironyard.model.Customer;

import com.mysql.cj.jdbc.MysqlDataSource;

public class AccountDAOImplTest
{
    static String URL = "jdbc:mysql://localhost/ssa_bank?user=root&password=root&";
    // "logger=org.ssa.ironyard.database.log.MySQLLog4jLogger&" +
    // "profileSQL=true&useServerPrpStmts=true";
    DataSource dataSource;
    AccountDAOImpl accountDAO;
    CustomerDAOImpl customerDAO;
    Account testAccount;
    Customer testCustomer;
    // static List<Customer> rawTestCustomers;
    List<Account> accountsInDB;

    // @BeforeClass
    // public static void setupBeforeClass() throws IOException
    // {
    // BufferedReader reader = null;
    // rawTestCustomers = new ArrayList<>();
    //
    // try
    // {
    // reader = Files.newBufferedReader(
    // Paths.get("C:\\Users\\admin\\workspace\\DatabaseApp\\resources\\MOCK_DATA.csv"),
    // Charset.defaultCharset());
    //
    // String line;
    //
    // while (null != (line = reader.readLine()))
    // {
    // String[] names = line.split(",");
    // rawTestCustomers.add(new Customer(names[0], names[1]));
    // }
    // }
    // catch (IOException iex)
    // {
    // System.err.println(iex);
    // throw iex;
    // }
    // finally
    // {
    // if (null != reader)
    // reader.close();
    // }
    // }

    @Before
    public void setupDB()
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        this.dataSource = mysqlDdataSource;

        this.accountDAO = new AccountDAOImpl(dataSource, new AccountORMImpl());
        this.customerDAO = new CustomerDAOImpl(dataSource, new CustomerORMImpl());

        testCustomer = new Customer("John", "Doe");

        ((AccountDAOImpl) this.accountDAO).clear();
        ((CustomerDAOImpl) this.customerDAO).clear();

        accountsInDB = new ArrayList<>();

    }

    @After
    public void teardown()
    {
        accountsInDB.clear();
        ((AccountDAOImpl) this.accountDAO).clear();
        ((CustomerDAOImpl) this.customerDAO).clear();
    }

    @Test
    public void insertAccountIntoDB()
    {
        // for(Customer c : rawTestCustomers)
        // {
        // Customer insertedCustomer = customerDAO.insert(c);
        // assertNotEquals(c, insertedCustomer);
        // assertTrue(insertedCustomer.getId() > 0);
        // assertEquals(c.getFirstName(), insertedCustomer.getFirstName());
        // assertEquals(c.getLastName(), insertedCustomer.getLastName());
        //
        // customersInDB.add(insertedCustomer);
        // }
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(1000.0));

        Account testAccountInDB = accountDAO.insert(testAccount);

        assertNotEquals(testAccount, testAccountInDB);
        assertEquals(testAccount.getCustomer(), testAccountInDB.getCustomer());
        assertEquals(testAccount.getType(), testAccountInDB.getType());
        assertEquals(testAccount.getBalance(), testAccountInDB.getBalance());
        assertEquals(testAccountInDB, accountDAO.read(testAccountInDB.getId()));

    }

    @Test
    public void updateAccountInDB()
    {
        Customer testCustomerInDB = customerDAO.insert(testCustomer);

        testAccount = new Account(testCustomerInDB, AccountType.CHECKING, BigDecimal.valueOf(1000.0));

        Account testAccountInDB = accountDAO.insert(testAccount);

        Account updatedTestAccount = new Account(testAccountInDB.getId(), testCustomerInDB,
                AccountType.CHECKING, BigDecimal.valueOf(2000.0));

        Account updatedTestAccountInDB = accountDAO.update(updatedTestAccount);

        assertEquals(updatedTestAccount.getId(), updatedTestAccountInDB.getId());
        assertTrue(updatedTestAccount.equals(updatedTestAccountInDB));
        assertTrue(testAccountInDB.equals(updatedTestAccountInDB));
        assertTrue(testAccountInDB.equals(accountDAO.read(updatedTestAccountInDB.getId())));

        assertFalse(testAccountInDB.deeplyEquals(updatedTestAccountInDB));
        assertFalse(testAccountInDB.deeplyEquals(accountDAO.read(updatedTestAccountInDB.getId())));

        assertEquals(accountDAO.read(updatedTestAccountInDB.getId()), updatedTestAccountInDB);
        assertTrue(updatedTestAccountInDB.deeplyEquals(accountDAO.read(updatedTestAccountInDB.getId())));
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

        userAccounts = accountDAO.readUser(testCustomerInDB.getId());

        assertEquals(2, userAccounts.size());

        for (Account a : userAccounts)
        {
            assertEquals(testCustomerInDB, a.getCustomer());
        }
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

        userAccounts = accountDAO.readUnderwater();

        assertEquals(2, userAccounts.size());

        for (Account a : userAccounts)
        {
            assertEquals(testCustomerInDB, a.getCustomer());
            assertEquals(-1, a.getBalance().compareTo(BigDecimal.ZERO));
        }
    }
}
