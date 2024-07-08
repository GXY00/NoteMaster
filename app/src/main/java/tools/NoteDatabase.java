package tools;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {

    private Context mContext;
    // 定义数据库表和字段的常量
    public static final String TABLE_NAME = "notes";
    public static final String TITLE = "title"; // 标题字段 // 表名
    public static final String CONTENT = "content"; // 内容字段
    public static final String HTML = "html"; //html字段
    public static final String ID = "_id"; // 主键字段
    public static final String TIME = "time"; // 时间字段

    // 定义数据库表的列名
    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.TITLE,
            NoteDatabase.HTML,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME
    };

    // 构造方法，接受上下文参数
    public NoteDatabase(Context mContext) {
        super(mContext, "notes", null, 1); // 创建数据库，指定名称和版本
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行 SQL 语句创建名为 "notes" 的表
        db.execSQL("CREATE TABLE " + TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键字段，自增长
                + TITLE + " TEXT NOT NULL, "
                + HTML + " TEXT NOT NULL, "
                + CONTENT + " TEXT NOT NULL, " // 内容字段，不能为空
                + TIME + " TEXT NOT NULL)"); // 时间字段，不能为空
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 添加一条笔记记录
    public Note addNote(Note note) {
        Log.e(TAG,"开始添加");
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues(); // 创建一个用于存储数据的 ContentValues 对象
        contentValues.put(NoteDatabase.TITLE, note.getTitle()); // 添加时间
        contentValues.put(NoteDatabase.HTML, note.getHtml()); // 添加html
        contentValues.put(NoteDatabase.CONTENT, note.getContent()); // 添加内容
        contentValues.put(NoteDatabase.TIME, note.getTime()); // 添加时间
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues); // 将数据插入数据库
        note.setId(insertId); // 将插入后的 ID 设置到笔记对象中
        db.close();
        Log.e(TAG,"添加成功");
        return note; // 返回包含新数据的笔记对象

    }

    public List<Note> getAllNotes() {
        Log.e(TAG,"开始获取");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                NoteDatabase.TABLE_NAME,  // 表名
                NoteDatabase.columns,                // 要查询的列（在这里是ID、内容、时间）
                null,                   // 查询条件（null表示无特殊条件）
                null,                   // 查询条件参数（null表示无特殊条件）
                null,                   // 分组方式（null表示不分组）
                null,                   // 过滤方式（null表示不过滤）
                null                    // 排序方式（null表示不排序）
        );

        List<Note> notes = new ArrayList<>(); // 创建一个笔记列表用于存储查询结果
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Note note = new Note(); // 创建笔记对象
                note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDatabase.ID))); // 设置 ID
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.TITLE))); // 设置标题
                note.setHtml(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.HTML)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.CONTENT))); // 设置内容
                note.setTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.TIME))); // 设置时间
                notes.add(note); // 将笔记对象添加到列表中
            }
        }
        cursor.close(); // 关闭游标
        db.close();
        Log.e(TAG,"获取所有笔记成功");
        return notes; // 返回包含所有笔记记录的列表
    }

    public void deleteNoteById(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // 执行删除操作，根据 ID 删除指定笔记
        db.delete(
                NoteDatabase.TABLE_NAME,
                NoteDatabase.ID + "=?",
                new String[]{String.valueOf(noteId)}
        );
        db.close();
    }

    // 根据 ID 获取笔记
    public Note getNoteById(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // 查询数据库，获取指定 ID 的笔记记录
        Cursor cursor = db.query(
                NoteDatabase.TABLE_NAME,   // 表名
                columns,                   // 要查询的列（在这里是ID、内容、时间）
                NoteDatabase.ID + "=?",    // 查询条件（通过 ID 进行查询）
                new String[]{String.valueOf(noteId)},  // 查询条件参数（指定要查询的 ID 值）
                null,                      // 分组方式（null表示不分组）
                null,                      // 过滤方式（null表示不过滤）
                null                       // 排序方式（null表示不排序）
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            // 如果查询到结果，则创建新的笔记对象并设置其属性
            note = new Note();
            note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDatabase.ID))); // 设置 ID
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.TITLE)));
            note.setHtml(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.HTML)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.CONTENT))); // 设置内容
            note.setTime(cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabase.TIME))); // 设置时间
        }

        cursor.close(); // 关闭游标，释放资源
        db.close();
        return note; // 返回获取到的笔记对象，如果未找到则返回 null
    }

    // 更新现有笔记
    public void updateNote(Note note) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.CONTENT, note.getContent());
        values.put(NoteDatabase.TITLE,note.getTitle());
        values.put(NoteDatabase.HTML,note.getHtml());
        values.put(NoteDatabase.TIME, note.getTime());

        db.update(
                NoteDatabase.TABLE_NAME,
                values,
                NoteDatabase.ID + " = ?",
                new String[]{String.valueOf(note.getId())}
        );
        db.close();
    }

}
