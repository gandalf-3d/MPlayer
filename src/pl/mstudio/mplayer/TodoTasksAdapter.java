package pl.mstudio.mplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class TodoTasksAdapter extends ArrayAdapter<TaskSong> {
    private Activity context;
    private List<TaskSong> tasks;
    private String text;
    private ArrayList<TaskSong> listpicOrigin;
    public TodoTasksAdapter(Activity context, List<TaskSong> tasks) {
        super(context, R.layout.todolistitem, tasks);
        this.context = context;
        this.tasks = tasks;
    }
 
    static class ViewHolder {
        public TextView tvTodoDescription;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.todolistitem, null, true);
            viewHolder = new ViewHolder();
            viewHolder.tvTodoDescription = (TextView) rowView.findViewById(R.id.tvTodoDescription);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        TaskSong taskSong = tasks.get(position);
        viewHolder.tvTodoDescription.setText(taskSong.getName());
        

        return rowView;
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
                if (pic.getName().toLowerCase().contains(charText)) {
                    tasks.add(pic);
                }
            }
        }
        notifyDataSetChanged();
    }
}