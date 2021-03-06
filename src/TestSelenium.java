import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;

public class TestSelenium {
    private static WebDriver driver;
    private static Connection con;

    @BeforeClass
    public static void connectDB() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/acme?useUnicode=true&characterEncoding=utf8&useSSL=false";
        String user = "acme";
        String password = "acme";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }catch(SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


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
    public void viewPassList() {
        assertEquals(1, driver.findElements(By.cssSelector("table")).size());
        assertEquals(1, driver.findElements(By.cssSelector("th[jh-sort-by=id]")).size());
    }

    @Test
    public void tablePwdVisibilityTest(){
        String initialInputType;
        String nextInputType;
        String finalInputType;

        createPass();

        WebElement pwdColumn = driver.findElements(By.cssSelector("td")).get(3);
        WebElement visibilityButton = pwdColumn.findElement(By.cssSelector("span"));

        initialInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");
        visibilityButton.click();
        nextInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");
        visibilityButton.click();
        finalInputType = pwdColumn.findElement(By.cssSelector("input")).getAttribute("type");

        assertEquals("password", initialInputType);
        assertEquals("text", nextInputType);
        assertEquals("password", finalInputType);
    }

    @Test
    public void editPwdVisibilityTest(){
        String initialInputType;
        String nextInputType;
        String finalInputType;

        createPass();
        driver.findElement(By.className("btn-info")).click();
        waitMS(300);
        WebElement visibilityButton = driver.findElement(By.cssSelector("span[role='button']"));

        initialInputType = driver.findElement(By.id("field_password")).getAttribute("type");
        visibilityButton.click();
        nextInputType = driver.findElement(By.id("field_password")).getAttribute("type");
        visibilityButton.click();
        finalInputType = driver.findElement(By.id("field_password")).getAttribute("type");

        assertEquals("Initially expect password to be hidden", "password", initialInputType);
        assertEquals("Password visible when button clicked", "text", nextInputType);
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
        clearDatabase();

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
	
    @Test
    public void Pagination() {
	    
        int NextCount = 0;							
        driver.findElement(By.linkText("ACMEPass")).click();
        waitMS(2000);
        //Check if the the number of pass bars is decreased by 20 after turning to next page (when number < 40)
        //Read the number of passes in the page
        String Items = driver.findElement(By.className("info")).getText();	
        String[] bits = Items.split(" ");
        String lastWord = bits[bits.length - 2];
        int NumofItems = Integer.parseInt(lastWord);

        while(NumofItems<=20){
            createPass();
            NumofItems++;
        }
	    
        Nextpage();
        waitMS(1000);
        List<WebElement> Nextelements = driver.findElements(By.cssSelector("tbody > tr"));

        if(NumofItems<=40){
            NextCount = NumofItems - 20;
        }else{
            NextCount = 20;
        }
        Previouspage();
        waitMS(1000);
        List<WebElement> PreviousElements = driver.findElements(By.cssSelector("tbody > tr"));

        //Compare the previous ID when turn to next page then turn back to see if they are still the same,
        //and compare the next ID when turn to previous page and then turn to next to see if they are the same.
        //Compare the current page ID and the next page ID to see if they are different
        //Compare the current page ID and the previous page ID to see if they are different
        String PreviousRowID = driver.findElement(By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(1)")).getText();		
        Nextpage();
        waitMS(1000);
        String NextRowID = driver.findElement(By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(1)")).getText();		
        Previouspage();
        waitMS(1000);
        String CurrentChanged_RowID = driver.findElement(By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(1)")).getText();		
        Nextpage();
        waitMS(1000);
        String NextChanged_RowID = driver.findElement(By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(1)")).getText();		

        int num_PreviousRowID = Integer.parseInt(PreviousRowID);
        int num_NextRowID = Integer.parseInt(NextRowID);
        int num_CurrentChanged_RowID = Integer.parseInt(CurrentChanged_RowID);
        int num_NextChanged_RowID = Integer.parseInt(NextChanged_RowID);

        boolean check = false;
        if(Nextelements.size()==NextCount && PreviousElements.size()==20 && num_PreviousRowID!=num_NextRowID && num_PreviousRowID==num_CurrentChanged_RowID && num_NextRowID!=num_CurrentChanged_RowID && num_NextRowID==num_NextChanged_RowID)
            check = true;

        assertEquals(true, check);
    }

    @After
    public void closeDown() {
        driver.close();
    }

    @AfterClass
    public static void closeBrowser(){
        clearDatabase();
        try {
            con.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
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
	
    private void Nextpage(){
        driver.findElement(By.cssSelector(".pager.ng-valid.ng-not-empty > .next a")).click();
    }

    private void Previouspage() {
        driver.findElement(By.cssSelector(".pager.ng-valid.ng-not-empty > .previous a")).click();
    }

    private static void databaseCommand(String sql) {
        try {
            Statement statement = con.createStatement();
            statement.executeUpdate(sql);
            statement.close();
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
