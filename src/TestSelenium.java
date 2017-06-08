import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;

public class TestSelenium {
    private static WebDriver driver;
    WebElement element;

    @BeforeClass
    public static void openBrowser(){
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

    @AfterClass
    public static void closeBrowser(){
        driver.quit();
    }
}