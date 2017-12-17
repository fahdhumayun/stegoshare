// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Fahd on 12/16/2017.
 */

public class CustomAdapter extends ArrayAdapter<String>  {

    private ArrayList<String> arrayList = new ArrayList<>(50);

    public CustomAdapter(@NonNull Context context, ArrayList<String> items) {
        super(context, R.layout.row_layout, items);

        Log.v("Test", "Size of items " + items.size());

        for(int i = 0; i < items.size(); i++){
            arrayList.add(items.get(i));
        }

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View cv = li.inflate(R.layout.row_layout, parent, false);

        TextView wordsTextView = (TextView) cv.findViewById(R.id.id_ListViewWordItem);
        wordsTextView.setText(arrayList.get(position));

        return cv;
    }
}
