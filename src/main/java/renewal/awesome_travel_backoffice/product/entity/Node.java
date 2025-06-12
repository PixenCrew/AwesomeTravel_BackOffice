package renewal.awesome_travel_backoffice.product.entity;

public class Node {
    private NodeType nodeType;
    private String city;
    private String description;
    public enum NodeType {
        FLIGHT,POINT,END
    }
}
