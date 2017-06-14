import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;

public class TestSelenium {
    private static WebDriver driver;


    @Before
    public void loginIn() {

        // Line below is necessary to connect to geckodriver on my machine -Brian
        //System.setProperty("webdriver.gecko.driver", "C:\\workspace\\seng426\\geckodriver.exe");

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("login")).click();
        driver.findElement(By.id("username")).sendKeys("frank.paul@acme.com");
        driver.findElement(By.id("password")).sendKeys("starwars");
        driver.findElement(By.className("btn-primary")).click();
        waitMS(1000);
        driver.get("http://localhost:8080/#/acme-pass");
        waitMS(1000);
    }

    @Test
    public void tablePwdVisibilityTest(){
        WebElement pwdColumn = driver.findElements(By.cssSelector("td")).get(3);
        WebElement visibilityButton = pwdColumn.findElement(By.cssSelector("span"));

        String initialInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");
        assertEquals("password", initialInputType);
        visibilityButton.click();
        String nextInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");
        assertEquals("text", nextInputType);
        visibilityButton.click();
        String finalInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");
        assertEquals("password", finalInputType);
    }

    @Test
    public void editPwdVisibilityTest(){
        createPass();

        driver.findElement(By.className("btn-info")).click();
        waitMS(300);

        WebElement visibilityButton = driver.findElement(By.cssSelector("span[role='button']"));

        String initialInputType = driver.findElement(By.id("field_password")).getAttribute("type");
        assertEquals("Initially expect password to be hidden", "password", initialInputType);
        visibilityButton.click();
        String nextInputType = driver.findElement(By.id("field_password")).getAttribute("type");
        assertEquals("Password visible when button clicked", "text", nextInputType);
        visibilityButton.click();
        String finalInputType = driver.findElement(By.id("field_password")).getAttribute("type");
        assertEquals("Password hidden when button clicked a second time","password", finalInputType);
    }


    @Test
    public void createTest() {
        clearDatabase();
        createPass();

        String site = driver.findElement(By.cssSelector("tbody>tr:nth-child(1)>td:nth-child(2)")).getText();
        String login = driver.findElement(By.cssSelector("tbody>tr:nth-child(1)>td:nth-child(3)")).getText();
        String pwd = driver.findElement(By.className("acmepass-password")).getAttribute("value");
    
        assertEquals(site, "site");
        assertEquals(login, "login");
        assertEquals(pwd, "password");
    }

    @Test
    public void genPwdTest() {

        driver.findElement(By.className("btn-primary")).click();
        waitMS(300);
        driver.findElement(By.className("btn-primary")).click();
        waitMS(300);
        driver.findElement(By.className("btn-primary")).click();
        String pwd = driver.findElement(By.id("field_password")).getAttribute("value");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        waitMS(300);
        String set_pwd = driver.findElement(By.id("field_password")).getAttribute("value");

        assertEquals(pwd, set_pwd);
    }

    @Test
    public void noSiteEnteredTest() {
        driver.findElement(By.className("btn-primary")).click();
        driver.findElement(By.id("field_login")).sendKeys("login");
        driver.findElement(By.id("field_password")).sendKeys("password");
        assertEquals(driver.findElement(By.xpath("//button[@type='submit']")).isEnabled(), false);
    }

    @Test
    public void pwdSettingTest() {

        driver.findElement(By.className("btn-primary")).click();
        waitMS(300);
        driver.findElement(By.className("btn-primary")).click();
        driver.findElement(By.id("field_lower")).click();
        driver.findElement(By.id("field_digits")).click();
        driver.findElement(By.id("field_length")).clear();
        driver.findElement(By.id("field_length")).sendKeys("10");
        driver.findElement(By.className("btn-primary")).click();
        waitMS(500);
        String pwd = driver.findElement(By.id("field_password")).getAttribute("value");

        assertEquals(pwd.matches(".*[a-z0-9].*"), false);
        assertEquals(pwd.length(), 10);
    }

    @Test
    public void editTest() {
        createPass();

        driver.findElement(By.className("btn-info")).click();
        waitMS(300);
        driver.findElement(By.id("field_site")).clear();
        driver.findElement(By.id("field_site")).sendKeys("new site");
        driver.findElement(By.id("field_login")).clear();
        driver.findElement(By.id("field_login")).sendKeys("new login");
        driver.findElement(By.id("field_password")).clear();
        driver.findElement(By.id("field_password")).sendKeys("new password");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        waitMS(300);
        String site = driver.findElement(By.cssSelector("tbody>tr:nth-child(1)>td:nth-child(2)")).getText();
        String login = driver.findElement(By.cssSelector("tbody>tr:nth-child(1)>td:nth-child(3)")).getText();
        String pwd = driver.findElement(By.className("acmepass-password")).getAttribute("value");
	
        assertEquals(site, "new site");
        assertEquals(login, "new login");
        assertEquals(pwd, "new password");
    }

    @Test
    public void deleteTest() {
        createPass();

        int size_bf = driver.findElements(By.tagName("tr")).size();
        driver.findElement(By.className("btn-danger")).click();
        waitMS(300);
        driver.findElement(By.className("btn-danger")).click();
        waitMS(300);
        int size_af = driver.findElements(By.tagName("tr")).size();
        assertEquals(size_bf-size_af, 1);
    }

    @Test
    public void sortByID() {
        //Line below not require so long as the table default sorts by ID
        //driver.findElement(By.cssSelector("th[jh-sort-by=id]")).click();

        clearDatabase();
        databaseCommand("INSERT INTO acmepass VALUES ('1','zombie.com','zula','3AC9t/Opqh+D4LEZU0LFYg==','2017-06-14 10:32:02','2017-06-14 10:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('2','allthebeans.com','alphonse','j2T+ivryl+YPPvI1CNvvjw==','2016-05-13 01:32:02','2016-05-13 01:32:02','6');");

        waitMS(300);
        String id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("1", id);

        driver.findElement(By.cssSelector("th[jh-sort-by=id]")).click();
        waitMS(300);
        id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("2", id);

        clearDatabase();
    }

    @Test
    public void sortBySite() {

        clearDatabase();
        databaseCommand("INSERT INTO acmepass VALUES ('2','allthebeans.com','zula','3AC9t/Opqh+D4LEZU0LFYg==','2017-06-14 10:32:02','2017-06-14 10:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('1','zombie.com','alphonse','j2T+ivryl+YPPvI1CNvvjw==','2016-05-13 01:32:02','2016-05-13 01:32:02','6');");

        driver.findElement(By.cssSelector("th[jh-sort-by=site]")).click();
        waitMS(300);
        String site = driver.findElements(By.cssSelector("tr > td:nth-child(2)")).get(0).getText();

        assertEquals("allthebeans.com", site);

        driver.findElement(By.cssSelector("th[jh-sort-by=site]")).click();
        waitMS(300);
        site = driver.findElements(By.cssSelector("tr > td:nth-child(2)")).get(0).getText();

        assertEquals("zombie.com", site);

        clearDatabase();
    }

    @Test
    public void sortByLogin() {

        clearDatabase();
        databaseCommand("INSERT INTO acmepass VALUES ('2','zombie.com','alphonse','3AC9t/Opqh+D4LEZU0LFYg==','2017-06-14 10:32:02','2017-06-14 10:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('1','allthebeans.com','zula','j2T+ivryl+YPPvI1CNvvjw==','2016-05-13 01:32:02','2016-05-13 01:32:02','6');");

        driver.findElement(By.cssSelector("th[jh-sort-by=login]")).click();
        waitMS(300);
        String login = driver.findElements(By.cssSelector("tr > td:nth-child(3)")).get(0).getText();

        assertEquals("alphonse", login);

        driver.findElement(By.cssSelector("th[jh-sort-by=login]")).click();
        waitMS(300);
        login = driver.findElements(By.cssSelector("tr > td:nth-child(3)")).get(0).getText();

        assertEquals("zula", login);

        clearDatabase();
    }

    @Test
    public void sortByPassword() {

        clearDatabase();
        //Passwords are inserted in their encrypted forms:
        databaseCommand("INSERT INTO acmepass VALUES ('2','zombie.com','zula','j2T+ivryl+YPPvI1CNvvjw==','2017-06-14 10:32:02','2017-06-14 10:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('1','allthebeans.com','alphonse','3AC9t/Opqh+D4LEZU0LFYg==','2016-05-13 01:32:02','2016-05-13 01:32:02','6');");

        driver.findElement(By.cssSelector("th[jh-sort-by=password]")).click();
        waitMS(300);

        String id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("2", id);

        driver.findElement(By.cssSelector("th[jh-sort-by=password]")).click();
        waitMS(300);

        id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("1", id);

        clearDatabase();
    }

    @Test
    public void sortByDateCreated() {

        clearDatabase();
        databaseCommand("INSERT INTO acmepass VALUES ('2','zombie.com','zula','3AC9t/Opqh+D4LEZU0LFYg==','2016-05-13 01:32:02','2017-06-14 10:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('1','allthebeans.com','alphonse','j2T+ivryl+YPPvI1CNvvjw==','2016-09-14 10:32:02','2016-10-29 11:32:02','6');");

        driver.findElement(By.cssSelector("th[jh-sort-by=createdDate]")).click();
        waitMS(300);
        String createdDate = driver.findElements(By.cssSelector("tr > td:nth-child(5)")).get(0).getText();

        assertEquals("May 13, 2016 1:32:02 AM", createdDate);

        driver.findElement(By.cssSelector("th[jh-sort-by=createdDate]")).click();
        waitMS(300);
        createdDate = driver.findElements(By.cssSelector("tr > td:nth-child(5)")).get(0).getText();

        assertEquals("Sep 14, 2016 10:32:02 AM", createdDate);

        clearDatabase();
    }

    @Test
    public void sortByDateModified() {

        clearDatabase();
        databaseCommand("INSERT INTO acmepass VALUES ('2','zombie.com','zula','3AC9t/Opqh+D4LEZU0LFYg==','2016-09-14 10:32:02','2016-10-29 11:32:02','6');");
        databaseCommand("INSERT INTO acmepass VALUES ('1','allthebeans.com','alphonse','j2T+ivryl+YPPvI1CNvvjw==','2016-05-13 01:32:02','2017-06-14 10:32:02','6');");

        driver.findElement(By.cssSelector("th[jh-sort-by=lastModifiedDate]")).click();
        waitMS(300);
        String lastModifiedDate = driver.findElements(By.cssSelector("tr > td:nth-child(6)")).get(0).getText();

        assertEquals("Oct 29, 2016 11:32:02 AM", lastModifiedDate);

        driver.findElement(By.cssSelector("th[jh-sort-by=lastModifiedDate]")).click();
        waitMS(300);
        lastModifiedDate = driver.findElements(By.cssSelector("tr > td:nth-child(6)")).get(0).getText();

        assertEquals("Jun 14, 2017 10:32:02 AM", lastModifiedDate);

        clearDatabase();
    }

    @After
    public void closeDown() {
        driver.close();
    }

    @AfterClass
    public static void closeBrowser(){
        clearDatabase();
    }

    private void createPass() {
        driver.findElement(By.className("btn-primary")).click();
        driver.findElement(By.id("field_site")).sendKeys("site");
        driver.findElement(By.id("field_login")).sendKeys("login");
        driver.findElement(By.id("field_password")).sendKeys("password");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        waitMS(500);
    }

    private void waitMS(int timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        }catch(Exception e){}
    }

    private static void databaseCommand(String sql) {
        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/acme?useUnicode=true&characterEncoding=utf8&useSSL=false";
        String user = "acme";
        String password = "acme";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            Statement statement = con.createStatement();
            statement.executeUpdate(sql);
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }catch(SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearDatabase() {
        databaseCommand("truncate acmepass");
    }
}
