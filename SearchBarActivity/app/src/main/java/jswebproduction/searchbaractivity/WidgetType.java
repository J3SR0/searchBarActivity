package jswebproduction.searchbaractivity;

/**
 * Created by Jsweb Stage 2 on 07/04/2017.
 */

public enum WidgetType {
    NONE(0), SEARCH_BAR(1), WEB_VIEW(2);

    private int value;
    private WidgetType(int value) { this.value = value; }
    public int getValue() { return value; }
}
