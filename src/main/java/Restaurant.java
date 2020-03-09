import java.util.HashSet;
import java.util.Set;

public class Restaurant {
    String restaurantName;
    String restaurantURL;
    Set<String> otherURLSets;
    Set<String> toastURLSets;
    Set<String> featureSet;
    String rewardsLink;
    String rewardsSignUpLink;
    String giftCardsLink;
    String findCardsLink;
    String onlineOrderLink;

    public String getRewardsLink() {
        return rewardsLink;
    }

    public void setRewardsLink(String rewardsLink) {
        this.rewardsLink = rewardsLink;
    }

    public String getRewardsSignUpLink() {
        return rewardsSignUpLink;
    }

    public void setRewardsSignUpLink(String rewardsSignUpLink) {
        this.rewardsSignUpLink = rewardsSignUpLink;
    }

    public String getGiftCardsLink() {
        return giftCardsLink;
    }

    public void setGiftCardsLink(String giftCardsLink) {
        this.giftCardsLink = giftCardsLink;
    }

    public String getFindCardsLink() {
        return findCardsLink;
    }

    public void setFindCardsLink(String findCardsLink) {
        this.findCardsLink = findCardsLink;
    }

    public String getOnlineOrderLink() {
        return onlineOrderLink;
    }

    public void setOnlineOrderLink(String onlineOrderLink) {
        this.onlineOrderLink = onlineOrderLink;
    }

    public Set<String> getFeatureSet() {
        return featureSet;
    }

    public void setFeatureSet(Set<String> featureSet) {
        this.featureSet = featureSet;
    }

    public int hashCode(){
        return restaurantURL.hashCode();
    }

    public boolean equals(Object obj) {
        Restaurant restaurant2 = (Restaurant) obj;
        return (this.restaurantURL.equalsIgnoreCase(((Restaurant) obj).getRestaurantURL()));
    }
    public Restaurant() {
        otherURLSets = new HashSet<>();
        toastURLSets = new HashSet<>();
        featureSet = new HashSet<>();
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
