package nordnetservice.critter.critterrule;

import java.util.Objects;

public class SellRuleArgs {
    private final double diffFromBought;
    private final double diffFromWatermark;
    private final double spot;
    private final double optionPrice;

    public SellRuleArgs(double diffFromBought,
                        double diffFromWatermark,
                        double spot,
                        double optionPrice) {
        this.diffFromBought = roundToTwoDecimals(diffFromBought);
        this.diffFromWatermark = roundToTwoDecimals(diffFromWatermark);
        this.spot = roundToTwoDecimals(spot);
        this.optionPrice = roundToTwoDecimals(optionPrice);
    }

    public double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public double getDiffFromWatermark() {
        return diffFromWatermark;
    }

    public double getDiffFromBought() {
        return diffFromBought;
    }

    public double getSpot() {
        return spot;
    }

    public double getOptionPrice() {
        return optionPrice;
    }

    @Override
    public String toString(){
        return String.format("[SellRuleArgs] DFB: %.2f, DFW: %.2f, option price: %.2f, spot: %.2f",
                diffFromBought, diffFromWatermark, optionPrice, spot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof SellRuleArgs)) {
            return false;
        }
        SellRuleArgs oa = (SellRuleArgs)o;
        return Objects.equals(diffFromBought, oa.getDiffFromBought()) &&
                Objects.equals(diffFromWatermark, oa.getDiffFromWatermark()) &&
                Objects.equals(spot, oa.getSpot()) &&
                Objects.equals(optionPrice, oa.getOptionPrice());
    }

    @Override
    public final int hashCode() {
        int result = 17;
        result = 31 * result + Double.hashCode(diffFromBought);
        result = 31 * result + Double.hashCode(diffFromWatermark);
        result = 31 * result + Double.hashCode(spot);
        result = 31 * result + Double.hashCode(optionPrice);
        return result;
    }
}
