package server.element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PublicKey;

/**
 * Represents an item price.
 */
// ToDo: Make this an internal class in Product?
public class Price {
    private BigDecimal price;

    // No-arg constructor required by Hibernate
    protected Price() {
        this.price = null;
    }

    public Price(BigDecimal itemPrice) {
        this.price = itemPrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    public Price(double itemPrice) {
        this(new BigDecimal(itemPrice));
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal newPrice) { this.price = newPrice; }

    @Override
    // ToDo: Consider I18N (i.e. other currencies)
    public String toString() { return super.toString() + "\n$" + price; }
}
