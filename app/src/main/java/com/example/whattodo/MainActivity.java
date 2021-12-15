package com.example.whattodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TodoAdapter.ItemClicked {

    EditText etTodo;
    Button btnSave, btnCancel;

    ImageView ivHello;
    TextView tvNothing;

    LinearLayout addTodoDetails;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Todo> myList;

    boolean isNewEntry;
    int todoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTodo = findViewById(R.id.etTodo);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        addTodoDetails = findViewById(R.id.addTodoDetails);
        ivHello = findViewById(R.id.ivHello);
        tvNothing = findViewById(R.id.tvNothing);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myTodo = etTodo.getText().toString().trim();
                if(myTodo.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter what todo.", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        TodoDB db = new TodoDB(MainActivity.this);
                        db.open();
                        if(isNewEntry){
                            db.createEntry(myTodo);
                            myList.add(new Todo(myTodo));
                            Toast.makeText(MainActivity.this, "Todo saved!", Toast.LENGTH_SHORT).show();

                            if(myList.isEmpty()){
                                ivHello.setVisibility(View.VISIBLE);
                                tvNothing.setVisibility(View.VISIBLE);
                            }
                            else{
                                ivHello.setVisibility(View.GONE);
                                tvNothing.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Todo todo = myList.get(todoIndex);
                            Todo updatedTodo = new Todo(myTodo);
                            updatedTodo.setID(todo.getID());
                            myList.set(todoIndex, updatedTodo);
                            db.updateEntry(todo.getID(), myTodo);
                            Toast.makeText(MainActivity.this, "Todo updated!", Toast.LENGTH_SHORT).show();

                        }
                        addTodoDetails.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                        db.close();
                    }
                    catch (SQLException exp){
                        Toast.makeText(MainActivity.this, exp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTodo.setText("");
                addTodoDetails.setVisibility(View.GONE);
            }
        });

        addTodoDetails.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try{
            TodoDB db = new TodoDB(this);
            db.open();
            myList = db.getAllEntries();
            db.close();
        }
        catch (SQLException exp){
            Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(myList.isEmpty()){
            ivHello.setVisibility(View.VISIBLE);
            tvNothing.setVisibility(View.VISIBLE);
        }
        else{
            ivHello.setVisibility(View.GONE);
            tvNothing.setVisibility(View.GONE);
        }

        /*
        myList = new ArrayList<Todo>();
        Todo todo1 = new Todo("Walk");
        Todo todo2 = new Todo("Drink");
        Todo todo3 = new Todo("I want to go Smoking after my exam. Yes I do");
        Todo todo4 = new Todo("Buy meat");
        Todo todo5 = new Todo("I want to start exercising as soon as I start okay. Yes Carlton Mpofu you definitely should start.");
        Todo todo6 = new Todo("Anime");

        myList.add(todo1);
        myList.add(todo2);
        myList.add(todo3);
        myList.add(todo4);
        myList.add(todo5);
        myList.add(todo6);
        */
        adapter = new TodoAdapter(this, myList);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int position) {
//        Toast.makeText(this, myList.get(position).getTodo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClicked(int position) {
        addTodoDetails.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        isNewEntry = false;
        todoIndex = position;
        etTodo.setText(myList.get(position).getTodo());
    }

    @Override
    public void onDeleteClicked(int position)
    {
        Todo todo = myList.get(position);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage("Are you sure you want to delete todo?")
                .setTitle("Delete Todo");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    TodoDB db = new TodoDB(MainActivity.this);
                    db.open();
                    db.deleteEntry(todo.getID());
                    myList.remove(todo);
                    db.close();
                    adapter.notifyDataSetChanged();
                    isNewEntry = true;
                    if(myList.isEmpty()){
                        ivHello.setVisibility(View.VISIBLE);
                        tvNothing.setVisibility(View.VISIBLE);
                    }
                    else{
                        ivHello.setVisibility(View.GONE);
                        tvNothing.setVisibility(View.GONE);
                    }
                }
                catch (SQLException exp){
                    Toast.makeText(MainActivity.this, exp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nothing
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.addNote:
//                Toast.makeText(this, "Add Note clicked", Toast.LENGTH_SHORT).show();
                etTodo.setText("");
                isNewEntry = true;
                addTodoDetails.setVisibility(View.VISIBLE);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}