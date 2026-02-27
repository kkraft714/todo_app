package server.note;

import server.categories.CategoryTag;
import server.element.Price;

// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Product extends Note {
    private Entity owner;
    private CategoryTag type;
    private Price productPrice;

    // ToDo: Include ProductType (extends CategoryTag)
    public Product(String name, String description, Entity owner) {
        super(name, description);
        this.owner = owner;
    }

    public Product(String name) { super(name, null); }

    public Entity getOwner() { return owner; }
    public Product setOwner(Entity newOwner) { owner = newOwner; return this; }
    public CategoryTag getType() { return type; }
    public Product setType(CategoryTag type) { this.type = type; return this; }
    public Price getPrice() { return productPrice; }
    public Product setPrice(Price productPrice) { this.productPrice = productPrice; return this; }
    public Product setPrice(double price) { this.productPrice = new Price(price); return this; }

/*
    // ToDo: Define toString()
    @Override
    public String toString() { }
*/
}
