package server;

import javax.persistence.MappedSuperclass;
import java.util.ArrayList;


/**
** Generic class for implementing a tree structure.
 */
// ToDo: Delete this class (not currently used anywhere)?
@MappedSuperclass
public class Node<T> {
  String name;
  private Node<T> parentItem;
  private int level;
  private ArrayList<Node<T>> childItems = new ArrayList<>();

  /**
   * Constructor for non-root node.
   */
  public Node(String name, Node<T> parent) {
    this.name = name;
    childItems = new ArrayList<Node<T>>();
    setParentItem(parent);
  }

  /**
   ** Constructor for root node.
   */
  public Node(String name) {
    this(name, null);
  }

  public int getLevel() { return level; }
  public String getName() { return name; }
  public void setName(String newName) { this.name = newName; }

  public Node<T> getParentItem() { return parentItem; }
  public Node<T> getChildItem(int index) { return childItems.get(index); }
  // getChildItem(String name)?
  public ArrayList<Node<T>> getChildItems() {
    return childItems;
  }

  public void addChildItem(Node<T> child) {
    childItems.add(child);
  }

  public void move(Node<T> newParent) {
    setParentItem(newParent);
  }

  // The level is set here (outside of the constructor) to support changing the parent node
  public void setParentItem(Node<T> newParent) {
    parentItem = newParent;
    if (parentItem != null) {
      parentItem.addChildItem(this);
      level = parentItem.getLevel() + 1;
    }
    else {
      level = 1;
    }
  }
}
