package pl.mstudio.mplayer;

import java.io.FileDescriptor;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Player extends Activity implements OnClickListener, OnPreparedListener, OnErrorListener{
	TextView textView;
	Button buttonPlayPause;
	Button next;
	Button prev;
	Button playListAll;
	Button albums;
	SeekBar seekBar;
	Handler handler;
	TaskSong taskSong;
	TextView time;
	TextView fullTime;
	ImageView cover;
	Context context;
	SharedPreferences preference;
	SharedPreferences.Editor editor;
    View.OnTouchListener gestureListener;
    TaskSong task;
	public static MediaPlayer mp = new MediaPlayer();
	AudioManager am;
	int position;
	int position_new;
	private NotificationManager nm;
	private String timer;
	private String fullT;
	public int mediaPos;
	public int mediaMax;
	private int stateMediaPlayer;
	private final int endList = 4;
    private final int stateMP_Playing = 1;
    private final int stateMP_Pausing = 2;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	public static boolean played = false;
	public static boolean phonesOn = false;
    final GestureDetector gdt = new GestureDetector(new GestureListener());
    private GestureDetector gestureDetector;
	public static String name;
	public static String uri;
    public static int albumId;
    
    private LinearLayout mLinearLayout;
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Visualizer mVisualizer;
    private VisualizerView2 mVisualizerView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout2);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		ui();
		
		mVisualizer.setEnabled(true);
		listeners();
		buttonPlayPause.setOnClickListener(buttonPlayPauseOnClickListener);
		seekBar.setOnSeekBarChangeListener(seekBarOnSeekChangeListener);
		
		earphonesConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            settings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	 }
	
	private void settings() {
		Intent intent = new Intent(getApplicationContext(), AudioFxDemo.class);
		startActivityForResult(intent, 50);
	}
	
	private void earphonesConnected(){
		am = (AudioManager)getSystemService(AUDIO_SERVICE);

		if(am.isWiredHeadsetOn()==true){
			phonesOn=true;
		}
		if(phonesOn==true && am.isWiredHeadsetOn()==false){
			mp.pause();
		    buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_play);
		    stateMediaPlayer = stateMP_Pausing;
			phonesOn=false;
		}
	}

	private void ui() {
		context = getApplicationContext();
		textView = (TextView) findViewById(R.id.songName);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		buttonPlayPause = (Button) findViewById(R.id.playpause);
		next = (Button) findViewById(R.id.next);
		prev = (Button) findViewById(R.id.prev);
		time = (TextView) findViewById(R.id.time);
		fullTime = (TextView) findViewById(R.id.fullTime);
		cover = (ImageView) findViewById(R.id.cover);
		playListAll = (Button) findViewById(R.id.btnPlaylist);
		albums = (Button) findViewById(R.id.btnAlbum);
		setupVisualizerFxAndUI();
	
		next.setBackgroundResource(android.R.drawable.ic_media_next);
		prev.setBackgroundResource(android.R.drawable.ic_media_previous);
	}
	
	private void listeners(){
		imgListener();
		buttonListener();
		playListListener();
	}

	private void playUri(){
		try {
			play(uri);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textView.setText(name);
		
		handler = new Handler();
		mediaPos = mp.getCurrentPosition();
		mediaMax = mp.getDuration();
		seekBar.setMax(mediaMax);
		seekBar.setProgress(mediaPos);
		handler.removeCallbacks(moveSeekBarThread);
		handler.postDelayed(moveSeekBarThread, 100);
	}
	
	private Runnable moveSeekBarThread = new Runnable() {
	    public void run() {
	            if(mp.isPlaying()){
	            	
	            int mediaPos_new = mp.getCurrentPosition();
	            int mediaMax_new = mp.getDuration();
	            seekBar.setMax(mediaMax_new);
	            seekBar.setProgress(mediaPos_new);
	        
	            int seconds = (int) ((mediaPos_new / 1000) % 60);
	            int minutes = (int) ((mediaPos_new / 1000) / 60);
	            
	            int seconds2 = (int) ((mediaMax_new / 1000) % 60);
	            int minutes2 = (int) ((mediaMax_new / 1000) / 60);
	        
	            if (seconds < 10) {
	            	timer = String.format("%d:0%d",minutes,seconds);
	            } else {
	            	timer = String.format("%d:%d",minutes,seconds);
	            }
	            time.setText(timer);
	            
	            fullT = String.format("%d:%d",minutes2,seconds2);
	            fullTime.setText(fullT);
	            
	            earphonesConnected();
	            
	            if(seconds==seconds2 && minutes==minutes2){
	            	nextSong();
	            }
	            
	            handler.postDelayed(this, 100);
	         } 

	    }
	};
	
	private void play(String uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setDataSource(uri);
		mp.setOnErrorListener(this);
		mp.setOnPreparedListener(this);
		mp.prepareAsync();
		
		buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_pause);
		stateMediaPlayer = stateMP_Playing;
		played=true;
		nm.notify(0, notification());
	   
		cover.setImageBitmap(getCover(albumId));
	}
	@Override
	public void onPrepared(MediaPlayer player) {
	    player.start();
	}
	

	private Bitmap getCover(int album_id){
		Bitmap bm = null;
        try 
        {
            final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = getApplicationContext().getContentResolver()
                .openFileDescriptor(uri, "r");

            if (pfd != null) 
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
    } catch (Exception e) {
    	bm = BitmapFactory.decodeResource(getResources(), R.drawable.no_cover);
    }
    return bm;
	}
	
	 SeekBar.OnSeekBarChangeListener seekBarOnSeekChangeListener = new SeekBar.OnSeekBarChangeListener() {

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress,
	                boolean fromUser) {
	            // TODO Auto-generated method stub

	            if (fromUser) {
	            	if(played==true){
	            		mp.seekTo(progress);
	            		seekBar.setProgress(progress);
	            	}
	            }

	        }
	    };
	    
	    private void nextSong(){
	    	
        	preference = getSharedPreferences(PREFS_NAME, 0);
			position = preference.getInt("position", 0);
			position_new = position+1;
			editor = preference.edit();

	    	if(ListSong.list.size() <= position_new){
	    		 mp.reset();
	    		 Toast.makeText(getApplicationContext(), "Koniec play listy!", Toast.LENGTH_SHORT).show();
	    		 buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_play);
	    		 stateMediaPlayer = stateMP_Playing;
	 	    	 editor.putInt("position", position);
		    	 editor.commit();
		    	 stateMediaPlayer = endList;
	    	}
			else{
				taskSong = ListSong.list.get(position_new);
				name = taskSong.getName();
				uri = taskSong.getUri();	
				mp.reset();
				playUri();
				
				task = ListSong.list.get(position_new);
				cover.setImageBitmap(getCover(task.getAlbum_id()));
				
		    	editor.putInt("position", position_new);
		    	editor.commit();
			}
	    }
	    
	    private void preSong(){
	    	preference = getSharedPreferences(PREFS_NAME, 0);
			position = preference.getInt("position", 0);
			position_new = position-1;
			editor = preference.edit();
	  
	    	
	    	if(position_new == -1){
	    		 mp.reset();
	    		 Toast.makeText(getApplicationContext(), "Koniec play listy!", Toast.LENGTH_SHORT).show();
	    		 buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_play);
	    		 stateMediaPlayer = stateMP_Playing;
	    		 editor.putInt("position", position);
	    		 editor.commit();
	    		 stateMediaPlayer = endList;
	    	}
			else{
				taskSong = ListSong.list.get(position_new);
				name = taskSong.getName();
				uri = taskSong.getUri();
				mp.reset();
				playUri();
				
				task = ListSong.list.get(position_new);
				cover.setImageBitmap(getCover(task.getAlbum_id()));
				
				editor.putInt("position", position_new);
				editor.commit();
			}
	    }
	    
	    private void buttonListener() {
			next.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {;
					if(played==true){
						nextSong();
					}
				}
			});
			
			prev.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {;
				if(played==true){
					preSong();
				}
				}
			});					
		}
	    
    private void playListListener(){
	    	playListAll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 Intent i = new Intent(getApplicationContext(), ListSong.class);
		             startActivityForResult(i, 100);
					
				}
			});
	    	
	    	albums.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 Intent i = new Intent(getApplicationContext(), Album.class);
		             startActivityForResult(i, 100);
					
				}
			});
	    }
    
    @Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            uri = data.getExtras().getString("uri");
            name = data.getExtras().getString("name");
            albumId = data.getExtras().getInt("albumid");
            if(played==false){
            	playUri();
            }
            else{
            	stop();
            	playUri();
            }
            played=true;
            
            if(resultCode == 50){
            	playUri();
            }
        }
    }
	    
	    Button.OnClickListener buttonPlayPauseOnClickListener = new Button.OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            switch (stateMediaPlayer) {
	            case stateMP_Playing:
	                mp.pause();
	                buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_play);
	                handler.removeCallbacks(moveSeekBarThread);
	        		handler.postDelayed(moveSeekBarThread, 100);
	                stateMediaPlayer = stateMP_Pausing;
	                break;
	            case stateMP_Pausing:
	                mp.start();
	                buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_pause);
	                handler.removeCallbacks(moveSeekBarThread);
	        		handler.postDelayed(moveSeekBarThread, 100);
	                stateMediaPlayer = stateMP_Playing;
	                break;
	            case endList:
	                playUri();
	                buttonPlayPause.setBackgroundResource(android.R.drawable.ic_media_pause);
	                handler.removeCallbacks(moveSeekBarThread);
	        		handler.postDelayed(moveSeekBarThread, 100);
	                stateMediaPlayer = stateMP_Playing;
	                break;
	            }
	        }
	    };
	    
	    private void imgListener(){
	    	cover.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(final View view, final MotionEvent event) {
	                gdt.onTouchEvent(event);
	                return true;
	            }
	        });
	    }

	    @SuppressLint("NewApi")
		public Notification notification(){
	    	Intent intent = new Intent(this, Player.class);
	    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
	    	
	    	Notification noti = new Notification.Builder(this)
	        .setContentTitle("Teraz gra")
	        .setContentText(name)
	        .setSmallIcon(android.R.drawable.ic_media_pause)
	        .setContentIntent(pIntent)
	        .addAction(android.R.drawable.ic_media_pause, "play", pIntent).build();
	    	
	    	return noti;
	    }
	    
	    public void stop(){
	    	mp.reset();
	    }
	    
		public static String getName() {
			return name;
		}

		public static void setName(String name) {
			Player.name = name;
		}
	    
	    
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	           if(mp.isPlaying()){
	        	   moveTaskToBack(true);
	        	   return true;
	           }
	           else{
	        	   moveTaskToBack(false);
	           }
	        }
	        return super.onKeyDown(keyCode, event);
	    }

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
		
		private class GestureListener extends SimpleOnGestureListener {
		    @Override
		    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		            float velocityY) {
		        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		        	nextSong();
		        	 Animation inFromLeft = new TranslateAnimation(
		        			 Animation.RELATIVE_TO_PARENT,1.0f,Animation.RELATIVE_TO_PARENT,0.0f,
		        	         Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f);
		        	 inFromLeft.setDuration(250);
		             inFromLeft.setInterpolator(new AccelerateInterpolator());
		             cover.setAnimation(inFromLeft);
		            return false; // Right to left
		        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		        	preSong();
		        	 Animation right = new TranslateAnimation(
		        			 Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,1.5f,
		        	         Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,1.0f);
		        	 right.setDuration(250);
		             right.setInterpolator(new AccelerateInterpolator());
		             cover.setAnimation(right);
		            return false; // Left to right
		        }

		        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
		                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
		            return false; // Bottom to top
		        } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
		                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
		            return false; // Top to bottom
		        }

		        return false;
		    }
		}
		
		private void setupVisualizerFxAndUI() {
	        mVisualizerView = new VisualizerView2(this);
	        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
	                ViewGroup.LayoutParams.FILL_PARENT,
	                (int)(VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
	        mLinearLayout.addView(mVisualizerView);

	        // Create the Visualizer object and attach it to our media player.
	        mVisualizer = new Visualizer(mp.getAudioSessionId());
	        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
	        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
	            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	                    int samplingRate) {
	                mVisualizerView.updateVisualizer(bytes);
	            }

	            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
	        }, Visualizer.getMaxCaptureRate() / 2, true, false);
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();

	        if (isFinishing() && mp != null) {
	            mVisualizer.release();;
	            mp.release();
	            mp = null;
	            nm.cancel(0);
	        }
	    }

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}
	}

