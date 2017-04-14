package jswebproduction.searchbaractivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jsweb Stage 2 on 10/04/2017.
 */

public class TemplateWidget extends SearchWidget {
    private class TemplateHolder extends Holder {
        public void setViewElements(View convertView) {}
        public void updateViewElements(Object data) {}
    }

    public TemplateWidget () {
        super.layoutId = R.layout.template_fragment_layout;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_fragment_layout, container, false);
        return view;
    }

    public Holder getHolder() {
        return new TemplateHolder();
    }
}
