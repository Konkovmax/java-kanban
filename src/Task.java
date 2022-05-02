import java.util.Objects;

public class Task {
    int id;
    String name;
    String description;
    Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return " {name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    //если я правильно понял твою мысль, то переопредлять equals и hashcode надо в любом случае, не важно, пользуюсь я ими
    // или нет. Это как использовать сеттеры и геттеры и т.п., верно? Я просто сначала, думал что надо их как-то для
    // данного ТЗ поиспользовать. И задавал вопросы в слаке в этом контексте.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, description, status);
        result = 31 * result + name.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
