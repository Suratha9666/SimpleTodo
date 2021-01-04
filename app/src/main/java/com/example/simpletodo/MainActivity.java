package com.example.simpletodo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;

    ItemsAdapter itemsAdapter;

    public static final String KEY_ITEM_TEXT="item_text";
    public static final String KEY_ITEM_POSITION="item_position";
    public static final int EDIT_TEXT_CODE=20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd=findViewById(R.id.btnAdd);
        etItem=findViewById(R.id.etItem);
        rvItems=findViewById(R.id.rvItems);

        //etItem.setText("I am doing this from Java");

        //items=new ArrayList<>();
        loadItems();
        //items.add("Buy milk");
        //items.add("Go to the gym");
        //items.add("Have fun");

        ItemsAdapter.onLongClickListener onLongClickListener = new ItemsAdapter.onLongClickListener() {
            @Override
            public void onLongClick(int position) {
            // Delete the item from the model
                items.remove(position);
            // Notify the adapter what item is removed
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item has been removed",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.onClickListener onClickListener = new ItemsAdapter.onClickListener() {

            @Override
            public void onClick(int position) {
                //Log.e("MainActivity","Single click at position "+position);
                // Create the new edit activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                // Pass the relevant data to the new activity
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //Display the edit activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };

        //To render Items
        itemsAdapter=new ItemsAdapter(items,onLongClickListener,onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //To add items
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem=etItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                // Notify adapter that an item is added
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(),"Item has been added",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // Handle the result of Edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK && requestCode==EDIT_TEXT_CODE){
            // Retrieve the updated text value
            String itemText=data.getStringExtra(KEY_ITEM_TEXT);

            // Extract the original position of updated item from position key
            int position=data.getExtras().getInt(KEY_ITEM_POSITION);

            //Update the model at the right position with new item text
            items.set(position,itemText);

            //Notify the adapter
            itemsAdapter.notifyItemChanged(position);

            //Persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(),"Item has been updated successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.w("MainActivity","Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
       return new File(getFilesDir(),"data.txt");
    }

    // This function will load items by reading every line of the data.txt file
    private void loadItems(){
        try {
            items=new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);
            items=new ArrayList<>();
        }
    }

    // This function will save items by writing every line of the data.txt file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items",e);
        }
    }

}