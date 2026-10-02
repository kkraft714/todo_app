package server.note;

import server.categories.InternalCategory;
import server.element.Price;

// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Product extends Note {
    private Entity owner;
    private InternalCategory type;
    private Price productPrice;

    // ToDo: Include ProductType (extends InternalCategory) in constructor
    public Product(String name, String description, Entity owner) {
        super(name, description);
        this.owner = owner;
    }

    public Product(String name) { super(name, null); }

    public Entity getOwner() { return owner; }
    public Product setOwner(Entity newOwner) { owner = newOwner; return this; }
    public InternalCategory getType() { return type; }
    public Product setType(InternalCategory type) {
        this.type = type;
        this.addCategory(type.toString());
        return this;
    }
    public Price getPrice() { return productPrice; }
    public Product setPrice(Price productPrice) { this.productPrice = productPrice; return this; }
    public Product setPrice(double price) { this.productPrice = new Price(price); return this; }

/*
    // ToDo: Define toString()
    @Override
    public String toString() { }
*/
}
