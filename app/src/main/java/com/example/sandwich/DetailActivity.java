package com.example.sandwich;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.example.sandwich.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    private TextView mAlsoKnownTv;
    private TextView mAlsoKnownLabel;
    private TextView mOriginTv;
    private TextView mOriginLabel;
    private TextView mDescriptionTv;
    private TextView mIngredientTv;
    private ImageView mSandwichIv;

    private static List<String> convertToListFromJsonArray(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        return list;
    }

    public Sandwich parseJSON(String json){

        final String NAME_CODE = "name";
        final String MAIN_NAME_CODE = "mainName";
        final String ALSO_KNOWN_AS_CODE = "alsoKnownAs";
        final String PLACE_OF_ORIGIN_CODE = "placeOfOrigin";
        final String DESCRIPTION_CODE = "description";
        final String IMAGE_CODE = "image";
        final String INGREDIENTS_CODE = "ingredients";

        try {
            JSONObject mainJsonObject = new JSONObject(json);

            JSONObject name = mainJsonObject.getJSONObject(NAME_CODE);
            String mainName = name.getString(MAIN_NAME_CODE);

            JSONArray JSONArrayAlsoKnownAs = name.getJSONArray(ALSO_KNOWN_AS_CODE);
            List<String> alsoKnownAs = convertToListFromJsonArray(JSONArrayAlsoKnownAs);

            String placeOfOrigin = mainJsonObject.optString(PLACE_OF_ORIGIN_CODE);

            String description = mainJsonObject.getString(DESCRIPTION_CODE);

            String image = mainJsonObject.getString(IMAGE_CODE);

            JSONArray JSONArrayIngredients = mainJsonObject.getJSONArray(INGREDIENTS_CODE);
            List<String> ingredients = convertToListFromJsonArray(JSONArrayIngredients);

            return new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSandwichIv = (ImageView) findViewById(R.id.image_iv);
        mAlsoKnownTv = (TextView) findViewById(R.id.also_known_tv);
        mAlsoKnownLabel = (TextView) findViewById(R.id.alsoKnownAs_label);
        mOriginTv = (TextView) findViewById(R.id.origin_tv);
        mOriginLabel = (TextView) findViewById(R.id.placeOfOrigin_label);
        mDescriptionTv = (TextView) findViewById(R.id.description_tv);
        mIngredientTv = (TextView) findViewById(R.id.ingredients_tv);

        Intent intent = getIntent();
        if ( intent == null ) {

            closeOnError();

        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);

        if ( position == DEFAULT_POSITION ) {
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = parseJSON(json);
        if ( sandwich == null ) {
            closeOnError();
            return;
        }

        populateUI(sandwich);

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {


        // set Text to alsoKnownTv
        if (sandwich.getAlsoKnownAs() != null && sandwich.getAlsoKnownAs().size() > 0) {

            for ( String name: sandwich.getAlsoKnownAs() ) {
                mAlsoKnownTv.append(name + "\n");
            }
        } else {
            mAlsoKnownTv.setVisibility(View.GONE);
            mAlsoKnownLabel.setVisibility(View.GONE);
        }

        // set Text to originTv
        if (sandwich.getPlaceOfOrigin().isEmpty()) {
            mOriginTv.setVisibility(View.GONE);
            mOriginLabel.setVisibility(View.GONE);
        } else {
            mOriginTv.setText(sandwich.getPlaceOfOrigin());
        }

        // set Text to descriptionTv
        mDescriptionTv.setText(sandwich.getDescription());

        // set Text to ingredientTv
        if (sandwich.getIngredients() != null && sandwich.getIngredients().size() > 0) {

            for ( String ingredient: sandwich.getIngredients() ) {

                mIngredientTv.append(ingredient + "\n");

            }

        }

        // display the image
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(mSandwichIv);
    }
}
