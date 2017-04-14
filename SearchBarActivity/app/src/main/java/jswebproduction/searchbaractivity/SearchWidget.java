package jswebproduction.searchbaractivity;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Jsweb Stage 2 on 07/04/2017.
 */

public abstract class SearchWidget extends Fragment {
    protected WidgetType    type = WidgetType.NONE;
    protected int           layoutId = -1;
    protected Context       context;

    protected SearchWidget() {}

    public WidgetType getType() { return this.type; }
    public int getLayoutId() { return this.layoutId; }
    public void setContext(Context context) { this.context = context; }
    public abstract Holder getHolder();
}
