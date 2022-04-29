public class Task {
   // int id;
    String name;
    String description;
    byte status;//я не зря решил память поэкономить? или это не актуально, и можно было делать int и даже String?

    public Task(String name, String description, byte status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return " {name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + getStatus(status) +
                '}';
    }

    public String getStatus(byte status){
        String result;
        if(status == 0){
            result="NEW";
        } else result=(status == 1)?"IN_PROGRESS":"DONE";
    return result;
    }
}
