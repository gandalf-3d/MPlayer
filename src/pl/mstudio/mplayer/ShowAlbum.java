package pl.mstudio.mplayer;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ShowAlbum extends ArrayAdapter<TaskSong> {
    private Activity context;
    private List<TaskSong> tasks;
    private String text;
    private ArrayList<TaskSong> listpicOrigin;
    public ShowAlbum(Activity context, List<TaskSong> tasks) {
        super(context, R.layout.todolistitemimg, tasks);
        this.context = context;
        this.tasks = tasks;
    }
 
    static class ViewHolder {
        public TextView tvTodoDescription;
        public ImageView img;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.todolistitemimg, null, true);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) rowView.findViewById(R.id.coverView);
            viewHolder.tvTodoDescription = (TextView) rowView.findViewById(R.id.tvTodoDescription1);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        TaskSong taskSong = tasks.get(position);
        viewHolder.img.setImageBitmap(getCover(taskSong.getAlbum_id()));
        viewHolder.tvTodoDescription.setText(taskSong.getAlbum());

        return rowView;
    }
    
    private Bitmap getCover(int album_id){
		Bitmap bm = null;
        try 
        {
            final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                .openFileDescriptor(uri, "r");

            if (pfd != null) 
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
    } catch (Exception e) {
    	bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_cover);
    }
    return bm;
	}
    
    public void filter(String charText) {
        listpicOrigin = new ArrayList<TaskSong>();
        listpicOrigin.addAll(tasks);
        charText = charText.toLowerCase();
        tasks.clear();
        if (charText.length() == 0) {
            tasks.addAll(listpicOrigin);
        } else {
            for (TaskSong pic : listpicOrigin) {
                if (pic.getAlbum().toLowerCase().contains(charText)) {
                    tasks.add(pic);
                }
            }
        }
        notifyDataSetChanged();
    }
}