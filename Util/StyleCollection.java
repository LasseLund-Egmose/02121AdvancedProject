package Util;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StyleCollection {

    protected ArrayList<StyleProp> props = new ArrayList<>();

    public static void build(Node applyTo, StyleProp... props) {
        StyleCollection instance = new StyleCollection(props);
        instance.applyTo(applyTo);
    }

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
            System.out.println(prop.toString());
            newStyle.append(prop.toString());
        }

        applyTo.setStyle(newStyle.toString());
    }

    public StyleCollection(StyleProp... props) {
        this.props.addAll(Arrays.asList(props));
    }

    public void applyTo(Node applyTo) {
        applyTo.setStyle(this.toString());
    }

    public String toString() {
        // Join without any "glue" chars
        Collector<CharSequence, ?, String> spaceCollector = Collectors.joining("");

        // Merge all string values of props to one string
        return this.props.stream()
            .map(StyleProp::toString)
            .collect(spaceCollector);
    }

}
