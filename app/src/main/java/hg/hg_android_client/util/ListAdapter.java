package hg.hg_android_client.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class ListAdapter<T> extends ArrayAdapter<T> {

    private int layoutId;

    public ListAdapter(@NonNull Context context, List<T> elements, int layoutId) {
        super(context, 0, elements);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T current = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutId, parent, false);
        }
        handleItem(convertView, current, position);
        return convertView;
    }

    protected abstract void handleItem(View view, T current, int position);

}
