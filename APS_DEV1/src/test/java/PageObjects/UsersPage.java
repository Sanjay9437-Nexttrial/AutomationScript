package PageObjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import TestCases.TC_002_createUser;

import java.time.Duration;

public class UsersPage {

    private WebDriver driver;
    private WebDriverWait wait;
    Logger logger = LogManager.getLogger(UsersPage.class);

    // ================= Locators =================
    
    
    @FindBy(xpath = "//button[@type='button' and normalize-space()='CREATE']")
    private WebElement createUserbutton;

    @FindBy(xpath = "//input[@id='userForm.userName']")
    private WebElement txtusername;

    @FindBy(xpath = "//input[@id='userForm.firstName']")
    private WebElement txtfirstname;

    @FindBy(xpath = "//input[@id='userForm.lastName']")
    private WebElement txtlastname;

    @FindBy(xpath = "//input[@id='userForm.password']")
    private WebElement txtpassword;

    @FindBy(xpath = "//input[@id='userForm.confirmPassword']")
    private WebElement txtcnfmpassword;

    @FindBy(xpath = "//input[@id='userForm.email']")       
    private WebElement txtemail;

    @FindBy(xpath = "//input[@id='userForm.activeFg']/ancestor::label")
    private WebElement toggleactive;

    @FindBy(xpath = "//button[contains(text(),'SAVE')]")
    private WebElement clickSave;

    @FindBy(xpath = "//div[@id='notistack-snackbar' and (contains(., 'created successfully') or contains(., 'updated successfully'))]")
    private WebElement successMessage;

    @FindBy(xpath = "//button[@type='button' and normalize-space()='Assign Roles']")
    private WebElement assignRoles;

    // ================= Constructor =================
    public UsersPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ================= Actions =================
    public void clickCreateUser() {
        wait.until(ExpectedConditions.elementToBeClickable(createUserbutton));
        createUserbutton.click();
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(txtusername)).clear();
        txtusername.sendKeys(username);
    }

    public void enterFirstname(String firstname) {
        wait.until(ExpectedConditions.visibilityOf(txtfirstname)).clear();
        txtfirstname.sendKeys(firstname);
    }

    public void enterLastname(String lastname) {
        wait.until(ExpectedConditions.visibilityOf(txtlastname)).clear();
        txtlastname.sendKeys(lastname);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(txtpassword)).clear();
        txtpassword.sendKeys(password);
    }

    public void confirmPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(txtcnfmpassword)).clear();
        txtcnfmpassword.sendKeys(password);
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.elementToBeClickable(txtemail));
        Actions actions = new Actions(driver);
        actions.moveToElement(txtemail).click()
               .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
               .sendKeys(Keys.DELETE)
               .sendKeys(email)
               .perform();
    }

    public void activeFlag() {
        wait.until(ExpectedConditions.elementToBeClickable(toggleactive)).click();
        logger.info("User is Active");
    }
    public void assignRoles() {
        wait.until(ExpectedConditions.elementToBeClickable(assignRoles)).click();
    }

    public void saveDetails() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(clickSave)).click();
        } catch (Exception e) {
            // Use JS click if normal click fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickSave);
        }
    }

    public boolean isSuccessMessageVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Wait for either 'created successfully' or 'updated successfully'
//            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.xpath("//div[@id='notistack-snackbar' and (contains(., 'created successfully') or contains(., 'updated successfully'))]")
//            ));

//            System.out.println("Snackbar message text: " + successMessage.getText());
           logger.info("Snackbar detected: " + successMessage.getText());
            return true;
        } catch (Exception e) {
            logger.warn("No success snackbar detected within timeout: " + e.getMessage());
            return false;
        }
    }
}
