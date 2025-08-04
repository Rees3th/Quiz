package quizLogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Diese Klasse repräsentiert ein Thema in einem Quiz.
 * Jedes Thema kann mehrere Fragen enthalten.
 * 
 * @author [Your Name]
 */
public class Thema extends QObjekt {

    private String title;
    private String text;

   
    private Map<Integer, Question> questionMap = new HashMap<>();

    public Thema() {
        super();
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }


    public void addQuestion(Question q) {
        if (q != null) {
            questionMap.put(q.getId(), q);
        }
    }

    public Question getQuestionById(int id) {
        return questionMap.get(id);
    }

    public void removeQuestionById(int id) {
        questionMap.remove(id);
    }

    public Collection<Question> getAllQuestions() {
        return questionMap.values();
    }

    @Override
    public String toString() {
        return getTitle();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Thema other = (Thema) obj;
        return this.getId() == other.getId(); 
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }


}
