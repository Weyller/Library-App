package com.library_app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.library_app.R;
import com.library_app.Utils.NavigationUtils;
import com.library_app.adapter.BookCardsAdapter;
import com.library_app.callbacks.FollowBookCallback;
import com.library_app.callbacks.GetBooksCallback;
import com.library_app.callbacks.ReserveBookCallback;
import com.library_app.callbacks.UpvoteBookCallback;
import com.library_app.controller.ReaderController;
import com.library_app.model.Book;
import com.mikepenz.materialdrawer.Drawer;

import java.util.List;

public class FollowedBooksActivity extends AppCompatActivity implements BookCardsAdapter.Listener
{

    /* UI */
    Drawer navigationDrawer;
    View content;
    RecyclerView recyclerViewGroups;
    SwipeRefreshLayout swipeRefresh;

    /* fields */
    BookCardsAdapter adapterCards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // setup layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_books);

        // get extras
        boolean canUpvote = getIntent().getBooleanExtra(getString(R.string.canUpvote), false);
        boolean canReserve = getIntent().getBooleanExtra(getString(R.string.canReserve), false);

        // setup navdrawer and toolbar
        content = findViewById(R.id.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView text = new TextView(this);
        text.setText("Followed Books");
        text.setTextAppearance(this, android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
        toolbar.addView(text);
        navigationDrawer = NavigationUtils.setupNavigationBar(this, 5, toolbar);

        // reference views
        recyclerViewGroups = (RecyclerView) findViewById(R.id.recyclerViewBooks);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        // setup listeners
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                loadBooks();
            }
        });

        // setup list
        adapterCards = new BookCardsAdapter(this, canReserve, canUpvote);
        adapterCards.setListener(this);
        recyclerViewGroups.setAdapter(adapterCards);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewGroups.setLayoutManager(gridLayoutManager);

        // load data
        loadBooks();

    }


    @Override
    public void onBackPressed()
    {
        if (navigationDrawer.isDrawerOpen())
            navigationDrawer.closeDrawer();
        else
            super.onBackPressed();
    }


    private void loadBooks()
    {
        // start swipe refersh refershing
        swipeRefresh.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefresh.setRefreshing(true);
            }
        });
        ReaderController controller = new ReaderController(this);
        controller.getFollowedBooks(new GetBooksCallback()
        {
            @Override
            public void success(List<Book> books)
            {
                swipeRefresh.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        swipeRefresh.setRefreshing(false);
                    }
                });
                for (Book book : books)
                    book.setIsFollowedByMe(true);
                adapterCards.setData(books);
            }

            @Override
            public void fail(String message)
            {
                swipeRefresh.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        swipeRefresh.setRefreshing(false);

                    }
                });
                Snackbar.make(content, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void upvote(Book book)
    {
        ReaderController controller = new ReaderController(this);
        controller.upvoteBook(book.getIsbn(), new UpvoteBookCallback()
        {
            @Override
            public void success()
            {
                Snackbar.make(content, getString(R.string.success), Snackbar.LENGTH_SHORT).show();
                loadBooks();
            }

            @Override
            public void fail(String message)
            {
                Snackbar.make(content, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void reserve(Book book)
    {
        ReaderController controller = new ReaderController(this);
        controller.reserveBook(book.getIsbn(), new ReserveBookCallback()
        {
            @Override
            public void success()
            {
                Snackbar.make(content, getString(R.string.success), Snackbar.LENGTH_SHORT).show();
                loadBooks();
            }

            @Override
            public void fail(String message)
            {
                Snackbar.make(content, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void follow(Book book)
    {
        ReaderController controller = new ReaderController(this);
        controller.followBook(book.getIsbn(), new FollowBookCallback()
        {
            @Override
            public void success()
            {
                Snackbar.make(content, getString(R.string.success), Snackbar.LENGTH_SHORT).show();
                loadBooks();
            }

            @Override
            public void fail(String message)
            {
                Snackbar.make(content, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
