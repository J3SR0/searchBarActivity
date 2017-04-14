package jswebproduction.searchbaractivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Arrays;

/**
 * Created by Jsweb Stage 2 on 12/04/2017.
 */

public class ContactSearchWidget extends SearchWidget {
    private static final String     TAG             = "CONTACT_SEARCH_WIDGET";
    private static final int        id              = 1337;
    public ContactSearchCursorAdapter adapter       = null;
    String                          currentFilter   = null;
    LoaderManager                   loaderManager   = null;
    Holder                          holder          = null;
    private int                     item_count      = 0;
    private boolean                 isDisplayable   = false;
    ColorGenerator                  generator       = ColorGenerator.MATERIAL;


    private class ContactSearchHolder extends Holder {
        public ListView         lvContacts;
        public View             view;
        public int              titleHeight = -1;

        public void setViewElements(View convertView) {
            this.lvContacts = (ListView) convertView.findViewById(R.id.lvContacts);
            if (titleHeight == -1) {
                this.titleHeight = convertView.findViewById(R.id.widget_contacts_title).getHeight();
            }
            this.view       = convertView;
            this.lvContacts.setAdapter(adapter);
        }

        public void updateViewElements(Object data) {}
    }

    public class ContactSearchCursorAdapter extends CursorAdapter {
        private static final String TAG = "CURSOR_ADAPTER";
        private LayoutInflater cursorInflater;

        public ContactSearchCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            this.cursorInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            if (cursor.getCount() == 0) return ;
            ImageButton callButton  = (ImageButton) view.findViewById(R.id.call_contact_button);
            ImageButton msgButton   = (ImageButton) view.findViewById(R.id.msg_contact_button);
            TextView    name        = (TextView) view.findViewById(R.id.tvName);
            ImageView   picture     = (ImageView) view.findViewById(R.id.ivImage);
            ImageView   image       = (ImageView) view.findViewById(R.id.contact_badge);

            final int position = cursor.getPosition();

            cursor.moveToPosition(position);

            final String textName = cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));

            if (cursor.getString(cursor.getColumnIndex("PHOTO_THUMB_URI")) != null && cursor.getString(cursor.getColumnIndex("PHOTO_URI")) != null) {
                picture.setVisibility(View.VISIBLE);
                Log.d(TAG, textName + " has a valid image");
                picture.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex("PHOTO_THUMB_URI"))));
                image.setVisibility(View.INVISIBLE);
            } else {
                image.setVisibility(View.VISIBLE);
                Log.d(TAG, textName + " has an invalid image");
                TextDrawable drawable = TextDrawable.builder().buildRound(textName.substring(0, 1), generator.getColor(textName.substring(0, 1)));
                //  BorderColor = ResourcesCompat.getColor(ContactSearchWidget.super.context.getResources(), R.color.widgetContactsDividerColor, null)

                image.setImageDrawable(drawable);

                picture.setVisibility(View.INVISIBLE);
            }
            name.setText(textName);
            final String contactId = cursor.getString(cursor.getColumnIndex("_ID"));

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId));
                    ContactSearchWidget.super.context.startActivity(intent);
                }
            });

            msgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "msg Button");
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId));
                    ContactSearchWidget.super.context.startActivity(intent);
                }
            });
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return cursorInflater.inflate(R.layout.item_contact, parent, false);
        }
    }

    public ContactSearchWidget () {
        super.layoutId      = R.layout.contacts_fragment_layout;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.loaderManager = ((AppCompatActivity)super.context).getSupportLoaderManager();
        updateQuery("");
        setupCursorAdapter();
    }

    public void updateQuery(String query) {
        this.currentFilter = query;
    }

    public int  getLoaderId() {
        return id;
    }

    public Loader<?> getLoader() {
        String[] projectionFields = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.TIMES_CONTACTED
        };
        if (currentFilter.equals(""))
            return new CursorLoader(super.context, ContactsContract.Contacts.CONTENT_URI, projectionFields, null, null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC" + " LIMIT 3");
        return new CursorLoader(super.context, ContactsContract.Contacts.CONTENT_URI, projectionFields, ContactsContract.Contacts.DISPLAY_NAME + " LIKE \'%" + currentFilter + "%\'", null, ContactsContract.Contacts.DISPLAY_NAME + " LIMIT 3");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_fragment_layout, container, false);
    }

    public Holder getHolder() {
        this.holder = new ContactSearchHolder();
        return this.holder;
    }

    public CursorAdapter getAdapter() {
        return this.adapter;
    }

    private void setupCursorAdapter() {
        this.adapter = new ContactSearchCursorAdapter(super.context, null, 0);
    }

    public void redraw() {
        if (this.holder != null && ((ContactSearchHolder)this.holder).view != null) {
            RelativeLayout v = (RelativeLayout)((ContactSearchHolder)this.holder).view.findViewById(R.id.widget_contacts_container);
            if (v == null)
                return ;
            else {
                DisplayMetrics  metrics = new DisplayMetrics();
                ((AppCompatActivity)super.context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                float logicalDensity = metrics.density;
                int dividerHeight = this.item_count > 0 ? 1 * (this.item_count - 1) : 0;
                v.getLayoutParams().height = (int) ((45 * this.item_count + 12 + dividerHeight) * logicalDensity);
                v.requestLayout();
            }
        } else {
            return;
        }
    }

    public void updateCount(int count) {
        this.item_count = count;
        this.isDisplayable = this.item_count == 0 ? false : true;
    }

    public boolean isDisplayable() {
        return this.isDisplayable;
    }
}
