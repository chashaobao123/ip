public class Task {
    protected String description;
    protected boolean isDone;

    public Task (String description) {
        this.description = description;
        this.isDone = false;
    }

    public Task (String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public Task markTask() {
        /** 
         * Marks the task if unmarked & returns task.
         */
        this.isDone = true;
        return this;
    }

    public Task unmarkTask() {
        /** 
         * Unmarks the task if marked & returns task.
         */
        this.isDone = false;
        return this;
    }

    public String toString() {
        /** 
         * Returns the String output
         */
        if (this.isDone) {
            return "1 | " + this.description;
        } else {
            return "0 | " + this.description;
        }
    }

    static public Task fromStringToTask(String taskString) throws InvalidTaskFormatException {
        /** 
         * Reads String version of task and returns task output
         */
    
        String[] parts = taskString.split(" \\| ");
        if (parts.length < 3) {
            throw new InvalidTaskFormatException("Invalid task format: " + taskString);
        }
    
        String taskType = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        
        // Check task type and create corresponding Task
        if (taskType.equals("T")) {
            return new Todo(description, isDone);
        } else if (taskType.equals("D")) {
            if (parts.length != 4) {
                throw new InvalidTaskFormatException("Invalid Deadline format: " + taskString);
            }
            String by = parts[3];
            return new Deadline(description, isDone, by);
        } else if (taskType.equals("E")) {
            if (parts.length != 5) {
                throw new InvalidTaskFormatException("Invalid Event format: " + taskString);
            }
            String from = parts[3];
            String to = parts[4];
            return new Event(description, isDone, from, to);
        } else {
            throw new InvalidTaskFormatException("Unknown task type: " + taskType);
        }
    }
    
}
