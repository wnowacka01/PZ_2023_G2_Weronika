package database_classes;

public class PositionsTable {
    private int idPosition;
    private String positionName;
    private String description;

    public int getIdPosition() {
        return idPosition;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getDescription() {
        return description;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
