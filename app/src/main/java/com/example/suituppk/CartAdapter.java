package com.example.suituppk;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView totalCartAmount;
    private boolean showDeleteBtn;


    public CartAdapter(List<CartItemModel> cartItemModelList , TextView totalCartAmount , boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.totalCartAmount = totalCartAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()){
            case 0:

                return CartItemModel.CART_ITEM;

            case 1:

                return CartItemModel.CART_Amount;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case CartItemModel.CART_ITEM:

                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout,viewGroup,false);
                return new CartItemViewholder(cartItemView);

            case CartItemModel.CART_Amount:

                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout,viewGroup,false);
                return new CartTotalAmountViewholder(cartTotalView);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {



        switch (cartItemModelList.get(position).getType()){
            case CartItemModel.CART_ITEM:
                String productId = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freecoupens = cartItemModelList.get(position).getFreeCoupens();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                Boolean COD = cartItemModelList.get(position).isCOD();

                ((CartItemViewholder)viewHolder).setItemDetails(productId,resource , title , freecoupens , productPrice , cuttedPrice , offersApplied , position , inStock , String.valueOf(productQuantity) , maxQuantity , qtyError , qtyIds , stockQty , COD);

                break;

            case CartItemModel.CART_Amount:

                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice ;
                int totalAmount;
                int savedAmount = 0;

                for (int x=0 ; x < cartItemModelList.size() ; x++){
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * quantity;
                        }else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()) * quantity;
                        }
                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }else{
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }

                    }
                }

                if (totalItemPrice > 2000){
                    deliveryPrice = "Free";
                    totalAmount = totalItemPrice;
                }else {
                    deliveryPrice = "150";
                    totalAmount = totalItemPrice + 150;
                }

                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewholder)viewHolder).setTotalAmount(totalItems , totalItemPrice , deliveryPrice , totalAmount , savedAmount);
                break;


            default:

                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {

        return cartItemModelList.size();
    }



    class CartItemViewholder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private ImageView freeCoupenIcon;
        private TextView producttitle;
        private TextView freeCoupens;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offersApplied;
        private TextView coupensApplied;
        private TextView productQuantity;
        private LinearLayout coupenRedemptionLayout;
        private TextView coupenRedemptionBody;
        private LinearLayout deleteBtn;
        private Button redeemBtn;
        private ImageView codIndicator;
        ////coupenDialog

        private TextView coupenTitle;
        private TextView coupenExpiryDate;
        private TextView coupenBody;
        private RecyclerView coupensrecyclerView;
        private LinearLayout selectedCoupen;
        private TextView discountedPrice;
        private TextView orignalPrice;
        private Button removeCoupenBtn , applyCoupenBtn;
        private LinearLayout applyORremiveBtnContainer;
        private TextView footerText;
        private String productOrignalPrice;
        ////coupenDialog


        public CartItemViewholder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            producttitle = itemView.findViewById(R.id.product_title);
            freeCoupenIcon = itemView.findViewById(R.id.free_coupen_icon);
            freeCoupens = itemView.findViewById(R.id.tv_free_coupen);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offer_applied);
            coupensApplied = itemView.findViewById(R.id.coupens_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            redeemBtn = itemView.findViewById(R.id.coupen_redemption_btn);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
            coupenRedemptionLayout = itemView.findViewById(R.id.coupen_redemption_layout);
            coupenRedemptionBody = itemView.findViewById(R.id.tv_coupen_redemption);
            codIndicator = itemView.findViewById(R.id.cod_indicator);

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setItemDetails(final String productId, String resource , String title , Long freeCoupensNo , final String productPriceText , String cuttedPriceText , Long offersAppliedNo , final int position , boolean inStock , final String Quantity , final Long maxQuantity , boolean qtyError , final List<String> qtyIds , final long stockQty , Boolean COD){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.nooo)).into(productImage);
            producttitle.setText(title);


            final Dialog checkCoupenPriceDialog = new Dialog(itemView.getContext());
            checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
            checkCoupenPriceDialog.setCancelable(false);
            checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (COD){
                codIndicator.setVisibility(View.VISIBLE);
            }else{
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock) {

                if (freeCoupensNo > 0 ) {

                    freeCoupenIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setVisibility(View.VISIBLE);

                    if (freeCoupensNo == 1) {
                        freeCoupens.setText("free " + freeCoupensNo + " coupen");
                    }else {
                        freeCoupens.setText("free " + freeCoupensNo + " coupens");
                    }
                }else {

                    freeCoupenIcon.setVisibility(View.INVISIBLE);
                    freeCoupens.setVisibility(View.INVISIBLE);
                }


                productPrice.setText("Rs." + productPriceText + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("Rs." + cuttedPriceText + "/-");
                coupenRedemptionLayout.setVisibility(View.VISIBLE);

                ////////////Coupen Dialog


                ImageView toggleRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggle_recyclerview);
                coupensrecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupens_recyclerview);
                selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);

                coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
                coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity);
                coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body);
                footerText = checkCoupenPriceDialog.findViewById(R.id.footer_text);
                applyORremiveBtnContainer = checkCoupenPriceDialog.findViewById(R.id.apply_or_remove_btns_container);
                removeCoupenBtn = checkCoupenPriceDialog.findViewById(R.id.remove_btn);
                applyCoupenBtn = checkCoupenPriceDialog.findViewById(R.id.apply_btn);

                footerText.setVisibility(View.GONE);
                applyORremiveBtnContainer.setVisibility(View.VISIBLE);

                orignalPrice = checkCoupenPriceDialog.findViewById(R.id.orignal_price);
                discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discounted_price);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                coupensrecyclerView.setLayoutManager(layoutManager);

                orignalPrice.setText(productPrice.getText());
                productOrignalPrice = productPriceText;


                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position ,DBqueries.rewardModelList, true , coupensrecyclerView , selectedCoupen , productOrignalPrice , coupenTitle , coupenExpiryDate , coupenBody , discountedPrice , cartItemModelList);
                coupensrecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();

                applyCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {

                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_backgroud));
                                    coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                    redeemBtn.setText("Coupen");
                                }
                            }
                            coupensApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() - 2)));
                            coupensApplied.setText("Coupen applied -Rs." + offerDiscountedAmt + "/-");
                            notifyItemChanged(cartItemModelList.size() -1);
                            checkCoupenPriceDialog.dismiss();
                        }
                    }
                });

                removeCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (RewardModel rewardModel : DBqueries.rewardModelList){
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupenTitle.setText("Coupen");
                        coupenExpiryDate.setText("Validity");
                        coupenBody.setText("Tap the icon on the top right corner to select your coupen.");
                        coupensApplied.setVisibility(View.INVISIBLE);
                        coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                        coupenRedemptionBody.setText("Apply your coupen here.");
                        redeemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCoupenId(null);
                        productPrice.setText("Rs." + productPriceText + "/-");
                        notifyItemChanged(cartItemModelList.size() -1);
                        checkCoupenPriceDialog.dismiss();

                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showDialogRecyclerview();

                    }
                });




                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {

                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                            coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_backgroud));
                            coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                            redeemBtn.setText("Coupen");

                            coupenBody.setText(rewardModel.getCoupenBody());
                            if (rewardModel.getType().equals("Discount")){
                                coupenTitle.setText(rewardModel.getType());
                            }else {
                                coupenTitle.setText("FLAT RS." + rewardModel.getdiscORamt()+ " OFF");
                            }

                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/YYYY");
                            coupenExpiryDate.setText("till " +simpleDateFormat.format(rewardModel.getTimestamp()));
                        }
                    }
                    discountedPrice.setText("Rs." + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    coupensApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("Rs." + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    coupensApplied.setText("Coupen applied -Rs." + offerDiscountedAmt + "/-");
                }else{
                    coupensApplied.setVisibility(View.INVISIBLE);
                    coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                    coupenRedemptionBody.setText("Apply your coupen here.");
                    redeemBtn.setText("Redeem");
                }

                //////////////////coupon Dialog

                productQuantity.setText("Qty: " + Quantity);

                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                    } else {


                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.btnblack));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.btnblack)));
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelbtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okbtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max " +String.valueOf( maxQuantity));

                        cancelbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });



                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.parseLong(quantityNo.getText().toString()) <= maxQuantity && Long.parseLong(quantityNo.getText().toString()) != 0) {

                                        if (itemView.getContext() instanceof  Home_Activity){
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        }else {

                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() -1);

                                        if (!showDeleteBtn){
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(Quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString()) ;
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {
                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timeStamp = new HashMap<>();
                                                    timeStamp.put("time", FieldValue.serverTimestamp());

                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY")
                                                            .document(quantityDocumentName).set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            qtyIds.add(quantityDocumentName);

                                                            if (finalY + 1 == finalQty - initialQty) {

                                                                firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY")
                                                                        .orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    List<String> serverQuantity = new ArrayList<>();

                                                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                        serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                    }

                                                                                    long availableQty = 0;
                                                                                    for (String qtyId : qtyIds) {

                                                                                        if (!serverQuantity.contains(qtyId)) {

                                                                                                DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                Toast.makeText(itemView.getContext(), "Sorry ! all product may not be available in required quantity...", Toast.LENGTH_SHORT).show();

                                                                                        }else {
                                                                                            availableQty++;
                                                                                        }
                                                                                    }

                                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();

                                                                                }
                                                                                DeliveryActivity.loadingDialog.dismiss();
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });
                                                }
                                            }else if (initialQty > finalQty){

                                                for (int x = 0 ; x < initialQty - finalQty ; x++) {

                                                    final String qtyId = qtyIds.get(qtyIds.size() -1 -x);

                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productId)
                                                            .collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if (finalX + 1 == initialQty - finalQty){
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });

                                                }

                                            }


                                        }

                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity : " + maxQuantity.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });

                        quantityDialog.show();


                    }
                });

                if (offersAppliedNo > 0){
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(cuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("offer applied - Rs." + offerDiscountedAmt + "/-");
                }else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            }else {
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setText("");
                coupenRedemptionLayout.setVisibility(View.GONE);
                freeCoupens.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.GONE);
                coupensApplied.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCoupenIcon.setVisibility(View.INVISIBLE);
            }


            if (showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
            }


            redeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (RewardModel rewardModel : DBqueries.rewardModelList){
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCoupenPriceDialog.show();

                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList){
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        }
                        if (!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position , itemView.getContext() , totalCartAmount);
                    }
                }
            });
        }

        private void showDialogRecyclerview() {


            if (coupensrecyclerView.getVisibility() == View.GONE) {
                coupensrecyclerView.setVisibility(View.VISIBLE);
                selectedCoupen.setVisibility(View.GONE);

            } else {
                coupensrecyclerView.setVisibility(View.GONE);
                selectedCoupen.setVisibility(View.VISIBLE);

            }
        }

    }


    class CartTotalAmountViewholder extends RecyclerView.ViewHolder{

        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView saveAmount;


        public CartTotalAmountViewholder(@NonNull View itemView)
        {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            saveAmount = itemView.findViewById(R.id.saved_amount);


        }


        private void setTotalAmount(int totalItemText , int totalItemPriceText , String deliveryPriceText , int totalAmountText , int savedAmountText){
            totalItems.setText("Price(" + totalItemText + " items) ");
            totalItemPrice.setText("Rs."+totalItemPriceText+"/-");
            if (deliveryPriceText.equals("Free")) {
                deliveryPrice.setText(deliveryPriceText);
            }else {
                deliveryPrice.setText("Rs." + deliveryPriceText + "/-");
            }
            totalAmount.setText("Rs." + totalAmountText + "/-");
            totalCartAmount.setText("Rs." + totalAmountText + "/-");
            saveAmount.setText("You saved Rs." + savedAmountText + "/- on this order.");

            LinearLayout parent = (LinearLayout) totalCartAmount.getParent().getParent();
            if (totalItemPriceText == 0){
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }

                if (showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }

        }
    }


}
