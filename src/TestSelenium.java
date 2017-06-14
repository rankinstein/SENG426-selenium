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
        int size_bf = driver.findElements(By.tagName("tr")).size();
        createPass();
        int size_af = driver.findElements(By.tagName("tr")).size();
        assertEquals(1, size_af-size_bf);
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
        driver.findElement(By.id("field_length")).clear();
        driver.findElement(By.id("field_length")).sendKeys("10");
        driver.findElement(By.className("btn-primary")).click();
        waitMS(500);
        String pwd = driver.findElement(By.id("field_password")).getAttribute("value");

        assertEquals(pwd.matches(".*[a-z].*"), false);
        assertEquals(pwd.length(), 10);
    }

    @Test
    public void editTest() {
        createPass();

        driver.findElement(By.className("btn-info")).click();
        waitMS(300);
        driver.findElement(By.id("field_password")).clear();
        driver.findElement(By.id("field_password")).sendKeys("new password");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        waitMS(300);
        String pwd = driver.findElement(By.className("acmepass-password")).getAttribute("value");

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

        waitMS(300);
        String id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("1", id);

        driver.findElement(By.cssSelector("th[jh-sort-by=id]")).click();
        waitMS(300);
        id = driver.findElements(By.cssSelector("tr > td:nth-child(1)")).get(0).getText();

        assertEquals("8", id);
    }

    @Test
    public void sortBySite() {
        driver.findElement(By.cssSelector("th[jh-sort-by=site]")).click();
        waitMS(300);
        String site = driver.findElements(By.cssSelector("tr > td:nth-child(2)")).get(0).getText();

        assertEquals("site", site);

        driver.findElement(By.cssSelector("th[jh-sort-by=site]")).click();
        waitMS(300);
        site = driver.findElements(By.cssSelector("tr > td:nth-child(2)")).get(0).getText();

        assertEquals("site", site);
    }

    @Test
    public void sortByLogin() {
        driver.findElement(By.cssSelector("th[jh-sort-by=login]")).click();
        waitMS(300);
        String login = driver.findElements(By.cssSelector("tr > td:nth-child(3)")).get(0).getText();

        assertEquals("1ogin", login);

        driver.findElement(By.cssSelector("th[jh-sort-by=login]")).click();
        waitMS(300);
        login = driver.findElements(By.cssSelector("tr > td:nth-child(3)")).get(0).getText();

        assertEquals("login", login);
    }

    @Test
    public void sortByPassword() {
        driver.findElement(By.cssSelector("th[jh-sort-by=password]")).click();
        waitMS(300);
        String password = driver.findElements(By.cssSelector("tr > td:nth-child(4)")).get(0).getText();

        //FIXME: FIGURE OUT WHAT THESE PASSWORDS ARE
        assertEquals("password", password);

        driver.findElement(By.cssSelector("th[jh-sort-by=password]")).click();
        waitMS(300);
        password = driver.findElements(By.cssSelector("tr > td:nth-child(4)")).get(0).getText();

        //FIXME: FIGURE OUT WHAT THESE PASSWORDS ARE
        assertEquals("password", password);
    }

    @Test
    public void sortByDateCreated() {
        driver.findElement(By.cssSelector("th[jh-sort-by=createdDate]")).click();
        waitMS(300);
        String createdDate = driver.findElements(By.cssSelector("tr > td:nth-child(5)")).get(0).getText();

        assertEquals("Jun 12, 2017 6:08:03 PM", createdDate);

        driver.findElement(By.cssSelector("th[jh-sort-by=createdDate]")).click();
        waitMS(300);
        createdDate = driver.findElements(By.cssSelector("tr > td:nth-child(5)")).get(0).getText();

        assertEquals("Jun 13, 2017 12:25:12 PM", createdDate);
    }

    @Test
    public void sortByDateModified() {
        driver.findElement(By.cssSelector("th[jh-sort-by=lastModifiedDate]")).click();
        waitMS(300);
        String lastModifiedDate = driver.findElements(By.cssSelector("tr > td:nth-child(6)")).get(0).getText();

        assertEquals("Jun 12, 2017 6:08:03 PM", lastModifiedDate);

        driver.findElement(By.cssSelector("th[jh-sort-by=lastModifiedDate]")).click();
        waitMS(300);
        lastModifiedDate = driver.findElements(By.cssSelector("tr > td:nth-child(6)")).get(0).getText();

        assertEquals("Jun 13, 2017 12:25:12 PM", lastModifiedDate);

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

    private static void clearDatabase() {
        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/acme?useUnicode=true&characterEncoding=utf8&useSSL=false";
        String user = "acme";
        String password = "acme";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            Statement statement = con.createStatement();
            String sql = "truncate acmepass";
            statement.executeUpdate(sql);
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }catch(SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}