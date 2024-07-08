package tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.notemaster.R;

import java.util.Arrays;
import java.util.List;

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private List<Note> noteList;
    private OnNoteItemLongClickListener onNoteItemLongClickListener;
    private OnNoteItemClickListener onNoteItemClickListener;

    // 默认构造函数
    public NoteAdapter(){
    }

    // 带参数的构造函数，接受上下文和笔记列表
    public NoteAdapter(Context Context,List<Note> noteList){
        this.context=Context;
        this.noteList=noteList;
    }

    public NoteAdapter(Context context, List<Note> noteList, OnNoteItemClickListener listener, OnNoteItemLongClickListener longClickListener) {
        this.context = context;
        this.noteList = noteList;
        this.onNoteItemClickListener = listener;
        this.onNoteItemLongClickListener = longClickListener;
    }


    /*此处接口的引入主要是为了实现解耦。
    将长按事件的处理逻辑从适配器中抽离，使得适配器可以更灵活地应对不同的事件处理需求。
    通过接口，我们将长按事件的处理权力交给了主活动类。*/
    public interface OnNoteItemClickListener {
        void onNoteItemClick(long noteId);
    }
    public interface OnNoteItemLongClickListener {
        void onNoteItemLongClick(long noteId);
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.note_list_item,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_subtitle = view.findViewById(R.id.tv_subtitle);
        TextView tv_time = view.findViewById(R.id.tv_time);
        String title = noteList.get(position).getTitle();
        String subtitle = noteList.get(position).getContent();
        String time = noteList.get(position).getTime();

        tv_title.setText(title);
        tv_subtitle.setText(subtitle);
        tv_time.setText(time);

        view.setTag(noteList.get(position).getId());

        //通过在 getView 方法中设置长按事件监听器, 可以为每个笔记项设置独立的长按事件监听器。
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 触发笔记项点击事件
                if (onNoteItemClickListener != null) {
                    view.setBackgroundResource(R.drawable.selector);
                    onNoteItemClickListener.onNoteItemClick(noteList.get(position).getId());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 触发笔记项长按事件
                if (onNoteItemLongClickListener != null) {
                    view.setBackgroundResource(R.drawable.selector);
                    onNoteItemLongClickListener.onNoteItemLongClick(noteList.get(position).getId());
                }
                return true; // 消耗长按事件
            }
        });

        return view;
    }
}
