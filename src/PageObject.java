import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

/**
 * Created by Jonah on 2017-06-07.
 */
public class PageObject {
    protected static final String baseURL = "http://localhost:8080/#";
    protected WebDriver driver;

    public PageObject(WebDriver driver) {
        this.driver = driver;
    }

    public void go() {
        driver.get(baseURL);
    }

    public void home(){
        driver.get(baseURL);
    }

    public void openSignInModal() {
        driver.findElement(By.id("login")).click();
    }

    public void enterUsername(String username){
        driver.findElement(By.id("username")).sendKeys(username);
    }

    public void enterPassword(String password){
        driver.findElement(By.id("password")).sendKeys(password);
    }

    public void pressSignInButton(){
        driver.findElement(By.cssSelector("body > div.modal.fade.in > div > div > div.modal-body > div > div:nth-child(2) > form > button")).click();
    }

    public void clickRememberMe(){
        driver.findElement(By.id("rememberMe")).click();
    }

    public boolean onAcmeSite(){
        return driver.getCurrentUrl().contains(baseURL);
    }

    public void signIn(String email, String password, Boolean rememberMe){
        if(!onAcmeSite()){
            home();
        }
        openSignInModal();
        enterUsername(email);
        enterPassword(password);
        if(!rememberMe) {
           clickRememberMe();
        }
        pressSignInButton();
    }

    public void openAccountMenu(){
        driver.findElement(By.id("account-menu")).click();
    }

    public void signOut(){
        openAccountMenu();
        driver.findElement(By.id("logout")).click();
    }
}
