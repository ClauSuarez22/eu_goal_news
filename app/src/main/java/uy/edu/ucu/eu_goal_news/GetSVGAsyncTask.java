package uy.edu.ucu.eu_goal_news;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import uy.edu.ucu.eu_goal_news.R;

/**
 * Created by albarenguenatalia on 27/05/2015.
 */
public class GetSVGAsyncTask extends AsyncTask<String, Void, Drawable> {
    private Context mContext;
    private ImageView imgView;

    public GetSVGAsyncTask(Context context, ImageView imgView){
        this.mContext = context;
        this.imgView = imgView;
    }

    @Override
    protected Drawable doInBackground(String... params) {

        String urlString = params[0];
        HttpURLConnection connection = null;
        Drawable drawable = null;
        try {
            int endIndex = urlString.lastIndexOf('/');
            String logoQuery = urlString.substring(endIndex + 1);
            String url = urlString.substring(0, endIndex + 1);
            connection = (HttpURLConnection) new URL(url + URLEncoder.encode(logoQuery, "UTF-8")).openConnection();
            SVG svgLogo = SVGParser.getSVGFromInputStream(connection.getInputStream());
            drawable = svgLogo.createPictureDrawable();

        } catch (Exception e) {
            Log.d(GetSVGAsyncTask.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }finally {
            if(connection != null) connection.disconnect();
        }

        // si no tengo drawable disponible cargo un placeholder (por ej. el icono de la app)
        if(drawable == null){
            drawable = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
        }
        return drawable;
    }

    @Override
    public void onPostExecute(Drawable logoDrawable){
        // setLayerType fue introducido en la Version HONEYCOMB (API 11)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            imgView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imgView.setImageDrawable(logoDrawable);
        }else{
            // si no pudeo setear el layerType (Version < HONEYCOMB), uso el placeholder u oculto el imageview
            imgView.setVisibility(View.GONE);
        }

    }
}