package TestCases;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.Assert;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import Base.BaseClass;
import PageObjects.LoginPage;
import Utility.ExcelUtility;

public class TC_001_Login extends BaseClass {

    Logger logger = LogManager.getLogger(TC_001_Login.class);
    String excelPath = System.getProperty("user.dir") + "\\TestData\\LoginData.xlsx";
    String sheetName = "Sheet1";

    @BeforeClass(alwaysRun = true)
    public void openExcel() throws Exception {
        ExcelUtility.setExcelFile(excelPath, sheetName);
    }

    @AfterClass(alwaysRun = true)
    public void closeExcel() {
        try { ExcelUtility.closeExcel(); } catch (Exception ignored) {}
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws Exception {
        int rowCount = ExcelUtility.getRowCount();
        int colCount = 3; // username, password, type

        Object[][] data = new Object[rowCount - 1][colCount]; // skipping header
        for (int i = 1; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = ExcelUtility.getCellData(i, j);
            }
        }
        return data;
    }

    @Test(dataProvider = "loginData", groups = {"Regression"})
    public void loginTest(String username, String password, String type) throws Exception {

        logger.info("Starting login test for user: " + username);
        LoginPage loginPage = new LoginPage(driver);

        int row = findRow(username);

        try {
            // Perform login
            loginPage.login(username, password);

            if (type.equalsIgnoreCase("valid")) {

                Assert.assertTrue(loginPage.isHomePageVisible(), "Home page not visible for valid login");
                logger.info("Valid login successful for: " + username);

                // Capture screenshot immediately after login
                captureScreen("Login_" + username + "_PASS");

                loginPage.clickUserIcon();
                loginPage.logout();
                resetSession();

                ExcelUtility.setCellData("Pass", row, 3);

            } else { // invalid login

                Assert.assertTrue(loginPage.isInvalidMessageVisible(), "Invalid login message NOT displayed");
                logger.info("Invalid login handled correctly for: " + username);

                // Capture screenshot immediately after invalid login
                captureScreen("Login_" + username + "_PASS");

                resetSession();
                ExcelUtility.setCellData("Pass", row, 3);
            }

        } catch (AssertionError | Exception e) {

            // Capture screenshot on failure
            captureScreen("Login_" + username + "_FAIL");

            ExcelUtility.setCellData("Fail", row, 3);
            logger.error("Test failed for user: " + username + " — " + e.getMessage());
            throw e;
        }
    }

    private int findRow(String username) throws Exception {
        int rowCount = ExcelUtility.getRowCount();
        for (int i = 1; i <= rowCount; i++) {
            if (ExcelUtility.getCellData(i, 0).equalsIgnoreCase(username)) {
                return i;
            }
        }
        return -1;  // Not found
    }
}
