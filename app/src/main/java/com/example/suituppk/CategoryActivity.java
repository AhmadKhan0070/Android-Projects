package com.example.suituppk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.suituppk.DBqueries.lists;
import static com.example.suituppk.DBqueries.loadFragmentData;
import static com.example.suituppk.DBqueries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private HomePageAdapter adapter ;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //////////Home page fake list

        List<sliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new sliderModel("null" , "#ffffff"));
        sliderModelFakeList.add(new sliderModel("null" , "#ffffff"));
        sliderModelFakeList.add(new sliderModel("null" , "#ffffff"));
        sliderModelFakeList.add(new sliderModel("null" , "#ffffff"));
        sliderModelFakeList.add(new sliderModel("null" , "#ffffff"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));


        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"" ,"#ffffff"));
        homePageModelFakeList.add(new HomePageModel(2,"" , "#ffffff" , horizontalProductScrollModelFakeList , new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"" , "#ffffff" , horizontalProductScrollModelFakeList));


        //////////Home page fake list

        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);


        adapter = new HomePageAdapter(homePageModelFakeList);


        int listposition = 0 ;
        for (int x=0 ; x < loadedCategoriesNames.size() ; x++){
            if (loadedCategoriesNames.get(x).equals(title)){
                listposition = x;
            }
        }

        if (listposition == 0){

            loadedCategoriesNames.add(title);
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(categoryRecyclerView , this , loadedCategoriesNames.size() - 1 , title);

        }else {

            adapter = new HomePageAdapter(lists.get(listposition));
        }

        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.search_icon){

            Intent searchIntent = new Intent(this , SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }else if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}