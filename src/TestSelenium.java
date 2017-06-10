import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSelenium {
    private static WebDriver driver;

    @BeforeClass
    public static void openBrowser(){
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Before
    public void loginIn() {
        ACMEpass page = new ACMEpass(driver);
        page.signIn("admin@acme.com", "K-10ficile", false);
        TimeUnit.SECONDS.sleep(1);
        driver.get("http://localhost:8080/#/acme-pass");
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void goto_HomePage(){
        System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
        ACMEpass page = new ACMEpass(driver);
        page.signIn("admin@acme.com", "K-10ficile", false);
        String url = driver.getCurrentUrl();
        Assert.assertEquals("http://localhost:8080/#/", url);
        try{
            TimeUnit.SECONDS.sleep(3);
        }catch(Exception e){

        }
        page.go();
        try{
            TimeUnit.SECONDS.sleep(3);
        }catch(Exception e){

        }
        page.signOut();
        try{
            TimeUnit.SECONDS.sleep(3);
        }catch(Exception e){

        }
        System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test
    public void createTest() throws Exception {
        int size_bf = driver.findElements(By.tagName("tr")).size();
        createPass();
        int size_af = driver.findElements(By.tagName("tr")).size();
        assertEquals(1, size_af-size_bf);
    }

    @Test
    public void genPwdTest() throws Exception {

        driver.findElement(By.className("btn-primary")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        driver.findElement(By.className("btn-primary")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        driver.findElement(By.className("btn-primary")).click();
        String pwd = driver.findElement(By.id("field_password")).getAttribute("value");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        TimeUnit.MILLISECONDS.sleep(300);
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
    public void pwdSettingTest() throws Exception {

        driver.findElement(By.className("btn-primary")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        driver.findElement(By.className("btn-primary")).click();
        driver.findElement(By.id("field_lower")).click();
        driver.findElement(By.id("field_length")).clear();
        driver.findElement(By.id("field_length")).sendKeys("10");
        driver.findElement(By.className("btn-primary")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        String pwd = driver.findElement(By.id("field_password")).getAttribute("value");

        assertEquals(pwd.matches(".*[a-z].*"), false);
        assertEquals(pwd.length(), 10);
    }

    @Test
    public void editTest() throws Exception {
        createPass();

        driver.findElement(By.className("btn-info")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        driver.findElement(By.id("field_password")).clear();
        driver.findElement(By.id("field_password")).sendKeys("new password");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        TimeUnit.MILLISECONDS.sleep(500);
        String pwd = driver.findElement(By.className("acmepass-password")).getAttribute("value");

        assertEquals(pwd, "new password");
    }

    @Test
    public void deleteTest() throws Exception {
        createPass();

        int size_bf = driver.findElements(By.tagName("tr")).size();
        driver.findElement(By.className("btn-danger")).click();
        TimeUnit.MILLISECONDS.sleep(300);
        driver.findElement(By.className("btn-danger")).click();
        TimeUnit.MILLISECONDS.sleep(500);
        int size_af = driver.findElements(By.tagName("tr")).size();
        assertEquals(size_bf-size_af, 1);
    }

    @After
    public void closeDown() {
        driver.close();
    }

    @AfterClass
    public static void closeBrowser(){
        clearDatabase();
    }

    private void createPass() throws Exception {
        driver.findElement(By.className("btn-primary")).click();
        driver.findElement(By.id("field_site")).sendKeys("site");
        driver.findElement(By.id("field_login")).sendKeys("login");
        driver.findElement(By.id("field_password")).sendKeys("password");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        TimeUnit.MILLISECONDS.sleep(500);
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