import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jsoup.internal.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MyRunnable implements Runnable {
    static int PAGE_LOAD_TIME_OUT =  20;
    String chromeDriverPath = "chromedriver";
    WebDriver driver;
    List<Restaurant> restaurantList;

    public MyRunnable(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        options.addArguments("--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIME_OUT, TimeUnit.SECONDS);
    }


    @Override
    public void run() {
        for(Restaurant restaurant : restaurantList){
            System.out.println("---------------Thread +"+Thread.currentThread().getName()+"------------------------------------------------------------------------");
            System.out.println("Processing restaurant: " + restaurant.getRestaurantName());
            try {
                runBySelenium(restaurant);
            }catch (Exception e){
                System.out.println("Failed to fetch restaurant +"+ restaurant.getRestaurantName() +" " + restaurant.getRestaurantURL());
                System.out.println(e.getMessage());
                Application.failedList.add(restaurant);
                continue;
            }
            if (restaurant.getToastURLSets().size() == 0) {
                System.out.println(restaurant.getRestaurantName() + " has no ToastTab URL");
            } else {
                System.out.println(restaurant.getRestaurantName() + " has many: " + restaurant.getToastURLSets().size()
                        + " ToastTab URL");
            }
        }
    }
    private  void runBySelenium(Restaurant restaurant) throws Exception {
        //Setup
        long endTime, startTime, timeElapsed;
        String restaurantURL = restaurant.getRestaurantURL();
        startTime = System.currentTimeMillis();

        //Perform
        try{
            driver.get(restaurantURL);
            Set<String> toastURLSetGetByFindElements = new HashSet<>();
            Set<String> toastURLSetGetByBruteForce = new HashSet<>();
            toastURLSetGetByFindElements = getURLByFindElement();
            toastURLSetGetByBruteForce = getURLByBruteForce();

            if(toastURLSetGetByBruteForce.size() != toastURLSetGetByFindElements.size()){
                System.out.println("Two lists are not equal");
            }

            toastURLSetGetByBruteForce.addAll(toastURLSetGetByFindElements);
            restaurant.setToastURLSets(toastURLSetGetByBruteForce);
            endTime = System.currentTimeMillis();
            timeElapsed = endTime - startTime;
            System.out.println("Execution time in milliseconds: " + timeElapsed);

            //Persist
            Application.persist(restaurant);
            System.out.println("Unique URLs: "+ toastURLSetGetByFindElements.size());
        }catch (Exception e){
            if (e.getClass().getName().contains("TimeoutException")) {
                System.out.println("Timeout for " + restaurant.getRestaurantName());
            } else {
                System.out.println("New exception: " + e.getMessage());
            }
        }

    }
    private  Set<String> getURLByFindElement(){
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

    private  Set<String> getURLByBruteForce() throws Exception{
        Set<String> urlSetGetByBruteForce = new HashSet<>();
        List<WebElement> listOfElements = driver.findElements(By.xpath("//*"));
        for(WebElement webElement : listOfElements){
            try{
                Map<String, String > attributeMap = (Map<String, String>) ((ChromeDriver) driver).executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;"
                        , webElement);
                for(Entry<String, String> entry: attributeMap.entrySet()){
                    if(entry.getValue().contains("toasttab")){
                        urlSetGetByBruteForce.add(entry.getValue());
                    }
                }
            }catch (StaleElementReferenceException e){
                continue;
            }
        }
        return urlSetGetByBruteForce;
    }

}
