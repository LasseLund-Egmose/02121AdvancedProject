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

public class CheckerPiece {

    protected boolean canHighlight = true; // Can this piece be highlighted?
    protected Cylinder cylinder = null; // Cylinder shape
    protected StackPane cylinderContainer = new StackPane(); // Cylinder container
    protected boolean isActive = false; // Is this piece added to board?
    protected boolean isKing = false; // Is this piece a king?
    protected Cylinder kingCylinder = null; // King cylinder shape
    protected PhongMaterial material = null; // Cylinder texture
    protected Field parent = null; // Parent field containing cylinderContainer
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
    public void setupPiece() {
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

    public CheckerPiece(boolean isActive, boolean isKing, double size, Team team) {
        this.isActive = isActive;
        this.isKing = isKing;
        this.size = size;
        this.team = team;
    }

    // Make sure piece is either highlighted or not
    public void assertHighlight(boolean shouldHighlight) {
        if(!this.canHighlight) {
            return;
        }

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
    public void attachToField(Field field, HashMap<Team, Integer> activeCount) {
        // Detach
        this.detach(activeCount);

        // Add to field
        field.getChildren().add(this.getPane());

        this.parent = field;
        field.setAttachedPiece(this);

        // Justify activeCount if applicable
        if (!this.isActive) {
            int activeCountInt = activeCount.get(this.team);
            activeCountInt++;
            activeCount.put(this.team, activeCountInt);
        }

        // Set active
        this.isActive = true;
    }

    // Detach from current field and set activeCount accordingly
    public void detach(HashMap<Team, Integer> activeCount) {
        if (this.parent != null) {
            this.parent.getChildren().clear();
            this.parent.setAttachedPiece(null);
            this.parent = null;
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

    public Field getParent() {
        return this.parent;
    }

    public Point getPosition() {
        return this.getParent().getPosition();
    }

    public double getSize() {
        return size;
    }

    public Team getTeam() {
        return this.team;
    }

    public boolean isActive() {
        return this.isActive;
    }

    // Setup click event on piece
    public void setupEvent(AbstractController controller) {
        this.cylinderContainer.setOnMouseClicked(e -> controller.setSelectedPiece(this));
    }

    public void setCanHighlight(boolean canHighlight) {
        this.canHighlight = canHighlight;

        if (!canHighlight) {
            if(this.kingCylinder != null) {
                this.kingCylinder.setMaterial(new PhongMaterial(Color.GRAY));
            }

            this.cylinder.setMaterial(new PhongMaterial(Color.GRAY));
            return;
        }

        if(this.kingCylinder != null) {
            this.kingCylinder.setMaterial(this.getMaterial());
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    public void setKing() {
        this.isKing = true;

        this.setupPiece();
    }

    public void setCylinderContainer(StackPane cylinderContainer) {
        this.cylinderContainer = cylinderContainer;
    }

    public void setParent(Field parent) {
        this.parent = parent;
    }
}