package photo.viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoGalleryViewer extends Activity
{
	public class GridImageAdapter extends BaseAdapter
	{
		private Context mContext;

		public GridImageAdapter(Context c)
		{
			mContext = c;
			loadPhoto();
		}

		public int getCount()
		{
			return cc.getCount();
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position)
		{
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView i = new ImageView(mContext);
			// Move cursor to current position
			imageposition = getCount();
			cc.moveToPosition(imageposition - position - 1);
			// Get the current value for the requested column
			int imageID = cc.getInt(columnIndex);
			// obtain the image URI
			Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(imageID));
			String url = uri.toString();
			// Set the content of the image based on the image URI
			int originalImageId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));
			try
			{
				Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), originalImageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
				if (convertView == null)
				{ // if it's not recycled, initialize
					// some attributes
					i.setLayoutParams(new GridView.LayoutParams(250, 250));
					i.setScaleType(ImageView.ScaleType.FIT_XY);
					i.setPadding(8, 8, 8, 8);
				} else
				{
					i = (ImageView) convertView;
				}
				i.setImageBitmap(b);
			} catch (Exception e)
			{
				Toast.makeText(getApplicationContext(), String.format("Error in GridAdapter: %s", e.getMessage()), Toast.LENGTH_LONG).show();
			}
			return i;
		}
	}

	// Adapter for the Gallery
	public class PhotoImageAdapter extends BaseAdapter
	{
		int mGalleryItemBackground;
		private Context mContext;
		private int windowwidth = 0;

		public PhotoImageAdapter(Context c)
		{
			mContext = c;
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			windowwidth = metrics.widthPixels;
			loadPhoto();
			TypedArray typArray = obtainStyledAttributes(R.styleable.GalleryTheme);
			mGalleryItemBackground = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			typArray.recycle();
		}

		public int getCount()
		{
			return cc.getCount();
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView i = new ImageView(mContext);
			// Move cursor to current position
			imageposition = getCount();
			cc.moveToPosition(imageposition - 1 - position);
			// Get the current value for the requested column
			int imageID = cc.getInt(columnIndex);
			// obtain the image URI
			Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(imageID));
			String url = uri.toString();
			// Set the content of the image based on the image URI
			int originalImageId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));
			try
			{
				Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), originalImageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
				i.setImageBitmap(b);
				i.setLayoutParams(new Gallery.LayoutParams(windowwidth - 20, holder.getLayoutParams().height));
				i.setScaleType(ImageView.ScaleType.FIT_XY);
			} catch (Exception e)
			{
				Toast.makeText(getApplicationContext(), String.format("Error loading Photos: %s", e.getMessage()), Toast.LENGTH_LONG).show();
			}
			return i;
		}
	}

	private static final int SWITCH_VIEW = 0;
	private static final int REFRESH = 1;
	/* columnIndex of the cursor */
	private int columnIndex;
	/* Cursor holding the images and positions */
	private Cursor cc;
	private RelativeLayout holder;
	private static Gallery photos = null;
	private static GridView photogrid = null;
	private static boolean dump = true;
	public static ImageView selectedImage;

	private int imageposition;
	private static boolean grid = true;

	/*
	 * Name: loadPhoto Preconditions: NA Inputs: NA Outputs: Sets up cursor for
	 * images and assigns columnindex
	 */
	public void loadPhoto()
	{
		try
		{
			String[] columns = new String[] { BaseColumns._ID, MediaColumns.TITLE, MediaColumns.DATA, MediaColumns.MIME_TYPE, MediaColumns.SIZE };
			cc = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
			columnIndex = cc.getColumnIndexOrThrow(BaseColumns._ID);
		} catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), String.format("Error loading Photos: %s", e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			photos = new Gallery(this.getApplicationContext());
			photogrid = new GridView(this.getApplicationContext());
			loadPhoto();
			holder = new RelativeLayout(this.getApplicationContext());
			holder.setBackgroundResource(R.drawable.yunoguy);
			TextView title = new TextView(this.getApplicationContext());
			title.setText("Y U NO SELECT A PHOTO!");
			title.setTextSize(25);
			title.setTextColor(Color.WHITE);
			title.setTypeface(Typeface.DEFAULT_BOLD);

			Button credit = new Button(this);
			credit.setText("About");
			credit.setBackgroundResource(R.drawable.troll_small);
			credit.setTextColor(Color.BLACK);
			credit.setTextSize(25);
			credit.setTypeface(Typeface.SANS_SERIF);
			credit.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					try
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
						builder.setTitle("About");
						TableLayout box = new TableLayout(v.getContext());
						TextView credit = new TextView(v.getContext());
						credit.setText("Development and Design by\nBrad Catlett-Rossen\nAll Rights Reserved"
								+ "\nAll Memebase pictures are in NO way, shape or form my property\n\n"
								+ "\nTo Delete an added photo just Double Tap\n\nTo Scale just engaged with one finger than move a second finger\n"
								+ "\nTo add a caption first select a photo then select the Add Caption menu item"
								+ "\n\nYou can switch from grid to gallery view by using the main menu and selecting Switch View"
								+ "\n\nRefresh the photo view by clicking Refresh in the main menu");
						box.addView(credit);
						builder.setView(box);
						AlertDialog alert = builder.create();
						alert.show();
					} catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), String.format("Error on about button %s", e.getMessage()), Toast.LENGTH_LONG).show();
					}
				}
			});
			RelativeLayout.LayoutParams top = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			top.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			top.addRule(RelativeLayout.CENTER_HORIZONTAL);
			RelativeLayout.LayoutParams bottom = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			bottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			bottom.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.addView(title, top);
			holder.addView(credit, bottom);
			if (grid == true)
			{
				setupgridview();
			} else
			{
				setupgalleryview();
			}
			this.setContentView(holder);
		} catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), String.format("Error in Gallery: %s", e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, PhotoGalleryViewer.SWITCH_VIEW, 0, "Switch View");
		menu.add(0, PhotoGalleryViewer.REFRESH, 1, "Refresh");
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		cc.close();
		finish();
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case SWITCH_VIEW:
			if (grid)
			{
				setupgalleryview();
			} else
			{
				setupgridview();
			}
			return true;
		case REFRESH:
			/*
			 * Restarts the activity to refresh the cursor and views to the most
			 * current state
			 */
			Toast.makeText(getApplicationContext(), "Refreshing View", Toast.LENGTH_LONG).show();
			cc = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
			;
			((BaseAdapter) photogrid.getAdapter()).notifyDataSetChanged();
			if (grid)
			{
				photogrid.invalidateViews();
			} else
			{
				photos.invalidate();
			}
			holder.invalidate();
			return true;
		}
		return false;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (dump == true)
		{
			finish();
		}
	}

	/*
	 * Requeries the cursor to check for deletion of photos or addition of new
	 * ones
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		loadPhoto();
		dump = true;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	/*
	 * Sets up the gallery view
	 */
	private void setupgalleryview()
	{
		try
		{
			grid = false;
			holder.removeView(photogrid);
			holder.invalidate();
			loadPhoto();
			photos = new Gallery(this.getApplicationContext());
			photos.setAdapter(new PhotoImageAdapter(this));
			photos.setFadingEdgeLength(110);
			photos.setSpacing(125);
			/*
			 * Click Listener OnClick - Starts an activity with the image
			 * position for use
			 */
			photos.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View v, int position, long id)
				{
					dump = false;
					selectedImage = (ImageView) v;
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), photo.viewer.PhotoEditer.class);
					startActivity(intent);
				}
			});
			RelativeLayout.LayoutParams mid = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			mid.bottomMargin = 125;
			mid.topMargin = 50;
			mid.addRule(RelativeLayout.CENTER_IN_PARENT);
			holder.addView(photos, mid);
		} catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), String.format("Error setting up grid %s", e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * Sets up the grid view
	 */
	private void setupgridview()
	{
		try
		{
			grid = true;
			holder.removeView(photos);
			holder.invalidate();
			loadPhoto();
			photogrid = new GridView(this.getApplicationContext());
			photogrid.setAdapter(new GridImageAdapter(this));
			photogrid.setColumnWidth(200);
			photogrid.setNumColumns(3);
			photogrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			photogrid.setGravity(Gravity.CENTER);
			photogrid.setPadding(0, 70, 0, 40);
			photogrid.setFadingEdgeLength(100);
			/*
			 * Click Listener OnClick - Starts an activity with the image
			 * position for use
			 */
			photogrid.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View v, int position, long id)
				{
					dump = false;
					selectedImage = (ImageView) v;
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), photo.viewer.PhotoEditer.class);
					startActivity(intent);
				}
			});
			RelativeLayout.LayoutParams mid = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			mid.bottomMargin = 70;
			mid.addRule(RelativeLayout.CENTER_IN_PARENT);
			holder.addView(photogrid, mid);
		} catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), String.format("Error setting up grid %s", e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}
}