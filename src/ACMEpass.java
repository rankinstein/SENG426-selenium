import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonah on 2017-06-07.
 */
public class ACMEpass extends PageObject {

    List<tableRow> table;

    public ACMEpass(WebDriver driver) {
        super(driver);
    }

    public void go(){
        driver.get(baseURL + "/acme-pass");
        table = readTable();
    }

    private List<tableRow> readTable(){
        List<WebElement> rows = driver.findElements(By.cssSelector("div.table-responsive > table > tbody > tr"));
        List<tableRow> table = null;
        for (WebElement row : rows) {
            table.add(new tableRow(row));
        }
        return table;
    }
}
class tableRow {
    public Integer id;
    public String site;
    public String login;
    public String password;
    private WebElement visibility;
    public Date created;
    public Date modified;
    private WebElement edit;
    private WebElement delete;

    public tableRow(WebElement row){
        id = Integer.parseInt(row.findElement(By.cssSelector("td:nth-child(1)")).getText());
        site = row.findElement(By.cssSelector("td:nth-child(2)")).getText();
        login = row.findElement(By.cssSelector("td:nth-child(3)")).getText();
        password = row.findElement(By.cssSelector("td:nth-child(4) input")).getText();
        visibility = row.findElement(By.cssSelector("td:nth-child(4) span"));
        created = new Date(row.findElement(By.cssSelector("td:nth-child(5)")).getText());
        modified = new Date(row.findElement(By.cssSelector("td:nth-child(6)")).getText());
        edit = row.findElement(By.className("btn-info"));
        delete = row.findElement(By.className("btn-danger"));
    }

    public void edit(){
        edit.click();
    }

    public void delete(){
        delete.click();
    }

    public void toggleVisibility(){
        visibility.click();
    }
}
