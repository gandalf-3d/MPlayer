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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Album extends Activity{
	ListView listView;
	TaskSong taskSong;
	ShowAlbum artistAdapter;
	Intent intent;
	Cursor cursor;
	Cursor cursor2;
	Player player;
	EditText filterText;
	SharedPreferences preference;
	SharedPreferences.Editor editor;
	private String album;
	public static List<TaskSong> listSong;
	private String[] allSongsStringUri = {"*"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ui();
		cursor = managedQuery( MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, allSongsStringUri, null, null, null);
		getAlbum();
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

	public void getAlbum(){
		artistAdapter = new ShowAlbum(this, getAllAlbums());
		listView.setAdapter(artistAdapter);
	}
	
	public void listListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, final View v, int position,
                    long id) {
				taskSong = listSong.get(position);
				
				if(AlbumListSong.res==false){
					intent = new Intent(getBaseContext(), AlbumListSong.class);
					intent.putExtra("album", taskSong.getAlbum());
					startActivityForResult(intent, 100);
				}
				else{
					intent = new Intent(getBaseContext(), AlbumListSong.class);
					intent.putExtra("album", taskSong.getAlbum());
					setResult(99, intent);
					finish();
				}
            }
        });
	}

	public List getAllAlbums(){
		listSong = new ArrayList<TaskSong>();
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
	            do {
	                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
	                
	                cursor2 = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, allSongsStringUri, MediaStore.Audio.Albums.ALBUM+ "=?", new String[] {album}, null);
	                cursor2.moveToFirst();
	                Player.albumId = cursor2.getInt(cursor2.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
	                
	                listSong.add(new TaskSong(null, null, album, Player.albumId));
	            } while (cursor.moveToNext());
	        }
	       // cursor.close();
	    }
		return listSong;
	}
	
	@Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
        	Player.name = data.getExtras().getString("name");
        	Player.uri = data.getExtras().getString("uri");
        	Player.albumId = data.getExtras().getInt("albumid");
        	
        	intent = new Intent(getBaseContext(), Player.class);
        	intent.putExtra("name", Player.name);
        	intent.putExtra("uri", Player.uri);
        	intent.putExtra("albumid", Player.albumId);
        	setResult(100, intent);
        	cursor.close();
        	finish();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	//  AlbumListSong al = new AlbumListSong();
        	  

           }
        return super.onKeyDown(keyCode, event);
    }
}
