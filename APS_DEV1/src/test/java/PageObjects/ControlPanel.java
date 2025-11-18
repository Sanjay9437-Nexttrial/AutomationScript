package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ControlPanel {
	
	private WebDriver driver;
    private WebDriverWait wait;
	
 
    @FindBy(xpath ="//*[@id=\"root\"]/div/div[1]/header/div/div[2]/div[2]/button[1]")
    WebElement controlpanel;
    
    public ControlPanel(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void clickcontrolPanel() {
        wait.until(ExpectedConditions.elementToBeClickable(controlpanel));
        controlpanel.click();
    }
    
    
}
