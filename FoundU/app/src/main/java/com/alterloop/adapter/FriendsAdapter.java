package com.alterloop.adapter;


/**
 * Created by Ankit on 07-12-2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.alterloop.foundu.Friends;
import com.alterloop.util.FriendHelper;
import com.alterloop.util.Utility;
import com.alterloop.util.Views;
import com.alterloop.foundu.R;
import com.alterloop.util.FriendHelper;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class FriendsAdapter extends ItemsAdapter<FriendHelper> implements View.OnClickListener {

    public FriendsAdapter(Context context) {
        super(context);

        setItemsList((Arrays.asList(FriendHelper.populateListViewFromDB(this.getContext()))));
    }

    //Constructor added for SearchBar Action
    public FriendsAdapter(String query, Context context)
    {
        super(context);
        setItemsList((Arrays.asList(FriendHelper.populateListViewFromDBOnSearch(query,context))));

    }



    @Override
    protected View createView(FriendHelper item, int pos, ViewGroup parent, LayoutInflater inflater) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.image = Views.find(view, R.id.list_item_image);
        vh.image.setOnClickListener(this);
        vh.title = Views.find(view, R.id.list_item_name);
        vh.date=Views.find(view, R.id.list_item_date);
        view.setTag(vh);

        return view;
    }

    @Override
    protected void bindView(FriendHelper item, int pos, View convertView) {
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.image.setTag(item);
        Picasso.with(convertView.getContext()).load(item.getImageId()).noFade().into(vh.image);
        ImageView friend_imageView = (ImageView) convertView.findViewById(R.id.list_item_image);
        String imageId=item.getImageId();
        if(!imageId.equals("null") && friend_imageView!=null) {
            Bitmap myBitmap = Utility.ConvertToImage(imageId);
            friend_imageView.setImageBitmap(myBitmap);
        }
        else
        {
            friend_imageView.setImageResource(R.drawable.defaultimg);
        }
        vh.title.setText(item.getName());
        vh.date.setText(item.getLastSeenOn().substring(0,6));
    }

    @Override
    public void onClick(View view) {
        if (view.getContext() instanceof Friends) {
            Friends activity = (Friends) view.getContext();
            activity.openDetails(view, (FriendHelper) view.getTag());
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView date;
    }

}


