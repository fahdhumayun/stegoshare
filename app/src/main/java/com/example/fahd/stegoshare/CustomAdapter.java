// By Fahd Humayun
// CustomAdapter (Class) - Used for the listView elements to have custom adapter.

package com.example.fahd.stegoshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fahd on 12/16/2017.
 */

public class CustomAdapter extends ArrayAdapter<String>  {

    private ArrayList<String> arrayList = new ArrayList<>(50);

    public CustomAdapter(@NonNull Context context, ArrayList<String> items) {
        super(context, R.layout.row_layout, items);

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
