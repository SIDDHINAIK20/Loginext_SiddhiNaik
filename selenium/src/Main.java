import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Set path to chromedriver executable
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\91935\\Desktop\\selenium\\chromedriver.exe");

            WebDriver driver = new ChromeDriver();

            // 1. Go to Google Maps
            driver.get("https://maps.google.com");
            Thread.sleep(3000);

            // 2. Click 'Directions' button
            driver.findElement(By.xpath("//button[@aria-label='Directions']")).click();
            Thread.sleep(2000);

            // 3. Enter starting location (your residential location)
            WebElement start = driver.findElement(By.xpath("//input[contains(@aria-label, 'Choose starting point')]"));
            start.sendKeys("YOUR_HOME_ADDRESS");
            Thread.sleep(1000);
            start.sendKeys(Keys.RETURN);
            Thread.sleep(2000);

            // 4. Enter destination as "91 Springboard, Vikhroli"
            WebElement dest = driver.findElement(By.xpath("//input[contains(@aria-label, 'Choose destination')]"));
            dest.sendKeys("91 Springboard, Vikhroli");
            Thread.sleep(1000);
            dest.sendKeys(Keys.RETURN);
            Thread.sleep(5000); // Wait for routes to load

            // 5. Select the first route (usually selected by default, so skipping extra click)

            // 6. Extract all driving instructions
            List<WebElement> directions = driver.findElements(By.xpath("//div[contains(@class,'directions-mode-step')]//div[@jstcache]"));

            // Fallback if above doesn't find steps
            if (directions.size() == 0) {
                directions = driver.findElements(By.xpath("//div[@class='M7jA4b']"));
            }

            // Write directions into Excel sheet
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Directions");

            for (int i = 0; i < directions.size(); i++) {
                String stepText = directions.get(i).getText().replace("\n", " ");
                if (!stepText.isEmpty()) {
                    XSSFRow row = sheet.createRow(i);
                    row.createCell(0).setCellValue("Step " + (i + 1));
                    row.createCell(1).setCellValue(stepText);
                }
            }

            FileOutputStream fos = new FileOutputStream("driving_instructions.xlsx");
            workbook.write(fos);
            fos.close();
            workbook.close();

            // 7. Take screenshot of the page
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);
            ImageIO.write(fullImg, "png", new File("directions_screenshot.png"));

            System.out.println("Automation Completed. Excel and screenshot saved.");

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
