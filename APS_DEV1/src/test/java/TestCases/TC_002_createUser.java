package TestCases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Base.BaseClass;
import PageObjects.Admin;
import PageObjects.ControlPanel;
import PageObjects.LoginPage;
import PageObjects.UsersPage;
import Utility.ExcelUtility;

public class TC_002_createUser extends BaseClass {

    Logger logger = LogManager.getLogger(TC_002_createUser.class);
    String excelPath = System.getProperty("user.dir") + "\\TestData\\UserData.xlsx";
    String loginsheetName = "Sheet2";
    String usersheetName = "Sheet1";

    @DataProvider(name = "combinedData")
    public Object[][] combinedData() throws Exception {

        // Read login data
        ExcelUtility.setExcelFile(excelPath, loginsheetName);
        String loginUsername = ExcelUtility.getCellData(1, 0);
        String loginPassword = ExcelUtility.getCellData(1, 1);
        ExcelUtility.closeExcel();

        // Read user creation data
        ExcelUtility.setExcelFile(excelPath, usersheetName);
        int userRowCount = ExcelUtility.getRowCount();
        int colCount = 6;

        Object[][] data = new Object[userRowCount - 1][colCount + 2];

        for (int i = 1; i < userRowCount; i++) {
            data[i - 1][0] = loginUsername;
            data[i - 1][1] = loginPassword;

            for (int j = 0; j < colCount; j++) {
                data[i - 1][j + 2] = ExcelUtility.getCellData(i, j);
            }
        }
        ExcelUtility.closeExcel();
        return data;
    }

    @Test(dataProvider = "combinedData", groups = {"Regression"})
    public void loginAndCreateUser(String loginUsername, String loginPassword,
                                   String newUsername, String firstname, String lastname,
                                   String newPassword, String email, String activeFlag) throws Exception {

        logger.info("===== Starting Login for user: " + loginUsername + " =====");

        LoginPage loginPage = new LoginPage(driver);

        try {
  
            loginPage.enterUsername(loginUsername);
            loginPage.enterPassword(loginPassword);
            loginPage.clickLogin();

            Assert.assertTrue(loginPage.isHomePageVisible(), "Login failed — home page not visible.");
            logger.info("Login successful for user: " + loginUsername);

            logger.info("===== Starting User Creation for: " + newUsername + " =====");

            ControlPanel controlpanel = new ControlPanel(driver);
            Admin admin = new Admin(driver);
            UsersPage usersPage = new UsersPage(driver);

            controlpanel.clickcontrolPanel();
            admin.clickadmin();
            usersPage.clickCreateUser();

            usersPage.enterUsername(newUsername);
            usersPage.enterFirstname(firstname);
            usersPage.enterLastname(lastname);
            usersPage.enterPassword(newPassword);
            usersPage.confirmPassword(newPassword);
            usersPage.enterEmail(email);

           usersPage.activeFlag();

            usersPage.saveDetails();

            boolean success = usersPage.isSuccessMessageVisible();
            Assert.assertTrue(success, "User creation failed for: " + newUsername);

            logger.info("===== User created successfully: " + newUsername + " =====");

            captureScreen("CreateUser_" + newUsername + "_PASS");

        } catch (Exception | AssertionError e) {
          
            captureScreen("CreateUser_" + newUsername + "_FAIL");

            logger.error("❌ Failure during Login or User Creation for: " + newUsername);
            logger.error("Reason: " + e.getMessage());

            throw e;
        } finally {

            try {
                driver.manage().deleteAllCookies();
                driver.navigate().refresh();
                logger.info("Session reset for next iteration.");
            } catch (Exception ex) {
                logger.warn("Could not reset session after test: " + ex.getMessage());
            }
        }
    }
}
