import java.util.HashSet;
import java.util.Set;

public class Restaurant {
    String restaurantName;
    String restaurantURL;
    Set<String> otherURLSets;
    Set<String> toastURLSets;

    public int hashCode(){
        return restaurantURL.hashCode();
    }

    public boolean equals(Object obj) {
        Restaurant restaurant2 = (Restaurant) obj;
        return (this.restaurantURL.equals(((Restaurant) obj).getRestaurantURL()));
    }
    public Restaurant() {
        otherURLSets = new HashSet<>();
        toastURLSets = new HashSet<>();
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantURL() {
        return restaurantURL;
    }

    public void setRestaurantURL(String restaurantURL) {
        this.restaurantURL = restaurantURL;
    }

    public Set<String> getOtherURLSets() {
        return otherURLSets;
    }

    public void setOtherURLSets(Set<String> otherURLSets) {
        this.otherURLSets = otherURLSets;
    }

    public Set<String> getToastURLSets() {
        return toastURLSets;
    }

    public void setToastURLSets(Set<String> toastURLSets) {
        this.toastURLSets = toastURLSets;
    }
}
