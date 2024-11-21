import java.io.Serializable;

public class HelpArticles implements Serializable {
    private static final long serialVersionUID = 1L;

    private String header;
    private String title;
    private String authors;
    private String abstractText;

    public HelpArticles(String header, String title, String authors, String abstractText) {
        this.header = header;
        this.title = title;
        this.authors = authors;
        this.abstractText = abstractText;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }
}
