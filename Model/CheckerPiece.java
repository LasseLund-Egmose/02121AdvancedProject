package Model;

import Controller.AbstractController;
import Enum.Team;

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

    protected Cylinder cylinder = null; // Cylinder shape
    protected StackPane cylinderContainer = null; // Cylinder container
    protected boolean isActive = false; // Is this piece added to board?
    protected boolean isKing = false; // Is this piece a king?
    protected Cylinder kingCylinder = null; // King cylinder shape
    protected PhongMaterial material = null; // Cylinder texture
    protected Point position = null; // Current position of piece
    protected double size; // Size of one field
    protected Team team; // Team of this piece

    protected void setupCylinder(boolean isKing) {
        double radius = (this.size * 2) / 5;
        double height = radius / 1.5;

        Cylinder cylinder = new Cylinder(radius, height);

        cylinder.setMaterial(this.getMaterial());
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90);

        if(isKing) {
            cylinder.setTranslateZ(height + height / 2 + 2);
            this.kingCylinder = cylinder;
        } else {
            cylinder.setTranslateZ(height / 2);
            this.cylinder = cylinder;
        }
    }

    protected void setupMaterial() {
        this.material = new PhongMaterial();
        this.material.setDiffuseMap(
            new Image(getClass().getResourceAsStream(
                team == Team.BLACK ? "/assets/piece_black.jpg" : "/assets/piece_white.jpg"
            ))
        );
    }

    // Setup pane, shape and material
    protected void setupPiece() {
        if(this.cylinderContainer == null) {
            this.cylinderContainer = new StackPane();
        }

        if(this.material == null) {
            this.setupMaterial();
        }

        if(this.cylinder == null) {
            this.setupCylinder(false);
        }

        if(this.isKing && this.kingCylinder == null) {
            this.setupCylinder(true);
        }

        this.cylinderContainer.getChildren().clear();

        if(this.isKing) {
            this.cylinderContainer.getChildren().addAll(this.cylinder, this.kingCylinder);
            return;
        }

        this.cylinderContainer.getChildren().add(this.cylinder);
    }

    // Construct
    public CheckerPiece(double size, Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    // Make sure piece is either highlighted or not
    public void assertHighlight(boolean shouldHighlight) {
        if (shouldHighlight) {
            if(this.kingCylinder != null) {
                this.kingCylinder.setMaterial(new PhongMaterial(Color.LIMEGREEN));
            }

            this.cylinder.setMaterial(new PhongMaterial(Color.LIMEGREEN));
            return;
        }

        if(this.kingCylinder != null) {
            this.kingCylinder.setMaterial(this.getMaterial());
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    // Detach and afterwards attach piece to given pane (black field)
    public void attachToField(StackPane pane, Point position, HashMap<Team, Integer> activeCount) {
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
        HashMap<Team, Integer> activeCount
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
        HashMap<Team, Integer> activeCount
    ) {
        StackPane pane = fields.get(position.x).get(position.y);
        this.attachToField(pane, position, activeCount);
    }

    // Detach from current field and set activeCount accordingly
    public void detach(HashMap<Team, Integer> activeCount) {
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

    public boolean getIsKing() {
        return this.isKing;
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

    public Team getTeam() {
        return this.team;
    }

    // Setup click event on piece
    public void setupEvent(AbstractController controller) {
        this.cylinderContainer.setOnMouseClicked(e -> controller.setSelectedPiece(this));
    }

    public void setKing() {
        this.isKing = true;

        this.setupPiece();
    }

    public void setNormal() {
        this.isKing = false;

        this.setupPiece();
    }
}