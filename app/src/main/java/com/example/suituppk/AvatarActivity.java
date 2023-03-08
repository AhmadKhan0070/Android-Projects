package com.example.suituppk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarActivity extends AppCompatActivity {
    private RecyclerView shirtRecyclerView, pentRecyclerView;
    private ArrayList<ShirtModel> shirtModelArrayList = new ArrayList<>();
    private ArrayList<PentModel> pentModelArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private ShirtAdapter shirtAdapter;
    private PentAdapter pentAdapter;
    private FirebaseFirestore firebaseFirestore;
    private String productId;
    private ImageView avatarBox;
    private Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        productId = getIntent().getStringExtra("productId");
        firebaseFirestore = FirebaseFirestore.getInstance();

        shirtRecyclerView = findViewById(R.id.shirt_recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        shirtRecyclerView.setHasFixedSize(true);
        shirtRecyclerView.setLayoutManager(layoutManager);

        pentRecyclerView = findViewById(R.id.pent_recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        pentRecyclerView.setHasFixedSize(true);
        pentRecyclerView.setLayoutManager(layoutManager);

        avatarBox = (ImageView) findViewById(R.id.avatar_box);
        resetBtn = findViewById(R.id.reset_button);

        getProductShirtImages();
        getProductPentImages();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avatarBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.avatar_template));
            }
        });

    }

    private void getProductShirtImages() {
        firebaseFirestore.collection("PRODUCTS")
                .document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        shirtModelArrayList.clear();
                        DocumentSnapshot documentSnapshot = task.getResult();
                        {
                            if (task.isSuccessful()) {
                                ShirtModel shirtModel = documentSnapshot.toObject(ShirtModel.class);
                                shirtModelArrayList.add(shirtModel);
                            }
                        }
                        shirtAdapter = new ShirtAdapter(AvatarActivity.this, shirtModelArrayList);
                        shirtRecyclerView.setAdapter(shirtAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AvatarActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getProductPentImages() {
        firebaseFirestore.collection("PRODUCTS")
                .document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        pentModelArrayList.clear();
                        DocumentSnapshot documentSnapshot = task.getResult();
                        {
                            if (task.isSuccessful()) {
                                PentModel pentModel = documentSnapshot.toObject(PentModel.class);
                                pentModelArrayList.add(pentModel);
                            }
                        }
                        pentAdapter = new PentAdapter(AvatarActivity.this, pentModelArrayList);
                        pentRecyclerView.setAdapter(pentAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AvatarActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private class ImageProcessor {
        private static final int TYPE_SHIRT = 0;
        private static final int TYPE_PANT = 1;
        /**
         * The Template used to display the shirt on avatar
         */
        private Bitmap template;
        /**
         * The image to show on the avatar
         */
        private Bitmap source;
        /**
         * The Type of cloth i.e. shirt/pant
         */
        private int type;

        /**
         * The view where the avatar is  shown
         */
        private ImageView v;

        ImageProcessor(int type, BitmapDrawable template, BitmapDrawable source, ImageView view) {
            // Loading initial data
            this.template = template.getBitmap();
            this.source = source.getBitmap();
            this.v = view;
            this.type = type;
        }

        void processResults() {
            Bitmap res; // The result box
            int height, width, // The height and width of the template image
            heightBreakPoint; // breakpoint for shirt vs pants

            height = this.template.getHeight();
            width = this.template.getWidth();

            this.source = resizeImage(this.source, width, height);

            heightBreakPoint = (int) (height * 0.7);

            res = Bitmap.createBitmap(this.template.getWidth(), this.template.getHeight(), this.template.getConfig());

            for (int x = 0; x < width; x++) {
                if (x >= this.source.getWidth()) {
                    // Breaking if x exceeds source width
                    break;
                }
                for (int y = 0; y < height; y++) {
                    if (y >= this.source.getHeight()) {
                        // Breaking if y exceeds source height
                        break;
                    }
                    int pix = this.template.getPixel(x, y);
                    int pixR = Color.red(pix);
                    int pixB = Color.blue(pix);
                    int pixG = Color.green(pix);

                    int sourcePix = this.source.getPixel(x, y);
                    int sourcePixR = Color.red(sourcePix);
                    int sourcePixB = Color.blue(sourcePix);
                    int sourcePixG = Color.green(sourcePix);

                    if (pixB > pixG && this.type == ImageProcessor.TYPE_SHIRT && y < heightBreakPoint) {
                        // We are replacing only the green portion of the shirt
                        res.setPixel(x, y, Color.rgb(sourcePixR, sourcePixG, sourcePixB));
                    } else if (pixG > pixB && pixG > pixR && this.type == ImageProcessor.TYPE_PANT && y > heightBreakPoint) {
                        // Drawing Pants
                        res.setPixel(x, y, Color.rgb(sourcePixR, sourcePixG, sourcePixB));
                    } else {
                        // This is not the green portion, so we post the original image
                        res.setPixel(x, y, Color.rgb(pixR, pixG, pixB));
                    }
                }
            }
            this.v.setImageBitmap(res);
        }
    }

    static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public class ShirtAdapter extends RecyclerView.Adapter<ShirtAdapter.ViewHolder> {
        Context context;
        ArrayList<ShirtModel> shirtModelArrayList;

        public ShirtAdapter(Context context, ArrayList<ShirtModel> shirtModelArrayList) {
            this.context = context;
            this.shirtModelArrayList = shirtModelArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shirt_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            Glide.with(context).load(shirtModelArrayList.get(position).getProduct_image_1()).into(holder.shirtImage1);
            Glide.with(context).load(shirtModelArrayList.get(position).getProduct_image_2()).into(holder.shirtImage2);
            Glide.with(context).load(shirtModelArrayList.get(position).getProduct_image_3()).into(holder.shirtImage3);

            holder.shirtImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img1, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.shirtImage1.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_SHIRT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_1();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.shirtImage1.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.shirtImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img2, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.shirtImage2.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_SHIRT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_2();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.shirtImage2.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.shirtImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img3, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.shirtImage3.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_SHIRT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_3();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.shirtImage2.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return shirtModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageButton shirtImage1, shirtImage2, shirtImage3;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                shirtImage1 = itemView.findViewById(R.id.pent_image1);
                shirtImage2 = itemView.findViewById(R.id.pent_image2);
                shirtImage3 = itemView.findViewById(R.id.pent_image3);
            }
        }
    }

    public class PentAdapter extends RecyclerView.Adapter<PentAdapter.ViewHolder> {
        Context context;
        ArrayList<PentModel> pentModelArrayList;

        public PentAdapter(Context context, ArrayList<PentModel> pentModelArrayList) {
            this.context = context;
            this.pentModelArrayList = pentModelArrayList;
        }

        @NonNull
        @Override
        public PentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pent_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PentAdapter.ViewHolder holder, final int position) {
            Glide.with(context).load(pentModelArrayList.get(position).getProduct_image_1()).into(holder.pentImage1);
            Glide.with(context).load(pentModelArrayList.get(position).getProduct_image_2()).into(holder.pentImage2);
            Glide.with(context).load(pentModelArrayList.get(position).getProduct_image_3()).into(holder.pentImage3);

            holder.pentImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img1, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.pentImage1.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_PANT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_1();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.pentImage1.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.pentImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img2, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.pentImage2.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_PANT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_2();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.pentImage2.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.pentImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BitmapDrawable template; // The avatar image used a template
                    BitmapDrawable sourceImage; // The source image to print on shirt
                    ImageProcessor processor; // The image processor class

                    // This is where we will be displaying our avatar
                    ImageView avatarBox = (ImageView) findViewById(R.id.avatar_box);

                    // Making sure our image will not overload memory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(getResources(), R.drawable.img3, options);

                    // loading template and sources
                    template = (BitmapDrawable) avatarBox.getDrawable();

                    sourceImage = (BitmapDrawable) holder.pentImage3.getDrawable();
                    processor = new ImageProcessor(ImageProcessor.TYPE_PANT, template, sourceImage, avatarBox);
                    processor.processResults();

                    String url = shirtModelArrayList.get(position).getProduct_image_3();

                    ImagesInitializer loader = new ImagesInitializer();

                    try {
                        Bitmap image = loader.execute(url).get();
                        holder.pentImage3.setImageBitmap(image);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to load image: " + url, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return pentModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageButton pentImage1, pentImage2, pentImage3;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                pentImage1 = itemView.findViewById(R.id.pent_image1);
                pentImage2 = itemView.findViewById(R.id.pent_image2);
                pentImage3 = itemView.findViewById(R.id.pent_image3);
            }
        }
    }


    class ImagesInitializer extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap result = null;
            URL url;
            HttpURLConnection con;
            InputStream in;

            try {
                url = new URL(params[0]);
                con = (HttpURLConnection) url.openConnection();
                in = con.getInputStream();
                result = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                System.out.println(e);
            }
            return result;
        }
    }
}
