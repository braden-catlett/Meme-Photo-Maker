package photo.viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoEditer extends Activity
{
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private final static int SAVE_FILE = 1;
	private final static int ADD_CAPTION = 2;
	private static int status;
	private int drag = 0;
	private int click = 0;
	private static RelativeLayout playground;
	private ImageView mainimage;
	private RelativeLayout imagelayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setImage();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		((BitmapDrawable) mainimage.getDrawable()).getBitmap().recycle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, PhotoEditer.SAVE_FILE, 0, "Save File");
		menu.add(0, PhotoEditer.ADD_CAPTION, 0, "Add Caption");
		return true;
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case SAVE_FILE:
			playground.buildDrawingCache();
			savePhoto(playground.getDrawingCache());
			return true;
		case ADD_CAPTION:
			addCaption();
			return true;
		}
		return false;
	}

	private void setImage()
	{
		try
		{
			PhotoGalleryViewer.selectedImage.buildDrawingCache();

			imagelayout = new RelativeLayout(this.getApplicationContext());

			playground = new RelativeLayout(this.getApplicationContext());

			mainimage = new ImageView(this.getApplicationContext());
			Bitmap b = Bitmap.createBitmap(PhotoGalleryViewer.selectedImage.getDrawingCache());
			mainimage.setImageBitmap(b);
			mainimage.setAdjustViewBounds(true);
			mainimage.setScaleType(ImageView.ScaleType.FIT_XY);
			mainimage.setId(1);

			playground.addView(mainimage, new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT));

			Gallery gallery = new Gallery(this.getApplicationContext());
			gallery.setBackgroundColor(Color.parseColor("#363636"));
			gallery.setAdapter(new PhotoImageAdapter(this.getApplicationContext()));
			gallery.setSpacing(100);
			gallery.setId(2);

			RelativeLayout.LayoutParams top = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			top.addRule(RelativeLayout.ABOVE, gallery.getId());
			RelativeLayout.LayoutParams bottom = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			bottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			bottom.height = 125;

			imagelayout.addView(playground, top);
			imagelayout.addView(gallery, bottom);
			this.setContentView(imagelayout);

		} catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), String.format("Error in loading photo: %s", e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	// Image adapter containing all the faces and the adapter functions for the
	// gallery
	public class PhotoImageAdapter extends BaseAdapter
	{
		int mGalleryItemBackground;
		private Context mContext;
		private int[] imageid = { R.drawable.troll, R.drawable.troll_small, R.drawable.foreveralone, R.drawable.megusta, R.drawable.derpina, R.drawable.fuface,
				R.drawable.ahhyeah, R.drawable.challenge, R.drawable.creep, R.drawable.dancingdad, R.drawable.fap, R.drawable.fuckyea, R.drawable.guyfox,
				R.drawable.happy, R.drawable.happyalone, R.drawable.ilied, R.drawable.indeed, R.drawable.interestingman, R.drawable.laugh, R.drawable.lolface,
				R.drawable.lolface2, R.drawable.longneck, R.drawable.notbad, R.drawable.okay, R.drawable.oldgusta, R.drawable.omg, R.drawable.papalol,
				R.drawable.pedobear, R.drawable.pedoshocked, R.drawable.pervert, R.drawable.poker, R.drawable.rawr, R.drawable.sadface, R.drawable.scumbag,
				R.drawable.seriously, R.drawable.sweetjesus, R.drawable.tee, R.drawable.teehee, R.drawable.toughkid, R.drawable.truestory, R.drawable.whoa,
				R.drawable.wtf, R.drawable.xzibit, R.drawable.yuno, R.drawable.weyes, R.drawable.rageface, R.drawable.bacon, R.drawable.nyancat,
				R.drawable.raptor, R.drawable.shoop, R.drawable.joseph, R.drawable.ceral, R.drawable.ceral2, R.drawable.parrot, R.drawable.badass,
				R.drawable.fry, R.drawable.glenn, R.drawable.faith, R.drawable.watchingu, R.drawable.crunkface, R.drawable.jesus };

		public PhotoImageAdapter(Context c)
		{
			mContext = c;
			TypedArray typArray = obtainStyledAttributes(R.styleable.GalleryTheme);
			mGalleryItemBackground = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			typArray.recycle();
		}

		public int getCount()
		{
			return imageid.length;
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
			i.setImageResource(imageid[position]);
			i.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{
					ImageView image = new ImageView(mContext);
					image.setImageDrawable(((ImageView) arg0).getDrawable());
					image.setScaleType(ScaleType.FIT_XY);
					image.setAdjustViewBounds(true);
					RelativeLayout.LayoutParams newparams = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
					image.setLayoutParams(newparams);
					image.setOnTouchListener(new OnTouchListener()
					{
						float dx = 0;
						float dy = 0;
						float oldDist = -1f;
						float oldScale = -1f;
						public boolean onTouch(View v, MotionEvent me)
						{
							ImageView image = (ImageView)v;
							switch (me.getAction() & MotionEvent.ACTION_MASK)
							{
							case MotionEvent.ACTION_DOWN:
								status = DRAG;
								click++;
								if (click > 2)
								{
									playground.removeView(v);
									click = 0;
									drag = 0;
								}
								float x = me.getRawX();
								float y = me.getRawY();
							    dx = x - image.getX();
							    dy = y - image.getY();
								break;
							case MotionEvent.ACTION_UP:
								status = NONE;
								if (drag < 4)
								{
									click++;
								} 
								else
								{
									click = 0;
									drag = 0;
								}
								break;
							case MotionEvent.ACTION_OUTSIDE:
								status = NONE;
								break;
							case MotionEvent.ACTION_POINTER_UP:
								status = NONE;
								break;
							case MotionEvent.ACTION_POINTER_DOWN:
								oldDist = spacing(me);
								click = 0;
								status = ZOOM;
								break;
							case MotionEvent.ACTION_MOVE:
								if (status == DRAG)
								{
									float _x = me.getRawX();
									float _y = me.getRawY();
									image.setX(_x - dx);
									image.setY(_y - dy);
									drag++;
								}
								else if (status == ZOOM)
								{
									float newDist = spacing(me);
									
									if(oldDist == -1)
										oldDist = newDist;
									
									float scale = (newDist / oldDist);
									if(scale == oldScale)
									{
										scale = 1.0f;
									}
									
									if (scale < .50f)
									{
										scale = .50f;
									}
									if (scale > 1.50f)
									{
										scale = 1.50f;
									}
									if(image.getHeight() < 400 && image.getWidth() < 400)
									{
										image.setScaleX(scale);
										image.setScaleY(scale);
									}
									
									oldScale = scale;
									oldDist = newDist;
								}
								break;
							}
							return true;
						}
					});
					if (playground.getChildCount() < 100)
					{
						playground.addView(image);
						playground.invalidate();
					} else
					{
						Toast.makeText(mContext, "Android Y U NO HAVE MORE MEMORY!", Toast.LENGTH_LONG).show();
					}
				}
			});
			return i;
		}
	}

	/*
	 * Pythagorean Theory to find the distance between the two points of the
	 * motion event
	 */
	private float spacing(MotionEvent event)
	{
		float x = (event.getX(0) - event.getX(1)) * event.getXPrecision();
		float y = (event.getY(0) - event.getY(1)) * event.getYPrecision();
		return (float) Math.abs(Math.sqrt(x * x + y * y));
	}

	/*
	 * Saves the bitmap of the playground layout where all the images will be
	 * set up
	 */
	private void savePhoto(final Bitmap bmImg)
	{
		String cap = MediaStore.Images.Media.insertImage(getContentResolver(), bmImg, ("Memebasetastic" + Math.random() * 100),
				"Created by Memebase Photo Editor");

		if (cap != null)
			Toast.makeText(getApplicationContext(), "Saved Photo", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(), "Error Saving Photo", Toast.LENGTH_LONG).show();

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), photo.viewer.PhotoGalleryViewer.class);
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	private void addCaption()
	{
		final TextView caption = new TextView(this.getApplicationContext());
		caption.setTextSize(30);
		caption.setTypeface(Typeface.DEFAULT_BOLD);
		caption.setOnTouchListener(new OnTouchListener()
		{
			float dx = 0;
			float dy = 0;
			float oldDist = -1f;
			float oldScale = -1f;
			public boolean onTouch(View v, MotionEvent me)
			{
				TextView text = (TextView)v;
				switch (me.getAction() & MotionEvent.ACTION_MASK)
				{
				case MotionEvent.ACTION_DOWN:
					status = DRAG;
					click++;
					if (click > 2)
					{
						playground.removeView(v);
						click = 0;
						drag = 0;
					}
					float x = me.getRawX();
					float y = me.getRawY();
				    dx = x - text.getX();
				    dy = y - text.getY();
					break;
				case MotionEvent.ACTION_UP:
					status = NONE;
					if (drag < 4)
					{
						click++;
					} 
					else
					{
						click = 0;
						drag = 0;
					}
					break;
				case MotionEvent.ACTION_OUTSIDE:
					status = NONE;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					status = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(me);
					click = 0;
					status = ZOOM;
					break;
				case MotionEvent.ACTION_MOVE:
					if (status == DRAG)
					{
						float _x = me.getRawX();
						float _y = me.getRawY();
						text.setX(_x - dx);
						text.setY(_y - dy);
						drag++;
					}
					else if (status == ZOOM)
					{
						float newDist = spacing(me);
						
						if(oldDist == -1)
							oldDist = newDist;
						
						float scale = (newDist / oldDist);
						if(scale == oldScale)
						{
							scale = 1.0f;
						}
						
						if (scale < .50f)
						{
							scale = .50f;
						}
						if (scale > 1.05f)
						{
							scale = 1.00f;
						}
						
						if(text.getTextSize() < 150 && text.getTextSize() > 8)
							text.setTextSize(text.getTextSize() * scale);
						
						oldScale = scale;
						oldDist = newDist;
					}
					break;
				}
				return true;
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter in your Caption");
		TableLayout box = new TableLayout(this);
		final EditText text = new EditText(this);
		Spinner spinner = new Spinner(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_picker, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				switch (pos)
				{
				case 0:
					caption.setTextColor(Color.RED);
					break;
				case 1:
					caption.setTextColor(Color.BLUE);
					break;
				case 2:
					caption.setTextColor(Color.GREEN);
					break;
				case 3:
					caption.setTextColor(Color.YELLOW);
					break;
				case 4:
					caption.setTextColor(Color.BLACK);
					break;
				case 5:
					caption.setTextColor(Color.WHITE);
					break;
				case 6:
					caption.setTextColor(Color.MAGENTA);
					break;
				case 7:
					caption.setTextColor(Color.parseColor("#DD7500"));
					break;
				}
				caption.invalidate();
			}

			public void onNothingSelected(AdapterView<?> arg0)
			{
				// Do nothing
			}
		});
		box.addView(text);
		box.addView(spinner);
		builder.setView(box);
		final AlertDialog alert = builder.create();
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				caption.setText(text.getText().toString().trim());
				playground.addView(caption);
			}
		});
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				alert.dismiss();
			}
		});
		alert.show();
	}
}
