import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CheckerPiece {

    protected Cylinder cylinder; // Cylinder shape
    protected StackPane cylinderContainer; // Cylinder container
    protected boolean isActive = false; // Is this piece added to board?
    protected PhongMaterial material; // Cylinder texture
    protected Point position; // Current position of piece
    protected double size; // Size of one field
    protected Controller.Team team; // Team of this piece

    // Setup pane, shape and material
    protected void setupPiece() {
        double radius = (this.size * 2) / 5;

        this.material = new PhongMaterial();
        this.material.setDiffuseMap(
            new Image(getClass().getResourceAsStream(
                team == Controller.Team.BLACK ? "/assets/piece_black.jpg" : "/assets/piece_white.jpg"
            ))
        );

        this.cylinder = new Cylinder(radius, radius / 1.5);
        this.cylinder.setMaterial(this.getMaterial());
        this.cylinder.setRotationAxis(Rotate.X_AXIS);
        this.cylinder.setRotate(90);
        this.cylinder.setTranslateZ(4);

        this.cylinderContainer = new StackPane();
        this.cylinderContainer.getChildren().add(this.cylinder);
    }

    // Construct
    public CheckerPiece(double size, Controller.Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    // Make sure piece is either highlighted or not
    public void assertHighlight(boolean shouldHighlight) {
        if (shouldHighlight) {
            this.cylinder.setMaterial(new PhongMaterial(Color.LIMEGREEN));
            return;
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    // Detach and afterwards attach piece to given pane (black field)
    public void attachToField(StackPane pane, Point position, HashMap<Controller.Team, Integer> activeCount) {
        // Detach
        this.detach(activeCount);

        // Set new position
        this.position = position;

        // Add to pane
        pane.getChildren().add(this.getPane());

        // Justify activeCount if applicable
        if (!this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt++;
            activeCount.put(this.team, activeCountInt);
        }

        // Set active
        this.isActive = true;
    }

    // Find position of given pane (black field) and run attachToField
    public void attachToFieldByPane(
        HashMap<Integer, HashMap<Integer, StackPane>> fields,
        StackPane pane,
        HashMap<Controller.Team, Integer> activeCount
    ) {
        // Reverse lookup position by pane in fields HashMap
        for (Map.Entry<Integer, HashMap<Integer, StackPane>> hmap : fields.entrySet()) {
            int x = hmap.getKey();

            for (Map.Entry<Integer, StackPane> e : hmap.getValue().entrySet()) {
                if (e.getValue() != pane) {
                    continue;
                }

                Point p = new Point(x, e.getKey());
                this.attachToField(pane, p, activeCount);

                return;
            }
        }
    }

    // Find pane (black field) by position and run attachToField
    public void attachToFieldByPosition(
        HashMap<Integer, HashMap<Integer, StackPane>> fields,
        Point position,
        HashMap<Controller.Team, Integer> activeCount
    ) {
        StackPane pane = fields.get(position.x).get(position.y);
        this.attachToField(pane, position, activeCount);
    }

    // Detach from current field and set activeCount accordingly
    public void detach(HashMap<Controller.Team, Integer> activeCount) {
        Pane p = this.getPane();
        Object parent = p.getParent();

        if (parent instanceof StackPane) {
            StackPane parentPane = (StackPane) parent;
            parentPane.getChildren().remove(p);
        }

        if (this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt--;
            activeCount.put(this.team, activeCountInt);
        }

        this.isActive = false;
    }

    public PhongMaterial getMaterial() {
        return this.material;
    }

    public Pane getPane() {
        return this.cylinderContainer;
    }

    public Point getPosition() {
        return this.position;
    }

    // Setup click event on piece
    public void setupEvent(Controller controller) {
        this.cylinderContainer.setOnMouseClicked(e -> controller.setSelectedPiece(this));
    }
}