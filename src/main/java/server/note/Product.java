package server.note;

import server.element.Price;

// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Product extends Note<Product> {
    private final Entity owner;
    private Price productPrice;

    public Product(String name, String description, Entity owner) {
        super(name, description);
        this.owner = owner;
    }

    public Entity getOwner() { return owner; }
    public Price getPrice() { return productPrice; }
    public void setPrice(Price productPrice) { this.productPrice = productPrice; }
    public void setPrice(double price) { this.productPrice = new Price(price); }

/*
    // ToDo: Define toString()
    @Override
    public String toString() { }
*/
}
