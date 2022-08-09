package hooks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.core.LoggerContext;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class TestListeners {
    private final Logger _logger = LogManager.getLogger(TestListeners.class);

    @SuppressWarnings("all")
    @BeforeSuite(alwaysRun = true)
    public void initializeTestSuite(ITestContext context) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        System.setProperty("current.date", LocalDateTime.now().format(format));
        new File(System.getProperty("user.dir") + File.separator + ".reports").mkdirs();

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.reconfigure();

        _logger.info(String.format("%s*** Test Suite %s started running... ***%s", utils.Logger.ANSI_GREEN, context.getName(), utils.Logger.ANSI_RESET));
    }

    @BeforeClass(alwaysRun = true)
    public void initializeTestClass(ITestContext context) {
    }

    @BeforeMethod(alwaysRun = true)
    public void config(ITestResult result) {
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult iTestResult) {
    }

    @AfterSuite(alwaysRun = true)
    public void completeSuite(ITestContext context) {
        _logger.info(String.format("%s*** Test Suite %s ended. ***%s", utils.Logger.ANSI_GREEN, context.getName(), utils.Logger.ANSI_RESET));
    }
}
