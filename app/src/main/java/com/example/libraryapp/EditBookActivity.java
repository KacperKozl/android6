package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class EditBookActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_BOOK_TITLE="com.example.libraryapp.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_BOOK_AUTHOR="com.example.libraryapp.EDIT_BOOK_AUTHOR";
    private EditText editTitleEditText;
    private EditText editAuthorEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        Intent requestIntent=getIntent();
        editTitleEditText=findViewById(R.id.edit_book_title);
        editAuthorEditText=findViewById(R.id.edit_book_author);
        if(requestIntent.hasExtra(EXTRA_EDIT_BOOK_TITLE))
            editTitleEditText.setText(requestIntent.getStringExtra(EXTRA_EDIT_BOOK_TITLE));
        if(requestIntent.hasExtra(EXTRA_EDIT_BOOK_AUTHOR))
            editAuthorEditText.setText(requestIntent.getStringExtra(EXTRA_EDIT_BOOK_AUTHOR));
        final Button button=findViewById(R.id.button_save);
        button.setOnClickListener(view->{
            Intent replyIntent=new Intent();
            if(TextUtils.isEmpty(editTitleEditText.getText())||TextUtils.isEmpty(editAuthorEditText.getText())){
                setResult(RESULT_CANCELED,replyIntent);
            }else {
                String title=editTitleEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_BOOK_TITLE,title);
                String author= editAuthorEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_BOOK_AUTHOR,author);
                replyIntent.putExtra(MainActivity.REQUEST_CODE,requestIntent.getIntExtra(MainActivity.REQUEST_CODE,0));
                setResult(RESULT_OK,replyIntent);
            }
            finish();
        });
    }
}