import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Jonah on 2017-06-07.
 */
public class ACMEpass extends PageObject {

    private List<tableRow> table;

    public ACMEpass(WebDriver driver) {
        super(driver);
    }

    /*
     * Reads the table data on the ACMEPass page
     */
    public List<tableRow> readTable(){
        List<WebElement> rows = driver.findElements(By.cssSelector("div.table-responsive > table > tbody > tr"));
        List<tableRow> table = new ArrayList<tableRow>();
        for (WebElement row : rows) {
            table.add(new tableRow(row));
        }
        return table;
    }

    public void go(){
        driver.get(baseURL + "/acme-pass");
        table = readTable();
    }

    public void clickCreatePassButton() {
        driver.findElement(By.cssSelector("body > div:nth-child(3) > div > div > div.container-fluid > div > div > button")).click();
    }
}
class tableRow {
    private Integer id;
    private String site;
    private String login;
    private String password;
    private WebElement visibility;
    private boolean passwordVisible;
    private Date created;
    private Date modified;
    private WebElement edit;
    private WebElement delete;

    public tableRow(WebElement row){
        id = Integer.parseInt(row.findElement(By.cssSelector("td:nth-child(1)")).getText());
        site = row.findElement(By.cssSelector("td:nth-child(2)")).getText();
        login = row.findElement(By.cssSelector("td:nth-child(3)")).getText();
        WebElement passwordEl = row.findElement(By.cssSelector("td:nth-child(4) input"));
        password = passwordEl.getAttribute("value");
        passwordVisible = !passwordEl.getAttribute("type").equals("password");
        visibility = row.findElement(By.cssSelector("td:nth-child(4) span"));
        created = new Date(row.findElement(By.cssSelector("td:nth-child(5)")).getText());
        modified = new Date(row.findElement(By.cssSelector("td:nth-child(6)")).getText());
        edit = row.findElement(By.className("btn-info"));
        delete = row.findElement(By.className("btn-danger"));
    }

    public Integer getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public void edit(){
        edit.click();
    }

    public void delete(){
        delete.click();
    }

    public boolean isVisible(){
        return passwordVisible;
    }

    public void toggleVisibility(){
        visibility.click();
    }
}
