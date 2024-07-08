package tools;

public class Note {
    private String content,time,title,html;
    long id;

    public Note(){

    }

    public Note(String title, String content, String time, String html) {
        this.content = content;
        this.time = time;
        this.title = title;
        this.html = html;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public long getId(){
        return id;
    }

    public void setId(long insertId) {
        this.id = insertId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) { this.title = title; }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
