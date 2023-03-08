package com.example.suituppk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter <WishlistAdapter.ViewHolder> {


    private List<WishlistModel> wishlistModelList;
    private Boolean wishList;
    private int lastPosition = -1;

    public WishlistAdapter(List<WishlistModel> wishlistModelList , Boolean wishList) {
        this.wishlistModelList = wishlistModelList;
        this.wishList = wishList;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCoupens = wishlistModelList.get(position).getFreeCoupens();
        String rating = wishlistModelList.get(position).getRating();
        long totlalRatings = wishlistModelList.get(position).getTotlalRatings();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();

        holder.setData(productId , resource , title , freeCoupens , rating , totlalRatings , productPrice , cuttedPrice , paymentMethod , position , inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView coupenIcon;
        private TextView productTitle;
        private TextView freeCoupens;
        private TextView rating;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private TextView totlalRatings;
        private View pricecut;
        private ImageButton deleteBtn;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupens= itemView.findViewById(R.id.free_coupens);
            coupenIcon = itemView.findViewById(R.id.coupen_icon);
            rating  = itemView.findViewById(R.id.tv_product_rating_miniview);
            totlalRatings = itemView.findViewById(R.id.total_ratings);
            pricecut = itemView.findViewById(R.id.price_cut);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            deleteBtn = itemView.findViewById(R.id.delete_btn);

        }

        private void setData(final String productId , String resource, String title, long freeCoupenNo, String avarageRate, long totalRatinngsNo, String price, String cuttedPriceValue, boolean COD , final int index , boolean inStock){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.nooo)).into(productImage);
            productTitle.setText(title);
            if (freeCoupenNo !=0 && inStock){
                coupenIcon.setVisibility(View.VISIBLE);
                if (freeCoupenNo == 0){
                    freeCoupens.setText("free " + freeCoupenNo + " coupen");
                }else {
                    freeCoupens.setText("free " + freeCoupenNo + " coupens");
                }

            }else {
                coupenIcon.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.INVISIBLE);
            }


            LinearLayout linearLayout = (LinearLayout) rating.getParent();

            if (inStock){


                rating.setVisibility(View.VISIBLE);
                totlalRatings.setVisibility(View.VISIBLE);
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.btnblack));
                cuttedPrice.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                rating.setText(avarageRate);
                totlalRatings.setText("("+totalRatinngsNo+")" + "ratings");
                productPrice.setText("Rs."+price+"/-");
                cuttedPrice.setText("Rs."+cuttedPriceValue+"/-");

                if(COD){
                    paymentMethod.setVisibility(View.VISIBLE);
                }else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }

            }else {


                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totlalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }


            if (wishList){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
                rating.setVisibility(View.GONE);
                totlalRatings.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBqueries.removeFromWishlist(index, itemView.getContext());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext() , ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID" , productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });


        }

    }
}
