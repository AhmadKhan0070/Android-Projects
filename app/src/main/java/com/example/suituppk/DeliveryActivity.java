package com.example.suituppk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerview;
    private Button changeoraddnewaddressbtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalCartAmount;
    private TextView fullname;
    private String name , mobileNo;
    private TextView fullAddress;
    private TextView pinCode;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageView cod;
    private ImageView jazzCash;
    private String paymentMethod = "jazzCash";
    private TextView codTitle;
    private View divider;
    public static ConstraintLayout orderConfirmationLaoyut;
    private static ImageButton continueShoppingBtn;
    static TextView orderIdTv;
    private static String order_id;
    public static boolean codOrderConfirmed = false;
    private boolean successResponse = false;
    public static boolean fromCart;
    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIDs = true;
    public static CartAdapter cartAdapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        changeoraddnewaddressbtn = findViewById(R.id.change_or_add_address_btn);
        totalCartAmount = findViewById(R.id.total_cart_amount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pinCode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        deliveryRecyclerview = findViewById(R.id.delivery_recyclerview);
        orderConfirmationLaoyut = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn1);
        orderIdTv = findViewById(R.id.order_id_tv);


        /////////// loading Dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        /////////// loading Dialog


        /////////// payment Dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        jazzCash = paymentMethodDialog.findViewById(R.id.jazz);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.divider);
        /////////// payment Dialog

        order_id = UUID.randomUUID().toString().substring(0 , 20);
        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerview.setLayoutManager(layoutManager);





         cartAdapter = new CartAdapter(cartItemModelList , totalCartAmount , false);
        deliveryRecyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        changeoraddnewaddressbtn.setVisibility(View.VISIBLE);

        changeoraddnewaddressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQtyIDs = false;

                Intent myAddressesIntent = new Intent(DeliveryActivity.this , MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE" , SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.dismiss();
                Boolean allProductsAvilible = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvilible = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);

                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codTitle.setAlpha(1f);

                        }
                    }
                }
                if (allProductsAvilible){
                    paymentMethodDialog.show();
                }

            }
        });



        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrderDetails();
                orderIdTv.setText("Order Id : " + order_id);

            }
        });
        jazzCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "jazzCash";
                placeOrderDetails();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        ///////// accessing quantity


        if (getQtyIDs) {

            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (int y = 0 ; y < cartItemModelList.get(x).getProductQuantity() ; y++){
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0 , 20);

                    Map<String , Object> timeStamp = new HashMap<>();
                    timeStamp.put("time" , FieldValue.serverTimestamp());

                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                            .document(quantityDocumentName).set(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()){

                                    cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                    if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()){

                                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY")
                                                .orderBy("time" , Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){

                                                            List<String> serverQuantity = new ArrayList<>();

                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                            }

                                                            long availableQty = 0;
                                                            boolean noLongerAvailable = false;
                                                            for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()){


                                                                cartItemModelList.get(finalX).setQtyError(false);
                                                                if (!serverQuantity.contains(qtyId)){

                                                                    if (noLongerAvailable){
                                                                        cartItemModelList.get(finalX).setInStock(false);
                                                                    }else{

                                                                        cartItemModelList.get(finalX).setQtyError(true);
                                                                        cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                        Toast.makeText(DeliveryActivity.this, "Sorry ! all product may not be available in required quantity...", Toast.LENGTH_SHORT).show();

                                                                    }


                                                                }else {
                                                                    availableQty++;
                                                                    noLongerAvailable = false;
                                                                }

                                                            }

                                                            cartAdapter.notifyDataSetChanged();

                                                        }else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();

                                                        }
                                                        loadingDialog.dismiss();
                                                    }
                                                });

                                    }

                                }else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();

                                }
                        }
                    });
                }
            }
        }else {
            getQtyIDs = false;
        }

        ///////// accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullname.setText(name + " - " + mobileNo);
        }else {
            fullname.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")){
            fullAddress.setText(flatNo + " " + locality  + " " + city + " " + state);
        }else {
            fullAddress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pinCode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());

      if (codOrderConfirmed){
            showConfirmationLayout();
       }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
     if (id == android.R.id.home){
         finish();
         return true;
     }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {

            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                                .collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))){

                                            cartItemModelList.get(finalX).getQtyIDs().clear();


                                        }

                                    }
                                });

                    }
                }else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }

        }
    }

    public void showConfirmationLayout(){

        successResponse = true;
        loadingDialog.show();
        codOrderConfirmed = false;
        getQtyIDs = false;

        for (int x = 0 ; x < cartItemModelList.size() -1 ; x++){

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()){
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                        .collection("QUANTITY").document(qtyID).update("user_ID" , FirebaseAuth.getInstance().getUid());
            }

        }



        if (Home_Activity.mainActivity != null){
            Home_Activity.mainActivity.finish();
            Home_Activity.mainActivity = null;
            Home_Activity.showCart = false;
        }else {

            Home_Activity.resetMainActivity = true;
        }

        if (ProductDetailsActivity.productDetailsActivity != null){
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }

        if (fromCart){

            loadingDialog.show();
            Map<String , Object> updateCartList = new HashMap<>();

            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();

            for (int x = 0 ; x < DBqueries.cartList.size() ; x++){
                if (!cartItemModelList.get(x).isInStock()){
                    updateCartList.put("product_ID_" + cartListSize , cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size" , cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_CART").set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        for (int x = 0 ; x <  indexList.size() ; x++){
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() -1);
                        }

                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                    Toast.makeText(DeliveryActivity.this , "order placed successfully", Toast.LENGTH_SHORT).show();

                }
            });


        }


        orderConfirmationLaoyut.setVisibility(View.VISIBLE);

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadingDialog.dismiss();
        paymentMethodDialog.dismiss();



    }

    private void placeOrderDetails() {
        String userID = FirebaseAuth.getInstance().getUid();

        loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                Map<String , Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID" , order_id);
                orderDetails.put("Product Id" , cartItemModel.getProductID());
                orderDetails.put("Product Image" , cartItemModel.getProductImage());
                orderDetails.put("Product Title" , cartItemModel.getProductTitle());
                orderDetails.put("User Id" , userID);
                orderDetails.put("Product Quantity" , cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null ) {
                    orderDetails.put("Cutted price", cartItemModel.getCuttedPrice());
                }else{
                    orderDetails.put("Cutted price", "");
                }
                orderDetails.put("Product price" , cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCoupenId() != null) {
                    orderDetails.put("Coupen Id", cartItemModel.getSelectedCoupenId());
                }else{
                    orderDetails.put("Coupen Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted price", cartItemModel.getDiscountedPrice());
                }else{
                    orderDetails.put("Discounted price", "");
                }
                orderDetails.put("Ordered date" , FieldValue.serverTimestamp());
                orderDetails.put("Packed date" , FieldValue.serverTimestamp());
                orderDetails.put("Shipped date" , FieldValue.serverTimestamp());
                orderDetails.put("Delivered date" , FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date" , FieldValue.serverTimestamp());
                orderDetails.put("Order status " , "Ordered");

                orderDetails.put("Payment Method" , paymentMethod);
                orderDetails.put("Address" , fullAddress.getText());
                orderDetails.put("Full Name" , fullname.getText());
                orderDetails.put("Pin code" , pinCode.getText());
                orderDetails.put("Free coupens" , cartItemModel.getFreeCoupens());
                orderDetails.put("Delivery price" , cartItemModelList.get(cartItemModelList.size() -1).getDeliveryPrice());
                orderDetails.put("cancellation requested" , false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("orderItems").document(cartItemModel.getProductID())
                .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){

                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {

                Map<String , Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalItems());
                orderDetails.put("Total Items price",cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount",cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("payment status" , "not paid");
                orderDetails.put("Order status " , "cancelled");

                firebaseFirestore.collection("ORDERS").document(order_id).set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    if (paymentMethod.equals( "jazzCash")){
                                        jazzCash();
                                    }else {
                                        cod();
                                    }
                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    }

    private void jazzCash(){

        getQtyIDs = false;
    }
    private void cod(){

        getQtyIDs = false;



            Map<String , Object> updateStatus = new HashMap<>();
            updateStatus.put("payment status" , "not paid");
            updateStatus.put("Order status " , "Ordered");
            firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Map<String , Object> userOrder = new HashMap<>();
                            userOrder.put("order_id" , order_id);
                            userOrder.put("time" , FieldValue.serverTimestamp());
                            if (task.isSuccessful()){
                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id)
                                        .set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       showConfirmationLayout();
                                   }else {
                                       Toast.makeText(DeliveryActivity.this, "failed to update user's Order list", Toast.LENGTH_SHORT).show();
                                   }
                                    }
                                });

                            }else {
                                Toast.makeText(DeliveryActivity.this, "Order cancelled", Toast.LENGTH_LONG).show();
                            }
                        }
                    });





    }
    @Override
    public void onBackPressed() {

        if (successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }
}