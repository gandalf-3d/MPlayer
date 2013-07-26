package pl.mstudio.mplayer;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumListSong extends Activity {
		ListView listView;
		TaskSong taskSong;
		TodoTasksAdapter artistAdapter;
		Intent intent;
		Cursor cursor;
		Player player;
		EditText filterText;
		SharedPreferences preference;
		SharedPreferences.Editor editor;
		private String album;
		private String[] allSongsStringUri = {"*"};
		public static boolean res=false;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			ui();
			cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allSongsStringUri, MediaStore.Audio.Albums.ALBUM+ "=?", new String[] {album}, null);
			getArtistSongs();
			listeners();
			filterText.addTextChangedListener(filterTextWatcher);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		public void ui(){
			listView = (ListView) findViewById(R.id.lvTodos);
			album = (String) getIntent().getExtras().get("album");
			filterText = (EditText) findViewById(R.building_list.search_box);
		}
		
		private TextWatcher filterTextWatcher = new TextWatcher() {

			@Override
	        public void afterTextChanged(Editable s) {
			   String text = filterText.getText().toString().toLowerCase();
			   artistAdapter.filter(text);
	        }

		    public void beforeTextChanged(CharSequence s, int start, int count,
		            int after) {
		    }

		    public void onTextChanged(CharSequence s, int start, int before,
		            int count) {
		    }

		};
		
		public void listeners(){	
			listListener();
		}

		public void getArtistSongs(){
			artistAdapter = new TodoTasksAdapter(this, getAllAlbumSong());
			listView.setAdapter(artistAdapter);
		}
	
	public void listListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("deprecation")
			@Override
            public void onItemClick(AdapterView<?> parent, final View v, int position,
                    long id) {
				taskSong = ListSong.list.get(position);				
				intent = new Intent(getBaseContext(), Album.class);
				intent.putExtra("name", taskSong.getName());
				intent.putExtra("uri", taskSong.getUri());
				intent.putExtra("albumid", taskSong.getAlbum_id());
				setResult(100, intent);
				
				preference = getSharedPreferences("MyPrefsFile", 0);
		    	editor = preference.edit();
		    	editor.putInt("position", position);
		    	editor.commit();
		    	cursor.close();
		    	finish();
            }
        });
	}

	public List getAllAlbumSong(){
		ListSong.list = new ArrayList<TaskSong>();
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
	            do {
	            	 Player.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		             Player.uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		             Player.albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

		             ListSong.list.add(new TaskSong(Player.name, Player.uri , null, Player.albumId));
	            } while (cursor.moveToNext());
	        }
	       // cursor.close();
	    }
		return ListSong.list;
	}
	
	@Override
	public void onBackPressed() {
	   Log.d("CDA", "onBackPressed Called");
	   		intent = new Intent(getBaseContext(), Album.class);
	   		//res=true;
	   		//startActivityForResult(intent, 99);
	   		setResult(10, intent);
	   		finish();
	}
	
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 99){
        	album = data.getExtras().getString("album");       	
        	cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allSongsStringUri, MediaStore.Audio.Albums.ALBUM+ "=?", new String[] {album}, null);
        	getArtistSongs();
        }
    }
}
