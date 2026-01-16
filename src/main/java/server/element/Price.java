package server.element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PublicKey;

/**
 * Represents an item price.
 */
public class Price extends NoteElement<Price> {
    private BigDecimal price;

    // No-arg constructor required by Hibernate
    protected Price() {
        super(null, null);
        this.price = null;
    }

    public Price(String itemName, String description, BigDecimal itemPrice) {
        super(itemName, description);
        this.price = itemPrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    public Price(String itemName, String description, double itemPrice) {
        this(itemName, description, new BigDecimal(itemPrice));
    }

    public Price(Double itemPrice) {
        this("Price:", null, new BigDecimal(itemPrice));
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal newPrice) { this.price = newPrice; }

    @Override
    // ToDo: Consider I18N (i.e. other currencies)
    public String toString() { return super.toString() + "\n$" + price; }
}
