package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ================= Locators =================
    @FindBy(id = "loginForm.username")
    private WebElement txtUsername;

    @FindBy(id = "loginForm.password")
    private WebElement txtPassword;

    @FindBy(xpath = "//button[contains(text(), 'Submit')]")
    private WebElement btnLogin;

    @FindBy(xpath = "//div[contains(text(), 'Home')]")
    private WebElement homePageText;

    @FindBy(xpath = "//div[contains(text(),'Invalid')]")
    private WebElement invalidMessage;
    
    @FindBy(xpath = "//div[contains(@class,'MuiAvatar-root') and contains(@class,'MuiAvatar-circular')]")
    private WebElement userIcon;

    @FindBy(xpath = "//li[contains(text(),'Sign out')]")
    private WebElement btnLogout;

    // ================= Constructor =================
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ================= Actions =================
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(txtUsername));
        txtUsername.clear();
        txtUsername.sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(txtPassword));
        txtPassword.clear();
        txtPassword.sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLogin));
        btnLogin.click();
    }

    public boolean isHomePageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(homePageText));
            return homePageText.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInvalidMessageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(invalidMessage));
            return invalidMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void clickUserIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(userIcon));
        userIcon.click();
    }

    public void logout() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnLogout));
            btnLogout.click();
        } catch (Exception e) {
            System.out.println("Logout button not found or already logged out.");
        }
    }

    // ================= Combined Methods =================
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}
