package com.example.android.carmaintenance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.carmaintenance.MainActivity.DISTANCE_KEY;

/**
 * A placeholder fragment containing a simple view.
 */
public  class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String USER_NAME = "user_name";

    private ImageView imageView;

    private ProgressDialog progressDialog;

    private ImageUrls imageUrls;
    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(USER_NAME, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView distanceText = (TextView) rootView.findViewById(R.id.textView2);
        Button button = (Button) rootView.findViewById(R.id.update_button);
        imageView = rootView.findViewById(R.id.imageView);

        imageUrls = new ImageUrls();

        String imageString = loadPreferenceUri();
        if (imageString.equals("")){

        }else {
            imageView.setImageURI(Uri.parse(imageString));
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(getActivity().getResources().getString(com.example.android.carmaintenance.R.string.loading_image));
        progressDialog.setMessage(getActivity().getResources().getString(com.example.android.carmaintenance.R.string.image_loading));


        distanceText.setText(String.valueOf(loadPreferences()));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask()
                        .execute(stringToURL(imageUrls.getLink()
                        ));
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private int loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt(DISTANCE_KEY, 0);
    }

    private void savePreferences( String value) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uri", value);
        editor.apply();
    }

    private String loadPreferenceUri(){
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        return sharedPreferences.getString("uri", "");
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {

        protected void onPreExecute(){
            progressDialog.show();
        }

        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);


                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                return bmp;

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                connection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result){
            progressDialog.dismiss();

            if(result!=null){
                imageView.setImageBitmap(result);

                Uri imageInternalUri = saveImageToInternalStorage(result);
                imageView.setImageURI(imageInternalUri);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else {
                Toast.makeText(getContext(),com.example.android.carmaintenance.R.string.failed,Toast.LENGTH_LONG).show();
            }
        }
    }

    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    protected Uri saveImageToInternalStorage(Bitmap bitmap){
        ContextWrapper wrapper = new ContextWrapper(getActivity().getApplicationContext());

        File file = wrapper.getDir("Images",MODE_PRIVATE);
        file = new File(file, "image_car"+".jpg");

        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        savePreferences(String.valueOf(savedImageURI));
        return savedImageURI;
    }
}