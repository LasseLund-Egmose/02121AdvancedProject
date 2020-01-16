package Util;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StyleCollection {

    protected ArrayList<StyleProp> props = new ArrayList<>(); // A list of styles to be applied to given Node

    /*
     * Pre-made style collections
     */

    public static void buttonStyle(Node applyTo) {
        StyleCollection.build(
                applyTo,
                StyleProp.BACKGROUND_IMAGE("url(/assets/dark_wood.jpg)"),
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.CURSOR("hand"),
                StyleProp.FONT_SIZE("30px"),
                StyleProp.FONT_WEIGHT("bold"),
                StyleProp.TEXT_FILL("#DAA520")
        );
    }

    public static void buttonStyleLight(Node applyTo) {
        StyleCollection.build(
                applyTo,
                StyleProp.BACKGROUND_IMAGE("url(/assets/grid.png)"),
                StyleProp.BORDER_COLOR("#7d6425"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.CURSOR("hand"),
                StyleProp.FONT_SIZE("18px"),
                StyleProp.FONT_WEIGHT("bold"),
                StyleProp.TEXT_FILL("#7d6425")
        );
    }

    public static void labelStyle(Node applyTo) {
        StyleCollection.modifyProps(
                applyTo,
                StyleProp.BACKGROUND_IMAGE("url(/assets/dark_wood.jpg)"),
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.FONT_SIZE("15px"),
                StyleProp.FONT_WEIGHT("bold"),
                StyleProp.PADDING("5 5 5 5"),
                StyleProp.TEXT_FILL("#DAA520")
        );
    }

    public static void mainMenuContainer(Node applyTo) {
        StyleCollection.build(
                applyTo,
                StyleProp.BORDER_IMAGE_SOURCE("url(/assets/dark_wood.jpg)"),
                StyleProp.BORDER_IMAGE_SLICE("10"),
                StyleProp.BORDER_IMAGE_WIDTH("10"),
                StyleProp.PADDING("10 10 10 10")
        );
    }

    /*
     * Static helper methods
     */

    // Build a new set of styles
    public static void build(Node applyTo, StyleProp... props) {
        StyleCollection instance = new StyleCollection(props);
        instance.applyTo(applyTo);
    }

    // Modify an existing set of styles
    public static void modifyProps(Node applyTo, StyleProp... props) {
        List<StyleProp> styleProps = new ArrayList<StyleProp>();
        Collections.addAll(styleProps, props);

        StringBuilder newStyle = new StringBuilder();
        String[] styles = applyTo.getStyle().split(";");
        for(String style : styles) {
            boolean modified = false;

            for(StyleProp prop : styleProps) {
                String fxProp = "-fx-" + prop.getProperty();

                if(!style.startsWith(fxProp)) {
                    continue;
                }

                if(prop.getValue() != null) {
                    newStyle.append(prop.toString());
                }

                // Prop is found and set
                styleProps.remove(prop);

                modified = true;
                break;
            }

            if(!modified) {
                newStyle.append(style).append(";");
            }
        }

        // Add remaining props
        for(StyleProp prop : props) {
            newStyle.append(prop.toString());
        }

        applyTo.setStyle(newStyle.toString());
    }

    public StyleCollection(StyleProp... props) {
        this.props.addAll(Arrays.asList(props));
    }

    // Apply style-String to Node
    public void applyTo(Node applyTo) {
        applyTo.setStyle(this.toString());
    }

    // Return combined String of all StyleProps
    public String toString() {
        // Join without any "glue" chars
        Collector<CharSequence, ?, String> spaceCollector = Collectors.joining("");

        // Merge all string values of props to one string
        return this.props.stream()
            .map(StyleProp::toString)
            .collect(spaceCollector);
    }

}
