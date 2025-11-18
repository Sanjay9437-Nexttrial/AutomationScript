package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Admin{
	
	private WebDriver driver;
    private WebDriverWait wait;
	
 
    @FindBy(xpath ="//div[contains(text(),'Admin')]")
    WebElement admin;
    
    public Admin(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void clickadmin() {
        wait.until(ExpectedConditions.elementToBeClickable(admin));
        admin.click();
    }
    
    
}

