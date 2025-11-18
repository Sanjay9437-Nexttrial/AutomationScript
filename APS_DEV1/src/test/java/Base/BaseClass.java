package Base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterMethod;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

    public WebDriver driver;
    public static Logger logger;
    public Properties p;

    @BeforeClass(alwaysRun = true)
    @Parameters({"os", "browser"})
    public void setup(String os, String br) throws IOException {
        System.setProperty("log4j.configurationFile",
                System.getProperty("user.dir") + File.separator + "Resources" + File.separator + "log4j2.xml");
        logger = LogManager.getLogger(this.getClass());
        logger.info("========== Starting Test Setup ==========");

        FileReader file = new FileReader("./Resources/config.properties");
        p = new Properties();
        p.load(file);
        logger.info("Loaded configuration file successfully.");

        if (p.getProperty("execution_env").equalsIgnoreCase("remote")) {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            switch (os.toLowerCase()) {
                case "windows" -> capabilities.setPlatform(Platform.WIN10);
                case "linux" -> capabilities.setPlatform(Platform.LINUX);
                case "mac" -> capabilities.setPlatform(Platform.MAC);
                default -> throw new IllegalArgumentException("No matching OS");
            }

            switch (br.toLowerCase()) {
                case "chrome" -> capabilities.setBrowserName("chrome");
                case "edge" -> capabilities.setBrowserName("MicrosoftEdge");
                case "firefox" -> capabilities.setBrowserName("firefox");
                default -> throw new IllegalArgumentException("No matching browser");
            }

            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
            logger.info("Remote WebDriver initialized for browser: " + br);

        } else if (p.getProperty("execution_env").equalsIgnoreCase("local")) {
            switch (br.toLowerCase()) {
                case "chrome" -> {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    driver = new ChromeDriver(chromeOptions);
                    logger.info("Local Chrome browser launched successfully.");
                }
                case "edge" -> {
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    logger.info("Local Edge browser launched successfully.");
                }
                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    logger.info("Local Firefox browser launched successfully.");
                }
                default -> throw new IllegalArgumentException("Invalid browser name");
            }
        } else {
            throw new IllegalArgumentException("Invalid execution environment");
        }

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(p.getProperty("appURL1"));
        driver.navigate().refresh();
        logger.info("Browser window maximized, cookies cleared, navigated to: " + p.getProperty("appURL1"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing the browser.");
            driver.quit();
            logger.info("========== Test Execution Completed ==========");
        }
    }

    public void resetSession() {
        try {
            driver.manage().deleteAllCookies();
            driver.navigate().refresh();
            logger.info("Session reset: cookies cleared and page refreshed.");
        } catch (Exception e) {
            logger.warn("Failed to reset session: " + e.getMessage());
        }
    }

    public String randomString() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String randomNumber() {
        return RandomStringUtils.randomNumeric(10);
    }

    public String randomAlphaNumeric() {
        return RandomStringUtils.randomAlphabetic(3) + "@" + RandomStringUtils.randomNumeric(3);
    }

    public String captureScreen(String tname) throws IOException {
        File dir = new File(System.getProperty("user.dir") + File.separator + "screenshots");
        if (!dir.exists()) dir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File targetFile = new File(dir, tname + "_" + timeStamp + ".png");
        java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath());
        logger.info("Screenshot captured: " + targetFile.getAbsolutePath());
        return targetFile.getAbsolutePath();
    }

    // ===== New: Capture screenshot for both PASS and FAIL =====
    @AfterMethod(alwaysRun = true)
    public void captureScreenshotAfterTest(ITestResult result) {
        try {
            String status;
            if (result.getStatus() == ITestResult.FAILURE) {
                status = "FAIL";
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                status = "PASS";
            } else {
                status = "SKIP";
            }

            String screenshotName = result.getName() + "_" + status;
            String path = captureScreen(screenshotName);
            logger.info("Test " + status + ": " + result.getName() + ", screenshot saved at: " + path);
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: " + e.getMessage());
        }
    }
}
