package vigya.digipodium.booksearch;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    public static final String TAG= NetworkUtils.class.getSimpleName();
    private static final String BOOK_API_BASE="https://www.googleapis.com/books/v1/volumes?";
    private static final String MAX_RESULTS="maxResults";
    private static final String QUERY_PARAM="q";
    private static final String PRINT_TYPE="printType";
    private static final String YOUR_API_KEY="printType";
    private static final String DOWNLOAD="FILTER_UNDEFINED";
    private static final String FILTER="free-ebooks";

    static String getBookInfo(String query){

       // GET download=EPUB&filter=free-ebooks&langRestrict=english& HTTP/1.1




        HttpURLConnection urlConnection=null;
        BufferedReader reader= null;
        String bookJSONData=null;
        try{
            Uri buildUri=Uri.parse(BOOK_API_BASE).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,query)
                    .appendQueryParameter(MAX_RESULTS,"10")
                    .appendQueryParameter(PRINT_TYPE,"books")
                    .appendQueryParameter(DOWNLOAD,"FILTER_UNDEFINED")
                    .appendQueryParameter(FILTER,"FILTER_UNDEFINED")
                    .build();
            URL requestUrl=new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inp = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inp));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            bookJSONData = builder.toString();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG,bookJSONData);
        return bookJSONData;

    }
}
