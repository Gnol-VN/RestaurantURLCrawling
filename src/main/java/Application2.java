import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jsoup.internal.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Application2 {
    private static WebDriver driver;
    public static void main(String[] args) throws Exception {

        String chromeDriverPath = "chromedriver";
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        driver.get("https://www.blueashchili.com/");
        Set<String> toastURLSetGetByFindElements = new HashSet<>();
        Set<String> toastURLSetGetByBruteForce = new HashSet<>();
        toastURLSetGetByFindElements = Application2.getURLByFindElement();
        toastURLSetGetByBruteForce = Application2.getURLByBruteForce();

        System.out.println("");
    }
    private static Set<String> getURLByBruteForce() throws Exception{
        Set<String> urlSetGetByBruteForce = new HashSet<>();
        List<WebElement> listOfElements = driver.findElements(By.xpath("//*"));
        for(WebElement webElement : listOfElements){
            Map<String, String > attributeMap = (Map<String, String>) ((ChromeDriver) driver).executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;"
                    , webElement);
            for(Entry<String, String> entry: attributeMap.entrySet()){
                if(entry.getValue().contains("toasttab")){
                    urlSetGetByBruteForce.add(entry.getValue());
                }
            }
        }
        return urlSetGetByBruteForce;
    }
    private static Set<String> getURLByFindElement(){
        Set<String> allURLs = new HashSet<>();
        Set<String> toastURLs = new HashSet<>();

        List<WebElement> hrefList = driver.findElements(By.cssSelector("a"));
        for(WebElement href : hrefList){
            allURLs.add(href.getAttribute("href"));
        }

        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        for(WebElement button : buttons){
            allURLs.add(button.getAttribute("formaction"));
        }

        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        for(WebElement input : inputs){
            allURLs.add(input.getAttribute("formaction"));
        }

        List<WebElement> forms = driver.findElements(By.cssSelector("form"));
        for(WebElement form : forms){
            allURLs.add(form.getAttribute("action"));
        }

        List<WebElement> iFrames = driver.findElements(By.cssSelector("iframe"));
        for(WebElement iFrame : iFrames){
            allURLs.add(iFrame.getAttribute("src"));
        }
        for(String url : allURLs){
            if(!StringUtil.isBlank(url)){
                if(url.contains("toasttab.com")) {
                    toastURLs.add(url);
                }
//                }else{
//                    restaurant.getOtherURLSets().add(url);
//                }
            }
        }


        return toastURLs;
    }
}
