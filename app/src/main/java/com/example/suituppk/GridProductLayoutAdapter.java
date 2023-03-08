package com.example.suituppk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GridProductLayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @Override
    public int getCount()
    {
        return horizontalProductScrollModelList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontol_scroll_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent  = new Intent(parent.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID" , horizontalProductScrollModelList.get(position).getProductID());
                    parent.getContext().startActivity(productDetailsIntent);
                }
            });

            ImageView productimage = view.findViewById(R.id.hs_product_image);
            TextView producttitle = view.findViewById(R.id.hs_product_titile);
            TextView productdescription = view.findViewById(R.id.hs_product_discription);
            TextView productprice = view.findViewById(R.id.hs_product_price);


            Glide.with(parent.getContext()).load(horizontalProductScrollModelList.get(position).getProductimage()).apply(new RequestOptions().placeholder(R.drawable.nooo)).into(productimage);
            producttitle.setText(horizontalProductScrollModelList.get(position).getProductTitle());
            productdescription.setText(horizontalProductScrollModelList.get(position).getProductDescription());
            productprice.setText("Rs."+horizontalProductScrollModelList.get(position).getProductPrice()+"/-");

        }else {

            view = convertView;
        }

        return view;
    }
}
