package jswebproduction.searchbaractivity;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by Jsweb Stage 2 on 07/04/2017.
 */

public abstract class Holder {
    protected View view = null;

    public abstract void setViewElements(View convertView);
    public abstract void updateViewElements(Object data);
}
