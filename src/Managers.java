
public class Managers {//из теории Практикума не совсем понял, как это должно выглядеть, поэтому сделал как у
                       // Б.Эккеля в "Философия Java" надеюсь похоже

    static TaskManager getDefault(){//про static не написано было, но я подумал, что должно быть похоже на HistoryManager
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}