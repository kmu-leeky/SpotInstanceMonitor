package kr.ac.kookmin.cs.cloud.spotinstance.model;

public class SpotInstancePrice {
    private int price;
    private String availabilityZone;

    public SpotInstancePrice(String availabilityZone, int price) {
        this.availabilityZone = availabilityZone;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }


    @Override
    public int hashCode() {
        return availabilityZone.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpotInstancePrice)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        SpotInstancePrice rhs = (SpotInstancePrice) obj;
        return (rhs.getAvailabilityZone() == this.availabilityZone);
    }
}
