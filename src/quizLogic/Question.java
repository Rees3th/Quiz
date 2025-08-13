package quizLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import persistence.DataAccessObject;

public class Question extends DataAccessObject {
    private static final long serialVersionUID = 1L;

    private String title;
    private String text;
    private Thema thema;


    private Map<Integer, Answer> answerMap = new LinkedHashMap<>();

    public Question(Thema thema) {
        super();
        this.thema = thema;
  
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Thema getThema() { return thema; }
    public void setThema(Thema thema) { this.thema = thema; }

    public void addAnswer(Answer a) {
        if (a != null) {
            answerMap.put(a.getId(), a);
        }
    }

    public List<Answer> getAnswers() {
        return new ArrayList<>(answerMap.values());
    }

    public void clearAnswers() {
        answerMap.clear();
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Question)) return false;
        Question other = (Question) obj;
        return getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
