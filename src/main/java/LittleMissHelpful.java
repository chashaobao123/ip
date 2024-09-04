import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class LittleMissHelpful {
    private static final String FILE_PATH = "./data/LittleMissHelpfullist.txt";
    public static void main(String[] args) {
        /**
         * Simulates a chatbot
         * Reads input and provides a reply
         */

        String name = "Ah Bang Mang";
        String lineBreak = "---------------------------------";
        String greeting = "Hello! I'm " + name + ".\nWhat you want sia?";
        String exitLine = "Ok, I zao first then!";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Print greeting
        System.out.println(lineBreak);
        System.out.println(greeting);
        System.out.println(lineBreak);

        ArrayList<Task> list = new ArrayList<Task>();

        // Load tasks from file
        loadTasks(list);

        int counter = list.size();;
        while (true) { // Continuous input
            try {
                String input = br.readLine();
                
                // Split into command and description/arguments
                String[] inputList = input.split(" ", 2);
                String command = inputList[0];

                if (inputList.length < 2) {
                    // Check if the user command is "bye" -> exit
                    if (command.equalsIgnoreCase("bye")) {
                        System.out.println(lineBreak);
                        System.out.println(exitLine);
                        System.out.println(lineBreak);
                        saveTasks(list);
                        break; // Exit the loop
                    }

                    // Check if the user command is "list" -> print list
                    if (command.equalsIgnoreCase("list")) {
                        System.out.println(lineBreak);

                        if (counter == 0) {
                            System.out.println("Wah shiok! No tasks at the moment leh.");
                        } else {
                            System.out.println("Siao liao! This your current task list leh...");
                            for (int i = 0; i < counter; i++) {
                                int listNumber = i + 1;
                                Task listItem = list.get(i);
                                System.out.println(listNumber + ". " + listItem.toString());
                            }
                        }
                        System.out.println(lineBreak);
                        continue;
                    }

                    // Invalid command --> throw exception
                    System.out.println(lineBreak);
                    System.out.println("Alamak! Format salah already. Please provide a valid command.");
                    System.out.println(lineBreak);
                    continue;
                }

                String item = inputList[1];

                // Check if user command is "mark" / "unmark" -> mark / unmark task
                if (command.equalsIgnoreCase("mark")) {
                    try {
                        int listIndex = Integer.parseInt(item) - 1;
                        if (listIndex < 0 || listIndex >= counter) {
                            throw new TaskNotFoundException(lineBreak + "\nHai-ya, task number out of range lah.\n" + lineBreak);
                        }
                        Task curTask = list.get(listIndex);
                        list.set(listIndex, curTask.markTask());
                        System.out.println(lineBreak);
                        System.out.println("Wah upz! You have marked this task as done: " + curTask.toString());
                        System.out.println(lineBreak);
                    } catch (NumberFormatException e) {
                        System.out.println(lineBreak + "\nHai-yo, task number must be an integer lah.\n" + lineBreak);
                    } catch (TaskNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    continue;

                } else if (command.equalsIgnoreCase("unmark")) {
                    try {
                        int listIndex = Integer.parseInt(item) - 1;
                        if (listIndex < 0 || listIndex >= counter) {
                            throw new TaskNotFoundException(lineBreak + "Hai-ya, task number out of range lah.\n" + lineBreak);
                        }
                        Task curTask = list.get(listIndex);
                        list.set(listIndex, curTask.unmarkTask());
                        System.out.println(lineBreak);
                        System.out.println("Ok, I see you laze. You have marked this task as not done yet: " + curTask.toString());
                        System.out.println(lineBreak);
                    } catch (NumberFormatException e) {
                        System.out.println(lineBreak + "\nHai-yo, task number must be an integer lah.\n" + lineBreak);
                    } catch (TaskNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    continue;
                }

                //deletes task from list
                if (command.equalsIgnoreCase("delete")) {
                    try {
                        int listIndex = Integer.parseInt(item) - 1;
                        if (listIndex < 0 || listIndex >= counter) {
                            throw new TaskNotFoundException(lineBreak + "\nHai-ya, task number out of range lah.\n" + lineBreak);
                        }
                        Task curTask = list.get(listIndex);
                        list.remove(listIndex);
                        counter--;

                        System.out.println(lineBreak);
                        System.out.println("Wah shiok! This task no more liao: " + curTask.toString());
                        System.out.println("Now got only " + counter + " tasks left.");
                        System.out.println(lineBreak);
                    } catch (NumberFormatException e) {
                        System.out.println(lineBreak + "\nHai-yo, task number must be an integer lah.\n" + lineBreak);
                    } catch (TaskNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    continue;
                }

                // Adds tasks to list
                if (command.equals("todo") || command.equals("deadline") || command.equals("event")) {
                    Task newTask = null;

                    try {
                        if (command.equalsIgnoreCase("todo")) {
                            newTask = new Todo(item);

                        } else if (command.equalsIgnoreCase("deadline")) {
                            String[] itemList = item.split("/by", 2);
                            if (itemList.length < 2) {
                                throw new InvalidTaskFormatException(
                                    lineBreak + "\nAlamak! Deadline format salah already. Use 'deadline description /by date'.\n" + lineBreak);
                            }
                            String desc = itemList[0].trim();
                            String by = itemList[1].trim();

                            newTask = new Deadline(desc, by);

                        } else if (command.equalsIgnoreCase("event")) {
                            String[] itemList = item.split("/from | /to", 3);
                            if (itemList.length < 3) {
                                throw new InvalidTaskFormatException(
                                    lineBreak + "\nAlamak! Event format salah already. Use 'event description /from start /to end'.\n" + lineBreak);
                            }
                            String desc = itemList[0].trim();
                            String from = itemList[1].trim();
                            String to = itemList[2].trim();

                            newTask = new Event(desc, from, to);
                        }

                        list.add(newTask);
                        System.out.println(lineBreak);
                        System.out.println("Added to list liao: " + newTask.toString());
                        System.out.println("Sian, now got " + (counter + 1) + " tasks in your list.");
                        System.out.println(lineBreak);
                        counter++;

                    } catch (InvalidTaskFormatException e) {
                        System.out.println(e.getMessage());
                    }
                    continue;

                } else {
                    // Invalid command --> throw exception
                    System.out.println(
                        lineBreak + "\nAlamak! Format salah already... Please provide a valid command and description.\n" + lineBreak);
                }
            } catch (IOException e) {
                System.out.println("Aiyo salah! Try again lor...");
                e.printStackTrace();
            }
        }
    }

    private static void loadTasks(ArrayList<Task> list) {
        /**
         * Loads tasks from a txt file from harddrive to generate initial list
         * 
         * Creates a new file with a new path if the file does not currently exist
         */
        File dataFile = new File(FILE_PATH);
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
        if (dataFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        Task task = Task.readSavedTask(line);
                        list.add(task);
                    } catch (InvalidTaskFormatException e) {
                        System.out.println("Warning: Skipping invalid task format: " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading the file: " + e.getMessage());
            }
        }
    }

    private static void saveTasks(ArrayList<Task> list) {
        /**
         * Saves tasks to a txt file on harddrive
         */
        File dataFile = new File(FILE_PATH);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile))) {
            for (Task task : list) {
                try {
                    bw.write(task.writeSavedTask());
                    bw.newLine();
                } catch (InvalidTaskFormatException e) {
                    System.out.println("Warning: Cannot write task: " + task.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
        }
    }
}


