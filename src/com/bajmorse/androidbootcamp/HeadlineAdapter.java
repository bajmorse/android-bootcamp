package com.bajmorse.androidbootcamp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HeadlineAdapter extends ArrayAdapter<HeadlineEntity> {

	private Context mContext;
	private List<HeadlineEntity> mHeadlineEntityList;

	public HeadlineAdapter(Context context,
			List<HeadlineEntity> headlineEntityList) {
		super(context, R.layout.row_layout, headlineEntityList);
		mHeadlineEntityList = headlineEntityList;
		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_layout, parent, false);
		}

		TextView headlineView = (TextView) convertView
				.findViewById(R.id.headline);
		ImageView photoView = (ImageView) convertView
				.findViewById(R.id.headlinePhoto);
		HeadlineEntity h = mHeadlineEntityList.get(position);

		headlineView.setText(h.getHeadline());

		InputStream path;
		try {
			path = mContext.getAssets().open("images/loading.jpg");
			Bitmap bit = BitmapFactory.decodeStream(path);
			photoView.setImageBitmap(bit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DownloadImageASYNC task = new DownloadImageASYNC(photoView);
		task.execute(h.getPictureURL());

		return convertView;
	}

	private class DownloadImageASYNC extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageASYNC(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

}
