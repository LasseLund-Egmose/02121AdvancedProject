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
import java.io.Serializable;
import java.util.HashMap;

public class CheckerPiece implements Serializable {

    private static final long serialVersionUID = 4460854533891541153L;

    protected boolean canHighlight = true; // Can this piece be highlighted?
    protected transient Cylinder cylinder = null; // Cylinder shape
    protected transient StackPane cylinderContainer = new StackPane(); // Cylinder container
    protected boolean isActive = false; // Is this piece added to board?
    protected boolean isKing = false; // Is this piece a king?
    protected transient Cylinder kingCylinder = null; // King cylinder shape
    protected transient PhongMaterial material = null; // Cylinder texture
    protected Field parent = null; // Parent field containing cylinderContainer
    protected double size; // Size of one field
    protected Team team; // Team of this piece

    // Make sure piece is either highlighted or not
    protected void setHighlight(boolean shouldHighlight, Color color) {
        if(!this.canHighlight) {
            return;
        }

        // Set piece color to the specified color
        if (shouldHighlight) {
            if(this.kingCylinder != null) {
                this.kingCylinder.setMaterial(new PhongMaterial(color));
            }

            this.cylinder.setMaterial(new PhongMaterial(color));
            return;
        }

        // Set piece color to team color
        if(this.kingCylinder != null) {
            this.kingCylinder.setMaterial(this.getMaterial());
        }

        this.cylinder.setMaterial(this.getMaterial());
    }

    // Setup cylinder shape(s)
    protected void setupCylinder(boolean isKing) {

        // Calculate size of piece
        double radius = (this.size * 2) / 5;
        double height = radius / 1.5;

        Cylinder cylinder = new Cylinder(radius, height);

        // Set color of piece to team color and rotate it correctly
        cylinder.setMaterial(this.getMaterial());
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90);

        if(isKing) {
            // Move the king cylinder on top of regular piece cylinder
            cylinder.setTranslateZ(height + height / 2 + 2);
            this.kingCylinder = cylinder;
        } else {
            // Move cylinder on top of board
            cylinder.setTranslateZ(height / 2);
            this.cylinder = cylinder;
        }
    }

    // Setup texture material
    protected void setupMaterial() {
        this.material = new PhongMaterial();
        this.material.setDiffuseMap(
            new Image(getClass().getResourceAsStream(
                team == Team.BLACK ? "/assets/piece_black.jpg" : "/assets/piece_white.jpg"
            ))
        );
    }

    // Short constructor
    public CheckerPiece(double size, Team team) {
        this.size = size;
        this.team = team;

        this.setupPiece();
    }

    // Long constructor
    public CheckerPiece(boolean isActive, boolean isKing, double size, Team team, boolean canHighlight) {
        this.isActive = isActive;
        this.isKing = isKing;
        this.size = size;
        this.team = team;
        this.canHighlight = canHighlight;
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

    /*
     *  Getters
     */
    public boolean getIsKing() { return this.isKing; }

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

    public Team getTeam() {
        return this.team;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean getCanHighlight() {
        return canHighlight;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    /*
     * Setters
     */

    // Set whether or not this piece can be highlighted (if not, it should be grayed out)
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

    // Set or remove shape parent (used when saving/loaded to remove JavaFx refs)
    public void setCylinderContainer(StackPane cylinderContainer) {
        this.cylinderContainer = cylinderContainer;
    }

    // Set highlighted for player
    public void setHighlight(boolean shouldHighlight) {
        this.setHighlight(shouldHighlight, Color.web("green"));
    }

    // Set highlighted for CPU
    public void setHighlightCPU(boolean shouldHighlight) {
        this.setHighlight(shouldHighlight, Color.web("blue"));
    }

    // Set whether or not this is a king
    public void setKing() {
        this.isKing = true;

        this.setupPiece();
    }

    // Setup click event on piece
    public void setupEvent(AbstractController controller) {
        this.cylinderContainer.setOnMouseClicked(e -> controller.setSelectedPiece(this));
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
}