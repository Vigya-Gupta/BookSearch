package vigya.digipodium.booksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private TextView textAuthor;
    private TextView textBookName;
    private EditText editBookName;
    private TextView textURL;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearch = findViewById(R.id.btnSearch);
        textBookName = findViewById(R.id.textBookName);
        textAuthor = findViewById(R.id.textAuthorName);
        editBookName = findViewById(R.id.editBookName);
        textURL = findViewById(R.id.textURLName);
        btnBuy = findViewById(R.id.btnBuy);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBooks();
            }
        });

    }

    public void searchBooks() {
        String queryString = editBookName.getText().toString();
        new FetchBook(textBookName, textAuthor, textURL).execute(queryString);
    }

    public class FetchBook extends AsyncTask<String, Void, String> {

        private WeakReference<TextView> textTitle;
        private WeakReference<TextView> textAuthor;
        private WeakReference<TextView> textURL;

        public FetchBook(TextView textTitle, TextView textAuthor, TextView textURL) {
            this.textTitle = new WeakReference<>(textTitle);
            this.textAuthor = new WeakReference<>(textAuthor);
            this.textURL = new WeakReference<>(textURL);
        }


        @Override
        protected String doInBackground(String... query) {
            return NetworkUtils.getBookInfo(query[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                //JSONArray itemsArray=jsonObject.getJSONArray("items");
                int i = 0;
                String title = null;
                String authors = null;
                String buyURL = null;
                String rating = null;
                String url = null;
                final String finalUrl;
                while (i < itemsArray.length() && (authors == null && title == null)) {
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volume = book.getJSONObject("volumeInfo");
                    JSONObject link = book.getJSONObject("saleInfo");
                    try {
                        title = volume.getString("title");
                        authors = volume.getString("authors");
                        buyURL = link.getString("buyLink");
                        rating = volume.getString("averageRating");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                if (title != null && authors != null) {
                    textTitle.get().setText(title);
                    textAuthor.get().setText(authors);
                    textURL.get().setText(rating);
                    url = buyURL.toString();
                    finalUrl = url;
                    btnBuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openWebPage(finalUrl);
                        }
                    });
                } else {
                    textTitle.get().setText("Unknown Book Name");
                    textAuthor.get().setText("No results found");
                    textURL.get().setText("Not found");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void openWebPage(String url) {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}