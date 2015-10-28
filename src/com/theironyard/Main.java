package com.theironyard;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String, User> users = new HashMap();
        ArrayList<Message> messages = new ArrayList();

        addTestUsers(users);
        addTestMessages(messages);

        Spark.get(
                "/",
                ((request, response) -> {
                    ArrayList<Message> threads = new ArrayList();
                    for (Message message : messages) {
                        if (message.replyId == -1) {
                            threads.add(message);
                        }
                    }

                    HashMap m = new HashMap();
                    m.put("threads", threads);
                    return new ModelAndView(m, "threads.html");
                }),
                new MustacheTemplateEngine()
        );
    }

    static void addTestUsers(HashMap<String, User> users) {
        users.put("Alice", new User());
        users.put("Bob", new User());
        users.put("Charlie", new User());
    }
    static void addTestMessages (ArrayList<Message> messages) {
        messages.add(new Message(0,-1, "Alice", "This is a thread!"));
        messages.add(new Message(1,-1, "Bob", "This is a thread!"));
        messages.add(new Message(2, 0, "Charlie", "Cool thread, Alice!"));
        messages.add(new Message(3, 2, "Alice", "Fuck off Charlie"));

    }
}
