package com.example.libraryapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private BookViewModel bookViewModel;
    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE=1;
    public static final int EDIT_BOOK_ACTIVITY_REQUEST_CODE=2;
    public static final String REQUEST_CODE="REQUEST_CODE";
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Book editedBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        final BookAdapter adapter=new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback touchHelperCallBack=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.book_archived),
                        Snackbar.LENGTH_LONG).show();
                adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(touchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        bookViewModel=new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this,adapter::setBooks);
        FloatingActionButton addBookButton=findViewById(R.id.add_button);
         someActivityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK){
                            Intent data=result.getData();
                            if(data.getIntExtra(REQUEST_CODE,0)==NEW_BOOK_ACTIVITY_REQUEST_CODE)
                            {
                                Book book=new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
                                bookViewModel.insert(book);
                                Snackbar.make(findViewById(R.id.coordinator_layout),getString(R.string.book_added),
                                    Snackbar.LENGTH_LONG).show();
                            }
                            if(data.getIntExtra(REQUEST_CODE,0)==EDIT_BOOK_ACTIVITY_REQUEST_CODE){
                                editedBook.setAuthor(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
                                editedBook.setTitle(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE));
                                bookViewModel.update(editedBook);
                                Snackbar.make(findViewById(R.id.coordinator_layout),getString(R.string.book_edited),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }else {
                            Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.empty_not_saved),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
        addBookButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,EditBookActivity.class);
                intent.putExtra(REQUEST_CODE,NEW_BOOK_ACTIVITY_REQUEST_CODE);
                someActivityResultLauncher.launch(intent);
            }
        });
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if(requestCode==NEW_BOOK_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
//            Book book=new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
//                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
//            bookViewModel.insert(book);
//            Snackbar.make(findViewById(R.id.main_layout),getString(R.string.book_added),
//                    Snackbar.LENGTH_LONG).show();
//        }else {
//            Snackbar.make(findViewById(R.id.main_layout),
//                    getString(R.string.empty_not_saved),
//                    Snackbar.LENGTH_LONG).show();
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private Book book;
        public BookHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.book_list_item,parent,false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            bookAuthorTextView=itemView.findViewById(R.id.book_author);
            bookTitleTextView=itemView.findViewById(R.id.book_title);
        }
        public void bind(Book book){
            this.book=book;
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
        }
        @Override
        public void onClick(View view) {
            editedBook=this.book;
            Intent intent;
            intent=new Intent(MainActivity.this, EditBookActivity.class);
            intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE,book.getTitle());
            intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR,book.getAuthor());
            intent.putExtra(REQUEST_CODE,EDIT_BOOK_ACTIVITY_REQUEST_CODE);
            someActivityResultLauncher.launch(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            bookViewModel.delete(this.book);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.book_deleted),
                    Snackbar.LENGTH_LONG).show();
            return true;
        }

    }
    private class BookAdapter extends RecyclerView.Adapter<BookHolder>{
        private List<Book> books;
        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new BookHolder(getLayoutInflater(),parent);
        }
        @Override
        public void onBindViewHolder(@NonNull BookHolder holder,int position){
            if(books!=null){
                Book book=books.get(position);
                holder.bind(book);
            }else {
                Log.d("MainActivity","No books");
            }
        }
        @Override
        public int getItemCount(){
            if(books!=null) return books.size(); else return 0;
        }
        void setBooks(List<Book> books){
            this.books=books;
            notifyDataSetChanged();
        }
    }
}