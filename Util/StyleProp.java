package Util;

public class StyleProp {

    public static StyleProp BACKGROUND_COLOR(String value) {
        return new StyleProp("background-color", value);
    }

    public static StyleProp BACKGROUND_IMAGE(String value) {
        return new StyleProp("background-image", value);
    }

    public static StyleProp BORDER_COLOR(String value) {
        return new StyleProp("border-color", value);
    }

    public static StyleProp BORDER_IMAGE_SLICE(String value) {
        return new StyleProp("border-image-slice", value);
    }

    public static StyleProp BORDER_IMAGE_SOURCE(String value) {
        return new StyleProp("border-image-source", value);
    }

    public static StyleProp BORDER_IMAGE_WIDTH(String value) {
        return new StyleProp("border-image-width", value);
    }

    public static StyleProp BORDER_WIDTH(String value) {
        return new StyleProp("border-width", value);
    }

    public static StyleProp CURSOR(String value) {
        return new StyleProp("cursor", value);
    }

    public static StyleProp EFFECT(String value) {
        return new StyleProp("effect", value);
    }

    public static StyleProp FILL(String value) {
        return new StyleProp("fill", value);
    }

    public static StyleProp FONT(String value) {
        return new StyleProp("font", value);
    }

    public static StyleProp FONT_SIZE(String value) {
        return new StyleProp("font-size", value);
    }

    public static StyleProp FONT_WEIGHT(String value) {
        return new StyleProp("font-weight", value);
    }

    public static StyleProp OPACITY(String value) {
        return new StyleProp("opacity", value);
    }

    public static StyleProp PADDING(String value) {
        return new StyleProp("padding", value);
    }

    public static StyleProp TEXT_FILL(String value) {
        return new StyleProp("text-fill", value);
    }

    protected String property;
    protected String value;

    public StyleProp(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return this.property;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "-fx-" + this.property + ":" + this.value + ";";
    }
}
