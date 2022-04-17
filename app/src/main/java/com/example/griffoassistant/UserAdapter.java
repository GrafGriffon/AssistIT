package com.example.griffoassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<CheckUser> objects;

    UserAdapter(Context context, ArrayList<CheckUser> elements) {
        ctx = context;
        objects = elements;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        CheckUser element = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.tvDescr)).setText(element.idUser);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(element.last);
        Picasso.with(view.getContext())
                .load(R.drawable.unnamed)
                .placeholder(R.drawable.load)
                .error(R.drawable.exit)
                .into((ImageView) view.findViewById(R.id.ivImage));

        return view;
    }

    // товар по позиции
    CheckUser getProduct(int position) {
        return objects.get(position);
    }
}
